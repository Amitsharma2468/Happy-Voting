package com.example.happyvoting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteActivity extends AppCompatActivity {

    private Spinner spinnerExecutiveMember, spinnerVicePresident, spinnerGeneralSecretary,
            spinnerAssistantGeneralSecretary, spinnerOrganizingSecretary,
            spinnerPublicationSecretary, spinnerAssistantPublicationSecretary, spinnerSportsSecretary;

    private Button buttonSubmitVote;

    private FirebaseFirestore firestore;
    private String userId;
    private boolean hasVoted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        spinnerExecutiveMember = findViewById(R.id.spinnerExecutiveMember);
        spinnerVicePresident = findViewById(R.id.spinnerVicePresident);
        spinnerGeneralSecretary = findViewById(R.id.spinnerGeneralSecretary);
        spinnerAssistantGeneralSecretary = findViewById(R.id.spinnerAssistantGeneralSecretary);
        spinnerOrganizingSecretary = findViewById(R.id.spinnerOrganizingSecretary);
        spinnerPublicationSecretary = findViewById(R.id.spinnerPublicationSecretary);
        spinnerAssistantPublicationSecretary = findViewById(R.id.spinnerAssistantPublicationSecretary);
        spinnerSportsSecretary = findViewById(R.id.spinnerSportsSecretary);
        buttonSubmitVote = findViewById(R.id.buttonSubmitVote);

        // Get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        // Set up button click listener
        buttonSubmitVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitVote();
            }
        });

        // Fetch candidate names from Firestore and set up spinners
        fetchCandidateNames("Executive Member", spinnerExecutiveMember);
        fetchCandidateNames("Vice President", spinnerVicePresident);
        fetchCandidateNames("General Secretary", spinnerGeneralSecretary);
        fetchCandidateNames("Assistant General Secretary", spinnerAssistantGeneralSecretary);
        fetchCandidateNames("Organizing Secretary", spinnerOrganizingSecretary);
        fetchCandidateNames("Publication Secretary", spinnerPublicationSecretary);
        fetchCandidateNames("Assistant Publication Secretary", spinnerAssistantPublicationSecretary);
        fetchCandidateNames("Sports Secretary", spinnerSportsSecretary);

        // Check if the user has already voted
        checkIfUserVoted();
    }

    private void fetchCandidateNames(String position, Spinner spinner) {
        firestore.collection("candidates")
                .whereEqualTo("position", position)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> candidateNames = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String candidateName = document.getString("name");
                            candidateNames.add(candidateName);
                        }

                        // Set up spinner with candidate names
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(VoteActivity.this, android.R.layout.simple_spinner_item, candidateNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } else {
                        // Handle query failure
                        Toast.makeText(VoteActivity.this, "Failed to fetch candidate names", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfUserVoted() {
        firestore.collection("votes")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User has already voted
                            hasVoted = true;
                            disableVoting();
                            Toast.makeText(VoteActivity.this, "You have already voted", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle query failure
                        Toast.makeText(VoteActivity.this, "Failed to check if user voted", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void disableVoting() {
        // You can implement this method to disable the voting UI elements
        spinnerExecutiveMember.setEnabled(false);
        spinnerVicePresident.setEnabled(false);
        spinnerGeneralSecretary.setEnabled(false);
        spinnerAssistantGeneralSecretary.setEnabled(false);
        spinnerOrganizingSecretary.setEnabled(false);
        spinnerPublicationSecretary.setEnabled(false);
        spinnerAssistantPublicationSecretary.setEnabled(false);
        spinnerSportsSecretary.setEnabled(false);
        buttonSubmitVote.setEnabled(false);
    }

    private void submitVote() {
        if (hasVoted) {
            Toast.makeText(VoteActivity.this, "You have already voted", Toast.LENGTH_SHORT).show();
            return;
        }

        String executiveMember = spinnerExecutiveMember.getSelectedItem().toString();
        String vicePresident = spinnerVicePresident.getSelectedItem().toString();
        String generalSecretary = spinnerGeneralSecretary.getSelectedItem().toString();
        String assistantGeneralSecretary = spinnerAssistantGeneralSecretary.getSelectedItem().toString();
        String organizingSecretary = spinnerOrganizingSecretary.getSelectedItem().toString();
        String publicationSecretary = spinnerPublicationSecretary.getSelectedItem().toString();
        String assistantPublicationSecretary = spinnerAssistantPublicationSecretary.getSelectedItem().toString();
        String sportsSecretary = spinnerSportsSecretary.getSelectedItem().toString();

        // Validate if all positions are selected
        if (executiveMember.isEmpty() || vicePresident.isEmpty() || generalSecretary.isEmpty()
                || assistantGeneralSecretary.isEmpty() || organizingSecretary.isEmpty()
                || publicationSecretary.isEmpty() || assistantPublicationSecretary.isEmpty() || sportsSecretary.isEmpty()) {
            Toast.makeText(VoteActivity.this, "Please vote for all positions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map with the user's vote data
        Map<String, Object> voteData = new HashMap<>();
        voteData.put("executiveMember", executiveMember);
        voteData.put("vicePresident", vicePresident);
        voteData.put("generalSecretary", generalSecretary);
        voteData.put("assistantGeneralSecretary", assistantGeneralSecretary);
        voteData.put("organizingSecretary", organizingSecretary);
        voteData.put("publicationSecretary", publicationSecretary);
        voteData.put("assistantPublicationSecretary", assistantPublicationSecretary);
        voteData.put("sportsSecretary", sportsSecretary);

        // Add the user's vote data to Firestore
        firestore.collection("votes")
                .document(userId)
                .set(voteData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Vote submitted successfully
                        Toast.makeText(VoteActivity.this, "Vote submitted successfully", Toast.LENGTH_SHORT).show();
                        updateCandidateVoteCount(executiveMember);
                        updateCandidateVoteCount(vicePresident);
                        updateCandidateVoteCount(generalSecretary);
                        updateCandidateVoteCount(assistantGeneralSecretary);
                        updateCandidateVoteCount(organizingSecretary);
                        updateCandidateVoteCount(publicationSecretary);
                        updateCandidateVoteCount(assistantPublicationSecretary);
                        updateCandidateVoteCount(sportsSecretary);
                        hasVoted = true;
                        disableVoting();
                    } else {
                        // Handle vote submission failure
                        Toast.makeText(VoteActivity.this, "Failed to submit vote", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCandidateVoteCount(String candidateName) {
        firestore.collection("candidates")
                .whereEqualTo("name", candidateName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String candidateId = document.getId();
                            int currentCount = document.getLong("countVote").intValue();
                            firestore.collection("candidates")
                                    .document(candidateId)
                                    .update("countVote", currentCount + 1)
                                    .addOnSuccessListener(aVoid -> Log.d("VoteActivity", "Vote count updated successfully"))
                                    .addOnFailureListener(e -> Log.e("VoteActivity", "Failed to update vote count", e));
                        }
                    } else {
                        // Handle query failure
                        Log.e("VoteActivity", "Failed to fetch candidate document for updating vote count", task.getException());
                    }
                });
    }

    // ... (existing code)
}
