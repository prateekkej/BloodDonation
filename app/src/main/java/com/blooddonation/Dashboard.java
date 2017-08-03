package com.blooddonation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser me;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static User_Class user;
    public Geocoder geocoder;
    public int count=0;
    public TabLayout tabLayout;
    public static String fbloodgroup;
    public static StorageReference storageReference;
    public ViewPager viewPager;
    public Home home;
    public static String currentLocality;
    public Profile profile;
    public static boolean updatemode=false;
    public History history;
    public MyAdapter myAdapter;
    public Menu myMenu;
    private AlertDialog storyDialog;
    public String storyImageURL;
    ByteArrayOutputStream outputStream;
    String myStory,myDonatedTo;
    int d,m,y;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu=menu;
        getMenuInflater().inflate(R.menu.sort,menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId()==R.id.upd){
          if(!updatemode) {
              profile.phone.setVisibility(View.GONE);
              profile.age.setVisibility(View.GONE);
              profile.bg.setVisibility(View.GONE);
              profile.bgEdit.setVisibility(View.VISIBLE);
              profile.ageField.setVisibility(View.VISIBLE);
              profile.updatePhone.setVisibility(View.VISIBLE);
              item.setTitle("Upload new Data");
              profile.updatedPhone.setText(profile.phone.getText());
              profile.newAge.setText(profile.age.getText());
              profile.bgEdit.setSelection(getBloodGroupPosition(profile.bg));
              updatemode = true;
          }else
              {
                  Map<String,Object> haha= new HashMap<>();
                  haha.put("lastdonated",profile.ld.getText().toString());
                  haha.put("mage",profile.newAge.getText().toString());
                  haha.put("mbloodgroup",profile.bgEdit.getSelectedItem().toString());
                  if(profile.updatedPhone.getText().toString().length()>=13){
                  haha.put("mcontact",profile.updatedPhone.getText().toString());
                      updatemode=false;
                      try{
                          geocoder= new Geocoder(this);
                          List<Address> s=geocoder.getFromLocation(MapsFragment.myLatLng.latitude,MapsFragment.myLatLng.longitude,1);
                          currentLocality=s.get(0).getLocality();
                          if(currentLocality!=null){
                              haha.put("mcity",currentLocality);
                          }
                      }catch (IOException e){}
                      final ProgressDialog pd= new ProgressDialog(this);
                      pd.setMessage("Updating Profile ...");
                      pd.setCancelable(false);
                      pd.show();
                      Dashboard.databaseReference.child("users").child(me.getUid()).updateChildren(haha).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              item.setTitle("Update Profile");
                              profile.phone.setVisibility(View.VISIBLE);
                              profile.age.setVisibility(View.VISIBLE);
                              profile.bgEdit.setVisibility(View.GONE);
                              profile.bg.setVisibility(View.VISIBLE);
                              profile.ageField.setVisibility(View.GONE);
                              profile.updatePhone.setVisibility(View.GONE);
                              Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_LONG).show();
                              pd.dismiss();
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(Dashboard.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                              pd.dismiss();
                          }
                      });
              }else{
                  Toast.makeText(getApplicationContext(),"Check Phone Number",Toast.LENGTH_LONG).show();
              }

           }
       }else if(item.getItemId()==R.id.exit){
           finish();
       }else if(item.getItemId()==R.id.sort){
           final AlertDialog alertDialog= new AlertDialog.Builder(this,R.style.AlertDialog).setView(R.layout.sorting).create();
           alertDialog.show();
           Button clear=(Button)alertDialog.findViewById(R.id.clearFilter);
           final Spinner bgFilter=(Spinner)alertDialog.findViewById(R.id.bgFilterSpinner);
           Button bgFilterButton =(Button)alertDialog.findViewById(R.id.filterButton);
           bgFilterButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   fbloodgroup = bgFilter.getSelectedItem().toString();
                   if(fbloodgroup.equals("Blood Group")){
                       home.clearFilter();
                   }
                   else {
                       home.filter();
                   }
                       alertDialog.dismiss();
               }
           });
           clear.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   home.clearFilter();
                   alertDialog.dismiss();

               }
           });

       }else if(item.getItemId()==R.id.about){
           AlertDialog alertDialog= new AlertDialog.Builder(this).setTitle("About Us ").setMessage("BloodBank is an app " +
                   "designed for finding the valuable blood donators as soon as possible ast the earliest by TCS.").create();
                      alertDialog.show();

       }else if(item.getItemId()==R.id.contactus){
           Intent em = new Intent(Intent.ACTION_SENDTO);
           em.setData(Uri.parse("mailto:"));
           em.putExtra(Intent.EXTRA_SUBJECT, "Query []");
           em.putExtra(Intent.EXTRA_EMAIL, "blooddonation.tcs@gmail.com");
           startActivity(em);
        }
        else if(item.getItemId()==R.id.sharemylocation){

                   Intent lo= new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"));
                   lo.putExtra("sms_body","Hey! I am in Emergency. My Location is :" );
                   startActivity(lo);
       }
       else if(item.getItemId()==R.id.addHist)
       { storyDialog= new AlertDialog.Builder(this).setTitle("Add a donation.").setView(R.layout.story).create();
           storyDialog.show();
           ImageView selfi=(ImageView)storyDialog.findViewById(R.id.donationImage);
           final EditText story=(EditText)storyDialog.findViewById(R.id.story);
           final EditText donatedTo=(EditText)storyDialog.findViewById(R.id.donatedto);
           Button saveMyStory=(Button)storyDialog.findViewById(R.id.save);
           selfi.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   PopupMenu popupMenu=new PopupMenu(Dashboard.this,view);
               popupMenu.getMenuInflater().inflate(R.menu.image_pop_up,popupMenu.getMenu());
                   //Third party library for image Cropping
                   CropImage.activity()
                           .setGuidelines(CropImageView.Guidelines.ON)
                           .setAspectRatio(16,9).setOutputCompressQuality(50).start(Dashboard.this);
               }
           });
           saveMyStory.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   myStory=story.getText().toString().trim();
                   myDonatedTo=donatedTo.getText().toString().trim();
                   d= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                   m=Calendar.getInstance().get(Calendar.MONTH);
                   y=Calendar.getInstance().get(Calendar.YEAR);
                   final ProgressDialog p= new ProgressDialog(Dashboard.this);
                   p.setMessage("Uploading Image");
                   p.setCancelable(false);p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                   p.show();
                   final String key=databaseReference.child("users-stories").child(me.getUid()).push().getKey();
                  storageReference.child("users-stories").child(me.getUid()).child(key).putBytes(outputStream.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           if(task.isSuccessful()){
                               p.setMessage("Image upload successful.Uploading story.");
                               Story a= new Story(key,myStory,"",myDonatedTo,d,m+1,y);
                               a.photoURL=task.getResult().getDownloadUrl().toString();
                               databaseReference.child("users-stories").child(me.getUid()).child(key).setValue(a).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       Toast.makeText(Dashboard.this, "Story Saved.", Toast.LENGTH_SHORT).show();
                                       storyDialog.dismiss();
                                       storyDialog=null;
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       storageReference.child("users-stories").child(me.getUid()).child(key).delete();
                                       Toast.makeText(Dashboard.this, "The Story failed to upload . Please try again.", Toast.LENGTH_SHORT).show();
                                   }
                               });
                               p.dismiss();
                           }else{
                               Toast.makeText(Dashboard.this,task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                               p.dismiss();
                           }
                       }
                   });
               }
           });

       }
        return true;
    }

    public void firebaseInit(){
        firebaseAuth= FirebaseAuth.getInstance();
        me= firebaseAuth.getCurrentUser();
        firebaseDatabase=Firebase.getDatabase();
        databaseReference=firebaseDatabase.getReference();
        storageReference= FirebaseStorage.getInstance().getReference("user-images");


}
    int getBloodGroupPosition(TextView b){
        String a=b.getText().toString();
        switch (a){
            case "A+": return 2;

            case "B+":return 3;
            case "AB+":return 7;
            case "O+":return 5;
            case "A-":return 1;
            case "B-":return 4;
            case "AB-":return 8;
            case "O-":return 6;
            default:
                return 0;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseInit();
        databaseReference.child("users").child(me.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User_Class.class);
                if(user!=null) {
                    SmallUserObject obj = new SmallUserObject();
                    obj.copyFromUserObject(user);
                    if (MapsFragment.myLatLng != null) {
                        obj.lat = MapsFragment.myLatLng.latitude;
                        obj.lon = MapsFragment.myLatLng.longitude;
                    }
                    databaseReference.child("users-location").child(me.getUid()).setValue(obj);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(me==null){
            Toast.makeText(this, "How did you even get here?", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Hello",Toast.LENGTH_SHORT).show();
        }
        viewPager = (ViewPager)findViewById(R.id.content);
        myAdapter= new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
updateActionBar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp).setText("Home"),0);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person_black_24dp).setText("Profile"),1);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_history_black_24dp).setText("History"),2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getSupportActionBar().setTitle("Donors around me");

    }

    @Override
    public void onBackPressed() {
        if(count<1){
        Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
        count++;}else{
            super.onBackPressed();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                if(result!=null){
                    Uri imageUri = result.getUri();
                    storyImageURL=imageUri.toString();
                    Glide.with(getApplicationContext()).asBitmap().load(imageUri).into((ImageView) storyDialog.findViewById(R.id.donationImage));
                    Bitmap ab= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    outputStream = new ByteArrayOutputStream();
                    ab.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
                }
            }
        catch (Exception e){

        }}
    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                home = new Home();
                return home;
            }
            else if(position==2) {
                history = new History();
                return history;
            }else if(position==1){
               profile= new Profile();
                return profile;
                }
        return null;
        }
    }
    void updateActionBar(int x)
    {if (x==0){
        myMenu.clear();
        getSupportActionBar().setTitle("Donors around me");
        getMenuInflater().inflate(R.menu.sort,myMenu);
    }else if(x==1){
        myMenu.clear();
        getSupportActionBar().setTitle("Me");
        getMenuInflater().inflate(R.menu.edit,myMenu);

    }
    else if (x==2){getSupportActionBar().setTitle("History");
        myMenu.clear();
        getMenuInflater().inflate(R.menu.exit,myMenu);

    }
    }
}
