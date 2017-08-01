package com.blooddonation;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Prateek on 7/30/2017.
 */

public class SmallUserObject {
    public String name,phone,email,photo,uid,city,bloodgroup,lastDonated;
    public boolean clicked;
    public double lat,lon;
    public Marker marker;
    public SmallUserObject()
    {


    }
    public void copyFromUserObject(User_Class user){
        name=user.mname;
        photo= user.getPhotoUrl();
        phone=user.mcontact;
        email=user.memail;
        uid=user.uid;
        city=user.mcity;
        lastDonated=user.lastdonated;
        bloodgroup=user.mbloodgroup;

    }

    public void copyfromBro(SmallUserObject smallUserObject){
        this.name=smallUserObject.name;
        this.photo=smallUserObject.photo;
        this.email=smallUserObject.email;
        this.uid=smallUserObject.uid;
        this.bloodgroup=smallUserObject.bloodgroup;
        this.city=smallUserObject.city;
        this.phone=smallUserObject.phone;
        this.lastDonated=smallUserObject.lastDonated;
        this.lat=smallUserObject.lat;
        this.lon=smallUserObject.lon;
        this.clicked=false;
    }


}
