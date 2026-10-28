
package com.example.shoplite;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {

    private EditText nameInput, emailInput;
    private EditText passwordInput, confirmPasswordInput;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.registerEmailInput);
        passwordInput = findViewById(R.id.registerPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        Button createButton = findViewById(R.id.createAccountButton);
        Button backButton = findViewById(R.id.backToLoginButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createButton.setOnClickListener(v -> registerUser());
        backButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim().toLowerCase();
        String password = passwordInput.getText().toString();
        String confirm = confirmPasswordInput.getText().toString();

        if (name.isEmpty()) {
            nameInput.setError("Enter your name");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirm)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        // Create the account in Firebase Authentication.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser() == null) {
                            Toast.makeText(this,
                                    "Account created, but user session is unavailable.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        String uid = mAuth.getCurrentUser().getUid();

                        // Save the user's profile in Firestore.
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);

                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this,
                                            "Account created successfully!",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(
                                            RegisterActivity.this,
                                            MainActivity.class);

                                    intent.addFlags(
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Account created, but profile save failed: "
                                                    + e.getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {
                        String message = task.getException() != null
                                ? task.getException().getLocalizedMessage()
                                : "Registration failed. Please try again.";

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
