/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package learn2crack.utilities;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.example.android.contactslist.ui.ContactDetailActivity;
//import com.example.android.contactslist.ui.ContactsListActivity;

/**
 * This class contains static utility methods.
 */
public class Utils {

    // Prevents instantiation.
    private Utils() {}

    /**
     * Enables strict mode. This should only be called when debugging the application and is useful
     * for finding some potential bugs or best practice violations.
     */
    /*@TargetApi(11)
    public static void enableStrictMode() {
        // Strict mode is only available on gingerbread or later
        if (Utils.hasGingerbread()) {

            // Enable all thread strict mode policies
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            // Enable all VM strict mode policies
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            // Honeycomb introduced some additional strict mode features
            if (Utils.hasHoneycomb()) {
                // Flash screen when thread policy is violated
                threadPolicyBuilder.penaltyFlashScreen();
                // For each activity class, set an instance limit of 1. Any more instances and
                // there could be a memory leak.
                vmPolicyBuilder
                        .setClassInstanceLimit(ContactsListActivity.class, 1)
                        .setClassInstanceLimit(ContactDetailActivity.class, 1);
            }

            // Use builders to enable strict mode policies
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }*/

    /**
     * Uses static final constants to detect if the device's platform version is Gingerbread or
     * later.
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
     * later.
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Uses static final constants to detect if the device's platform version is ICS or
     * later.
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static ArrayList<Integer> getSelectedOpetions(String optionsArray){
        String[] items = optionsArray.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ","").split(",");

        ArrayList<Integer> results = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            try {
                results.add(i, Integer.parseInt(items[i]));
            } catch (NumberFormatException nfe) {};
        }
        return results;
    }

    public static String DateFormat(String inputDate){
        String mytime=  inputDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mytime);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate.toString();
//        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String finalDate = timeFormat.format(myDate);
//
//        System.out.println(finalDate);
//        return finalDate;

    }

}
