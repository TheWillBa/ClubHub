package com.example.clubhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateSchoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_school);

    }


    @Override
    protected void onStart(){
        super.onStart();
        initiateSchools();
        initiateClubs();
        initiateUsers();

    }

    /**
     * Helper method to be called in the first activity
     * the user encounters in order to keep an updated list
     * of the users in the database
     */
    private void initiateUsers() {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    UserManager.putUser(ds.getValue(UserData.class).getID(), ds.getValue(UserData.class));
                    Log.v("MainActivity", "Added USer: " + ds.getValue(UserData.class).getName() + " " + ds.getValue(UserData.class).getID());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Helper method to be called in the first activity
     * the user encounters in order to keep an updated list
     * of the clubs in the database
     */
    private void initiateClubs() {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("clubs");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ClubManager.putClub(ds.getValue(Club.class).getNumID(), ds.getValue(Club.class));
                    Log.v("MainActivity", "Added Club: " + ds.getValue(Club.class).getName());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /**
     * Helper method to be called in the first activity
     * the user encounters in order to keep an updated list
     * of the schools in the database
     */
    private void initiateSchools() {


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("schools");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    SchoolManager.putSchool(ds.getValue(School.class).getID(), ds.getValue(School.class));
                    Log.v("MainActivity", "Added School: " + ds.getValue(School.class).getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    public void setSchoolName(View v){


        EditText nameText = findViewById(R.id.schoolEditText);
        String uID = nameText.getText().toString();


        // Find some way to effectively store what school the user is currently in

        SchoolManager.createSchool(uID);

        Log.v("MainActivity", "Added school with id " + SchoolManager.currentSchoolID);
        SchoolManager.getSchool(SchoolManager.currentSchoolID);
    }

    public void progressToClub(View v){

        setSchoolName(v);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}