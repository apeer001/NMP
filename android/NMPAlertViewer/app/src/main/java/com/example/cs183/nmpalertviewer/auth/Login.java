package com.example.cs183.nmpalertviewer.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs183.nmpalertviewer.R;
import com.example.cs183.nmpalertviewer.http.HttpClient;
import com.example.cs183.nmpalertviewer.tasks.SendHttpRequestTask;
import com.example.cs183.nmpalertviewer.ui.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * Created by Daniel on 6/2/2016.
 */
public class Login extends FragmentActivity{

    private static final int PERMISSION_ALL = 0;
    EditText passwordEditText;
    EditText userEditText;
    Button loginButton;
    public static String username;
    public static String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        userEditText = (EditText) findViewById(R.id.userEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        final String dstAddress = "http://52.32.201.77";
        final String dstPort = "8080";


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = userEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (username.isEmpty() && password.isEmpty()){
                    Toast.makeText(Login.this, "Please insert an username & apassword", Toast.LENGTH_SHORT).show();
                }
                else if(username.isEmpty()){
                    Toast.makeText(Login.this, "Please insert a username", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty()){
                    Toast.makeText(Login.this, "Please insert a password", Toast.LENGTH_SHORT).show();
                }
                else {
                    attemptLogin(username,password);
                }
            }
        });
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

    private void attemptLogin(String uname, String pass) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        String response;
        // Check if the phone is connected to the internet
        if (ni != null) {
            boolean isConnected = ni.isConnected();
            if(isConnected) {
                String destAddr = getResources().getString(R.string.ServerAddr);
                String destPort = getResources().getString(R.string.ServerPort);
                String FullPath = destAddr + ":" + destPort;

                try {
                    SendHttpRequestTask sendHttpRequestTask = new SendHttpRequestTask(getApplicationContext());
                    sendHttpRequestTask.execute(FullPath, username, password);
                    String rsp = sendHttpRequestTask.get();
                    Log.d(getClass().getSimpleName(), "attemptLogin: " + rsp);

                    try {
                        if(rsp != null && rsp.contains("\r\n")) {
                            String lines[] = rsp.split("\r\n");
                            String arr[] = lines[0].split(":");
                            if (arr.length == 2) {
                                String str = new String(Base64.decode(arr[1], 0), "UTF-8");
                                if (str.equals(password)) {
                                    Intent i = new Intent(this, MainActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Incorrect Login information", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Incorrect Login information", Toast.LENGTH_SHORT).show();
                        }
                    } catch (UnsupportedEncodingException u) {
                        Log.d(getClass().getSimpleName(), u.getMessage());
                    }

                } catch (InterruptedException i) {
                    Log.d(getClass().getSimpleName(), i.getMessage());
                } catch (ExecutionException e) {
                    Log.d(getClass().getSimpleName(), e.getMessage());
                }

            } else {
                response = "NOT_CONNECTED";
            }
        }

        //log me in!!!send to server
        //Intent viewIntent  = new Intent(getApplicationContext(), MainActivity.class);
        //startActivity(viewIntent);
    }
}
