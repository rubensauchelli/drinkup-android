package com.example.drinkup.objects;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.drinkup.EventsActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Event {

    public static Geocoder geocoder;
    public String ID;
    public String eventName;
    public String ownerID;
    public Integer num_participants;
    public Integer num_polls;
    public double latitute;
    public double longitude;
    public String location="Earth";

    public Event(String eventID, String eventName, String user_id, Integer n_polls, Integer n_participants, double lat, double lon) {

        this.ID=eventID;
        this.eventName=eventName;
        this.ownerID=user_id;
        this.num_polls=n_polls;
        this.num_participants=n_participants;
        this.latitute=lat;
        this.longitude=lon;

        Log.d("<<DEBUG>>", String.valueOf(lat) + " | " + String.valueOf(lon));

        try {
            List<Address> addresses  = geocoder.getFromLocation(lat, lon, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryName();

            Log.d("<<DEBUG>>", address + " | "+city+ " | "+state+ " | "+zip+ " | "+country);
            if (city!=null) {
                this.location=city;
            } else if (state!=null) {
                this.location=state;
            } else {
                this.location=country;
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.location="Earth";
        } catch (IndexOutOfBoundsException e) {
            //NO found location, set to earth
            this.location="Earth";
        } finally {
            Log.d("<<DEBUG>>", this.location);
            Log.d("<<DEBUG>>", "----------------");
        }



    }
}
