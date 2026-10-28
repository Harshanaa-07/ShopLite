
package com.example.shoplite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class OrderConfirmationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        TextView orderIdText = findViewById(R.id.confirmationOrderId);
        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);
        Button continueShoppingButton =
                findViewById(R.id.continueShoppingButton);

        String orderId = getIntent().getStringExtra("orderId");
        int total = getIntent().getIntExtra("total", 0);

        orderIdText.setText(
                "Order ID: " + orderId + "\n"
                        + "Total payable: ₹" + total
        );

        viewOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    OrderHistoryActivity.class
            );
            startActivity(intent);
        });

        continueShoppingButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    HomeActivity.class
            );
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );
            startActivity(intent);
            finish();
        });
    }
}
