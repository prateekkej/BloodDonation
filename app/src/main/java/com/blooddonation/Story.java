package com.blooddonation;

/**
 * Created by Prateek on 8/4/2017.
 */

public class Story {
    public String photoURL,story,to,key;
    public int d,m,y;
    public Story(){

    }
    public Story(String a,String b, String c,String g,int d,int e,int f){
        photoURL=c;
        story=b;
        key=a;
        to=g;
        this.d=d;
        this.m=e;
        this.y=f;
           }
}
