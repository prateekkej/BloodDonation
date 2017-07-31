package com.blooddonation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {
        private Button logOut;
    public EditText updatedPhone,newAge;
    public  TextView name,phone,location,bg,ld;
    private ImageView userImage;
    public Spinner bgEdit;
    public TextView email,age;
    public  TextInputLayout updatePhone,ageField;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.my_profile,container,false);
        initializeViews(v);
            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dashboard.firebaseAuth.signOut();
                    startActivity(new Intent(getContext(),Login.class));
                    getActivity().finish();
                }
            });
        insertDatatoProfile();
         return v;
    }
    void insertDatatoProfile(){
        Dashboard.databaseReference.child("users").child(Dashboard.firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dashboard.user = dataSnapshot.getValue(User_Class.class);
                if (Dashboard.user != null) {
                    name.setText(Dashboard.user.mname);
                    phone.setText(Dashboard.user.mcontact);
                    location.setText(Dashboard.user.mcity);
                    bg.setText(Dashboard.user.mbloodgroup);
                    age.setText(Dashboard.user.mage);
                    if(Dashboard.user.lastdonated !=null)
                    {
                    ld.setText(Dashboard.user.lastdonated);}
                    email.setText(Dashboard.firebaseAuth.getCurrentUser().getEmail());
                    if(!Dashboard.user.getPhotoUrl().isEmpty())
try{                    Glide.with(getActivity()).asBitmap().load(Dashboard.user.getPhotoUrl()).into(userImage);}catch (NullPointerException n){}

                } else {
                    if (Dashboard.firebaseAuth.getCurrentUser().getPhotoUrl() != null) {
                       try{ Glide.with(getActivity()).asBitmap().load(Dashboard.firebaseAuth.getCurrentUser().getPhotoUrl()).into(userImage);}catch (NullPointerException e){}
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    void initializeViews(View view){
        logOut=(Button)view.findViewById(R.id.logoutbutton);
        updatePhone=(TextInputLayout)view.findViewById(R.id.updatePhoneField);
        updatePhone.setVisibility(View.GONE);
        ageField=(TextInputLayout)view.findViewById(R.id.ageField);
        email=(TextView)view.findViewById(R.id.email);
        updatedPhone=(EditText)view.findViewById(R.id.updatedPhone);
        name=(TextView)view.findViewById(R.id.userName);
        phone=(TextView)view.findViewById(R.id.phone);
        newAge=(EditText)view.findViewById(R.id.newAge);
        bgEdit=(Spinner)view.findViewById(R.id.bloodEdit);
        age=(TextView)view.findViewById(R.id.userAge);
        bg=(TextView)view.findViewById(R.id.bloodGroup);
        ld=(TextView)view.findViewById(R.id.lastdonated);
        ld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Dashboard.updatemode){
                Calendar abc= Calendar.getInstance();
                DatePickerDialog datePickerDialog= new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2){
ld.setText(String.valueOf(i2)+" / "+String.valueOf(i1)+" / "+String.valueOf(i));
                    }
                }, abc.get(Calendar.YEAR), abc.get(Calendar.MONTH), abc.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }else{}
            }
        });

        location=(TextView)view.findViewById(R.id.location);
        userImage=(ImageView)view.findViewById(R.id.donorImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=  new PopupMenu(getActivity(),view);
                popupMenu.getMenuInflater().inflate(R.menu.image_pop_up,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.ch){
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(1,1).setOutputCompressQuality(50)
                                    .start(getContext(),Profile.this);
                        }else if(item.getItemId()==R.id.rem){
                            Map<String,Object> remImage= new HashMap<>();
                            remImage.put("photo","");
                            Dashboard.databaseReference.child("users").child(Dashboard.firebaseAuth.getCurrentUser().getUid()).updateChildren(remImage);
                            Dashboard.storageReference.child(Dashboard.firebaseAuth.getCurrentUser().getUid()+".jpg").delete();
                            if(Dashboard.firebaseAuth.getCurrentUser().getPhotoUrl()==null){
                            userImage.setImageResource(R.drawable.ic_person_black_24dp);}
                            else{
                                Glide.with(getContext()).asBitmap().load(Dashboard.firebaseAuth.getCurrentUser().getPhotoUrl()).into(userImage);

                            }

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                Uri imageUri = result.getUri();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream outputStream= new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,40,outputStream);
                final ProgressDialog progress= new ProgressDialog(getContext());
                progress.setMessage("Uploading Image");
                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();
                UploadTask uploadTask= Dashboard.storageReference.child(Dashboard.firebaseAuth.getCurrentUser().getUid()+ ".jpg").putBytes(outputStream.toByteArray());
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progress.dismiss();
                            Toast.makeText(getContext(),"Photo Uploaded",Toast.LENGTH_SHORT).show();
                            Map<String,Object> abc= new HashMap<>();
                            abc.put("photo",taskSnapshot.getDownloadUrl().toString());
                        Dashboard.databaseReference.child("users").child(Dashboard.firebaseAuth.getCurrentUser().getUid()).updateChildren(abc);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Photo not uploaded.try again",Toast.LENGTH_SHORT).show();
progress.dismiss();
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
