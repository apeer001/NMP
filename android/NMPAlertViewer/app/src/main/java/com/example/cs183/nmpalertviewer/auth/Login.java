package com.example.cs183.nmpalertviewer.auth;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs183.nmpalertviewer.R;


/**
 * Created by Daniel on 6/2/2016.
 */
public class Login extends FragmentActivity{

    private static final int PERMISSION_ALL = 0;
    EditText passwordEditText;
    EditText userEditText;
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        userEditText = (EditText) findViewById(R.id.userEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = userEditText.getText().toString();
                String password = passwordEditText.getText().toString();
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

                }
            }
        });
    }

}
