package venicius.sensores.principal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import venicius.sensores.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.text.TextUtils.isEmpty;

public class Login extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;


    // [START declare_auth]
    public static FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        // Views

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);


        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        // [END initialize_auth]

        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);


    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(Login.this, "Autenticado com sucesso",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Autenticação Falhou.",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {

                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (isEmpty(email)) {
            mEmailField.setError("Requerido.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (isEmpty(password)) {
            mPasswordField.setError("Requerido.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

            // Obter o novo InstanceID
            String firebaseToken = FirebaseInstanceId.getInstance().getToken();

            //Atualizar na base de dados
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("/Users/"+uid+"/FirebaseToken").setValue(firebaseToken);
            Log.d("TOKEN",firebaseToken);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);



        } else {

        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        if(i==R.id.email_sign_in_button) {

            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());

        } else if (i==R.id.change_password){
            reset();
        }

    }

    public void reset(){

        String email = mEmailField.getText().toString();
        if (isEmpty(email)) {
            mEmailField.setError("Requerido.");
            return;
        } else {
            mEmailField.setError(null);
            showProgressDialog();

            mAuth
                    .sendPasswordResetEmail(mEmailField.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                mEmailField.setText("");
                                Toast.makeText(
                                        Login.this,
                                        "Recuperação de acesso iniciada. Email enviado.",
                                        Toast.LENGTH_LONG
                                ).show();
                            } else {
                                Toast.makeText(
                                        Login.this,
                                        "Falhou! Tente novamente",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    });
            hideProgressDialog();
        }
    }


}