package base.droidtool.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import anindya.sample.smackchat.R;


public class LocationTracker extends Service implements LocationListener {
    //declaring Context variable
    private final Context con;
    //flag for gps
    boolean isGPSOn=false;
    //flag for network location
    boolean isNetWorkEnabled=false;
    //flag to get location
    boolean isLocationEnabled=false;
    //minimum distance to request for location update
    private static final long MIN_DISTANCE_TO_REQUEST_LOCATION=1; // in meters
    // minimum time to request location updates
    private static final long MIN_TIME_FOR_UPDATES=1000*1; // 1 sec
    //location
    Location location;
    //latitude and longitude
    double latitude,longitude;
    //Declaring a LocationManager
    LocationManager locationManager;

    public LocationTracker(Context context)
    {
        this.con=context;
        checkIfLocationAvailable();
    }

    public Location checkIfLocationAvailable()
    {
        try
        {
            locationManager=(LocationManager)con.getSystemService(LOCATION_SERVICE);
            //check for gps availability
            isGPSOn=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //check for network availablity
            isNetWorkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSOn && !isNetWorkEnabled)
            {
                isLocationEnabled=false;
                // no location provider is available show toast to user
                Toast.makeText(con, con.getString(R.string.no_location),Toast.LENGTH_SHORT).show();
            }
            else {
                isLocationEnabled=true;
                // if network location is available request location update
                if(isNetWorkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_FOR_UPDATES,MIN_DISTANCE_TO_REQUEST_LOCATION,this);
                    if(locationManager!=null)
                    {
                        location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location!=null)
                        {
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                        }
                    }
                }
                if(isGPSOn)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_FOR_UPDATES,MIN_DISTANCE_TO_REQUEST_LOCATION,this);
                    if(locationManager!=null)
                    {
                        location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location!=null)
                        {
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
                        }
                    }
                }
            }
        }catch (Exception e)
        {
        }
        return location;
    }

    // call this to getLatitude
    public double getLatitude()
    {
        if(location!=null)
        {
            latitude=location.getLatitude();
        }
        return latitude;
    }

    //call this to getLongitude
    public double getLongitude()
    {
        if(location!=null)
        {
            longitude=location.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

