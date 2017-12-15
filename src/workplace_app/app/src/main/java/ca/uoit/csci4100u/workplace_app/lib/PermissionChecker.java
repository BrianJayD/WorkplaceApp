package ca.uoit.csci4100u.workplace_app.lib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by brianjayd on 2017-12-15.
 */

public class PermissionChecker {
    private static final int REQUEST_GPS = 0;
    private Context context;
    private Activity activity;


    public PermissionChecker(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void getPermissions() {
        int permissionsGPS = android.support.v4.content.PermissionChecker.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionsGPS != 0) {

            if (android.support.v4.content.PermissionChecker.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
                }


            }

        }
    }


}
