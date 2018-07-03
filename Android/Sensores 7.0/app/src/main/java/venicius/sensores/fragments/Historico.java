package venicius.sensores.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import venicius.sensores.principal.HourAxisValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import venicius.sensores.R;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Historico extends Fragment {

    View v;


    static BarChart temperaturaChart;
    static BarChart umidadeChart;
    static BarChart presencaChart;
    static BarChart chamasChart;
    static BarChart luzChart;
    FloatingActionButton fabData;

    private LineChart mChart;

    static Activity activity ;

    public Historico() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_historico, container, false);
        temperaturaChart = (BarChart) v.findViewById(R.id.barchartTemperatura);
        umidadeChart = (BarChart) v.findViewById(R.id.barchartUmidade);
        presencaChart = (BarChart) v.findViewById(R.id.barchartMovimento);
        chamasChart = (BarChart) v.findViewById(R.id.barchartChamas);
        luzChart = (BarChart) v.findViewById(R.id.barchartLuz);
        activity = this.getActivity();

        temperaturaChart.setNoDataText("Carregando...");
        umidadeChart.setNoDataText("Carregando...");
        presencaChart.setNoDataText("Carregando...");
        chamasChart.setNoDataText("Carregando...");
        luzChart.setNoDataText("Carregando...");



        fabData = (FloatingActionButton) activity.findViewById(R.id.fabData);
        fabData.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                showDatePickerDialog(v);

            }
        });

        fabData.show();


        if(!this.conectado(this.getContext())){
            Toast.makeText(this.getContext(), "Sem conexão com a internet!",
                    Toast.LENGTH_LONG).show();
        }


        try {
            showProgressDialog();


            new CountDownTimer(2000, 1000) {
                public void onFinish() {
                    hideProgressDialog();
                }

                public void onTick(long millisUntilFinished) {
                    gerarGraficoTemperatura(getHoje());
                    gerarGraficoUmidade(getHoje());
                    gerarGraficoPresenca(getHoje());
                    gerarGraficoChamas(getHoje());
                    gerarGraficoLuz(getHoje());
                }
            }.start();
           //

        } catch (Exception exception){

        }



        return v;
    }



    public static void gerarGraficoTemperatura(final String data) {



        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("/DadosSensores/Temperatura/"+ data + "/");

        reference.addValueEventListener(new ValueEventListener() {

            List<BarEntry> entries = new ArrayList<>();


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Iterator<DataSnapshot> hrIterator = dataSnapshot.getChildren().iterator();
                    while (hrIterator.hasNext()) {
                        DataSnapshot dias = hrIterator.next();
                        String valor = dias.getValue().toString().substring(7,9);


                        String horario = dias.getKey();
                        String horaminuto[] = horario.split(":");
                        int horaInteira = Integer.parseInt(horaminuto[0]) * 60;
                        int minutos = Integer.parseInt(horaminuto[1]);

                        final float horaEmMinutos = horaInteira + minutos;

                        entries.add(new BarEntry(horaEmMinutos, Float.parseFloat(valor)));

                        BarDataSet set = new BarDataSet(entries, "Temperatura");

                        BarData data = new BarData(set);

                        data.setBarWidth(5f); // set custom bar width
                        data.setDrawValues(false);

                        temperaturaChart.setData(data);
                        temperaturaChart.setFitBars(true); // make the x-axis fit exactly all bars
                        temperaturaChart.invalidate(); // refresh
                        //temperaturaChart.setDrawBarShadow(true);
                        temperaturaChart.setDrawBorders(true);

                        IAxisValueFormatter custom = new HourAxisValueFormatter(temperaturaChart);
                        XAxis xAxis = temperaturaChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextSize(10f);
                        xAxis.setTextColor(Color.BLACK);
                        xAxis.setDrawAxisLine(true);
                        xAxis.setDrawGridLines(false);
                        xAxis.setValueFormatter(custom);

                        YAxis yAxis = temperaturaChart.getAxisLeft();
                        yAxis.setTextSize(10f); // set the text size
                        yAxis.setAxisMinimum(10f); // start at zero
                        yAxis.setCenterAxisLabels(true);
                        yAxis.setDrawAxisLine(true);
                        yAxis.setAxisMaximum(50f); // the axis maximum is 100
                        yAxis.setTextColor(Color.BLACK);

                        YAxis yAxisRight = temperaturaChart.getAxisRight();
                        yAxisRight.setTextSize(10f); // set the text size
                        yAxisRight.setAxisMinimum(10f); // start at zero
                        yAxisRight.setCenterAxisLabels(true);
                        yAxisRight.setAxisMaximum(50f); // the axis maximum is 100
                        yAxisRight.setTextColor(Color.BLACK);



                    } //while
                } else {

                    temperaturaChart.setData(null);
                    temperaturaChart.setNoDataText("Sem informações para esta data!");
                    temperaturaChart.invalidate();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    public static void gerarGraficoUmidade(String data) {

        FirebaseDatabase.getInstance().getReference().child("/DadosSensores/Umidade/"+ data + "/").
                addValueEventListener(new ValueEventListener() {
                    List<BarEntry> entries = new ArrayList<>();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Iterator<DataSnapshot> hrIterator = dataSnapshot.getChildren().iterator();
                            while (hrIterator.hasNext()) {
                                DataSnapshot dias = hrIterator.next();
                                String valor = dias.getValue().toString().substring(7,9);

                                String horario = dias.getKey();
                                String horaminuto[] = horario.split(":");
                                int horaInteira = Integer.parseInt(horaminuto[0]) * 60;
                                int minutos = Integer.parseInt(horaminuto[1]);

                                final float horaEmMinutos = horaInteira + minutos;

                                entries.add(new BarEntry(horaEmMinutos, Float.parseFloat(valor)));

                                BarDataSet set = new BarDataSet(entries, "Umidade");
                                BarData data = new BarData(set);
                                data.setBarWidth(5f); // set custom bar width
                                data.setDrawValues(false);

                                umidadeChart.setData(data);
                                umidadeChart.setFitBars(true); // make the x-axis fit exactly all bars
                                umidadeChart.invalidate(); // refresh

                                IAxisValueFormatter custom = new HourAxisValueFormatter(umidadeChart);
                                XAxis xAxis = umidadeChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextSize(10f);
                                xAxis.setTextColor(Color.BLACK);
                                xAxis.setDrawAxisLine(true);
                                xAxis.setDrawGridLines(false);
                                xAxis.setValueFormatter(custom);

                                YAxis yAxis = umidadeChart.getAxisLeft();
                                yAxis.setTextSize(10f); // set the text size
                                yAxis.setAxisMinimum(10f); // start at zero
                                yAxis.setCenterAxisLabels(true);
                                yAxis.setDrawAxisLine(true);
                                yAxis.setAxisMaximum(100f); // the axis maximum is 100
                                yAxis.setTextColor(Color.BLACK);

                                YAxis yAxisRight = umidadeChart.getAxisRight();
                                yAxisRight.setTextSize(10f); // set the text size
                                yAxisRight.setAxisMinimum(10f); // start at zero
                                yAxisRight.setCenterAxisLabels(true);
                                yAxisRight.setAxisMaximum(100f); // the axis maximum is 100
                                yAxisRight.setTextColor(Color.BLACK);



                            }
                        } else {

                            umidadeChart.setData(null);
                            umidadeChart.setNoDataText("Sem informações para esta data!");
                            umidadeChart.invalidate();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void gerarGraficoPresenca(String data) {

        FirebaseDatabase.getInstance().getReference().child("/DadosSensores/Presenca/"+ data + "/").
                addValueEventListener(new ValueEventListener() {
                    List<BarEntry> entries = new ArrayList<>();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Iterator<DataSnapshot> hrIterator = dataSnapshot.getChildren().iterator();
                            while (hrIterator.hasNext()) {
                                DataSnapshot dias = hrIterator.next();
                                String valor = dias.getValue().toString().substring(7,20);
                                // Log.d("TESTE: ", valor);
                                float valorF = 0;
                                if(valor.equals("Sem movimento")){
                                    valorF = 0f;
                                } else if(valor.equals("Com movimento")) {
                                    valorF = 1f;
                                }


                                String horario = dias.getKey();
                                String horaminuto[] = horario.split(":");
                                int horaInteira = Integer.parseInt(horaminuto[0]) * 60;
                                int minutos = Integer.parseInt(horaminuto[1]);

                                final float horaEmMinutos = horaInteira + minutos;

                                entries.add(new BarEntry(horaEmMinutos, valorF));

                                BarDataSet set = new BarDataSet(entries, "Presença");
                                BarData data = new BarData(set);
                                data.setBarWidth(5f); // set custom bar width
                                data.setDrawValues(false);

                                presencaChart.setData(data);
                                presencaChart.setFitBars(true); // make the x-axis fit exactly all bars
                                presencaChart.invalidate(); // refresh

                                IAxisValueFormatter custom = new HourAxisValueFormatter(presencaChart);
                                XAxis xAxis = presencaChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextSize(10f);
                                xAxis.setTextColor(Color.BLACK);
                                xAxis.setDrawAxisLine(true);
                                xAxis.setDrawGridLines(false);
                                xAxis.setValueFormatter(custom);

                                YAxis yAxis = presencaChart.getAxisLeft();
                                yAxis.setTextSize(10f); // set the text size
                                yAxis.setAxisMinimum(0f); // start at zero
                                yAxis.setCenterAxisLabels(true);
                                yAxis.setDrawAxisLine(true);
                                yAxis.setAxisMaximum(1f); // the axis maximum is 100
                                yAxis.setTextColor(Color.BLACK);

                                YAxis yAxisRight = presencaChart.getAxisRight();
                                yAxisRight.setTextSize(10f); // set the text size
                                yAxisRight.setAxisMinimum(0f); // start at zero
                                yAxisRight.setCenterAxisLabels(true);
                                yAxisRight.setAxisMaximum(1f); // the axis maximum is 100
                                yAxisRight.setTextColor(Color.BLACK);



                            }
                        } else {

                            presencaChart.setData(null);
                            presencaChart.setNoDataText("Sem informações para esta data!");
                            presencaChart.invalidate();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void gerarGraficoChamas(String data) {

        FirebaseDatabase.getInstance().getReference().child("/DadosSensores/Chamas/"+ data + "/").
                addValueEventListener(new ValueEventListener() {
                    List<BarEntry> entries = new ArrayList<>();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Iterator<DataSnapshot> hrIterator = dataSnapshot.getChildren().iterator();
                            while (hrIterator.hasNext()) {
                                DataSnapshot dias = hrIterator.next();
                                String valor[] = dias.getValue().toString().split("=");
                                // Log.d("TESTE: ", valor[1]);


                                float valorF = 0;
                                if(valor[1].equals("Sem Registro}")){
                                    valorF = 0f;
                                } else if(valor[1].equals("Fogo}")) {
                                    valorF = 1f;
                                }


                                String horario = dias.getKey();
                                String horaminuto[] = horario.split(":");
                                int horaInteira = Integer.parseInt(horaminuto[0]) * 60;
                                int minutos = Integer.parseInt(horaminuto[1]);

                                final float horaEmMinutos = horaInteira + minutos;

                                entries.add(new BarEntry(horaEmMinutos, valorF));

                                BarDataSet set = new BarDataSet(entries, "Presença");
                                BarData data = new BarData(set);
                                data.setBarWidth(5f); // set custom bar width
                                data.setDrawValues(false);

                                chamasChart.setData(data);
                                chamasChart.setFitBars(true); // make the x-axis fit exactly all bars
                                chamasChart.invalidate(); // refresh

                                IAxisValueFormatter custom = new HourAxisValueFormatter(chamasChart);
                                XAxis xAxis = chamasChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextSize(10f);
                                xAxis.setTextColor(Color.BLACK);
                                xAxis.setDrawAxisLine(true);
                                xAxis.setDrawGridLines(false);
                                xAxis.setValueFormatter(custom);

                                YAxis yAxis = chamasChart.getAxisLeft();
                                yAxis.setTextSize(10f); // set the text size
                                yAxis.setAxisMinimum(0f); // start at zero
                                yAxis.setCenterAxisLabels(true);
                                yAxis.setDrawAxisLine(true);
                                yAxis.setAxisMaximum(1f); // the axis maximum is 100
                                yAxis.setTextColor(Color.BLACK);

                                YAxis yAxisRight = chamasChart.getAxisRight();
                                yAxisRight.setTextSize(10f); // set the text size
                                yAxisRight.setAxisMinimum(0f); // start at zero
                                yAxisRight.setCenterAxisLabels(true);
                                yAxisRight.setAxisMaximum(1f); // the axis maximum is 100
                                yAxisRight.setTextColor(Color.BLACK);



                            }
                        } else {

                            chamasChart.setData(null);
                            chamasChart.setNoDataText("Sem informações para esta data!");
                            chamasChart.invalidate();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void gerarGraficoLuz(String data) {

        FirebaseDatabase.getInstance().getReference().child("/DadosSensores/Luz/"+ data + "/").
                addValueEventListener(new ValueEventListener() {
                    List<BarEntry> entries = new ArrayList<>();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Iterator<DataSnapshot> hrIterator = dataSnapshot.getChildren().iterator();
                            while (hrIterator.hasNext()) {
                                DataSnapshot dias = hrIterator.next();
                                String valor[] = dias.getValue().toString().split("=");
                                // Log.d("TESTE: ", valor[1]);


                                float valorF = 0;
                                if(valor[1].equals("Apagada}")){
                                    valorF = 0f;
                                } else if(valor[1].equals("Acesa}")) {
                                    valorF = 1f;
                                }


                                String horario = dias.getKey();
                                String horaminuto[] = horario.split(":");
                                int horaInteira = Integer.parseInt(horaminuto[0]) * 60;
                                int minutos = Integer.parseInt(horaminuto[1]);

                                final float horaEmMinutos = horaInteira + minutos;

                                entries.add(new BarEntry(horaEmMinutos, valorF));

                                BarDataSet set = new BarDataSet(entries, "Presença");
                                BarData data = new BarData(set);
                                data.setBarWidth(5f); // set custom bar width
                                data.setDrawValues(false);

                                luzChart.setData(data);
                                luzChart.setFitBars(true); // make the x-axis fit exactly all bars
                                luzChart.invalidate(); // refresh

                                IAxisValueFormatter custom = new HourAxisValueFormatter(luzChart);
                                XAxis xAxis = luzChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextSize(10f);
                                xAxis.setTextColor(Color.BLACK);
                                xAxis.setDrawAxisLine(true);
                                xAxis.setDrawGridLines(false);
                                xAxis.setValueFormatter(custom);

                                YAxis yAxis = luzChart.getAxisLeft();
                                yAxis.setTextSize(10f); // set the text size
                                yAxis.setAxisMinimum(0f); // start at zero
                                yAxis.setCenterAxisLabels(true);
                                yAxis.setDrawAxisLine(true);
                                yAxis.setAxisMaximum(1f); // the axis maximum is 100
                                yAxis.setTextColor(Color.BLACK);

                                YAxis yAxisRight = luzChart.getAxisRight();
                                yAxisRight.setTextSize(10f); // set the text size
                                yAxisRight.setAxisMinimum(0f); // start at zero
                                yAxisRight.setCenterAxisLabels(true);
                                yAxisRight.setAxisMaximum(1f); // the axis maximum is 100
                                yAxisRight.setTextColor(Color.BLACK);



                            }
                        } else {

                            luzChart.setData(null);
                            luzChart.setNoDataText("Sem informações para esta data!");
                            luzChart.invalidate();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static String getHoje(){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String data_hora = df.format(data_atual);
        //Toast.makeText(getContext(), data_hora,Toast.LENGTH_LONG).show();
        String hoje = data_hora.substring(0,10);


        return hoje;
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String mes = "0";
            if(month < 10){
                month = month+1;
                mes = mes + String.valueOf(month);
            } else {
                mes = String.valueOf(month+1);
            }

            String dia = String.valueOf(day);

            if(day < 10){
                dia = '0'+dia;
            }

            final String novadata = dia +"-"+mes+"-"+String.valueOf(year);
            try {

                showProgressDialog();
                new CountDownTimer(2000, 1000) {
                    public void onFinish() {
                        hideProgressDialog();
                    }

                    public void onTick(long millisUntilFinished) {
                        gerarGraficoTemperatura(novadata);
                        gerarGraficoUmidade(novadata);
                        gerarGraficoPresenca(novadata);
                        gerarGraficoChamas(novadata);
                        gerarGraficoLuz(novadata);
                    }
                }.start();


            } catch (Exception ex) {

            }


        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getActivity().getFragmentManager(),"date");
    }

    public static boolean conectado(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()){
            return true;
        } else return false;
    }


    @VisibleForTesting
    public static ProgressDialog mProgressDialog;

    public static void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Carregando...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }




}

