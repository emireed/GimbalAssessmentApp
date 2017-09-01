package com.example.emi.gimbalassessmentapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by emi on 8/31/17.
 */

public class InROWReceiver  extends BroadcastReceiver{

    MainActivity mainActivity;

    public InROWReceiver(Activity activity) {
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtain the passed in values
        Boolean inROW = intent.getBooleanExtra("inROW", false);
        Boolean inOuter = intent.getBooleanExtra("inOuter", true);

        // Check where the user is and change the UX or speed accordingly
        if (inROW) {
            mainActivity.textView.setText(mainActivity.getResources().getString(R.string.outsideROW));
        } else {
            mainActivity.textView.setText(mainActivity.getResources().getString(R.string.outsideROW));
            if (inOuter) {
                mainActivity.speedUpUpdates();
            } else {
                mainActivity.slowDownUpdates();
            }
        }
    }
}
