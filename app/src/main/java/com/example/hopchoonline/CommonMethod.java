package com.example.hopchoonline;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class CommonMethod {
    public static String getCityFromAddress(Context context, String addressString) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(addressString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            String city = address.getAdminArea();
            return city;
        } else {
            return null;
        }
    }

//    public static Location getLocationFromString(String locationString) {
//        String[] coordinates = locationString.split(",");
//        if (coordinates.length == 2) {
//            double latitude = Double.parseDouble(coordinates[0]);
//            double longitude = Double.parseDouble(coordinates[1]);
//            Location location = new Location("");
//            location.setLatitude(latitude);
//            location.setLongitude(longitude);
//            return location;
//        } else {
//            return null;
//        }
//    }

    public static Location getLocationFromString(Context context, String addressString) {
        Geocoder geocoder = new Geocoder(context);
        Location location = null;

        try {
            List<Address> addresses = geocoder.getFromLocationName(addressString, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                location = new Location("");
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return location;
    }

    public static float getDistance(Location location1, Location location2) {
        return location1.distanceTo(location2);
    }

    public static boolean isValidAddress(String address, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                if (latitude != 0 || longitude != 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ
        }
        return false;
    }

    public static long distanceBetweenDays(String expiredDate) {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();
        LocalDate expired = LocalDate.parse(expiredDate);

        return ChronoUnit.DAYS.between(currentDate, expired);
    }
}
