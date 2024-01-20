package com.example.happyvoting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateCandidateActivity extends AppCompatActivity {

    private EditText editTextName, editTextRegistrationNo, editTextBatchNo;
    private Spinner spinnerPosition;
    private Button buttonSubmit;

    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_candidate);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextRegistrationNo = findViewById(R.id.editTextRegistrationNo);
        editTextBatchNo = findViewById(R.id.editTextBatchNo);
        spinnerPosition = findViewById(R.id.spinnerBatch);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Set up Spinner dynamically
        String[] positions = {"Executive Member", "Vice President", "General Secretary"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, positions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapter);

        // Get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        // Set up button click listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCandidateData();
            }
        });
    }
//need to be private
    private void submitCandidateData() {
        String name = editTextName.getText().toString().trim();
        String registrationNo = editTextRegistrationNo.getText().toString().trim();
        String batchNo = editTextBatchNo.getText().toString().trim();
        String position = spinnerPosition.getSelectedItem().toString();
        int countVote = 0;
        // Validate input fields
        if (name.isEmpty() || registrationNo.isEmpty() || batchNo.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map with candidate data
        Map<String, Object> candidateData = new HashMap<>();
        candidateData.put("name", name);
        candidateData.put("registrationNo", registrationNo);
        candidateData.put("batchNo", batchNo);
        candidateData.put("position", position);// Initialize countVote to 0
        // Add more fields as needed

        // Add candidate data to Firestore
        firestore.collection("candidates").document()
                .set(candidateData)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Candidate data added to Firestore successfully
                        Toast.makeText(CreateCandidateActivity.this, "Candidate data submitted successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        // Handle Firestore data addition failure
                        Toast.makeText(CreateCandidateActivity.this, "Firestore data addition failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        editTextName.getText().clear();
        editTextRegistrationNo.getText().clear();
        editTextBatchNo.getText().clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finish the current activity (CreateCandidateActivity)
        finish();

        // Navigate to the profile page
        // You need to replace ProfileActivity.class with the actual class for your profile page
        // e.g., Intent intent = new Intent(CreateCandidateActivity.this, ProfileActivity.class);
        Intent intent = new Intent(CreateCandidateActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
