package com.example.cs183.nmpalertviewer.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs183.nmpalertviewer.R;
import com.example.cs183.nmpalertviewer.ui.MainActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;



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
                    //log me in!!!send to server
                    Intent viewIntent  = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(viewIntent);
                    return;
                    /*ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                                        Log.d("Login", "Read the data from stream");
                                    }
                                } finally {
                                    urlConnection.disconnect();
                                }
                            } catch (MalformedURLException m) {
                                Log.d("Login", "doInBackground: Malformed url Exception");
                            } catch (IOException e) {
                                Log.d("Login", "doInBackground: IOException");
                            }
                        } else {
                            response = "NOT_CONNECTED";
                        }
                    }*/
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

}
