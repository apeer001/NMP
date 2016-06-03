package com.example.cs183.nmpalertviewer.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.cs183.nmpalertviewer.R;
import com.example.cs183.nmpalertviewer.adapters.ExpandableListAdapter;
import com.example.cs183.nmpalertviewer.services.ServerPullService;
import com.example.cs183.nmpalertviewer.tasks.HttpClientTask;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RunnableFuture;

/**
 * Created by Aaron on 5/31/2016.
 */
public class MainFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Context context;
    private BottomBar mBottomBar = null;

    Thread listThread = new Thread(new PrepareRunnable());
    boolean threadAlive = false;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page,container,false);

        context = getActivity().getApplicationContext();

        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        // preparing list data
        prepareListData();
        //listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        // setting list adapter
        //expListView.setAdapter(listAdapter);
        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /*Toast.makeText(
                        context,
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                        */
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                /*
                Toast.makeText(context,
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
                        */
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                /*
                Toast.makeText(context.getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
                        */

            }
        });

        if (mBottomBar == null) {
            mBottomBar = BottomBar.attach(getActivity(), savedInstanceState);
            mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
                @Override
                public void onMenuTabSelected(@IdRes int menuItemId) {
                    if (menuItemId == R.id.bottomBarItemOne) {
                        // The user selected item number one.
                        Log.d(getClass().getSimpleName(), "onMenuTabSelected: menu tab selected");
                        Intent i = new Intent(context, ServerPullService.class);
                        context.startService(i);
                        prepareListData();
                    }
                }

                @Override
                public void onMenuTabReSelected(@IdRes int menuItemId) {
                    if (menuItemId == R.id.bottomBarItemOne) {

                        // The user reselected item number one, scroll your content to top.
                        Log.d(getClass().getSimpleName(), "onMenuTabReSelected: menu tab selected");
                        Intent i = new Intent(context, ServerPullService.class);
                        context.startService(i);
                        prepareListData();
                        //listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
                        // setting list adapter
                        //expListView.setAdapter(listAdapter);
                    }
                }
            });
        }



        return view;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        try {
            if (!threadAlive) {
                threadAlive = true;
                listThread.setPriority(Thread.MAX_PRIORITY);
                listThread.run();
            }
        } catch (IllegalThreadStateException i) {
            Log.d(getClass().getSimpleName(), "prepareListData: thread started and it tried to run again");
        }
    }

    private class PrepareRunnable implements Runnable {

        @Override
        public void run() {
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();

            File clientLog = new File(context.getFilesDir(), HttpClientTask.filename);
            //Read text from file
            ArrayList<StringArrayList> Recentlines = new ArrayList<>();
            ArrayList<StringArrayList> Alllines = new ArrayList<>();
            if (clientLog.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(clientLog));
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.contains("----------------------")) {
                            String list [] = line.split(",");
                            if (list != null && list.length >= 8) {
                                StringArrayList parts = new StringArrayList();
                                parts.set(list);
                                Recentlines.add(parts);
                            }
                        } else {
                            break;
                        }
                    }

                    while ((line = br.readLine()) != null) {
                        StringArrayList parts = new StringArrayList();
                        parts.set(line.split(","));
                        Alllines.add(parts);
                    }
                    br.close();
                } catch (FileNotFoundException f) {
                    Log.d(getActivity().getApplication().getClass().getSimpleName(), f.getMessage());
                } catch (IOException i) {
                    Log.d(getActivity().getApplication().getClass().getSimpleName(), i.getMessage());
                }


                // Sort all lines by id
                Collections.sort(Alllines);

                List <List<String>> expandedData = new ArrayList<>();
                String currentid = "-1";
                for(StringArrayList arr : Alllines) {
                    String data [] = arr.getData();
                    if (data != null && data.length >= 8) {
                        currentid = data[1];
                        String header = "Client " + currentid + " " + data[3];

                        listDataHeader.add(header);

                        // Adding child data
                        List<String> expList = new ArrayList<String>();
                        expList.add("Admin: " + data[0]);
                        expList.add("comp id: " + data[1]);
                        expList.add("Time: " + data[3]);
                        expList.add("Status: " + data[4]);
                        expList.add("CPU Load: " + data[5]);
                        expList.add("Temp: " + data[6]);
                        expList.add("Net Load: " + data[7]);
                        expList.add("Desc: " + data[8]);

                        expandedData.add(expList);
                    }
                }

                for (int i = 0; i < listDataHeader.size(); i++) {
                    listDataChild.put(listDataHeader.get(i), expandedData.get(i)); // Header, Child data
                }
            }
            threadAlive = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
                    listAdapter.notifyDataSetChanged();
                    // setting list adapter
                    expListView.setAdapter(listAdapter);
                    //expListView.notifyAll();
                    Log.d(getClass().getSimpleName(), "run: Thread finished the view should now be updated");
                }
            });
        }
    }

    public static class StringArrayList implements Comparable<StringArrayList> {

        private String array [];
        private Integer id;

        public Integer getid() {
            return id;
        }
        public String [] getData() {
            return array;
        }

        public void set(String [] p) {
            this.id = Integer.parseInt(p[1]);
            array = p;
        }

        @Override
        public int compareTo(StringArrayList o) {
            return getid().compareTo(o.getid());
        }
    }

    public interface YourFragmentInterface {
        void fragmentBecameVisible();
    }
}
