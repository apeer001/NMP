package com.example.cs183.nmpalertviewer.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.cs183.nmpalertviewer.tasks.HttpClientTask;

public class ServerPullService extends Service {

    private HttpClientTask httpClientTask;
    public ServerPullService() {
        httpClientTask = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (httpClientTask == null) {
            httpClientTask = new HttpClientTask(getApplicationContext());
        }

        // Run task to collect new data from server
        httpClientTask.execute();

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
