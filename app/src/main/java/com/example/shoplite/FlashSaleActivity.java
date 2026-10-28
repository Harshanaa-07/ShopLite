
package com.example.shoplite;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class FlashSaleActivity extends Activity {

    private TextView tvFlashSaleStatus;
    private LinearLayout flashSaleContainer;
    private CountDownTimer countDownTimer;
    private long saleEndTime;

    private final String[] names = {
            "Wireless Headphones", "Wireless Mouse", "Keyboard",
            "T-Shirt", "Jeans", "Running Shoes",
            "Rice Pack", "Cooking Oil", "Coffee Pack",
            "Face Wash", "Moisturizer", "Shampoo"
    };

    private final int[] originalPrices = {
            1499, 699, 1299, 1999,
            999, 1299, 499, 199,
            399, 249, 499, 349
    };

    // Sale prices for products selected for the Flash Sale.
    private final int[] salePrices = {
            999, 499, 899, 1499,
            699, 899, 349, 149,
            279, 179, 349, 249
    };

    // true = discounted; false = normal price.
    // Only selected products receive Flash Sale discounts.
    private final boolean[] onSale = {
            true, true, true,
            false, true, false,
            true, false, false,
            true, false, true
    };

    private final int[] images = {
            R.drawable.img,
            R.drawable.img_1,
            R.drawable.img_2,
            R.drawable.img_3,
            R.drawable.img_4,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_7,
            R.drawable.img_8,
            R.drawable.img_9,
            R.drawable.img_10,
            R.drawable.img_11
    };

    private final int[] stock = {
            5, 8, 6, 4, 7, 5, 10, 8, 6, 7, 5, 9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_sale);

        tvFlashSaleStatus = findViewById(R.id.tvFlashSaleStatus);
        flashSaleContainer = findViewById(R.id.flashSaleContainer);

        Button backButton = findViewById(R.id.btnBackToHome);
        backButton.setOnClickListener(v -> finish());

        saleEndTime = System.currentTimeMillis() + 10 * 60 * 1000L;

        loadFlashSaleProducts();
        startCountdown();
    }

    private void loadFlashSaleProducts() {
        flashSaleContainer.removeAllViews();

        for (int i = 0; i < names.length; i++) {
            final int index = i;

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);
            card.setBackgroundColor(Color.WHITE);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            cardParams.setMargins(0, 8, 0, 8);
            card.setLayoutParams(cardParams);

            // Product image
            ImageView image = new ImageView(this);
            image.setImageResource(images[i]);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 160
                    );
            card.addView(image, imageParams);

            // Product name
            TextView name = new TextView(this);
            name.setText(names[i]);
            name.setTextSize(18);
            name.setTextColor(Color.BLACK);
            name.setGravity(Gravity.CENTER);
            name.setPadding(0, 8, 0, 8);
            card.addView(name);

            // Price display
            if (onSale[i]) {
                TextView original = new TextView(this);
                original.setText("Original Price: ₹" + originalPrices[i]);
                original.setTextSize(14);
                original.setTextColor(Color.GRAY);
                original.setPaintFlags(
                        original.getPaintFlags()
                                | Paint.STRIKE_THRU_TEXT_FLAG
                );
                original.setGravity(Gravity.CENTER);
                card.addView(original);

                TextView salePrice = new TextView(this);
                salePrice.setText("Flash Sale Price: ₹" + salePrices[i]);
                salePrice.setTextSize(20);
                salePrice.setTextColor(Color.RED);
                salePrice.setGravity(Gravity.CENTER);
                salePrice.setTypeface(null, Typeface.BOLD);
                card.addView(salePrice);

                int discount = Math.round(
                        (originalPrices[i] - salePrices[i]) * 100f
                                / originalPrices[i]
                );

                TextView discountText = new TextView(this);
                discountText.setText("🔥 " + discount + "% OFF");
                discountText.setTextSize(16);
                discountText.setTextColor(Color.rgb(22, 128, 60));
                discountText.setGravity(Gravity.CENTER);
                card.addView(discountText);
            } else {
                TextView normalPrice = new TextView(this);
                normalPrice.setText("Price: ₹" + originalPrices[i]);
                normalPrice.setTextSize(20);
                normalPrice.setTextColor(Color.BLACK);
                normalPrice.setGravity(Gravity.CENTER);
                normalPrice.setTypeface(null, Typeface.BOLD);
                card.addView(normalPrice);

                TextView normalLabel = new TextView(this);
                normalLabel.setText("Regular Price");
                normalLabel.setTextSize(14);
                normalLabel.setTextColor(Color.DKGRAY);
                normalLabel.setGravity(Gravity.CENTER);
                card.addView(normalLabel);
            }

            // Stock information
            TextView stockText = new TextView(this);
            stockText.setText(
                    stock[i] > 0
                            ? "Only " + stock[i] + " left!"
                            : "Out of stock"
            );
            stockText.setTextSize(14);
            stockText.setGravity(Gravity.CENTER);
            stockText.setTextColor(
                    stock[i] <= 2 ? Color.RED : Color.DKGRAY
            );
            card.addView(stockText);

            // Add to Cart
            Button addButton = new Button(this);
            addButton.setText(stock[i] > 0 ? "Add to Cart" : "SOLD OUT");
            addButton.setEnabled(stock[i] > 0);
            card.addView(addButton);

            addButton.setOnClickListener(v -> {
                if (System.currentTimeMillis() >= saleEndTime) {
                    Toast.makeText(
                            this,
                            "Flash sale has ended!",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                if (stock[index] <= 0) {
                    Toast.makeText(
                            this,
                            "Product is sold out",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                // Apply a discount only to selected products.
                int finalPrice = onSale[index]
                        ? salePrices[index]
                        : originalPrices[index];

                CartActivity.addProduct(names[index], finalPrice);

                stock[index]--;

                Toast.makeText(
                        this,
                        names[index] + " added for ₹" + finalPrice,
                        Toast.LENGTH_SHORT
                ).show();

                loadFlashSaleProducts();
            });

            flashSaleContainer.addView(card);
        }
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(
                Math.max(0, saleEndTime - System.currentTimeMillis()),
                1000
        ) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                tvFlashSaleStatus.setText(String.format(
                        Locale.getDefault(),
                        "⏰ Sale ends in %02d:%02d",
                        minutes, seconds
                ));
            }

            @Override
            public void onFinish() {
                tvFlashSaleStatus.setText("FLASH SALE ENDED!");

                for (int i = 0;
                     i < flashSaleContainer.getChildCount(); i++) {

                    LinearLayout card =
                            (LinearLayout) flashSaleContainer.getChildAt(i);

                    for (int j = 0; j < card.getChildCount(); j++) {
                        if (card.getChildAt(j) instanceof Button) {
                            Button button =
                                    (Button) card.getChildAt(j);
                            button.setText("SALE ENDED");
                            button.setEnabled(false);
                        }
                    }
                }
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
