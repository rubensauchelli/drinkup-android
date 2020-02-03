package com.example.drinkup;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

class RequestsManager {
    /*
    This class is in charge of generating and parsing request to DB.

    https://developer.android.com/training/volley/simple.html
    RequestQueue manages the underlying processes (threads) for networking operations like
    reading and writing to cache and parsing resposnses.

    RequestQueue takes as parameters a Request objects which contains the target URL, request type: (GET, POST)
    and eventually parameters.

    So, each communication with the server needs to instantiate and fill a Request object which is goign
    to be passes to RequestQueue
     */
    private static RequestsManager instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    public String server_url;

    public static synchronized RequestsManager getInstance(Context context) {
        /*
        Called from different activities when required to send request to server.
        Return the singleton object which then you can create the Requestd object
        and send the request by invoking addToRequestQueue()
         */
        if (instance == null) {
            instance = new RequestsManager(context);
        }
        return instance;
    }

    private RequestsManager(Context context) {
        server_url="http://drinkup-db.herokuapp.com/main.php";
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
