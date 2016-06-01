package com.example.cs183.nmpalertviewer.tasks;

/**
 * Created by Aaron on 5/31/2016.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class HttpClientTask extends AsyncTask<Void, Void, Void> {

    Context context;
    private final static String TAG = "HttpClientTask.class";
    private String filename = "clientLogs.csv";;
    public final static String NOT_CONNECTED = "Not connected to the internet";


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
        File clientLogs = new File(context.getFilesDir(),filename);
        if (!clientLogs.exists()) {
            try {
                clientLogs.createNewFile();
            } catch (IOException i) {
                Log.d(TAG, i.getMessage());
            }
        }

        try {
            // Empty file
            PrintWriter pw = new PrintWriter(clientLogs);
            pw.close();

            Log.d(TAG, response);
            // write log data to file
            pw = new PrintWriter(clientLogs);
            pw.write(response);
            pw.close();

        } catch (FileNotFoundException f) {
            Log.d(TAG, f.getMessage());
        }
    }
}