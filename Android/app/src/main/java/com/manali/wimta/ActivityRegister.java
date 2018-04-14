package com.manali.wimta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class ActivityRegister extends AppCompatActivity implements View.OnClickListener{
    //Create variables for getting the data entered

    private FirebaseAuth mAuth;
    private EditText mEmail;
    private EditText mPassword;
    private Button  mVerify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set Views
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mVerify = findViewById(R.id.verify);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.verify).setOnClickListener(this);


    }
    private void registerUser(){
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (email.isEmpty()) {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter a valid email");
            mEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPassword.setError("Minimum length of password should be 6");
            mPassword.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                      Toast.makeText(getApplicationContext(), "Registering!", Toast.LENGTH_SHORT).show();
//                      startActivity(new Intent(ActivityRegister.this, ActivityCreateBasicProfile.class));
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });



}
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verify:
                registerUser();
                break;

        }
    }
}