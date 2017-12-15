package ca.uoit.csci4100u.workplace_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager manager;
    private LocationListener locationListener;
    private String dest, origin;
    protected double oLAT, oLONG, dLAT, dLONG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        dest = intent.getStringExtra("location");
        //get the location service
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //request the location update through location manager

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    oLAT = location.getLatitude();
                    oLONG = location.getLongitude();
                    Address address = forwardGeocode(dest);
                    if(address != null){
                        dLAT = address.getLatitude();
                        dLONG = address.getLongitude();
                    }
                    LatLng destination = new LatLng(dLAT, dLONG);
                    LatLng latLng = new LatLng(oLAT, oLONG);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> list = geocoder.getFromLocation(oLAT, oLONG, 1);
                        origin = list.get(0).getAddressLine(0);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                        mMap.addMarker(new MarkerOptions().position(destination).title("Your Workplace"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.2f));
                        mMap.addPolygon(new PolygonOptions()
                                .add(latLng, destination)
                                .strokeColor(Color.BLUE));
                        mMap.setTrafficEnabled(true);
                        mMap.setBuildingsEnabled(true);
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }
                @Override
                public void onProviderEnabled(String s) {
                }
                @Override
                public void onProviderDisabled(String s) {
                }
            });
        }else if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    oLAT = location.getLatitude();
                    oLONG = location.getLongitude();
                    Address address = forwardGeocode(dest);
                    if(address != null){
                        dLAT = address.getLatitude();
                        dLONG = address.getLongitude();
                    }
                    LatLng destination = new LatLng(dLAT, dLONG);
                    LatLng latLng = new LatLng(oLAT, oLONG);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> list = geocoder.getFromLocation(oLAT, oLONG, 1);
                        origin = list.get(0).getAddressLine(0);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                        mMap.addMarker(new MarkerOptions().position(destination).title("Your Workplace"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.2f));
                        mMap.addPolygon(new PolygonOptions()
                                .add(latLng, destination)
                                .strokeColor(Color.BLUE));

                        mMap.setTrafficEnabled(true);
                        mMap.setBuildingsEnabled(true);
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                        if (convertToMiles(latLng.latitude, destination.latitude, latLng.longitude, destination.longitude) <= 0.1) {
                            Log.i("GPS", "FOUND YOU");
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }
                @Override
                public void onProviderEnabled(String s) {
                }
                @Override
                public void onProviderDisabled(String s) {
                }
            });
        }
        //manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    public Address forwardGeocode(String locationName) {
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> results = geocoder.getFromLocationName(locationName, 1);
                if (results.size() > 0) {
                    return results.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     *  Conversion taken from https://stackoverflow.com/questions/19056075/how-to-know-if-an-android-device-is-near-an-address-google-maps-api
     *
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    private double convertToMiles(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometers

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}