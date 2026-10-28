
package com.example.shoplite;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends Activity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Fill the email after returning from registration.
        String registeredEmail =
                getIntent().getStringExtra("registeredEmail");

        if (registeredEmail != null) {
            emailInput.setText(registeredEmail);
        }

        loginButton.setOnClickListener(v -> loginUser());

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailInput.getText()
                .toString().trim().toLowerCase();

        String password = passwordInput.getText().toString();

        if (email.isEmpty()) {
            emailInput.setError("Please enter your email");
            emailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {
            emailInput.setError("Enter a valid email address");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Please enter your password");
            passwordInput.requestFocus();
            return;
        }

        loginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()
                            && mAuth.getCurrentUser() != null) {

                        String uid = mAuth.getCurrentUser().getUid();

                        // Ensure the user's profile exists before opening Home.
                        db.collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(document -> {
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Login successful!",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(
                                            MainActivity.this,
                                            HomeActivity.class);

                                    intent.putExtra("userEmail", email);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Login succeeded, but profile could not be loaded: "
                                                    + e.getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {
                        String message = task.getException() != null
                                ? task.getException().getLocalizedMessage()
                                : "Login failed. Please try again.";

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Login Failed")
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
    }
}
