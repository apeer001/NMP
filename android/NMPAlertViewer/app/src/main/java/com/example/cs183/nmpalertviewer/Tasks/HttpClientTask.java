package com.example.cs183.nmpalertviewer.tasks;

/**
 * Created by Aaron on 5/31/2016.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;
import android.util.Log;

import com.example.cs183.nmpalertviewer.R;
import com.example.cs183.nmpalertviewer.auth.Login;
import com.example.cs183.nmpalertviewer.ui.ErrorFragment;
import com.example.cs183.nmpalertviewer.ui.MainActivity;

public class HttpClientTask extends AsyncTask<Void, Void, Void> {

    Context context;
    private final static String TAG = "HttpClientTask.class";
    public final static String filename = "clientLogs.csv";;
    public final static String NOT_CONNECTED = "Not connected to the internet";
    public final static String CARD_BUNDLE = "cardBundle";
    public final static String PAGEID = "pageId";


    private String dstAddress;
    private String dstPort;
    String response = "";


    public HttpClientTask(Context cxt) {
        dstAddress = "http://52.32.201.77";
        dstPort = "8080";
        context = cxt;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private static String readStream1(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            }
        }
        return sb.toString();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        // Check if the phone is connected to the internet
        if (ni != null) {
            boolean isConnected = ni.isConnected();
            if(isConnected) {
                String FullPath = dstAddress + ":" + dstPort;
                try {
                    //URL url = new URL(dstAddress);
                    URL url = new URL(FullPath);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        urlConnection.setRequestMethod("GET");
                        String userpass = Login.username + ":" + Login.password;
                        String basicAuth = "Basic " + userpass;//+ new String(Base64.encode(userpass.getBytes(), Base64.NO_WRAP ));
                        urlConnection.setRequestProperty("Authorization", basicAuth);
                        urlConnection.connect();

                        int code = urlConnection.getResponseCode();
                        if (code == 200) {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            response = readStream(in);
                            Log.d(TAG, "Read the data from stream");
                        }
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (MalformedURLException m) {
                    Log.d(TAG, "doInBackground: Malformed url Exception");
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: IOException");
                }
            } else {
                response = NOT_CONNECTED;
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        // Write to file
        final File clientLogs = new File(context.getFilesDir(),filename);
        if (!clientLogs.exists()) {
            try {
                clientLogs.createNewFile();
            } catch (IOException i) {
                Log.d(TAG, i.getMessage());
            }
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                CheckForNewErrors(clientLogs);
                if (!response.isEmpty()) {
                    // Empty file
                    try {
                        PrintWriter pw = new PrintWriter(clientLogs);
                        pw.close();

                        //Log.d(TAG, response);
                        // write log data to file
                        pw = new PrintWriter(clientLogs);
                        pw.write(response);
                        pw.close();
                    } catch (FileNotFoundException f) {
                        Log.d(TAG, f.getMessage());
                    }
                }
            }
        });
        t.start();
    }


    public void CheckForNewErrors(File clientLogs) {
        //Read text from file
        ArrayList<ErrorFragment.StringArrayList> Recentlines = new ArrayList<>();
        ArrayList<ErrorFragment.StringArrayList> Recentlines2 = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(clientLogs));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("----------------------")) {
                    //if (!line.isEmpty()) {
                        ErrorFragment.StringArrayList parts = new ErrorFragment.StringArrayList();
                        parts.set(line.split(","));
                        Recentlines.add(parts);
                    //}
                } else {
                    break;
                }
            }
            br.close();
        } catch (FileNotFoundException f) {
            Log.d(context.getClass().getSimpleName(), f.getMessage());
        } catch (IOException i) {
            Log.d(context.getClass().getSimpleName(), i.getMessage());
        }

        for (String line : response.split("\n")) {
            if (!line.contains("----------------------")) {
                //if (!line.isEmpty()) {
                    ErrorFragment.StringArrayList parts = new ErrorFragment.StringArrayList();
                    parts.set(line.split(","));
                    Recentlines2.add(parts);
                //}
            } else {
                break;
            }
        }

        // Sort all lines by id
        Collections.sort(Recentlines);
        Collections.sort(Recentlines2);

        if (!CheckErrorLogData(Recentlines,Recentlines2)) {
            Log.d(TAG, "CheckForNewErrors: new errors arrived");
            createNotification();
        }
    }

    public boolean CheckErrorLogData(ArrayList<ErrorFragment.StringArrayList> l, ArrayList<ErrorFragment.StringArrayList> r) {
        if (l.size() != r.size()) {
            Log.d(TAG, "CheckErrorLogData: not the same size");
            return false;
        }

        for (int i = 0; i < l.size(); i++) {
            ErrorFragment.StringArrayList left = l.get(i);
            ErrorFragment.StringArrayList right = r.get(i);

            String data1 [] = left.getData();
            String data2 [] = right.getData();
            for (int j = 0; j < data1.length; j++) {
                if(!data1[j].toString().trim().equals(data2[j].toString().trim())) {
                    Log.d(TAG, String.valueOf(data1[j].toString().length()) + " " + String.valueOf(data2[j].toString().length()));
                    return false;
                }
            }
        }

        return true;
    }

    public void createNotification() {

        int notificationId = 001;
        int pageId = 1;
        Intent viewIntent  = new Intent(context, MainActivity.class);
        Bundle b = new Bundle();
        b.putInt(PAGEID, pageId);
        viewIntent.putExtra(CARD_BUNDLE, b);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String eventTitle = "NMP Alert";
        String eventText = "There are new errors to view!";
        // Build notification
        long[] vibrationPattern = {100, 50, 75, 50};
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.refresh)
                        .setContentTitle(eventTitle)
                        .setContentText(eventText)
                        .setVibrate(vibrationPattern)
                        .setContentIntent(viewPendingIntent);
                        //.setPriority(NotificationCompat.PRIORITY_HIGH)
                        // .setFullScreenIntent(viewPendingIntent, true)


        // Get an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //Log.d(getClass().getSimpleName(), "Schedule Card Made for wearable Prediction");
        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
        Log.d(TAG, "created card");
    }
}