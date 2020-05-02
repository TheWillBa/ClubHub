package com.example.clubhub;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ClubDescription extends AppCompatActivity {

    private final String TAG = "ClubDescription";



    List<UserData> members = new ArrayList<>();
    List<UserData> leaders = new ArrayList<>();
    String thisClubID;
    Club thisClub;
    FirebaseUser currentUser;
    Button button;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_club_description);

        Intent intent = getIntent();
        thisClubID = intent.getStringExtra("clubID");

        button = findViewById(R.id.clubDescButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();



    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        thisClub = ClubManager.getClub(thisClubID);
        setTextViews();
        setUpMemberList();
        setUpLeaderList();
        setUpDayList();


        if(thisClub.isMember(currentUser.getUid())){
            button.setOnClickListener(new leaveClubOnClickListener());
            button.setText("LEAVE CLUB");
        }

        else if(thisClub.isLeader(currentUser.getUid()) || thisClub.getTeacherID().equals(currentUser.getUid())){
            button.setOnClickListener(new editClubOnClickListener());
            button.setText("EDIT CLUB INFO");
        }
        // else if this user is a teacher and not this clubs adviser
        else if(TeacherManager.getTeacher(currentUser.getUid())  != null && !thisClub.getTeacherID().equals(currentUser.getUid())){
            button.setOnClickListener(new adviseClubOnClickListener());
            button.setText("ADVISE CLUB");
        }
        else{
            button.setOnClickListener(new joinClubOnClickListener());
            button.setText("JOIN CLUB");
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextViews(){

        TextView clubNameText = findViewById(R.id.clubName2);
        clubNameText.setText(thisClub.getName());

        TextView descriptionText = findViewById(R.id.descriptionFillInText2);
        descriptionText.setText(thisClub.getDescription());

        TextView teacherNameText = findViewById(R.id.leaveTeacherAdvisorText2);
        if(thisClub.isHasTeacherAdviser()){
            teacherNameText.setText(TeacherManager.getTeacher(thisClub.getTeacherID()).toString());
        }
        else{
            teacherNameText.setText("This club does not have an adviser");
        }

        TextView timeText = findViewById(R.id.meetingTimeDisplay);
        timeText.setText("This club meets between " + thisClub.getMeetingInfo().getMeetingStartTime() + " and " +
                thisClub.getMeetingInfo().getMeetingEndTime() + " on:");

    }

    private void setUpMemberList(){

        StringBuilder membersStr = new StringBuilder();
        TextView leadersDisplay = findViewById(R.id.studentMemberDisplay);
        if(thisClub.getmIDs().size() < 1)
            membersStr.append("This club has no members");
        for(String memberRef : thisClub.getmIDs()){
            membersStr.append(StudentManager.getStudent(memberRef).getName()).append("\n");
        }

        leadersDisplay.setText(membersStr);
    }

    private void setUpLeaderList() {

        StringBuilder leadersStr = new StringBuilder();
        TextView leadersDisplay = findViewById(R.id.studentLeaderDisplay);
        for(String memberRef : thisClub.getlIDs()){
            leadersStr.append(StudentManager.getStudent(memberRef).getName()).append("\n");
        }

        leadersDisplay.setText(leadersStr);

    }

    private void setUpDayList() {


        ArrayList<String> days = thisClub.getMeetingInfo().onlyMeetingDays();
        TextView daysDisplay = findViewById(R.id.meetingDaysDisplay);
        StringBuilder daysStr = new StringBuilder();
        for(String day : days){
            daysStr.append(day).append("\n");
        }

        daysDisplay.setText(daysStr.toString());

    }



    //region Button onClick Listeners

    private class joinClubOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String currentUserID = currentUser.getUid();

            if(thisClub.isLeader(currentUserID) || thisClub.isMember(currentUserID))
                return;

            StudentManager.getStudent(currentUserID).addUserToClubAsMemberFirebase(thisClubID);
            thisClub.addMemberFirebase(currentUserID);

            // Directs the user somewhere else or to member page for this club
            Intent intent = new Intent(ClubDescription.this, ClubHubStudent.class);
            startActivity(intent);
        }
    }

    private class leaveClubOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Leaving club");
            String currentUserID = currentUser.getUid();

            // Remove this student from club
            thisClub.deleteMemberFirebase(currentUserID);
            // Remove this club from student
            StudentManager.getStudent(currentUserID).removeClubFromMemberFirebase(thisClubID);

            // Directs the user somewhere else or to member page for this club
            Intent intent = new Intent(ClubDescription.this, ClubHubStudent.class);
            startActivity(intent);
        }
    }

    private class editClubOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ClubDescription.this, EditClubInfoActivity.class);
            intent.putExtra("clubID", thisClubID);
            startActivity(intent);
        }
    }

    private class adviseClubOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(!thisClub.isHasTeacherAdviser()) {
                Teacher thisTeacher = TeacherManager.getTeacher(currentUser.getUid());
                thisTeacher.addAdvisingClub(thisClubID);
                thisClub.setTeacherID(thisTeacher.getFirebaseID());
                thisClub.setHasTeacherAdviser(true);
                thisClub.updateObjectDatabase();
            }

            Intent intent = new Intent(ClubDescription.this, ClubHubTeacher.class);
            startActivity(intent);
        }
    }

    //endregion


}