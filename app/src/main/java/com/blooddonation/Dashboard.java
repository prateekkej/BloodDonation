package com.blooddonation;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser me;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static User_Class user;
    TabLayout tabLayout;
    public static StorageReference storageReference;
    ViewPager viewPager;
    Home home;
    Profile profile;
   public static boolean updatemode=false;
    Notifications notifications;
    MyAdapter myAdapter;
    Menu myMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu= menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
       if(item.getItemId()==R.id.upd){
          if(!updatemode) {
              profile.phone.setVisibility(View.GONE);
              profile.updatePhone.setVisibility(View.VISIBLE);
              item.setTitle("Upload new Data");
              profile.updatedPhone.setText(profile.phone.getText());
              updatemode = true;
          }else
              {
                  Map<String,Object> haha= new HashMap<>();
                  haha.put("lastdonated",profile.ld.getText().toString());
                  if(profile.updatedPhone.getText().toString().length()>=13){
                  haha.put("mcontact",profile.updatedPhone.getText().toString());
                      updatemode=false;
                      Dashboard.databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(haha).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              item.setTitle("Update Profile");
                              profile.phone.setVisibility(View.VISIBLE);
                              profile.updatePhone.setVisibility(View.GONE);
                              Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_LONG).show();
                          }
                      });
              }else{
                  Toast.makeText(getApplicationContext(),"Check Phone Number",Toast.LENGTH_LONG).show();
              }

           }
       }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth= FirebaseAuth.getInstance();
        me= firebaseAuth.getCurrentUser();
        firebaseDatabase=Firebase.getDatabase();
        databaseReference=firebaseDatabase.getReference();
        storageReference= FirebaseStorage.getInstance().getReference("user-images");
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User_Class.class);
                SmallUserObject obj=new SmallUserObject();
                obj.copyFromUserObject(user);
                if(MapsFragment.myLatLng!=null){obj.lat=MapsFragment.myLatLng.latitude;
                obj.lon=MapsFragment.myLatLng.longitude;}
                Map<String,Object> up= new HashMap<>();
                up.put("phone",user.mcontact);
                up.put("lastDonated",user.lastdonated);
                up.put("photo",user.getPhotoUrl());
                databaseReference.child("users-location").child(firebaseAuth.getCurrentUser().getUid()).setValue(obj);
                databaseReference.child("users-location").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(up);

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
    }else if(x==1){
        getSupportActionBar().setTitle("Me");
        getMenuInflater().inflate(R.menu.edit,myMenu);

    }
    else if (x==2){getSupportActionBar().setTitle("Requests");
        myMenu.clear();
    }
    }
}
