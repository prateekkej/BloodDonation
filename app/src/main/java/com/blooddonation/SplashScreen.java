package com.blooddonation;

import android.Manifest;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    static  int PERMISSIONS=1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth = FirebaseAuth.getInstance();//Getting a Firebase Authorizing instance.
        getYourPermissions();// function for getting permissions like storage and location.

        }
        private void getYourPermissions(){
            ActivityCompat
                    .requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS);

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("Permission:",permissions[0]+"   "+ grantResults[0]);
        Log.v("Permission:",permissions[1]+"   "+ grantResults[1]);
        Log.v("Permission:",permissions[2]+"   "+ grantResults[2]);
       //Necessary posts to notify user in case of denied permissions and the collapse in functionality of app.
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
                    /* Firebase stores the currently logged-in user in app's data , until and unless he/she logs out.
                       In this case, we are checking if any user is logged in or not.
                    */
                    if(firebaseAuth.getCurrentUser()==null){
                        startActivity(new Intent(SplashScreen.this,Login.class));//If not logged in
                        finish();
                    }
                    else{
                        startActivity(new Intent(SplashScreen.this,Dashboard.class));
                        finish();
                    }
                }
            },500);

    }
}

