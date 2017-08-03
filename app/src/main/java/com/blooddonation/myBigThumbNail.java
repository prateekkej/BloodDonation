package com.blooddonation;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;

/**
 * Created by Prateek on 8/4/2017.
 */

public class myBigThumbNail extends AlertDialog {
    public myBigThumbNail(Context ct){
        super(ct);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

    }
}
