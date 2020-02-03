package com.example.drinkup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drinkup.RecyclerViewAdapters.RecyclerViewEventsAdapter;
import com.example.drinkup.objects.Event;
import com.example.drinkup.objects.EventsDictionary;
import com.example.drinkup.objects.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class EventsActivity extends AppCompatActivity {
    /*
     */

    GoogleSignInClient mGoogleSignInClient;

    RecyclerView recyclerView;
    RecyclerViewEventsAdapter events_adapter;

    SwipeRefreshLayout mRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 10);
        }

        Event.geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

        //LOGIN
        this.login();

        //Run splash screen
        //https://www.youtube.com/watch?v=jXtof6OUtcE

        //Setup recycler view
        recyclerView=findViewById(R.id.recycler_view_events);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRefreshLayout=findViewById(R.id.swipe_refresh_events);

        /*Setup lister triggered to refresh polls*/
        /*Check this website on how to update the recycler view: https://guides.codepath.com/android/implementing-pull-to-refresh-guide*/
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(EventsActivity.this, "Refreshed event list", Toast.LENGTH_LONG).show();

                getEventsAvailable();

                //Stop the refreshing thing
                mRefreshLayout.setRefreshing(false);
            }

        });

    }

    public void getEventsAvailable() {
        EventsDictionary.getInstance().clear();

        JSONObject data= new JSONObject();
        try {
            data.put("functionName", "getEvents");
            data.put("userID", User.getInstance().getID());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Impossible generating JSON object for retrieving events. Check logs", Toast.LENGTH_LONG).show();
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
                        Iterator<?> keys = response.keys();

                        while(keys.hasNext() ) {
                            String key = (String)keys.next();
                            try {
                                JSONObject event = new JSONObject(response.get(key).toString());
                                Event eventObj=new Event(event.get("id").toString(), event.get("name").toString(), event.get("owner_id").toString(), Integer.parseInt(event.get("num_polls").toString()), Integer.parseInt(event.get("num_participant").toString()), Double.valueOf(event.get("latitude").toString()), Double.valueOf(event.get("longitude").toString()));
                                EventsDictionary.getInstance().add(eventObj);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        populateEventList();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Some error retrieving events. Check INTERNET CONNECTION", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });


        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void generateEvent(View v) {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivityForResult(intent, 2);
    }

    public void addEvent(View v) {
        Intent intent = new Intent(this, ScanQREventActivity.class);
        startActivityForResult(intent, 3);
    }

    private void populateEventList() {
        events_adapter=new RecyclerViewEventsAdapter(EventsDictionary.getInstance(), this);
        recyclerView.setAdapter(events_adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If user logging-in. Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);

                User.getInstance().setID(account.getId());
                getEventsAvailable();

            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Failed to signIn. Check logs", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        //If AddEventActivity closed
        else if (requestCode == 2) {
            //check if poll created
            switch (resultCode) {
                case Activity.RESULT_OK:
                    this.populateEventList();
                    break;
                default:
                    break;
            }
        } else if (requestCode == 3) {
            //check if poll created
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String eventID=data.getStringExtra("eventID");

                    //add user to event
                    JSONObject dataObj= new JSONObject();
                    try {
                        dataObj.put("functionName", "addUserToEvent");
                        dataObj.put("eventID", eventID);
                        dataObj.put("userID", User.getInstance().getID());
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Impossible adding user to event. Check logs", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            RequestsManager.getInstance(this).server_url,
                            dataObj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Some error adding user to event. Check logs", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                }
                            });

                    // Add a request (in this example, called stringRequest) to your RequestQueue.
                    RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);


                    //Get event info, create event obj, add to the list and refresh the recyclerview
                    dataObj= new JSONObject();
                    try {
                        dataObj.put("functionName", "getEventInfo");
                        dataObj.put("eventID", eventID);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Impossible retrieving event info. Check logs", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            RequestsManager.getInstance(this).server_url,
                            dataObj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Iterator<?> keys = response.keys();

                                    while(keys.hasNext() ) {
                                        String key = (String) keys.next();
                                        try {
                                            JSONObject event = new JSONObject(response.get(key).toString());
                                            Event eventObj=new Event(event.get("id").toString(), event.get("name").toString(), event.get("owner_id").toString(), Integer.parseInt(event.get("num_polls").toString()), Integer.parseInt(event.get("num_participant").toString()), Double.valueOf(event.get("latitude").toString()), Double.valueOf(event.get("longitude").toString()));
                                            EventsDictionary.getInstance().add(eventObj);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    populateEventList();
                                }
                            },
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Some error retrieving event info. Check logs", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                }
                            });

                    // Add a request (in this example, called stringRequest) to your RequestQueue.
                    RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);

                    break;
                default:
                    break;
            }
        }
    }

    private void login() {
        //https://developers.google.com/identity/sign-in/android/start-integrating
        // Configure sign-in request. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null) {
            Toast.makeText(getApplicationContext(), "User already logged", Toast.LENGTH_LONG).show();

            User.getInstance().setID(account.getId());
            getEventsAvailable();

        } else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        }
    }

    public void logout(View v) {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                }
            });
    }
}
