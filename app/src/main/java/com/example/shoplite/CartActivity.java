
package com.example.shoplite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CartActivity extends Activity {

    public static final ArrayList<String> cartNames =
            new ArrayList<>();

    public static final ArrayList<Integer> cartPrices =
            new ArrayList<>();

    public static final ArrayList<Integer> cartQuantities =
            new ArrayList<>();

    public static final ArrayList<Integer> cartImages =
            new ArrayList<>();

    private LinearLayout cartItemsLayout;
    private TextView cartSubtotalText;
    private TextView deliveryChargeText;
    private TextView cartTotalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsLayout = findViewById(R.id.cartItemsLayout);
        cartSubtotalText = findViewById(R.id.cartSubtotalText);
        deliveryChargeText = findViewById(R.id.deliveryChargeText);
        cartTotalText = findViewById(R.id.cartTotalText);

        Button checkoutButton = findViewById(R.id.checkoutButton);

        checkoutButton.setOnClickListener(v -> {
            if (cartNames.isEmpty()) {
                Toast.makeText(
                        CartActivity.this,
                        "Your cart is empty",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Intent intent = new Intent(
                    CartActivity.this,
                    CheckoutActivity.class
            );
            startActivity(intent);
        });

        displayCart();
    }

    public static void addProduct(String name, int price) {
        int image = ShopLitePricing.getImage(name);
        addProduct(name, price, image);
    }

    public static void addProduct(
            String name,
            int price,
            int imageResource) {

        for (int i = 0; i < cartNames.size(); i++) {
            if (cartNames.get(i).equals(name)) {
                cartPrices.set(i, price);
                cartQuantities.set(
                        i,
                        cartQuantities.get(i) + 1
                );
                cartImages.set(i, imageResource);
                return;
            }
        }

        cartNames.add(name);
        cartPrices.add(price);
        cartQuantities.add(1);
        cartImages.add(imageResource);
    }

    private void displayCart() {
        cartItemsLayout.removeAllViews();

        int subtotal = 0;

        if (cartNames.isEmpty()) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("Your cart is empty.");
            emptyMessage.setTextSize(18);
            emptyMessage.setTextColor(Color.DKGRAY);
            emptyMessage.setPadding(8, 20, 8, 20);
            cartItemsLayout.addView(emptyMessage);
        }

        for (int i = 0; i < cartNames.size(); i++) {
            final int index = i;

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dpToPx(8), dpToPx(8),
                    dpToPx(8), dpToPx(8));
            item.setBackgroundColor(Color.WHITE);

            LinearLayout.LayoutParams itemParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            itemParams.setMargins(0, dpToPx(6), 0, dpToPx(6));
            item.setLayoutParams(itemParams);

            // Load the image resource stored for this cart item.
            ImageView image = new ImageView(this);

            int imageResource = cartImages.get(i);

            if (imageResource != 0) {
                image.setImageResource(imageResource);
            } else {
                image.setImageResource(
                        ShopLitePricing.getImage(cartNames.get(i))
                );
            }

            image.setVisibility(View.VISIBLE);
            image.setAdjustViewBounds(true);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setBackgroundColor(Color.WHITE);
            image.setContentDescription(cartNames.get(i));

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            dpToPx(120),
                            dpToPx(120)
                    );

            imageParams.setMargins(0, 0, dpToPx(10), 0);
            item.addView(image, imageParams);

            // Product details
            LinearLayout details = new LinearLayout(this);
            details.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams detailsParams =
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1f
                    );

            item.addView(details, detailsParams);

            TextView name = new TextView(this);
            name.setText(cartNames.get(i));
            name.setTextSize(16);
            name.setTextColor(Color.BLACK);
            name.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );
            details.addView(name);

            TextView price = new TextView(this);
            price.setText(
                    "₹" + cartPrices.get(i)
                            + " × " + cartQuantities.get(i)
                            + " = ₹"
                            + (cartPrices.get(i)
                            * cartQuantities.get(i))
            );
            price.setTextSize(14);
            price.setTextColor(Color.DKGRAY);
            details.addView(price);

            // Quantity controls
            LinearLayout controls = new LinearLayout(this);
            controls.setGravity(Gravity.CENTER_VERTICAL);
            details.addView(controls);

            Button minusButton = new Button(this);
            minusButton.setText("−");
            controls.addView(minusButton);

            TextView quantityText = new TextView(this);
            quantityText.setText(
                    String.valueOf(cartQuantities.get(i))
            );
            quantityText.setTextSize(16);
            quantityText.setTextColor(Color.BLACK);
            quantityText.setGravity(Gravity.CENTER);
            controls.addView(quantityText);

            Button plusButton = new Button(this);
            plusButton.setText("+");
            controls.addView(plusButton);

            Button removeButton = new Button(this);
            removeButton.setText("Remove");
            controls.addView(removeButton);

            minusButton.setOnClickListener(v -> {
                int quantity = cartQuantities.get(index);

                if (quantity > 1) {
                    cartQuantities.set(index, quantity - 1);
                } else {
                    removeProduct(index);
                }

                displayCart();
            });

            plusButton.setOnClickListener(v -> {
                cartQuantities.set(
                        index,
                        cartQuantities.get(index) + 1
                );
                displayCart();
            });

            removeButton.setOnClickListener(v -> {
                removeProduct(index);
                displayCart();
            });

            cartItemsLayout.addView(item);

            subtotal += cartPrices.get(i) * cartQuantities.get(i);
        }

        // Delivery: below ₹499 = ₹80,
        // ₹499–₹998 = ₹40, ₹999+ = free.
        int deliveryCharge;

        if (subtotal == 0 || subtotal >= 999) {
            deliveryCharge = 0;
        } else if (subtotal >= 499) {
            deliveryCharge = 40;
        } else {
            deliveryCharge = 80;
        }

        int finalTotal = subtotal + deliveryCharge;

        cartSubtotalText.setText("Subtotal: ₹" + subtotal);

        deliveryChargeText.setText(
                deliveryCharge == 0
                        ? "Delivery: FREE"
                        : "Delivery charge: ₹" + deliveryCharge
        );

        cartTotalText.setText("Total payable: ₹" + finalTotal);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private void removeProduct(int index) {
        cartNames.remove(index);
        cartPrices.remove(index);
        cartQuantities.remove(index);
        cartImages.remove(index);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cartItemsLayout != null) {
            displayCart();
        }
    }
}
