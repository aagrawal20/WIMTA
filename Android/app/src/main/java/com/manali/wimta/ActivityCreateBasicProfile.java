package com.manali.wimta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityCreateBasicProfile extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    int status = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_basic_profile);

        final TextInputLayout fullName = findViewById(R.id.fullName);
        final TextInputLayout username = findViewById(R.id.username);
        final Button nextButton = findViewById(R.id.nextButton);
        final Switch toggle = (Switch) findViewById(R.id.switch3);


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    status = 0;
                } else {
                    // The toggle is disabled
                    status = -1;
                }
            }
        });

        //push values to database on click on button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullNameText = Objects.requireNonNull(fullName.getEditText()).getText().toString().trim();
                final String userNameText = Objects.requireNonNull(username.getEditText()).getText().toString().trim();

                if(fullNameText.isEmpty()) { //check if full name is empty
                    fullName.setError("Name cannot be empty");
                    fullName.requestFocus();
                }
                else if(userNameText.isEmpty()) { // check if username is empty
                    username.setError("Username cannot be empty");
                }
                else {
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert currentUser != null;
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

                    //checking if the username already Exists
                    Query usernameQuery = mDatabaseReference.orderByValue().equalTo(userNameText);
                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                username.getEditText().setText("");
                                username.hasFocus();
                                username.setError("This username already exists");
                            }
                            else {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", currentUser.getEmail());
                                userMap.put("fullName", fullNameText);
                                userMap.put("userName", userNameText);
                                userMap.put("taStatus", status);

                                //Updating the database.
                                mDatabaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ActivityCreateBasicProfile.this, "User details were added successfully", Toast.LENGTH_SHORT).show();
                                            if(status == 0) {
                                                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("VerifyTA").child(currentUser.getUid());
                                                Intent studentIntent = new Intent(ActivityCreateBasicProfile.this, ActivityVerifyTA.class);
                                                startActivity(studentIntent);
                                                finish();
                                            }else if(status == -1){
                                                Intent studentIntent = new Intent(ActivityCreateBasicProfile.this, MainActivity.class);
                                                startActivity(studentIntent);
                                                finish();
                                            }

                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ActivityCreateBasicProfile.this, "Couldn't establish connection to the database. Try again!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
