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
        //request the location update thru location manager
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}