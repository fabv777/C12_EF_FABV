package com.fabv777_c12.c12_ef_fabv;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private AdView adView;
    private AdRequest adRequest;
    private TextView txtName;
    private TextView txtMail;
    private TextView txtUDI;
    private TextView txtInfoPie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //+ Asociación de variables con el front-end.
        txtName = (TextView) findViewById(R.id.txtName);
        txtMail = (TextView) findViewById(R.id.txtMail);
        txtUDI = (TextView) findViewById(R.id.txtUID);
        txtInfoPie = (TextView) findViewById(R.id.txtInfoPie);

        //+ Declaración para el manejo de la publicidad.
        adView = (AdView) findViewById(R.id.ad_view);
        adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //+ Si existe atenticación; es decir, hubo login desde Facebook.
        if (user != null) {
            String name = user.getDisplayName();
            String mail = user.getEmail();
            String uid = user.getUid();
            Uri photoUrl = user.getPhotoUrl();

            txtName.setText(name);
            txtMail.setText(mail);
            txtUDI.setText(uid);


            adView.loadAd(adRequest);

        } else {
            goLoginScreen();
        }
    }

    @Override
    protected void onPause() {
        if(adView != null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(adView != null){
            adView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }

    //+ Método para llamar a la ventana de Login de Facebook.
    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //+ Para cerrar sesión desde el botón.
    public void logout(View view) {
        //Toast.makeText(MainActivity.this, "logout", Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

}
