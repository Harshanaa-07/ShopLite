
package com.example.shoplite;

public class ShopLitePricing {

    public static final String[] NAMES = {
            "Wireless Headphones", "Wireless Mouse", "Keyboard",
            "T-Shirt", "Jeans", "Running Shoes",
            "Rice Pack", "Cooking Oil", "Coffee Pack",
            "Face Wash", "Moisturizer", "Shampoo"
    };

    public static final int[] ORIGINAL_PRICES = {
            1499, 699, 1299, 1999,
            999, 1299, 499, 199,
            399, 249, 499, 349
    };

    public static final int[] SALE_PRICES = {
            999, 499, 899, 1499,
            699, 899, 349, 149,
            279, 179, 349, 249
    };

    // Keep this consistent with FlashSaleActivity.
    public static final boolean[] ON_SALE = {
            true, true, true,
            false, true, false,
            true, false, false,
            true, false, true
    };

    public static final int[] IMAGES = {
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

    public static int getIndex(String name) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public static int getPrice(String name) {
        int index = getIndex(name);

        if (index == -1) {
            return 0;
        }

        return ON_SALE[index]
                ? SALE_PRICES[index]
                : ORIGINAL_PRICES[index];
    }

    public static int getImage(String name) {
        int index = getIndex(name);

        if (index == -1) {
            return R.drawable.img;
        }

        return IMAGES[index];
    }

    public static boolean isOnSale(String name) {
        int index = getIndex(name);
        return index >= 0 && ON_SALE[index];
    }
}
