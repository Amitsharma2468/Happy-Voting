package com.example.happyvoting;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText editTextUsername, editTextRegistration, editTextEmail, editTextPassword;
    private Button signupButton;
    private TextView loginLink;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextUsername = findViewById(R.id.signupEditTextUsername);
        editTextRegistration = findViewById(R.id.signupEditTextRegistration);
        editTextEmail = findViewById(R.id.signupEditTextEmail);
        editTextPassword = findViewById(R.id.signupEditTextPassword);
        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signUpUser() {
        String username = editTextUsername.getText().toString().trim();
        String registration = editTextRegistration.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input fields
        if (username.isEmpty() || registration.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // You can use the 'username', 'registration', 'email', and 'password' as needed in your sign-up logic.

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String username =editTextUsername.getText().toString();
                            String registration=editTextRegistration.getText().toString();
                            // Create a map with user data
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("registration", registration);
                            // Add more fields as needed

                            // Add user data to Firestore
                            firestore.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> firestoreTask) {
                                            if (firestoreTask.isSuccessful()) {
                                                // User data added to Firestore successfully
                                                Toast.makeText(SignUp.this, "Signup successful. Please log in.", Toast.LENGTH_SHORT).show();

                                                // Navigate to login page after successful signup
                                                Intent intent = new Intent(SignUp.this, LogIn.class);
                                                startActivity(intent);
                                                finish(); // Finish SignUp activity
                                            } else {
                                                // Handle Firestore data addition failure
                                                Toast.makeText(SignUp.this, "Firestore data addition failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
