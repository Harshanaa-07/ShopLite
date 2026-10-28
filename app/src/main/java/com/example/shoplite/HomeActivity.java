package com.example.shoplite;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Profile icon
        ImageButton profileButton = findViewById(R.id.profileIconButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    ProfileActivity.class
            );
            startActivity(intent);
        });

        // Cart icon
        ImageButton cartButton = findViewById(R.id.cartIconButton);
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    CartActivity.class
            );
            startActivity(intent);
        });

        // Category buttons
        int[] categoryButtons = {
                R.id.electronicsButton,
                R.id.fashionButton,
                R.id.groceryButton,
                R.id.beautyButton
        };

        String[] categories = {
                "Electronics",
                "Fashion",
                "Grocery",
                "Beauty"
        };

        for (int i = 0; i < categoryButtons.length; i++) {
            final String category = categories[i];
            Button button = findViewById(categoryButtons[i]);

            button.setOnClickListener(v -> {
                Intent intent = new Intent(
                        HomeActivity.this,
                        ProductsActivity.class
                );
                intent.putExtra("category", category);
                startActivity(intent);
            });
        }

        // Explore products
        Button explore = findViewById(R.id.viewProductsButton);
        explore.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    ProductsActivity.class
            );
            startActivity(intent);
        });

        // Flash Sale
        Button flashSaleButton = findViewById(R.id.flashSaleButton);
        flashSaleButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    FlashSaleActivity.class
            );
            startActivity(intent);
        });
    }
}
