package com.example.happyvoting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowVoteActivity extends AppCompatActivity {

    private ListView listViewResults;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vote);

        // Initialize Firebase components
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        listViewResults = findViewById(R.id.listViewResults);

        // Fetch and display voting results
        fetchAndDisplayResults();
    }
    //backbutton
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ShowVoteActivity.this, ProfileActivity.class));// Handle back button press
        super.onBackPressed();
    }
    private void fetchAndDisplayResults() {
        // Fetch data from the "votes" collection
        firestore.collection("votes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Display results dynamically using ListView
                            displayResults(task.getResult());
                        } else {
                            // Handle the case where fetching results fails
                            List<String> errorList = new ArrayList<>();
                            errorList.add("Failed to fetch voting results");
                            displayResultsList(errorList);
                        }
                    }
                });
    }

    private void displayResults(QuerySnapshot querySnapshot) {
        Map<String, Integer> candidateCounts = new HashMap<>();

        // Iterate through the "votes" collection
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            // Get all data from each document
            List<String> allData = getAllDataFromDocument(document);

            // Count occurrences of each candidate
            countAllDataOccurrences(allData, candidateCounts);
        }


        // Create a list of strings to display in the ListView
        List<String> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : candidateCounts.entrySet()) {
            resultList.add(entry.getKey() + ": " + entry.getValue());
        }

        // Display the results using ListView
        displayResultsList(resultList);
    }

    private List<String> getAllDataFromDocument(DocumentSnapshot document) {
        // Get all data from the document
        // Adjust this based on your actual Firestore data structure
        Map<String, Object> allDataMap = document.getData();
        List<String> allDataList = new ArrayList<>();

        if (allDataMap != null) {
            for (Map.Entry<String, Object> entry : allDataMap.entrySet()) {
                allDataList.add(entry.getValue().toString());
            }
        }

        return allDataList;
    }

    private void countAllDataOccurrences(List<String> allData, Map<String, Integer> dataCounts) {
        // Count occurrences of each data
        for (String data : allData) {
            if (dataCounts.containsKey(data)) {
                // Data is already in the map, increment the count
                dataCounts.put(data, dataCounts.get(data) + 1);
            } else {
                // Data is not in the map, add with count 1
                dataCounts.put(data, 1);
            }
        }
    }

    private void displayResultsList(List<String> resultList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultList);
        listViewResults.setAdapter(adapter);
    }
}


