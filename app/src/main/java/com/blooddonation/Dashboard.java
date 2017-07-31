package com.blooddonation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser me;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static User_Class user;
    Geocoder geocoder;
    int count=0;
    TabLayout tabLayout;
    public static String fbloodgroup;
    public static StorageReference storageReference;
    ViewPager viewPager;
    Home home;
    public static String currentLocality;
    Profile profile;
   public static boolean updatemode=false;
    Notifications notifications;
    MyAdapter myAdapter;
    Menu myMenu;

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
                              Map<String,Object> ob= new HashMap<>();
                              ob.put("mcity",currentLocality);
                              Dashboard.databaseReference.child("users").child(Dashboard.firebaseAuth.getCurrentUser().getUid()).updateChildren(ob);
                          }

                      }catch (IOException e){}
                      final ProgressDialog pd= new ProgressDialog(this);
                      pd.setMessage("Updating Profile ...");
                      pd.setCancelable(false);
                      pd.show();
                      Dashboard.databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(haha).addOnSuccessListener(new OnSuccessListener<Void>() {
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
           final Spinner bgFilter=(Spinner)alertDialog.findViewById(R.id.bgFilterSpinner);
           Button bgFilterButton =(Button)alertDialog.findViewById(R.id.filterButton);
           bgFilterButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   fbloodgroup = bgFilter.getSelectedItem().toString();
                   home.filter();
                   alertDialog.dismiss();
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
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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
                    Map<String, Object> up = new HashMap<>();
                    up.put("phone", user.mcontact);
                    up.put("bloodgroup",user.mbloodgroup);
                    up.put("lastDonated", user.lastdonated);
                    up.put("photo", user.getPhotoUrl().toString());
                    databaseReference.child("users-location").child(firebaseAuth.getCurrentUser().getUid()).setValue(obj);
                    databaseReference.child("users-location").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(up);
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
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_notifications_black_24dp).setText("Notifications"),2);
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
                notifications = new Notifications();
                return notifications;
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
    else if (x==2){getSupportActionBar().setTitle("Requests");
        myMenu.clear();
    }
    }
}
