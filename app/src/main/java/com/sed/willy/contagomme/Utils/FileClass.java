package com.sed.willy.contagomme.Utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by federico.marchesi on 06/02/2017.
 */

public class FileClass {
    private static final String TAG = FileClass.class.getName();
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public static String createFile(String fileName, String fileContent, String extension) {

        try {

//            fileName = "fede";
            if (!fileName.endsWith(".")) fileName += ".";
            fileName += extension.toLowerCase();
            // this will create a new name everytime and unique
            File root = new File(Environment.getExternalStorageDirectory(), "ContaGomme");
//            File root = new File(getExternalFilesDir(null), fileName);
            // if external memory exists and folder with name Notes
            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
            }

            File filepath = new File(root, fileName);  // file path to save
            FileWriter writer = new FileWriter(filepath);
            writer.append(fileContent);
            writer.flush();
            writer.close();

            return filepath.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();

        }
        return "";
    }

    public static void checkForArchiveAccess(AppCompatActivity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

//                Toast.makeText(this, getResources().getString(R.string.explain_why_write_storage),
//                        Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);

                // REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public static void changeSharedPreference(Context context, String prefName, Object value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPref.edit();
        if (value instanceof Boolean) {
            edit.putBoolean(prefName, (Boolean) value);
        } else if (value instanceof Integer) {
            edit.putInt(prefName, (Integer) value);
        } else if (value instanceof String) {
            edit.putString(prefName, value.toString());
        }
        edit.apply();
    }

}
