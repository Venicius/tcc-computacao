package venicius.sensores.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import venicius.sensores.R;
import venicius.sensores.principal.MQTTClient;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */

public class Sensores extends Fragment{


    private TextView mValor;
    private TextView mUpdate;
    View v;

    MQTTClient client = new MQTTClient(this);


    public Sensores() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sensores, container, false);

        updateView("sensoresFCT2017/temperatura","","");
        updateView("sensoresFCT2017/umidade","","");
        updateView("sensoresFCT2017/presenca","","");
        updateView("sensoresFCT2017/chamas","","");
        updateView("sensoresFCT2017/luz","","");


        return v;
    }


    public void updateView(final String topico, String sensorMessage, String data) {

        String mData = "data" + topico;


        try {
            SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
                    "com.fct.venicius.sensores.PREFERENCE_FILE_KEY",
                    Context.MODE_PRIVATE);

            String notif2 = sharedPref.getString("notTemperatura", "n");

            //pegando ultima informação
            if (sensorMessage == null || sensorMessage == "") {
                sensorMessage = sharedPref.getString(topico,"-");
                data = sharedPref.getString(mData,"-");
            }

            final String tempSensorMessage = sensorMessage;
            final String tempData = data;
            final String tempNotif = notif2;


            this.getActivity().runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    // TODO Auto-generated method stub


                    if (topico.equals("sensoresFCT2017/temperatura")) {
                        mValor = (TextView) v.findViewById(R.id.valor_sensor_temperatura);
                        mUpdate = (TextView) v.findViewById(R.id.update_sensor_temperatura);
                        mValor.setText(tempSensorMessage + "ºC");
                        mUpdate.setText(tempData);

                        String tempStr = tempSensorMessage;
                        Double temp = Double.parseDouble(tempStr);

                      /*  if(temp>25.00 && tempNotif.equals("y")) {
                            notificar("Temperatura", tempSensorMessage);
                        }*/

                    } else if (topico.equals("sensoresFCT2017/umidade")) {
                        mValor = (TextView) v.findViewById(R.id.valor_sensor_umidade);
                        mUpdate = (TextView) v.findViewById(R.id.update_sensor_umidade);
                        mValor.setText(tempSensorMessage + "%");
                        mUpdate.setText(tempData);
                        /*
                        if(Integer.parseInt(tempSensorMessage)>80) {
                            notificar("Umidade", tempSensorMessage);
                        }*/

                    } else if (topico.equals("sensoresFCT2017/chamas")) {
                        mValor = (TextView) v.findViewById(R.id.valor_sensor_chamas);
                        mUpdate = (TextView) v.findViewById(R.id.update_sensor_chamas);
                        mValor.setText(tempSensorMessage);
                        mUpdate.setText(tempData);

                        // notificar("Chamas",tempSensorMessage);

                    } else if (topico.equals("sensoresFCT2017/presenca")) {
                        mValor = (TextView) v.findViewById(R.id.valor_sensor_presenca);
                        mUpdate = (TextView) v.findViewById(R.id.update_sensor_presenca);
                        mValor.setText(tempSensorMessage);
                        mUpdate.setText(tempData);
                        // notificar("Presença",tempSensorMessage);

                    } else if (topico.equals("sensoresFCT2017/luz")) {
                        mValor = (TextView) v.findViewById(R.id.valor_sensor_luz);
                        mUpdate = (TextView) v.findViewById(R.id.update_sensor_luz);
                        mValor.setText(tempSensorMessage);
                        mUpdate.setText(tempData);
                        // notificar("Luz",tempSensorMessage);
                    }

                }
            });

            //salvando ultima informação
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(topico, sensorMessage);
            editor.putString(mData, tempData);
            editor.commit();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

}



