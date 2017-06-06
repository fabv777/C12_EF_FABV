package com.fabv777_c12.c12_ef_fabv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    //+ Declaración de las variables
    private LoginButton login_button;
    private CallbackManager callbackManager;
    private TextView txtInfo;
    private FirebaseAuth firebaseauth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //+ Se inicializa los controles para contar con la integración con Fcebook.
        iniControls();

        //+ Se obtiene la integración con Facebook.
        loginWithFB();

        //+ Se obtiene la integración con Firebase.
        firebaseauth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //+ Si no hay autenticación se llama a la ventana principal
                if (user != null) {
                    goMainScreen();
                }
            }
        };
    }

    //+ Se inicializan los controles a utilizar para la integración con Facebook.
    private void iniControls () {

        //+ Se declara los métodos principales de la integración con FB.
        callbackManager = CallbackManager.Factory.create();
        login_button = (LoginButton) findViewById(R.id.login_Button);

        //+ Se solicita permiso para el email en Facebook.
        login_button.setReadPermissions(Arrays.asList("email"));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtInfo = (TextView) findViewById(R.id.txtInfoDet);
    }

    //+ Se obtiene la integración con Facebook: Login.
    private void loginWithFB () {
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                txtInfo.setText("¡Inicio de sesión exitoso!: " + loginResult.getAccessToken().toString());
                //+ Se obtiene el Token de Facebook para pasárselo a Firebase.
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            //+ Si se cancela el login de Facebook.
            @Override
            public void onCancel() {
                txtInfo.setText("¡Inicio de sesión cancelado!");
            }

            //+ Si existe algún error en el login de Facebook.
            @Override
            public void onError(FacebookException exception) {
                txtInfo.setText("¡Inicio de sesión NO exitoso!: " + exception.getMessage().toString());
            }
        });

    }

    //+ Manejo de las credenciales pasadas al Firebase.
    private void handleFacebookAccessToken(AccessToken accessToken) {

        progressBar.setVisibility(View.VISIBLE);
        login_button.setVisibility(View.GONE);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseauth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
                login_button.setVisibility(View.VISIBLE);
            }
        });
    }

    //+ Método para ir a a ventana principal.
    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //+ Método para pasar el resultado de la actividad.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //+ Método para activar el Listener del Firebase.
    @Override
    protected void onStart() {
        super.onStart();
        firebaseauth.addAuthStateListener(firebaseAuthListener);
    }

    //+ Método para des-activar el Listener del Firebase.
    @Override
    protected void onStop() {
        super.onStop();
        firebaseauth.removeAuthStateListener(firebaseAuthListener);
    }
}
