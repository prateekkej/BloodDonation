package com.blooddonation;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Prateek on 7/30/2017.
 */

public class MapsFragment extends Fragment  implements GoogleMap.OnMarkerClickListener,ValueEventListener,GoogleApiClient.ConnectionCallbacks,OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,LocationListener{
public SupportMapFragment map;
    Location myLocation;
    public static LatLng myLatLng;
    GoogleMap readyMap;
    Marker myMarker;
    LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    MarkerOptions me;
    View view;
   public static java.util.concurrent.CopyOnWriteArrayList<SmallUserObject> closePeople;
    public void filter(){
        sortMyMap();
    }

void sortMyMap(){
    for(SmallUserObject i : closePeople){
        if(i.bloodgroup!=null && !i.bloodgroup.equals(Dashboard.fbloodgroup) || i.bloodgroup==null)
        {
            i.marker.setVisible(false);
        }
        if(i.bloodgroup!=null && i.bloodgroup.equals(Dashboard.fbloodgroup) || i.bloodgroup==null) {
            i.marker.setVisible(true);
        }


    }
    DonorsListFragment.adapter.notifyDataSetChanged();

}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient= new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API).addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this).build();
        closePeople=new java.util.concurrent.CopyOnWriteArrayList<SmallUserObject>();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
       try{
           googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style));
           googleMap.setMyLocationEnabled(true);
           readyMap=googleMap;
           googleMap.setOnMarkerClickListener(this);
       }catch (SecurityException e){
           Toast.makeText(getContext(),"Location Not Available",Toast.LENGTH_LONG).show();
       }
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.maps_fragment,container,false);
        map= (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
    myLocation=location;
        myLatLng= new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        Map<String,Object> loc= new HashMap<>();
        loc.put("latLng",myLatLng);
        putMyMarkeronMap(true);

    }
    void putMyMarkeronMap(boolean flag) throws SecurityException{
   if(!flag){
        if (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null) {
            double lon=LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLongitude();
            double lat=LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLatitude();
            myLatLng =new LatLng(lat,lon);
            me=new MarkerOptions().title("Me").position(myLatLng);
            myMarker=readyMap.addMarker(me);
            Map<String,Object> loc= new HashMap<>();
            loc.put("lat",myLatLng.latitude);
            loc.put("lon",myLatLng.longitude);
            Dashboard.databaseReference.child("users-location").child(Dashboard.firebaseAuth.getCurrentUser().getUid()).updateChildren(loc);
            Dashboard.databaseReference.child("users-location")
                    .orderByChild("lat").startAt(myLatLng.latitude-0.1).endAt(myLatLng.latitude +0.1).addValueEventListener(this);
            readyMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,15));
    }else{
            Toast.makeText(getContext(), "Please enable Location from Settings . If enabled , wait to get new Location.", Toast.LENGTH_SHORT).show();}}
   else{
        if(myMarker==null){
            me=new MarkerOptions().title("Me").position(myLatLng);
            myMarker=readyMap.addMarker(me);
            Dashboard.databaseReference.child("users-location")
                    .orderByChild("lat").startAt(myLatLng.latitude-0.1).endAt(myLatLng.latitude +0.1).addValueEventListener(this);
            readyMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,15));
        }else{
            myMarker.setPosition(myLatLng);
            Map<String,Object> loc= new HashMap<>();
            loc.put("lat",myLatLng.latitude);
            loc.put("lon",myLatLng.longitude);
            Dashboard.databaseReference.child("users-location").child(Dashboard.firebaseAuth.
                    getCurrentUser().getUid()).updateChildren(loc);
            Dashboard.databaseReference.child("users-location")
                    .orderByChild("lat").startAt(myLatLng.latitude-0.1).endAt(myLatLng.latitude +0.1).addValueEventListener(this);
        }
   }
}
    private void addMarker(SmallUserObject temp){
        temp.marker=readyMap.addMarker(new MarkerOptions().position(new LatLng(temp.lat,temp.lon)).title("Blood:"+temp.bloodgroup));
    }
    private void updatemarker(SmallUserObject ob){
        ob.marker.setPosition(new LatLng(ob.lat,ob.lon));
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
       try{
           locationRequest= new LocationRequest().setInterval(10).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,MapsFragment.this);
         putMyMarkeronMap(false);
       }catch (SecurityException e){}
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        boolean flag;
        for(DataSnapshot da:dataSnapshot.getChildren())
        {SmallUserObject s= da.getValue(SmallUserObject.class);
            flag=false;
            if(s.uid!=null && s.uid.equals(Dashboard.firebaseAuth.getCurrentUser().getUid())){}
            else{
            for(SmallUserObject i : closePeople){
                if(i.uid!=null && i.uid.equals(s.uid)){
                    i.copyfromBro(s);
                    updatemarker(i);
                    flag=true;
                    break;
                }
            }
            if(!flag){
            SmallUserObject temp= new SmallUserObject();
            temp.copyfromBro(s);
                    addMarker(temp);

            closePeople.add(temp);}

        }
    }}

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
     for(SmallUserObject i: closePeople){
         if(i.marker.getId()!=null && i.marker.getId().equals(marker.getId())){
             i.clicked=true;
             DonorsListFragment.adapter.notifyItemChanged(closePeople.indexOf(i));
             Home.tabLayout.getTabAt(1).select();
     }}
        return true;
    }
}
