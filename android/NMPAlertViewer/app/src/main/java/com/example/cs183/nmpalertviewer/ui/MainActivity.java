package com.example.cs183.nmpalertviewer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cs183.nmpalertviewer.adapters.PageAdapter;
import com.example.cs183.nmpalertviewer.alarms.StartupBroadcaster;
import com.example.cs183.nmpalertviewer.services.ServerPullService;
import com.example.cs183.nmpalertviewer.R;


public class MainActivity extends AppCompatActivity {


    PageAdapter pageAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        try {
            bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        } catch (NullPointerException n) {
            Log.d(getApplication().getClass().getSimpleName(), "onCreate: could not set color");
        }

        // Immediately pull data from the server to display
        Intent i = new Intent(getApplicationContext(), ServerPullService.class);
        i.setAction(StartupBroadcaster.startupStr);
        sendBroadcast(i);
        startService(i);

        FragmentManager fm;
        pageAdapter = new PageAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager = (ViewPager) findViewById(R.id.pager);
        try {
            viewPager.setAdapter(pageAdapter);
        } catch (NullPointerException n) {
            Log.d(getApplication().getClass().getSimpleName(), "onCreate: could not set pager");
        }
    }



}
