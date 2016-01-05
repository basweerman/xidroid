package xi.xidroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by basweerman on 11/28/15.
 */
public class XiDroidGPS {

    private LocationManager locationManager;
    private String bestProvider;
    private Context context;


    public XiDroidGPS(Context c) {
        locationManager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        context = c;
        bestProvider = this.getBestProvider();
    }

    public List<String> getAvailableProviders() {
        List<String> providers = locationManager.getAllProviders();
        return providers;
        //for (String provider : providers) {
        //    printProvider(provider);
        //}
    }

    public String getBestProvider() {
        Criteria criteria = new Criteria();
        return locationManager.getBestProvider(criteria, false);
    }

    public boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isNetworkEnabled(){
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public Location getLocation() {
        return getLocation(bestProvider);
    }


    public Location getLocation(String provider) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return locationManager.getLastKnownLocation(provider);
    }


    public String getLocationAsJson(Location location){
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("primkey", ((XiDroidApplication) context.getApplicationContext()).androidId);
        parameters.put("latitude", "" + location.getLatitude());
        parameters.put("longitude", "" + location.getLongitude());
        parameters.put("altitude", "" + location.getAltitude());
        parameters.put("accuracy", "" + location.getAccuracy());
        parameters.put("bearing", "" + location.getBearing());
        parameters.put("provider", "" + location.getProvider());
        parameters.put("ts", XiDroidFunctions.showDateTimeDatabase());

        Gson gson = new Gson();
        return gson.toJson(parameters);
    }

    public void storeLocation(boolean useGPS, boolean useNetwork){
        Location currentLocation = getLocation(useGPS, useNetwork);
        if (currentLocation != null){
            String locationAsJson = this.getLocationAsJson(currentLocation);
            String query = "INSERT INTO location (primkey, record) VALUES ('" + ((XiDroidApplication)context.getApplicationContext()).androidId + "', '" + locationAsJson + "')";
            ((XiDroidApplication) context.getApplicationContext()).communication.runLocalQuery(query);
        }

    }

    public Location getLocation(boolean useGPS, boolean useNetwork) {
        Location currentLocation = null;
        if (useGPS && this.isGPSEnabled()) {
            currentLocation = getLocation(LocationManager.GPS_PROVIDER);
        }
        if (currentLocation == null && useNetwork && this.isNetworkEnabled()){
            currentLocation = getLocation(LocationManager.NETWORK_PROVIDER);
        }
        return currentLocation;
    }

    public String getLocationAsString(boolean useGPS, boolean useNetwork) {
        Location currentLocation = getLocation(useGPS, useNetwork);
        if (currentLocation != null){
            return "lat:" + currentLocation.getLatitude() + " lon:" + currentLocation.getLongitude();
        }
        else {
            return "current location unknown";
        }

    }

}
