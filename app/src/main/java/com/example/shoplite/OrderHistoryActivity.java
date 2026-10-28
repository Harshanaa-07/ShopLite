
package com.example.shoplite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderHistoryActivity extends Activity {

    public static final String PREFS = "ShopLiteOrders";
    public static final String ORDERS_KEY = "orders";

    public static void saveOrder(
            Context context,
            String orderId,
            String customerName,
            String phone,
            String address,
            String payment,
            int total) {

        try {
            SharedPreferences prefs =
                    context.getSharedPreferences(
                            PREFS,
                            Context.MODE_PRIVATE
                    );

            JSONArray orders = new JSONArray(
                    prefs.getString(ORDERS_KEY, "[]")
            );

            JSONObject order = new JSONObject();
            order.put("id", orderId);
            order.put("name", customerName);
            order.put("phone", phone);
            order.put("address", address);
            order.put("payment", payment);
            order.put("total", total);

            JSONArray items = new JSONArray();

            for (int i = 0; i < CartActivity.cartNames.size(); i++) {
                JSONObject item = new JSONObject();

                item.put("name", CartActivity.cartNames.get(i));
                item.put("price", CartActivity.cartPrices.get(i));
                item.put("quantity", CartActivity.cartQuantities.get(i));

                // Save the image resource ID for this product.
                item.put("image", CartActivity.cartImages.get(i));

                items.put(item);
            }

            order.put("items", items);
            orders.put(order);

            prefs.edit()
                    .putString(ORDERS_KEY, orders.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        LinearLayout ordersLayout = findViewById(R.id.ordersLayout);
        Button backButton = findViewById(R.id.ordersBackButton);

        backButton.setOnClickListener(v -> finish());

        SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);

        try {
            JSONArray orders = new JSONArray(
                    prefs.getString(ORDERS_KEY, "[]")
            );

            if (orders.length() == 0) {
                TextView empty = new TextView(this);
                empty.setText("You haven't placed any orders yet.");
                empty.setTextSize(18);
                empty.setTextColor(Color.DKGRAY);
                empty.setPadding(12, 24, 12, 24);
                ordersLayout.addView(empty);
            }

            // Display newest orders first.
            for (int i = orders.length() - 1; i >= 0; i--) {
                JSONObject order = orders.getJSONObject(i);

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(
                        dpToPx(16),
                        dpToPx(16),
                        dpToPx(16),
                        dpToPx(16)
                );
                card.setBackgroundColor(Color.WHITE);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

                params.setMargins(
                        0,
                        dpToPx(8),
                        0,
                        dpToPx(8)
                );

                card.setLayoutParams(params);

                // Order ID
                TextView orderId = new TextView(this);
                orderId.setText("Order ID: " + order.optString("id"));
                orderId.setTextSize(17);
                orderId.setTextColor(Color.BLACK);
                orderId.setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                );
                card.addView(orderId);

                // Order total
                TextView total = new TextView(this);
                total.setText("Amount: ₹" + order.optInt("total"));
                total.setTextSize(16);
                total.setTextColor(0xFF1565C0);
                total.setPadding(0, dpToPx(4), 0, 0);
                card.addView(total);

                // Payment method
                TextView payment = new TextView(this);
                payment.setText(
                        "Payment: " + order.optString("payment")
                );
                payment.setTextColor(Color.DKGRAY);
                payment.setPadding(0, dpToPx(4), 0, 0);
                card.addView(payment);

                // Delivery information
                TextView address = new TextView(this);
                address.setText(
                        "Delivery to: " + order.optString("name")
                                + "\n" + order.optString("address")
                                + "\n" + order.optString("phone")
                );
                address.setTextColor(Color.DKGRAY);
                address.setPadding(0, dpToPx(8), 0, dpToPx(8));
                card.addView(address);

                // Ordered products heading
                TextView itemsHeading = new TextView(this);
                itemsHeading.setText("Ordered Products");
                itemsHeading.setTextColor(Color.BLACK);
                itemsHeading.setTextSize(16);
                itemsHeading.setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                );
                itemsHeading.setPadding(0, dpToPx(8), 0, dpToPx(4));
                card.addView(itemsHeading);

                JSONArray items = order.optJSONArray("items");

                if (items != null) {
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject item = items.getJSONObject(j);

                        // Product row
                        LinearLayout productRow = new LinearLayout(this);
                        productRow.setOrientation(LinearLayout.HORIZONTAL);
                        productRow.setGravity(Gravity.CENTER_VERTICAL);
                        productRow.setPadding(0, dpToPx(8), 0, dpToPx(8));

                        // Product image
                        ImageView productImage = new ImageView(this);

                        int imageResource = item.optInt("image", 0);

                        if (imageResource != 0) {
                            productImage.setImageResource(imageResource);
                        } else {
                            // Fallback for older orders without saved images.
                            int fallbackImage =
                                    ShopLitePricing.getImage(
                                            item.optString("name")
                                    );

                            if (fallbackImage != 0) {
                                productImage.setImageResource(fallbackImage);
                            }
                        }

                        productImage.setScaleType(
                                ImageView.ScaleType.FIT_CENTER
                        );
                        productImage.setContentDescription(
                                item.optString("name")
                        );

                        LinearLayout.LayoutParams imageParams =
                                new LinearLayout.LayoutParams(
                                        dpToPx(90),
                                        dpToPx(90)
                                );

                        imageParams.setMargins(
                                0,
                                0,
                                dpToPx(12),
                                0
                        );

                        productRow.addView(productImage, imageParams);

                        // Product name and price
                        LinearLayout details = new LinearLayout(this);
                        details.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout.LayoutParams detailsParams =
                                new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        1f
                                );

                        productRow.addView(details, detailsParams);

                        TextView productName = new TextView(this);
                        productName.setText(item.optString("name"));
                        productName.setTextSize(15);
                        productName.setTextColor(Color.BLACK);
                        productName.setTypeface(
                                null,
                                android.graphics.Typeface.BOLD
                        );
                        details.addView(productName);

                        int price = item.optInt("price");
                        int quantity = item.optInt("quantity");

                        TextView productPrice = new TextView(this);
                        productPrice.setText(
                                "₹" + price + " × " + quantity
                                        + " = ₹" + (price * quantity)
                        );
                        productPrice.setTextSize(14);
                        productPrice.setTextColor(Color.DKGRAY);
                        details.addView(productPrice);

                        card.addView(productRow);
                    }
                }

                ordersLayout.addView(card);
            }

        } catch (Exception e) {
            TextView error = new TextView(this);
            error.setText("Unable to load orders.");
            error.setTextColor(Color.RED);
            ordersLayout.addView(error);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}
