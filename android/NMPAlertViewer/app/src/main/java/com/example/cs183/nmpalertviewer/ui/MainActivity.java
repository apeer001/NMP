package com.example.cs183.nmpalertviewer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cs183.nmpalertviewer.adapters.PageAdapter;
import com.example.cs183.nmpalertviewer.alarms.StartupBroadcaster;
import com.example.cs183.nmpalertviewer.services.ServerPullService;
import com.example.cs183.nmpalertviewer.R;
import com.example.cs183.nmpalertviewer.tasks.HttpClientTask;


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

        // Send broadcast to start alarm
        Intent broadcast = new Intent(StartupBroadcaster.startupStr);
        sendBroadcast(broadcast);

        // Immediately pull data from the server to display
        Intent i = new Intent(getApplicationContext(), ServerPullService.class);
        startService(i);

        FragmentManager fm;
        pageAdapter = new PageAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager = (ViewPager) findViewById(R.id.pager);
        try {
            viewPager.setAdapter(pageAdapter);
        } catch (NullPointerException n) {
            Log.d(getApplication().getClass().getSimpleName(), "onCreate: could not set pager");
        }

        Bundle NotifBundle = getIntent().getExtras();
        if (NotifBundle != null) {
            int pageid = NotifBundle.getInt(HttpClientTask.PAGEID)+1;
            viewPager.setCurrentItem(pageid);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               /*
                if (position == 1) {
                    Log.d(getClass().getSimpleName(), "onPageSelected2: changed error alert fragment");
                    Fragment frg = pageAdapter.getFragment(position);
                    final FragmentTransaction ft = ((PageAdapter)viewPager.getAdapter()).getFragmentManager().beginTransaction();
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                    //viewPager.getAdapter().notifyDataSetChanged();
                }
                */
            }

            @Override
            public void onPageSelected(int position) {
                if (position > 0) {
                    PageAdapter mPagerAdapter = ((PageAdapter) viewPager.getAdapter());
                    MainFragment.YourFragmentInterface fragment = (MainFragment.YourFragmentInterface) mPagerAdapter.instantiateItem(viewPager, position);
                    if (fragment != null) {
                        fragment.fragmentBecameVisible();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



}
