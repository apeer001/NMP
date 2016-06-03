package com.example.cs183.nmpalertviewer.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.cs183.nmpalertviewer.http.HttpClient;

/**
 * Created by Aaron on 6/3/2016.
 */
public class SendHttpRequestTask extends AsyncTask<String, Void, String> {

    private Context context;

    public SendHttpRequestTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String username = params[1];
        String password = params[2];
        String data = null;
        try {
            HttpClient client = new HttpClient(url, context);
            client.connectForMultipart();
            client.addFormPart("param1", username);
            client.addFormPart("param2", password);
            //client.addFilePart("file", "logo.png", baos.toByteArray());
            client.finishMultipart();
            data = client.getResponse();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String data) {

    }

}
