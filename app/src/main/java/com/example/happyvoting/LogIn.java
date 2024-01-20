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

public class LogIn extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button loginButton;
    private TextView forgotPasswordButton;
    private TextView registerLink;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        forgotPasswordButton = findViewById(R.id.forgetLink);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPasswordDialog();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

    }



    private void loginUser() {
        String email = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            Intent intent = new Intent(LogIn.this, ProfileActivity.class);
                            // Pass user details to ProfileActivity
                            startActivity(intent);
                            finish(); // Optional: Finish the login activity to prevent going back
                        } else {
                            // If login fails, display a message to the user.
                            Toast.makeText(LogIn.this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void openForgotPasswordDialog() {
        String email = editTextUsername.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email first.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogIn.this, "Password reset email sent. Check your email inbox.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LogIn.this, "Failed to send password reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
