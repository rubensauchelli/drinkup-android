package com.example.drinkup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drinkup.RecyclerViewAdapters.RecyclerViewPollAdapter;
import com.example.drinkup.objects.EventsDictionary;
import com.example.drinkup.objects.Poll;
import com.example.drinkup.objects.PollsDictionary;
import com.example.drinkup.objects.PollsEventManager;
import com.example.drinkup.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;

public class EventActivity extends AppCompatActivity {
    //vars to handle the swap
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    String eventID;
    RecyclerView recyclerView;
    RecyclerView recyclerViewReleasedPolls;
    RecyclerView recyclerViewMyPolls;
    TextView eventName;

    SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent i = getIntent();
        eventID=i.getStringExtra("eventID");

        getPollsAvailable();
        getNumberParticipants();

        //Set event title
        eventName = findViewById(R.id.eventName);
        String eventNameStr=EventsDictionary.getInstance().get(eventID).eventName;
        eventName.setText(eventNameStr.substring(0, 1).toUpperCase() + eventNameStr.substring(1));

        //Initialize recyclerViews
        recyclerView=findViewById(R.id.recycler_view_open_polls);
        recyclerViewReleasedPolls=findViewById(R.id.recycler_view_released_polls);
        recyclerViewMyPolls=findViewById(R.id.recycler_view_my_polls);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerViewReleasedPolls.setNestedScrollingEnabled(false);
        recyclerViewMyPolls.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReleasedPolls.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyPolls.setLayoutManager(new LinearLayoutManager(this));

        //Setup refresh recycler views when swipe
        mRefreshLayout=findViewById(R.id.swiperefresh);

        /*Check this website on how to update the recycler view: https://guides.codepath.com/android/implementing-pull-to-refresh-guide*/
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(EventActivity.this, "Refreshed poll list", Toast.LENGTH_LONG).show();

                getPollsAvailable();
                getNumberParticipants();

                //Stop the refreshing thing
                mRefreshLayout.setRefreshing(false);
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        //Refresh the view every time the activity is viewed
        populateRecyclerViews();
    }

    public void ShowAddPoll(View v) {
        /*
        This function opens the activity to add a new poll
         */
        Intent intent = new Intent(this, AddPollActivity.class);
        intent.putExtra("eventID", eventID);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateRecyclerViews() {
        RecyclerViewPollAdapter adapter_open=new RecyclerViewPollAdapter(PollsEventManager.getInstance().IDOpenPolls, PollsDictionary.getInstance(), false, this);
        RecyclerViewPollAdapter adapter_released=new RecyclerViewPollAdapter(PollsEventManager.getInstance().IDReleasedPolls, PollsDictionary.getInstance(), false,this);
        RecyclerViewPollAdapter adapter_my=new RecyclerViewPollAdapter(PollsEventManager.getInstance().IDMyPolls, PollsDictionary.getInstance(), true,this);

        recyclerView.setAdapter(adapter_open);
        recyclerViewReleasedPolls.setAdapter(adapter_released);
        recyclerViewMyPolls.setAdapter(adapter_my);
    }

    private void getPollsAvailable() {
        /*
        This function is in charge of getting the available polls
        and populate the recycle view.
         */

        PollsEventManager.getInstance().clear();
        PollsDictionary.getInstance().clear();

        JSONObject data= new JSONObject();
        try {
            data.put("functionName", "getAllPolls");
            data.put("userID", User.getInstance().getID());
            data.put("eventID", eventID);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Impossible generating JSON object for retrieving polls. Check logs", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Formulate the request and handle the response.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RequestsManager.getInstance(this).server_url,
                data,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<?> keys = response.keys();

                        while(keys.hasNext() ) {
                            String key = (String)keys.next();
                            try {
                                JSONObject poll = new JSONObject(response.get(key).toString());

                                Hashtable<Character, String> optionsDict= new Hashtable<Character, String>();
                                optionsDict.put('a', poll.getString("answer_A"));
                                optionsDict.put('b', poll.getString("answer_B"));
                                optionsDict.put('c', poll.getString("answer_C"));
                                optionsDict.put('d', poll.getString("answer_D"));
                                Poll poll_obj=new Poll(poll.getString("poll_ID"), poll.getString("owner_ID"), poll.getString("question"), optionsDict, poll.getString("answer").charAt(0));

                                if (!poll.getString("user_answer").equals("null")) {
                                    poll_obj.playerAnswer=poll.getString("user_answer").charAt(0);
                                }

                                if (poll.getString("isOpen").equals("0")) {
                                    poll_obj.isReleased=true;
                                }

                                PollsDictionary.getInstance().addPoll(poll_obj.pollID, poll_obj);
                                PollsEventManager.getInstance().addPoll(poll_obj, User.getInstance().getID());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //once fetched and parsed the data from DB. Update the recycler to show the polls
                        populateRecyclerViews();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getApplicationContext(), "Some error retrieving polls. Check logs", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });


        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void getNumberParticipants() {
        /*
        This function is in charge of getting the number of participants of the given poll
         */
        JSONObject data= new JSONObject();
        try {
            data.put("functionName", "countEventParticipants");
            data.put("eventID", eventID);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Impossible fetching number participants. Check logs", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Formulate the request and handle the response.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RequestsManager.getInstance(this).server_url,
                data,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        TextView numUsersEventText=findViewById(R.id.numberOfParticipants);
                        try {
                            numUsersEventText.setText(response.getString("num_participants"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getApplicationContext(), "Some error fetching number participants. Check logs", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });


        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
