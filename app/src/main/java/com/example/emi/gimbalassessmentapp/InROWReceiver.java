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
        Boolean inROW = intent.getBooleanExtra("inROW", false);
        Boolean inOuter = intent.getBooleanExtra("inOuter", true);
        if (inROW) {
            mainActivity.textView.setText("You are at The ROW.");
        } else {
            mainActivity.textView.setText("You will be notified when you arrive at The ROW");
            if (inOuter) {
                mainActivity.speedUpUpdates();
            } else {
                mainActivity.slowDownUpdates();
            }
        }
    }
}
