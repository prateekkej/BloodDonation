package com.blooddonation;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Prateek on 7/29/2017.
 */

public class User_Class {
    public String memail, mname, mgender, mage, mcontact, mcity, mpincode, mbloodgroup,uid,photo,lastdonated;
    public int registered,ldMon,ldDate,ldYear;
    public User_Class()
    {
        photo="";
        ldMon=0;
        ldDate=0;
        ldYear=0;

    }

    User_Class(String uid,String email,String username,String gender,String age,String phone,String bloodGroup,String city,String pinCode,String ph,int registrar){
        memail= email;
        mname=username;
        mgender=gender;
        mage=age;
        mcontact=phone;
        mbloodgroup=bloodGroup;
        mcity=city;
        mpincode=pinCode;
        this.uid=uid;
        photo=ph;
       registered=registrar;
    }
public String getPhotoUrl(){
    return this.photo;

}

}
