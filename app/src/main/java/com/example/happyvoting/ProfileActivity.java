package com.example.happyvoting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewProfile, textViewCandidateName, textViewRegistrationNo;
    private Button buttonVote, buttonCreateCandidate;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the toolbar as the app bar
        setSupportActionBar(toolbar);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        textViewProfile = findViewById(R.id.textViewProfile);
        textViewCandidateName = findViewById(R.id.textViewCandidateName);
        textViewRegistrationNo = findViewById(R.id.textViewRegistrationNo);
        buttonVote = findViewById(R.id.buttonVote);
        buttonCreateCandidate = findViewById(R.id.buttonCreateCandidate);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            retrieveUserData();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToVotePage();
            }
        });

        buttonCreateCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreateCandidatePage();
            }
        });
    }

    private void retrieveUserData() {
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            String registration = document.getString("registration");
                            if (registration.equals("2020831009")) {
                                buttonCreateCandidate.setVisibility(View.VISIBLE);
                            } else {
                                buttonCreateCandidate.setVisibility(View.GONE);
                            }
                            textViewProfile.setText("Welcome, " + username + "!");
                            textViewCandidateName.setText("Candidate Name: " + username);
                            textViewRegistrationNo.setText("Registration No: " + registration);
                        } else {
                            Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "Error retrieving user data: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToVotePage() {

        Intent intent = new Intent(ProfileActivity.this, VoteActivity.class);
        startActivity(intent);
    }

    private void goToCreateCandidatePage() {
        Intent intent = new Intent(ProfileActivity.this, CreateCandidateActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(ProfileActivity.this, LogIn.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuShowVote) {
            startActivity(new Intent(ProfileActivity.this, ShowVoteActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.menuLogOut) {
            // Handle the Log Out option
            // Add your logic here
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LogIn.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
