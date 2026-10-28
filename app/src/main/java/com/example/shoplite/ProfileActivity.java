
package com.example.shoplite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);

        Button ordersButton = findViewById(R.id.myOrdersButton);
        Button addressesButton = findViewById(R.id.myAddressesButton);
        Button wishlistButton = findViewById(R.id.myWishlistButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadProfile();

        // Open existing order history
        ordersButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ProfileActivity.this,
                    OrderHistoryActivity.class
            );
            startActivity(intent);
        });

        // Address book will be connected next
        addressesButton.setOnClickListener(v ->
                Toast.makeText(
                        ProfileActivity.this,
                        "Address book is the next feature to set up.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Wishlist will be connected next
        wishlistButton.setOnClickListener(v ->
                Toast.makeText(
                        ProfileActivity.this,
                        "Wishlist is the next feature to set up.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Log out
        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    MainActivity.class
            );
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            openLogin();
            return;
        }

        // Email comes from Firebase Authentication
        profileEmail.setText(user.getEmail());

        // Name comes from the registration profile in Firestore
        firestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");

                        if (name != null && !name.trim().isEmpty()) {
                            profileName.setText(name);
                        } else {
                            profileName.setText("ShopLite User");
                        }
                    } else {
                        profileName.setText("ShopLite User");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                ProfileActivity.this,
                                "Could not load profile: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void openLogin() {
        Intent intent = new Intent(
                ProfileActivity.this,
                MainActivity.class
        );
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(intent);
        finish();
    }
}
