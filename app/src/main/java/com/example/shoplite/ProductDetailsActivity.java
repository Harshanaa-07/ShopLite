
package com.example.shoplite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductDetailsActivity extends Activity {

    private String productName;
    private int productPrice;
    private int productImage;
    private int quantity = 1;

    private TextView quantityText;
    private TextView totalPriceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ImageView productImageView =
                findViewById(R.id.detailProductImage);

        TextView nameText =
                findViewById(R.id.detailProductName);

        TextView priceText =
                findViewById(R.id.detailProductPrice);

        TextView descriptionText =
                findViewById(R.id.detailProductDescription);

        quantityText = findViewById(R.id.quantityText);
        totalPriceText = findViewById(R.id.detailTotalPrice);

        Button decreaseButton =
                findViewById(R.id.decreaseQuantityButton);

        Button increaseButton =
                findViewById(R.id.increaseQuantityButton);

        Button addToCartButton =
                findViewById(R.id.detailAddToCartButton);

        Button buyNowButton =
                findViewById(R.id.detailBuyNowButton);

        Button backButton =
                findViewById(R.id.detailBackButton);

        Intent intent = getIntent();

        String receivedName =
                intent.getStringExtra("productName");

        productName = receivedName == null
                ? "Product"
                : receivedName;

        // Use the shared pricing rules.
        int sharedPrice = ShopLitePricing.getPrice(productName);

        productPrice = sharedPrice > 0
                ? sharedPrice
                : intent.getIntExtra("productPrice", 0);

        productImage = intent.getIntExtra(
                "productImage",
                ShopLitePricing.getImage(productName)
        );

        String category =
                intent.getStringExtra("productCategory");

        productImageView.setImageResource(productImage);
        nameText.setText(productName);
        priceText.setText("₹" + productPrice);

        String priceDescription =
                ShopLitePricing.isOnSale(productName)
                        ? "Flash Sale price applied."
                        : "Regular product price.";

        descriptionText.setText(
                "Category: "
                        + (category == null ? "General" : category)
                        + "\n\nA useful, quality product for your everyday needs."
                        + "\n\n"
                        + priceDescription
        );

        updateQuantityDisplay();

        decreaseButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityDisplay();
            } else {
                Toast.makeText(
                        this,
                        "Minimum quantity is 1",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        increaseButton.setOnClickListener(v -> {
            quantity++;
            updateQuantityDisplay();
        });

        addToCartButton.setOnClickListener(
                v -> addSelectedQuantityToCart()
        );

        buyNowButton.setOnClickListener(v -> {
            addSelectedQuantityToCart();

            Intent cartIntent = new Intent(
                    ProductDetailsActivity.this,
                    CartActivity.class
            );

            startActivity(cartIntent);
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void updateQuantityDisplay() {
        quantityText.setText(String.valueOf(quantity));

        totalPriceText.setText(
                "Total: ₹" + (productPrice * quantity)
        );
    }

    private void addSelectedQuantityToCart() {
        for (int i = 0; i < quantity; i++) {
            CartActivity.addProduct(
                    productName,
                    productPrice,
                    productImage
            );
        }

        Toast.makeText(
                this,
                quantity + " item(s) added to cart",
                Toast.LENGTH_SHORT
        ).show();
    }
}
