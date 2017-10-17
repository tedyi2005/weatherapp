package com.sanmol.weatherapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sanmol.weatherapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Teddy Kidanne on 10/17/2017.
 */

public class Util {
    public static String convertDate(String rawDate) {
        // Last sold date
        if (rawDate.length() > 4) {
            long unixSeconds = Long.parseLong(rawDate);
            Date date = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
            rawDate = sdf.format(date);
        }
        return rawDate;
    }

    public static String convertTemp(Double temp) {
        return (new DecimalFormat("##.##").format((temp - 273.15) * 9 / 5 + 32));
    }

    public static String convertKm(int distance) {
        return "" + (distance * 0.001);
    }

    public static String convertTime(String rawTime) {
        // Last sold date
        if (rawTime.length() > 4) {
            long unixSeconds = Long.parseLong(rawTime);
            Date date = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a"); // the format of your date

            rawTime = sdf.format(date);
        }
        return rawTime;
    }

    public static double convertFeelsLike(Double temp, int hum,Double wind) {
        Double  feelsLike = (37-((37-temp)/(0.68-0.0014*hum+(1/(1.76+1.4*Math.pow((wind/3.6), 0.75)))))-0.29*temp*(1-(hum/100)));

        return feelsLike;
    }

    public static int setIcon(String icon) {
        int selectedIcon = 0;
        if (icon.equals("01d")) {
            selectedIcon = R.drawable.zeroone;
        } else if (icon.equals("02d")) {
            selectedIcon = R.drawable.zerotwod;
        } else if (icon.equals("03d")) {
            selectedIcon = R.drawable.zerothreed;
        } else if (icon.equals("04d")) {
            selectedIcon = R.drawable.zerofourd;
        } else if (icon.equals("09d")) {
            selectedIcon = R.drawable.zeronined;
        } else if (icon.equals("10d")) {
            selectedIcon = R.drawable.tend;
        } else if (icon.equals("11d")) {
            selectedIcon = R.drawable.elevand;
        } else if (icon.equals("13d")) {
            selectedIcon = R.drawable.thirteend;
        } else if (icon.equals("50d")) {
            selectedIcon = R.drawable.fiftyd;
        } else if (icon.equals("01n")) {
            selectedIcon = R.drawable.zeroonen;
        } else if (icon.equals("02n")) {
            selectedIcon = R.drawable.zerotwon;
        } else if (icon.equals("03n")) {
            selectedIcon = R.drawable.zerothreen;
        } else if (icon.equals("04n")) {
            selectedIcon = R.drawable.zerofourn;
        } else if (icon.equals("09n")) {
            selectedIcon = R.drawable.zeroninen;
        } else if (icon.equals("10n")) {
            selectedIcon = R.drawable.tenn;
        } else if (icon.equals("11n")) {
            selectedIcon = R.drawable.elevann;
        } else if (icon.equals("13n")) {
            selectedIcon = R.drawable.thirteenn;
        } else if (icon.equals("50n")) {
            selectedIcon = R.drawable.fiftyn;
        }
        return selectedIcon;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
