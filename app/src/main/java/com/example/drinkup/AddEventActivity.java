package com.example.drinkup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drinkup.objects.Event;
import com.example.drinkup.objects.EventsDictionary;
import com.example.drinkup.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class AddEventActivity extends AppCompatActivity {
    /*
    This activity is used to create a new event.
    test
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createEvent(View v) {
        /*
        This function is used to generate a new event
        -check that the user gave a name to the event.
        -generate a unique event ID
        -store everything in a Event object and send the info to the DB
         */

        EditText eventNameView=findViewById(R.id.event_name);
        String eventName=eventNameView.getText().toString();
        if (eventName.equals("")) {
            Toast.makeText(getApplicationContext(),"Please name the event",Toast.LENGTH_LONG).show();
        } else {
            double lat;
            double lon;
            //Check if possible to get location
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                lat=0.0;
                lon=0.0;
            } else {
                LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                Location net_loc = null, gps_loc = null, finalLoc = null;

                if (gps_enabled)
                    gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (network_enabled)
                    net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (gps_loc != null && net_loc != null) {
                    //smaller the number more accurate result will
                    if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                        finalLoc = net_loc;
                    else
                        finalLoc = gps_loc;
                } else {
                    if (gps_loc != null) {
                        finalLoc = gps_loc;
                    } else if (net_loc != null) {
                        finalLoc = net_loc;
                    }
                }

                if (finalLoc==null) {
                    lat=0.0;
                    lon=0.0;
                } else {
                    lat=finalLoc.getLatitude();
                    lon=finalLoc.getLongitude();
                }
            }

            //Set capital letter
            eventName=eventName.substring(0, 1).toUpperCase() + eventName.substring(1);

            //Generate unique ID
            String eventID=Long.toString(System.currentTimeMillis()) + User.getInstance().getID();

            Event eventObj=new Event(eventID, eventName, User.getInstance().getID(), 0, 1, lat, lon);

            EventsDictionary.getInstance().add(eventObj);

            JSONObject data= new JSONObject();
            try {
                data.put("functionName", "addEvent");
                data.put("eventID", eventID);
                data.put("eventName",eventName);
                data.put("userID",User.getInstance().getID());
                data.put("latitude",lat);
                data.put("longitude",lon);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Impossible generating JSON object for add event. Check logs", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            // Formulate the request and handle the response.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    RequestsManager.getInstance(this).server_url,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("<<DEBUG>>",response.toString());
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Toast.makeText(getApplicationContext(), "Some error while adding event. Check logs", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    });


            // Add a request (in this example, called stringRequest) to your RequestQueue.
            RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);

            Intent _result = new Intent();
            _result.putExtra("eventID", eventID);
            setResult(Activity.RESULT_OK, _result);
            onBackPressed();
        }
    }

    public void doNothing(View v) {
        /*
        Thsi function is used to avoid close the popup if clicked on some element
         */
    }

    public void closePopup(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        /*
        Override default transition effect when back button pressed
         */
        super.onBackPressed();
        overridePendingTransition(R.transition.fadein, R.transition.fadeout);
    }
}
