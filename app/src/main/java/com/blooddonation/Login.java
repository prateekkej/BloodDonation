package com.blooddonation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private GoogleApiClient googleApiClient;
    private SignInButton googleButton;
    private FirebaseAuth firebaseAuth;
    private Button signin,signup,forgot;
    private TextInputLayout passwordb;
    private EditText password,emailid;
    private DatabaseReference fire;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){

            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account= result.getSignInAccount();
                informFirebaseforGoogle(account);

            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initializeView();
        firebaseAuth=FirebaseAuth.getInstance();
        fire=Firebase.getDatabase().getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.myClientID)).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(emailid.getText().toString().isEmpty()){
                   Toast.makeText(getApplicationContext(),"Please enter an email id",Toast.LENGTH_LONG).show();
               }else if(isValidEmail(emailid.getText().toString())){

                   passwordb.setVisibility(View.VISIBLE);
               }
               else{
                   Toast.makeText(Login.this, "Please check your E-Mail id .", Toast.LENGTH_SHORT).show();
               }
               if(!emailid.getText().toString().isEmpty() && !password.getText().toString().isEmpty() )
               { signMeInWithEmailId(emailid.getText().toString(),password.getText().toString());
            }
        }});
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
startActivity(new Intent(Login.this,SignUp.class));
            }
        });
    }
    private void initializeView(){
        signin=(Button)findViewById(R.id.signinbutton);
        password=(EditText)findViewById(R.id.password);
        forgot=(Button)findViewById(R.id.forgot);
        passwordb=(TextInputLayout)findViewById(R.id.passwordb);
        passwordb.setVisibility(View.GONE);
        emailid=(EditText)findViewById(R.id.emailAddress);
        googleButton=(SignInButton)findViewById(R.id.googleButton);
        googleButton.setSize(SignInButton.SIZE_WIDE);
        googleButton.setColorScheme(SignInButton.COLOR_AUTO);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent,2);
            }
        });
        signup=(Button)findViewById(R.id.signup);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidEmail(emailid.getText().toString())){
                firebaseAuth.sendPasswordResetEmail(emailid.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Please check your mail for Password reset Link.",Toast.LENGTH_LONG).show();
                    }
                });}
            }
        });
    }
    public static boolean isValidEmail(String email) {

        String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +"\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +  "\\." +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
        String email1 = email;
        Matcher matcher = Pattern.compile(validemail).matcher(email);
        if (matcher.matches()) {return true;} else { return false;        }
    }
    private void signMeInWithEmailId(String email,String password){
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Logging in.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getApplicationContext(),"Signed in Successfully.",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this,Dashboard.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
            }
        });
    }
    private void informFirebaseforGoogle(GoogleSignInAccount account){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in");
        progressDialog.show();
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                fire.child("users").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(User_Class.class) == null) {
                            fire.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(new User_Class(
                                    firebaseAuth.getCurrentUser().getUid(),
                                    firebaseAuth.getCurrentUser().getEmail(),firebaseAuth.getCurrentUser().getDisplayName(),
                                    "", "", "", "", "", "",firebaseAuth.getCurrentUser().getPhotoUrl().toString(), 1));
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Signed in Successfully.",Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Signed in Successfully.",Toast.LENGTH_SHORT).show();

                        }
                        startActivity(new Intent(Login.this,Dashboard.class));
                        finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                                    }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
