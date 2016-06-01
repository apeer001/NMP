package com.example.cs183.nmpalertviewer.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.cs183.nmpalertviewer.tasks.HttpClientTask;

public class ServerPullService extends Service {


    public ServerPullService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClientTask httpClientTask = null;
                if (httpClientTask == null) {
                    httpClientTask = new HttpClientTask(getApplicationContext());
                }

                // Run task to collect new data from server
                if (httpClientTask.getStatus() != AsyncTask.Status.RUNNING) {
                    httpClientTask.execute();
                }
            }
        });
        t.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
