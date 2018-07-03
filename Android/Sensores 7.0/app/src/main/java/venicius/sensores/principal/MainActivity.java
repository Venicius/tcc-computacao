package venicius.sensores.principal;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

import venicius.sensores.R;
import venicius.sensores.fragments.Config;
import venicius.sensores.fragments.Historico;
import venicius.sensores.fragments.Sensores;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {

    TextView email;
    FloatingActionButton fab;
    FloatingActionButton fabmic;
    FloatingActionButton fabData;

    boolean flagVoz = false;

    Sensores fragmentSensores = new Sensores();
    Config fragmentConfig = new Config();
    Historico fragmentHist = new Historico();
    MQTTClient client = new MQTTClient(fragmentSensores);
    private TextView mValor;


    private static final int REQ_CODE_SPEECH_INPUT = 100;

    TextToSpeech engine;
    Locale localeBR = new Locale("pt","br");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences sharedPref = this.getSharedPreferences(
                "venicius.sensores.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);


        flagVoz = sharedPref.getBoolean("voz", false);


        engine = new TextToSpeech(this,  MainActivity.this);

        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.content,fragmentSensores,"fragmentSensores");
        fragmentTransaction2.commit();

        //startVoiceInput();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                try {
                       client.connectToMQTT();


                    if (flagVoz==true) {
                        speech("sensores atualizados");
                    } else {
                         Toast.makeText(getBaseContext(), "Sensores Atualizados",
                         Toast.LENGTH_SHORT).show();
                    }
                    }
                    catch(Exception ex)
                    {
                        Log.e(TAG, ex.getMessage());
                    }

            }
        });

        fabmic = (FloatingActionButton) findViewById(R.id.fabMic);
        fabmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        fabData = (FloatingActionButton) findViewById(R.id.fabData);
        fabData.hide();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        email= (TextView) headerView.findViewById(R.id.emailUser);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sensores) {

            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.content,fragmentSensores,"fragmentSensores");
            fragmentTransaction2.commit();
            fab.show();
            fabmic.show();

        } else if (id == R.id.nav_historico) {
            showProgressDialog();

            setTitle("Histórico");
            FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction3.replace(R.id.content,fragmentHist,"fragmentHist");
            fragmentTransaction3.commit();
            hideProgressDialog();
            fab.hide();
            fabmic.hide();


        } else if (id == R.id.nav_config) {

            setTitle("Configurações");
            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.content,fragmentConfig,"fragmentConfig");
            fragmentTransaction2.commit();
            fab.hide();
            fabmic.hide();

        } else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean conectado(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()){
            return true;
        } else return false;
    }

    private void signOut() {
        final Intent intent = new Intent(this, Login.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja realmente sair?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Login.mAuth.signOut();

                startActivity(intent);
                //updateUI(null);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Autenticando");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Olá, o que posso ajudar?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //mVoiceInputTv.setText(result.get(0));

                    //Toast.makeText(getBaseContext(), result.get(0), Toast.LENGTH_SHORT).show();

                    Log.d("COMANDO DE VOZ:", result.get(0));

                    if (result.get(0).equals("Atualizar sensores") || result.get(0).equals("atualizar sensores") || result.get(0).equals("sensores")){
                        try {
                            client.connectToMQTT();

                            // Toast.makeText(getBaseContext(), "Informações atualizadas",
                            // Toast.LENGTH_SHORT).show();
                            this.speech("Sensores, atualizados.");
                            startVoiceInput();
                        }
                        catch(Exception ex)
                        {
                            Log.e(TAG, ex.getMessage());
                        }


                    }

                    if(result.get(0).equals("abrir histórico") || result.get(0).equals("Abrir histórico")){
                        Toast.makeText(getBaseContext(), result.get(0), Toast.LENGTH_SHORT).show();
                        setTitle("Histórico");
                        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.content,fragmentHist,"fragmentHist");
                        fragmentTransaction3.commit();
                        hideProgressDialog();
                        fab.hide();
                        fabmic.hide();
                        speech("Abrindo histórico.");
                    }

                    if(result.get(0).equals("abrir configurações") || result.get(0).equals("Abrir configurações")){
                        Toast.makeText(getBaseContext(), result.get(0), Toast.LENGTH_SHORT).show();
                        setTitle("Histórico");
                        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.content,fragmentConfig,"fragmentconf");
                        fragmentTransaction3.commit();
                        hideProgressDialog();
                        fab.hide();
                        fabmic.hide();
                        this.speech("Abrindo configurações.");
                    }

                    if(result.get(0).equals("Verificar temperatura") || result.get(0).equals("temperatura") || result.get(0).equals("Mostrar temperatura") ||
                            result.get(0).equals("verificar temperatura") || result.get(0).equals("Temperatura")){
                       mValor = (TextView) findViewById(R.id.valor_sensor_temperatura);
                       String valor = (String) mValor.getText().subSequence(0,3);

                       this.speech("temperatura igual a " + valor + " graus celsius");
                        startVoiceInput();
                    }

                    if(result.get(0).equals("Verificar umidade") || result.get(0).equals("umidade") || result.get(0).equals("Mostrar umidade") ||
                            result.get(0).equals("verificar umidade") || result.get(0).equals("Umidade")){
                        mValor = (TextView) findViewById(R.id.valor_sensor_umidade);
                        String valor = (String) mValor.getText().subSequence(0,3);

                        this.speech("umidade igual a " + valor + " porcento");
                        startVoiceInput();
                    }

                    if(result.get(0).equals("Verificar presença") || result.get(0).equals("presença") || result.get(0).equals("Mostrar presença") ||
                            result.get(0).equals("verificar presença") || result.get(0).equals("Presença")
                            || result.get(0).equals("Verificar movimento") || result.get(0).equals("movimento") || result.get(0).equals("Mostrar movimento") ||
                            result.get(0).equals("verificar movimento") || result.get(0).equals("Movimento")

                            ){
                        mValor = (TextView) findViewById(R.id.valor_sensor_presenca);
                        String valor = (String) mValor.getText();

                        this.speech(valor);
                        startVoiceInput();
                    }

                    if(result.get(0).equals("Verificar chamas") || result.get(0).equals("chamas") || result.get(0).equals("Mostrar chamas") ||
                            result.get(0).equals("verificar chamas") || result.get(0).equals("Chamas")){
                        mValor = (TextView) findViewById(R.id.valor_sensor_chamas);
                        String valor = (String) mValor.getText();

                        this.speech(valor);
                        startVoiceInput();
                    }

                    if(result.get(0).equals("Verificar luz") || result.get(0).equals("luz") || result.get(0).equals("Mostrar luz") ||
                            result.get(0).equals("verificar luz") || result.get(0).equals("Luz")){
                        mValor = (TextView) findViewById(R.id.valor_sensor_luz);
                        String valor = (String) mValor.getText();

                        this.speech(valor);
                        startVoiceInput();
                    }

                }
                break;
            }

        }
    }

    @Override
    public void onStart() {
        if (flagVoz==true) {
            startVoiceInput();
        }
        super.onStart();
    }



        @Override
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status ["+status+"]");

        if (flagVoz==true) {
            if (status == TextToSpeech.SUCCESS) {
                Log.d("Speech", "Success!");
                Log.d("LOCALE", Locale.getDefault().toString());
                engine.setLanguage(Locale.getDefault());

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speech(String mensagemVoz) {
       // engine.setPitch((float) 3);
        engine.setSpeechRate((float) 1.0f);
        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            engine.speak(mensagemVoz,TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            engine.speak(mensagemVoz,TextToSpeech.QUEUE_FLUSH, null);
        }

    }
}
