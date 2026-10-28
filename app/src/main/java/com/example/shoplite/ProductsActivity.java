
package com.example.shoplite;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsActivity extends Activity {

    private final String[] names = ShopLitePricing.NAMES;

    private final String[] categories = {
            "Electronics", "Electronics", "Electronics",
            "Fashion", "Fashion", "Fashion",
            "Grocery", "Grocery", "Grocery",
            "Beauty", "Beauty", "Beauty"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String selectedCategory =
                getIntent().getStringExtra("category");

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(12, 12, 12, 12);
        main.setBackgroundColor(0xFFF4F7FC);

        // Page title
        TextView title = new TextView(this);
        title.setText(
                selectedCategory == null
                        ? "ShopLite Products"
                        : selectedCategory + " Products"
        );
        title.setTextSize(24);
        title.setTextColor(0xFF1565C0);
        title.setPadding(4, 8, 4, 8);
        main.addView(title);

        // View Cart button
        Button cartButton = new Button(this);
        cartButton.setText("View Cart");
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ProductsActivity.this,
                    CartActivity.class
            );
            startActivity(intent);
        });
        main.addView(cartButton);

        // Product catalogue
        ScrollView scrollView = new ScrollView(this);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setUseDefaultMargins(true);

        for (int i = 0; i < names.length; i++) {

            if (selectedCategory != null
                    && !categories[i].equals(selectedCategory)) {
                continue;
            }

            final String productName = names[i];
            final int productPrice =
                    ShopLitePricing.getPrice(productName);
            final int productImage =
                    ShopLitePricing.getImage(productName);
            final String productCategory = categories[i];
            final boolean discounted =
                    ShopLitePricing.isOnSale(productName);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(8, 8, 8, 8);
            card.setGravity(Gravity.CENTER);
            card.setBackgroundColor(0xFFFFFFFF);
            card.setClickable(true);
            card.setFocusable(true);

            // Product image
            ImageView image = new ImageView(this);
            image.setImageResource(productImage);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setContentDescription(productName);

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            130
                    );
            card.addView(image, imageParams);

            // Product name
            TextView name = new TextView(this);
            name.setText(productName);
            name.setTextSize(14);
            name.setTextColor(0xFF222222);
            name.setGravity(Gravity.CENTER);
            name.setPadding(0, 8, 0, 4);
            card.addView(name);

            // Product price
            if (discounted) {
                TextView originalPrice = new TextView(this);
                originalPrice.setText(
                        "₹" + ShopLitePricing.ORIGINAL_PRICES[i]
                );
                originalPrice.setTextSize(13);
                originalPrice.setTextColor(0xFF777777);
                originalPrice.setPaintFlags(
                        originalPrice.getPaintFlags()
                                | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                );
                originalPrice.setGravity(Gravity.CENTER);
                card.addView(originalPrice);

                TextView saleLabel = new TextView(this);
                saleLabel.setText("FLASH SALE");
                saleLabel.setTextSize(12);
                saleLabel.setTextColor(0xFF16833B);
                saleLabel.setGravity(Gravity.CENTER);
                card.addView(saleLabel);
            }

            TextView price = new TextView(this);
            price.setText("₹" + productPrice);
            price.setTextSize(16);
            price.setTextColor(
                    discounted ? 0xFFB12704 : 0xFF1565C0
            );
            price.setGravity(Gravity.CENTER);
            price.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );
            card.addView(price);

            // Add to Cart
            Button addButton = new Button(this);
            addButton.setText("Add to Cart");
            addButton.setTextSize(11);

            addButton.setOnClickListener(v -> {
                CartActivity.addProduct(
                        productName,
                        productPrice,
                        productImage
                );

                Toast.makeText(
                        ProductsActivity.this,
                        productName + " added to cart",
                        Toast.LENGTH_SHORT
                ).show();
            });

            card.addView(addButton);

            // Tap the card to open product details.
            card.setOnClickListener(v -> {
                Intent intent = new Intent(
                        ProductsActivity.this,
                        ProductDetailsActivity.class
                );

                intent.putExtra("productName", productName);
                intent.putExtra("productPrice", productPrice);
                intent.putExtra("productImage", productImage);
                intent.putExtra("productCategory", productCategory);

                startActivity(intent);
            });

            GridLayout.LayoutParams cardParams =
                    new GridLayout.LayoutParams();

            cardParams.width = 0;
            cardParams.columnSpec =
                    GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                    );

            cardParams.setMargins(4, 4, 4, 4);
            card.setLayoutParams(cardParams);

            grid.addView(card);
        }

        scrollView.addView(grid);

        main.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        setContentView(main);
    }
}
