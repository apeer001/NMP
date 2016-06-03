package com.example.cs183.nmpalertviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.roughike.bottombar.BottomBarTab;
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

/**
 * Created by Aaron on 5/31/2016.
 */
public class ErrorFragment extends Fragment implements MainFragment.YourFragmentInterface {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page2,container,false);

        context = getActivity().getApplicationContext();
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp2);
        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /*
                Toast.makeText(
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

        return view;
    }

    @Override
    public void onResume() {
        Log.d(getClass().getSimpleName(), "onResume: should update list");
        prepareListData();
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        listAdapter.notifyDataSetChanged();
        // setting list adapter
        expListView.setAdapter(listAdapter);
        super.onResume();
    }


    @Override
    public void fragmentBecameVisible() {
        Log.d(getClass().getSimpleName(), "fragment became visible");
        prepareListData();
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        listAdapter.notifyDataSetChanged();
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }


    /*
             * Preparing the list data
             */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        try {
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
                            if (line.length() > 0) {
                                StringArrayList parts = new StringArrayList();
                                parts.set(line.split(","));
                                Recentlines.add(parts);
                            }
                        } else {
                            break;
                        }
                    }

                    while ((line = br.readLine()) != null) {
                        if (line.length() > 0) {
                            StringArrayList parts = new StringArrayList();
                            parts.set(line.split(","));
                            Alllines.add(parts);
                        }
                    }
                    br.close();
                } catch (FileNotFoundException f) {
                    Log.d(getActivity().getApplication().getClass().getSimpleName(), f.getMessage());
                } catch (IOException i) {
                    Log.d(getActivity().getApplication().getClass().getSimpleName(), i.getMessage());
                }


                // Sort all lines by id
                Collections.sort(Recentlines);

                List<List<String>> expandedData = new ArrayList<>();
                String currentid = "-1";
                for (StringArrayList arr : Recentlines) {
                    String data[] = arr.getData();
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

                for (int i = 0; i < listDataHeader.size(); i++) {
                    listDataChild.put(listDataHeader.get(i), expandedData.get(i)); // Header, Child data
                }
            }
        } catch (NullPointerException n) {
            Log.d(getClass().getSimpleName(), "prepareListData: " + n.getMessage());
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
}
