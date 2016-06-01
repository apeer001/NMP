package com.example.cs183.nmpalertviewer.adapters;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;

import com.example.cs183.nmpalertviewer.ui.ErrorFragment;
import com.example.cs183.nmpalertviewer.ui.MainFragment;

/**
 * Created by Aaron on 5/31/2016.
 */
public class PageAdapter extends FragmentPagerAdapter{

    Fragment fragment;
    public PageAdapter (FragmentManager fm , Context context) {
        super(fm);
        fragment = null;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (fragment != null) {
                    View view = fragment.getView();
                    try {
                        view.setVisibility(View.GONE);
                    } catch (NullPointerException n) {
                        Log.d(getClass().getSimpleName(), "getItem: " + n.getMessage());
                    }
                }
                fragment = new MainFragment();
                break;
            case 1:
                if (fragment != null) {
                    View view = fragment.getView();
                    try {
                        view.setVisibility(View.GONE);
                    } catch (NullPointerException n) {
                        Log.d(getClass().getSimpleName(), "getItem: " + n.getMessage());
                    }
                }
                fragment = new ErrorFragment();
                break;
            default:
                fragment = null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
