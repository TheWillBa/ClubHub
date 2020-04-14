package com.example.clubhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clubhub.Club;
import com.example.clubhub.ClubManager;
import com.example.clubhub.R;
import com.example.clubhub.StudentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class YourClubMember extends AppCompatActivity{


    private TextView yourClubText;
    List<Club> memberClubs = new ArrayList<>();
    List<Club> leaderClubs = new ArrayList<>();

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_club_member);

        yourClubText = findViewById(R.id.yourClubs);


    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        for(String clubRef : StudentManager.getStudent(currentUser.getUid()).getmClubs()){
            if(ClubManager.getClub(clubRef) != null)
                memberClubs.add(ClubManager.getClub(clubRef));
        }

        for(String clubRef : StudentManager.getStudent(currentUser.getUid()).getlClubs()){
            if(ClubManager.getClub(clubRef) != null)
                leaderClubs.add(ClubManager.getClub(clubRef));
        }

        setShowMemberClubs();
    }

    private void setShowMemberClubs(){

        yourClubText.setText("Your member clubs");
        ArrayAdapter<Club> clubsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memberClubs);

        final ListView listView = findViewById(R.id.yourClubView);
        listView.setAdapter(clubsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /** Change to display member club description page
                 Club nClub = clubs.get(position);

                 Intent intent = new Intent(FindAClub.this, JoinClubDescriptionPage.class);
                 intent.putExtra("clubID", nClub.getNumID());
                 startActivity(intent);**/
            }
        });

    }

    private void setShowLeaderClubs(){

        yourClubText.setText("Your leader clubs");
        ArrayAdapter<Club> clubsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leaderClubs);

        final ListView listView = findViewById(R.id.yourClubView);
        listView.setAdapter(clubsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /** Change to display leader club description page
                 Club nClub = clubs.get(position);

                 Intent intent = new Intent(FindAClub.this, JoinClubDescriptionPage.class);
                 intent.putExtra("clubID", nClub.getNumID());
                 startActivity(intent);**/
            }
        });

    }

    public void memberOnClick(View v){
        setShowMemberClubs();
    }

    public void leaderOnClick(View v){
        setShowLeaderClubs();
    }

    public void backOnClick(View v){
        Intent intent = new Intent(this, ClubHubStudent.class);
        startActivity(intent);
    }

}