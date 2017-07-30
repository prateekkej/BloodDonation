package com.blooddonation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static com.blooddonation.Login.isValidEmail;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText username, email, password, confirm, age, contact, city, pincode;
    private Spinner gender,bloodgroup;
    private Button createAccount;
    String memail, musername, mpassword, mconfirm, mgender, mage, mcontact, mcity, mpincode, mbloodgroup;
private FirebaseDatabase firebaseDatabase;
        String MobilePattern = "[0-9]{10}";
    String PinPattern = "[0-9]{6}";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeViews();
        firebaseDatabase= Firebase.getDatabase();
        createAccount.setOnClickListener(new View.OnClickListener() {
          @Override
           public void onClick(View view) {
        sign_Up();
                     }
            });
    }

    public void sign_Up() {
        memail = email.getText().toString();
        mpassword = password.getText().toString();
        musername = username.getText().toString();
        mconfirm = confirm.getText().toString();
        mgender = gender.getSelectedItem().toString();
        mage = age.getText().toString();
        mcontact = contact.getText().toString();
        mcity = city.getText().toString();
        mpincode = pincode.getText().toString();
        mbloodgroup = bloodgroup.getSelectedItem().toString();



        if (memail.isEmpty() || mpassword.isEmpty() || musername.isEmpty() || mconfirm.isEmpty() || mgender.isEmpty() || mage.isEmpty() || mcontact.isEmpty() || mcity.isEmpty() || mpincode.isEmpty()){
            Toast.makeText(getApplicationContext(), "Fill the details completely", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mpassword.equals(mconfirm)){
            password.setError("Passwords don't match");
            confirm.setError("Passwords don't match");
            return;
        }
        if (!isValidEmail(memail))
        {
            email.setError("Please enter valid email address!");
        }

        if (!mcontact.matches(MobilePattern))
        {
            Toast.makeText(getApplicationContext(), "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
        }

        if (!mpincode.matches(PinPattern))
        {
            Toast.makeText(getApplicationContext(), "Enter Valid Pincode", Toast.LENGTH_SHORT).show();
        }
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("Creating User");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        firebaseAuth.createUserWithEmailAndPassword(memail, mpassword).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             Toast.makeText(getApplicationContext(),"Error occurred:"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getApplicationContext(),"User created Successfully.",Toast.LENGTH_LONG).show();
                pd.setMessage("Creating Profile.");
                User_Class user_class= new User_Class(null,memail,musername,mgender,mage,mcontact,mbloodgroup,mcity,mpincode);
                user_class.uid=firebaseAuth.getCurrentUser().getUid();
                firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(user_class).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Profile created Successfully.",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Profile creation failed. You can update details later.",Toast.LENGTH_LONG).show();

                    }
                });
                startActivity(new Intent(SignUp.this,Dashboard.class));
                finish();
            }
        });
    }
    void initializeViews(){
        username = (EditText) findViewById(R.id.input_name);
        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        confirm = (EditText) findViewById(R.id.input_confirmpassword);
        gender = (Spinner) findViewById(R.id.gender);
        age = (EditText) findViewById(R.id.input_Age);
        contact = (EditText) findViewById(R.id.input_phone);
        city = (EditText) findViewById(R.id.input_City);
        pincode = (EditText) findViewById(R.id.input_Pincode);
        bloodgroup = (Spinner) findViewById(R.id.blood);

        createAccount = (Button) findViewById(R.id.Bsignupbutton);
    }
}
