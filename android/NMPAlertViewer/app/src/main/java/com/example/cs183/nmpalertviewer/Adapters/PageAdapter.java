package com.example.cs183.nmpalertviewer.adapters;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.cs183.nmpalertviewer.ui.ErrorFragment;
import com.example.cs183.nmpalertviewer.ui.MainFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aaron on 5/31/2016.
 */
public class PageAdapter extends FragmentPagerAdapter{

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    Fragment fragment;
    Context context;


    public PageAdapter (FragmentManager fm , Context cxt) {
        super(fm);
        mFragmentManager = fm;
        fragment = null;
        mFragmentTags = new HashMap<Integer, String>();
        context = cxt;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }

    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                fragment = Fragment.instantiate(context, MainFragment.class.getName(), null);
                break;
            case 1:
                fragment = Fragment.instantiate(context, ErrorFragment.class.getName(), null);
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
