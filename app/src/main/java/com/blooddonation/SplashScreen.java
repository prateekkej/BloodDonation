package com.blooddonation;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth = FirebaseAuth.getInstance();
       getYourPermissions();

        }
        private void getYourPermissions(){
            ActivityCompat
                    .requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("Permission:",permissions[0]+"   "+ grantResults[0]);
        Log.v("Permission:",permissions[1]+"   "+ grantResults[1]);
        Log.v("Permission:",permissions[2]+"   "+ grantResults[2]);

        if(grantResults[2] ==-1)
        {
            Toast.makeText(this, "Storage Permission not granted. \nPlease allow the app to use Storage from Phone Settings.", Toast.LENGTH_SHORT).show();
        }
        if(grantResults[0] ==-1 || grantResults[1]== -1)
        {
            Toast.makeText(this, "Location Permission not granted. \nPlease allow the app to use Location from Phone Settings.", Toast.LENGTH_SHORT).show();
        }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(firebaseAuth.getCurrentUser()==null){
                        startActivity(new Intent(SplashScreen.this,Login.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(SplashScreen.this,Dashboard.class));
                        finish();
                    }
                }
            },1000);

    }
}

