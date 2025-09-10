package com.example.hopchoonline;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonConverter {
    public static String convertCurrencyVND(int amount) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);

        // Format the amount as VND currency
        String formattedAmount = nf.format(amount);

        // Remove the currency symbol and replace the decimal separator with a comma
        formattedAmount = formattedAmount.substring(0, formattedAmount.length() - 2)  // Remove the currency symbol
                .concat(" đ");  // Add the VND symbol

        return formattedAmount;
    }

    public static String convertDateVN(String originalDateString) {
        // Định dạng chuỗi ngày thành dd/MM/yyyy
        SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = originalDateFormat.parse(originalDateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String targetDateString = targetDateFormat.format(date);
        return targetDateString;
    }

    public static String convertDateUS(String originalDateString) {
        // Định dạng chuỗi ngày thành dd/MM/yyyy
        SimpleDateFormat originalDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalDateFormat.parse(originalDateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String targetDateString = targetDateFormat.format(date);
        return targetDateString;
    }
}
