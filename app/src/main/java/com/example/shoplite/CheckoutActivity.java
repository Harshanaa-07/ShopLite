
package com.example.shoplite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutActivity extends Activity {

    private EditText customerName;
    private EditText phoneNumber;
    private EditText address;
    private EditText city;
    private EditText state;
    private EditText pinCode;
    private EditText upiId;

    private RadioGroup paymentOptions;

    private TextView subtotalText;
    private TextView deliveryText;
    private TextView totalText;

    private int subtotal;
    private int deliveryCharge;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        customerName = findViewById(R.id.checkoutCustomerName);
        phoneNumber = findViewById(R.id.checkoutPhone);
        address = findViewById(R.id.checkoutAddress);
        city = findViewById(R.id.checkoutCity);
        state = findViewById(R.id.checkoutState);
        pinCode = findViewById(R.id.checkoutPinCode);
        upiId = findViewById(R.id.checkoutUpiId);

        paymentOptions = findViewById(R.id.paymentOptions);

        subtotalText = findViewById(R.id.checkoutSubtotal);
        deliveryText = findViewById(R.id.checkoutDelivery);
        totalText = findViewById(R.id.checkoutTotal);

        Button backButton = findViewById(R.id.checkoutBackButton);
        Button placeOrderButton = findViewById(R.id.placeOrderButton);

        backButton.setOnClickListener(v -> finish());

        // Show or hide the UPI ID field based on payment selection.
        paymentOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.paymentUPI) {
                upiId.setVisibility(View.VISIBLE);
            } else {
                upiId.setVisibility(View.GONE);
                upiId.setText("");
                upiId.setError(null);
            }
        });

        calculateTotal();

        placeOrderButton.setOnClickListener(v -> placeOrder());
    }

    private void calculateTotal() {
        subtotal = 0;

        for (int i = 0; i < CartActivity.cartNames.size(); i++) {
            subtotal += CartActivity.cartPrices.get(i)
                    * CartActivity.cartQuantities.get(i);
        }

        // Below ₹499: ₹80 delivery
        // ₹499 to ₹998: ₹40 delivery
        // ₹999 or more: free delivery
        if (subtotal == 0 || subtotal >= 999) {
            deliveryCharge = 0;
        } else if (subtotal >= 499) {
            deliveryCharge = 40;
        } else {
            deliveryCharge = 80;
        }

        total = subtotal + deliveryCharge;

        subtotalText.setText("Subtotal: ₹" + subtotal);

        if (deliveryCharge == 0) {
            deliveryText.setText("Delivery charge: FREE");
        } else {
            deliveryText.setText("Delivery charge: ₹" + deliveryCharge);
        }

        totalText.setText("Total payable: ₹" + total);
    }

    private boolean validateRequiredField(
            EditText field, String errorMessage) {

        String value = field.getText().toString().trim();

        if (value.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }

        field.setError(null);
        return true;
    }

    private void placeOrder() {

        if (CartActivity.cartNames.isEmpty()) {
            Toast.makeText(
                    this,
                    "Your cart is empty",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Validate all required delivery fields.
        if (!validateRequiredField(customerName, "Name is required")) {
            return;
        }

        if (!validateRequiredField(phoneNumber, "Phone number is required")) {
            return;
        }

        if (!validateRequiredField(address, "Address is required")) {
            return;
        }

        if (!validateRequiredField(city, "City is required")) {
            return;
        }

        if (!validateRequiredField(state, "State is required")) {
            return;
        }

        if (!validateRequiredField(pinCode, "PIN code is required")) {
            return;
        }

        String name = customerName.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String street = address.getText().toString().trim();
        String cityName = city.getText().toString().trim();
        String stateName = state.getText().toString().trim();
        String pin = pinCode.getText().toString().trim();

        if (!phone.matches("[0-9]{10}")) {
            phoneNumber.setError(
                    "Enter a valid 10-digit mobile number"
            );
            phoneNumber.requestFocus();
            return;
        }

        if (!pin.matches("[0-9]{6}")) {
            pinCode.setError("Enter a valid 6-digit PIN code");
            pinCode.requestFocus();
            return;
        }

        int selectedPayment = paymentOptions.getCheckedRadioButtonId();

        if (selectedPayment == -1) {
            Toast.makeText(
                    this,
                    "Please select a payment method",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String paymentMethod;

        if (selectedPayment == R.id.paymentCOD) {

            paymentMethod = "Cash on Delivery";

        } else if (selectedPayment == R.id.paymentUPI) {

            if (!validateRequiredField(upiId, "UPI ID is required")) {
                return;
            }

            String enteredUpiId =
                    upiId.getText().toString().trim();

            // Basic format validation, not a real payment verification.
            if (!enteredUpiId.matches(
                    "^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+$")) {

                upiId.setError(
                        "Enter a valid UPI ID, e.g. name@bank"
                );
                upiId.requestFocus();
                return;
            }

            paymentMethod = "UPI (demo): " + enteredUpiId;

        } else {

            Toast.makeText(
                    this,
                    "Please select a valid payment method",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Calculate the final amount before saving the order.
        calculateTotal();

        String fullAddress = street + ", "
                + cityName + ", "
                + stateName + " - " + pin;

        String orderId = "SL" + System.currentTimeMillis();

        // Save order details before clearing the cart.
        OrderHistoryActivity.saveOrder(
                this,
                orderId,
                name,
                phone,
                fullAddress,
                paymentMethod,
                total
        );

        Intent intent = new Intent(
                CheckoutActivity.this,
                OrderConfirmationActivity.class
        );

        intent.putExtra("orderId", orderId);
        intent.putExtra("total", total);

        // Clear the cart after recording the order.
        CartActivity.cartNames.clear();
        CartActivity.cartPrices.clear();
        CartActivity.cartQuantities.clear();
        CartActivity.cartImages.clear();

        startActivity(intent);
        finish();
    }
}
