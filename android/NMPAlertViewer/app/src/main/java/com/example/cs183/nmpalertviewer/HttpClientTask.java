package com.example.cs183.nmpalertviewer;

/**
 * Created by Daniel on 5/13/2016.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

//PORT: 12359 SERVER IP: 198.199.105.122

public class HttpClientTask extends AsyncTask<Void, Void, Void> {


    private final static String TAG = "HttpClientTask.class";
    public final static String NOT_CONNECTED = "Not connected to the internet";

    private String dstAddress;
    private String dstPort;
    String response = "";
    TextView textResponse;
    Context context;

    HttpClientTask(TextView textResponse, Context cxt) {
        dstAddress = "http://52.32.201.77";
        dstPort = "8080";
        this.textResponse = textResponse;
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
        textResponse.setText(response);
    }
}