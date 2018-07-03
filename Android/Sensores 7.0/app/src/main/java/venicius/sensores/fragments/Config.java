package venicius.sensores.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import venicius.sensores.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Config extends Fragment {

    View v;
    Button btnSalvar;
    Switch sTemperatura;
    Switch sUmidade;
    Switch sChamas;
    Switch sPresenca;
    Switch sLuz;
    Switch sVoz;


    public Config() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_config, container, false);


        btnSalvar = (Button) v.findViewById(R.id.btnSalvar);


        sTemperatura = (Switch) v.findViewById(R.id.switch_temperatura);
        sUmidade = (Switch) v.findViewById(R.id.switch_umidade);
        sChamas = (Switch) v.findViewById(R.id.switch_chamas);
        sPresenca = (Switch) v.findViewById(R.id.switch_presenca);
        sLuz = (Switch) v.findViewById(R.id.switch_luz);
        sVoz = (Switch) v.findViewById(R.id.switch_voz);

        //buscaConfig();

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarConfig();
            }
        });




        return v;
    }

    @Override
    public void onStart() {
        buscaConfig();
        super.onStart();
    }

    public void buscaConfig(){

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
                "venicius.sensores.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);
        Boolean voz  = sharedPref.getBoolean("voz", false);

        if (voz){
            sVoz.setChecked(true);
        } else sVoz.setChecked(false);


        if(!this.conectado(this.getContext())) {
            Toast.makeText(this.getContext(), "Sem conexão com a Internet: não é possível verificar suas configurações!",
                    Toast.LENGTH_LONG).show();
        } else {



            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();



            DatabaseReference fTemperatura = FirebaseDatabase.getInstance().getReference().child("/Users/" + uid + "/temperatura");
            DatabaseReference fUmidade = FirebaseDatabase.getInstance().getReference().child("/Users/" + uid + "/umidade");
            DatabaseReference fPresenca = FirebaseDatabase.getInstance().getReference().child("/Users/" + uid + "/presenca");
            DatabaseReference fChamas = FirebaseDatabase.getInstance().getReference().child("/Users/" + uid + "/chamas");
            DatabaseReference fLuz = FirebaseDatabase.getInstance().getReference().child("/Users/" + uid + "/luz");

            showProgressDialog();

            fTemperatura.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String marcador = dataSnapshot.getValue().toString();
                    if (marcador.equals("y")) {
                        sTemperatura.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            fUmidade.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String marcador = dataSnapshot.getValue().toString();
                    if (marcador.equals("y")) {
                        sUmidade.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            fChamas.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String marcador = dataSnapshot.getValue().toString();
                    if (marcador.equals("y")) {
                        sChamas.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            fPresenca.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String marcador = dataSnapshot.getValue().toString();
                    if (marcador.equals("y")) {
                        sPresenca.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            fLuz.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String marcador = dataSnapshot.getValue().toString();
                    if (marcador.equals("y")) {
                        sLuz.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            hideProgressDialog();

        }



    }

    public void salvarConfig(){

        // Obter o novo InstanceID
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        //Atualizar na base de dados
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/FirebaseToken").setValue(firebaseToken);
        Log.d("TOKEN",firebaseToken);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
                "venicius.sensores.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);


        //salvando configurações
        SharedPreferences.Editor editor = sharedPref.edit();

        if (sVoz.isChecked()){
            editor.putBoolean("voz",true);
            editor.apply();
        } else{
            editor.putBoolean("voz",false);
            editor.apply();
        }


        if(sTemperatura.isChecked()){
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/temperatura").setValue("y");
        } else {
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/temperatura").setValue("n");
        }

        if(sChamas.isChecked()){
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/chamas").setValue("y");
        } else {
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/chamas").setValue("n");
        }

        if(sPresenca.isChecked()){
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/presenca").setValue("y");
        } else {
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/presenca").setValue("n");
        }

        if(sUmidade.isChecked()){
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/umidade").setValue("y");
        } else {
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/umidade").setValue("n");
        }

        if(sLuz.isChecked()){
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/luz").setValue("y");
        } else {
            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/luz").setValue("n");
        }



        Toast.makeText(this.getContext(), "Configurações salvas!",
                Toast.LENGTH_LONG).show();

    }


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this.getActivity());
            mProgressDialog.setMessage("Carregando");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static boolean conectado(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()){
            return true;
        } else return false;
    }



}


