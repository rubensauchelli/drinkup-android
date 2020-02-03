package com.example.drinkup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drinkup.objects.Poll;
import com.example.drinkup.objects.PollsDictionary;
import com.example.drinkup.objects.PollsEventManager;
import com.example.drinkup.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PollManager extends AppCompatActivity {
    /*
    PollManager activity shows all the information regarding a selected poll.
    Question and answers.

    If poll owner, show option of releasing and deleting poll
    If user, let select answer
     */

    //vars to handle the swap
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    Poll myPoll; //contains the poll informations retrieved from the poll dictionary
    String playerID;
    boolean isPollOwner;

    //Views to fill with the info in the pollObject
    TextView questionView;
    TextView answerAView;
    TextView answerBView;
    TextView answerCView;
    TextView answerDView;

    //progress bars
    RoundCornerProgressBar progressA;
    RoundCornerProgressBar progressB;
    RoundCornerProgressBar progressC;
    RoundCornerProgressBar progressD;

    Button releaseAnswerBtn;
    Button deletePollBtn;

//for progress animation
    private Handler mHandler = new Handler();



    //animation for bar progress

    float currentProgress;
    float speed;
    //animation for bar progress

    public void animateProgress(final float target,  final RoundCornerProgressBar progressBar) {

        progressBar.setProgress(0);

        currentProgress=0;
        speed = target /100;



        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBar.getProgress() < target){


                    currentProgress = currentProgress + speed;
                    android.os.SystemClock.sleep(20);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(currentProgress);
                        }
                    });
                }

            }
        }).start();
    }

    //checck if text view is empty
    public void checkIfEmpty(TextView textView) {
        if (textView.getText().toString().isEmpty()){
            //Toast.makeText(getApplicationContext(), "text view should be gone", Toast.LENGTH_SHORT).show();
            textView.setVisibility(View.GONE);
        }
    }
    public void checkIfProgressExists(RoundCornerProgressBar progressBar, TextView textView) {
        if (textView.getText().toString().isEmpty()){
            //Toast.makeText(getApplicationContext(), "text view should be gone", Toast.LENGTH_SHORT).show();
            textView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        else{
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    //progress bar changes
    public void showCorrectProgressBar(TextView textView, RoundCornerProgressBar progressBar, float percenatge) {

        checkIfProgressExists(progressBar,textView);
        textView.setBackgroundResource(0);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        progressBar.setProgressBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenWeak));
        progressBar.setProgressColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        //progressBar.setProgress(percenatge);
        animateProgress(percenatge,progressBar);

    }

    public void showIncorrectProgressBar(TextView textView, RoundCornerProgressBar progressBar, float percenatge) {
        checkIfProgressExists(progressBar,textView);
        textView.setBackgroundResource(0);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        progressBar.setProgressBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.redWeak));
        progressBar.setProgressColor(ContextCompat.getColor(getApplicationContext(), R.color.red));

        animateProgress(percenatge,progressBar);
    }

    public void showProgressBar(TextView textView, RoundCornerProgressBar progressBar, float percenatge) {
        checkIfProgressExists(progressBar,textView);
        textView.setBackgroundResource(0);
        textView.setTextColor(Color.parseColor("#000000"));
        progressBar.setProgressBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.weakWhite));
        progressBar.setProgressColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        animateProgress(percenatge,progressBar);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selectAnswerButton(TextView textView) {
        textView.setBackgroundResource(R.drawable.answer_default_background);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void correctAnswerButton(TextView textView) {
        //button styles and colour
        textView.setBackgroundResource(R.drawable.answer_correct_background);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void incorrectAnswerButton(TextView textView) {
        //button styles and colour
        textView.setBackgroundResource(R.drawable.answer_incorrect_background);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void disableReleaseBtn(Button button) {
        //button paddings to keep layout intact
        int pL = button.getPaddingLeft();
        int pT = button.getPaddingTop();
        int pR = button.getPaddingRight();
        int pB = button.getPaddingBottom();
        //button styles and colour
        button.setBackgroundResource(R.drawable.answer_button_light);
        button.setPadding(pL, pT, pR, pB);
        button.setTextColor(Color.parseColor("#65000000"));
        button.setEnabled(false);
        button.setText("Released");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_manager);

        Intent i = getIntent();
        String pollID=i.getStringExtra("poolID");

        playerID= User.getInstance().getID();
        myPoll=PollsDictionary.getInstance().getPoll(pollID);
        isPollOwner=playerID.equals(myPoll.pollOwnerID);

        questionView=findViewById(R.id.question);
        answerAView=findViewById(R.id.a);
        answerBView=findViewById(R.id.b);
        answerCView=findViewById(R.id.c);
        answerDView=findViewById(R.id.d);
        releaseAnswerBtn=findViewById(R.id.releaseAnswerBtn);
        deletePollBtn=findViewById(R.id.deletePollBtn);

        questionView.setText(myPoll.question);
        answerAView.setText(myPoll.optionsDict.get('a'));
        answerBView.setText(myPoll.optionsDict.get('b'));
        answerCView.setText(myPoll.optionsDict.get('c'));
        answerDView.setText(myPoll.optionsDict.get('d'));

        //progress bars
        progressA = (RoundCornerProgressBar) findViewById(R.id.progress_a);
        progressB = (RoundCornerProgressBar) findViewById(R.id.progress_b);
        progressC = (RoundCornerProgressBar) findViewById(R.id.progress_c);
        progressD = (RoundCornerProgressBar) findViewById(R.id.progress_d);

        checkIfEmpty(answerAView);
        checkIfEmpty(answerBView);
        checkIfEmpty(answerCView);
        checkIfEmpty(answerDView);

        //Check if required to fetch results from server
        if (myPoll.isReleased && myPoll.releasedResults==null) {
            fetchResults();
        } else if (myPoll.isReleased && myPoll.releasedResults!=null) {
            if (isPollOwner) {
                setupOwnerReleasedInterface();
            } else {
                setupUserReleasedInterface();
            }
        }

        if (myPoll.isReleased && !isPollOwner) {
           //  flushPoll();
        } else if (!myPoll.isReleased && isPollOwner) {
            setupOwnerInterface();
        } else if (!myPoll.isReleased && !isPollOwner) {
            setupUserInterface();
        }
    }

    public void flushPoll() {
        /*
        This function is called when the user has seen the result for a given poll
         */
        PollsEventManager.getInstance().removeReleasedPoll(myPoll.pollID);
        PollsDictionary.getInstance().remove(myPoll.pollID);

        JSONObject data= new JSONObject();
        try {
            data.put("functionName", "hidePoll");
            data.put("pollID", myPoll.pollID);
            data.put("playerID", playerID);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Impossible generating JSON object for hiding poll. Check logs", Toast.LENGTH_LONG).show();
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getApplicationContext(), "Some error adding option selected. Check logs", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void fetchResults() {
        /*
        This functions is called to fetch the results for the given poll
         */
        JSONObject data= new JSONObject();
        try {
            data.put("functionName", "getPollAnswersRatio");
            data.put("pollID", myPoll.pollID);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Impossible generating JSON object for fetching results. Check logs", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Formulate the request and handle the response.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RequestsManager.getInstance(this).server_url,
                data,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(JSONObject response) {
                        //percentage values for ratios
                        HashMap<Character, Float> releasedResults=new HashMap<>();
                        releasedResults.put('a', 0f);
                        releasedResults.put('b', 0f);
                        releasedResults.put('c', 0f);
                        releasedResults.put('d', 0f);
                        //Integer shift =15;

                        for (Character key : myPoll.optionsDict.keySet()) {
                            try {
                                //Float value=(Float.parseFloat(response.getString(String.valueOf(key))) + shift)*100;
                                Float value=(Float.parseFloat(response.getString(String.valueOf(key))))*100;

                                //value = (value > 100 ? 100 : value);
                                releasedResults.put(key, value);
                            } catch (Exception e) {
                                //releasedResults.put(key, shift.floatValue());
                                releasedResults.put(key, 0f);
                            }
                        }
                        myPoll.setResult(releasedResults);

                        if (isPollOwner) {
                            setupOwnerReleasedInterface();
                        } else {
                            setupUserReleasedInterface();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getApplicationContext(), "Some error fetching results. Check logs", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setupUserInterface() {
        /*Setup user interface for poll not released yet
        TODO:
         -Change button to progress bar white for selected answer*/

        //if user answered
        if (myPoll.playerAnswer!=null) {
            switch (myPoll.playerAnswer) {
                case 'a':
                    selectAnswerButton(answerAView);
                    break;
                case 'b':
                    selectAnswerButton(answerBView);
                    break;
                case 'c':
                    selectAnswerButton(answerCView);
                    break;
                case 'd':
                    selectAnswerButton(answerCView);
                    break;
            }
        }
    }

    public void setupUserReleasedInterface() {
        /*Setup user interface if poll released*/
        Intent animationIntent = new Intent(PollManager.this, PollAnimationActivity.class);

        showProgressBar(answerAView, progressA, myPoll.getResult().get('a'));
        showProgressBar(answerBView, progressB, myPoll.getResult().get('b'));
        showProgressBar(answerCView, progressC, myPoll.getResult().get('c'));
        showProgressBar(answerDView, progressD, myPoll.getResult().get('d'));



        //Show correct option
        switch (myPoll.correctOption) {
            case 'a':
                showCorrectProgressBar(answerAView, progressA, myPoll.getResult().get('a'));
                break;
            case 'b':
                showCorrectProgressBar(answerBView, progressB, myPoll.getResult().get('b'));

                break;
            case 'c':
                showCorrectProgressBar(answerCView, progressC, myPoll.getResult().get('c'));

                break;
            case 'd':
                showCorrectProgressBar(answerDView, progressD, myPoll.getResult().get('d'));
                break;
        }

        //If user answer incorrect option
        if (myPoll.playerAnswer!=null && myPoll.correctOption != myPoll.playerAnswer) {
            switch (myPoll.playerAnswer) {
                case 'a':
                    showIncorrectProgressBar(answerAView, progressA, myPoll.getResult().get('a'));
                    break;
                case 'b':
                    showIncorrectProgressBar(answerBView, progressB, myPoll.getResult().get('b'));
                    break;
                case 'c':
                    showIncorrectProgressBar(answerCView, progressC, myPoll.getResult().get('c'));
                    break;
                case 'd':
                    showIncorrectProgressBar(answerDView, progressD, myPoll.getResult().get('d'));
                    break;
            }
            //show drinking animation
            //Toast.makeText(getApplicationContext(), "Incorrect Answer", Toast.LENGTH_SHORT).show();
            animationIntent.putExtra("isAnswerCorrect", "false");
            //startActivity(animationIntent);
        } else if (myPoll.playerAnswer!=null && myPoll.correctOption == myPoll.playerAnswer) {
            //Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_SHORT).show();
            animationIntent.putExtra("isAnswerCorrect", "true");
            //startActivity(animationIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setupOwnerInterface() {
        //allow to release poll and delete poll if not done yet
        releaseAnswerBtn.setVisibility(View.VISIBLE);
        deletePollBtn.setVisibility(View.VISIBLE);

        //highlight correct answer
        switch (myPoll.correctOption){
            case 'a':
                correctAnswerButton(answerAView);
                break;
            case 'b':
                correctAnswerButton(answerBView);
                break;
            case 'c':
                correctAnswerButton(answerCView);
                break;
            case 'd':
                correctAnswerButton(answerDView);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setupOwnerReleasedInterface() {
        /*Allow to see the results of its poll*/
        releaseAnswerBtn.setVisibility(View.VISIBLE);
        disableReleaseBtn(releaseAnswerBtn);

        //highlight correct answer and show progress ratios
        showProgressBar(answerAView,progressA,myPoll.getResult().get('a'));
        showProgressBar(answerBView,progressB,myPoll.getResult().get('b'));
        showProgressBar(answerCView,progressC,myPoll.getResult().get('c'));
        showProgressBar(answerDView,progressD,myPoll.getResult().get('d'));

        switch (myPoll.correctOption){
            case 'a':
                showCorrectProgressBar(answerAView,progressA,myPoll.getResult().get('a'));
                break;
            case 'b':
                showCorrectProgressBar(answerBView,progressB,myPoll.getResult().get('b'));
                break;
            case 'c':
                showCorrectProgressBar(answerCView,progressC,myPoll.getResult().get('c'));
                break;
            case 'd':
                showCorrectProgressBar(answerDView,progressD,myPoll.getResult().get('d'));
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selectAnswer(View v) {
        //Allow select answer only if: not poll owner, poll not released yet and answer not selected yet
        if (!isPollOwner && !myPoll.isReleased && myPoll.playerAnswer==null) {
            TextView textView = (TextView)v;
            selectAnswerButton(textView);

            //Get the selected answer
            String selectedOption=v.getResources().getResourceName(v.getId());
            myPoll.playerAnswer=selectedOption.charAt(selectedOption.length()-1);

            JSONObject data= new JSONObject();
            try {
                data.put("functionName", "addAnswer");
                data.put("pollID", myPoll.pollID);
                data.put("playerID", playerID);
                data.put("answer", myPoll.playerAnswer);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Impossible generating JSON object for adding option selected. Check logs", Toast.LENGTH_LONG).show();
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
                            try {
                                if (response.getString("success").equals("true")) {
                                    Toast.makeText(getApplicationContext(), "Answer saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Server error in answering poll. Check logs", Toast.LENGTH_SHORT).show();
                                    Log.d("<<DEBUG>>", response.getString("info"));
                                }
                            } catch (JSONException error) {
                                Toast.makeText(getApplicationContext(), "Server error in parsing JSON. Check logs", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Toast.makeText(getApplicationContext(), "Some error adding option selected. Check logs", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    });
            // Add a request (in this example, called stringRequest) to your RequestQueue.
            RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } else if (myPoll.playerAnswer!=null){
            Toast.makeText(getApplicationContext(), "Option already selected", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void releaseAnswer(View v) {
        /*
        This function is called when the poll owner decide to release the answer
         */
        //update pollObject
        myPoll.isReleased=true;

        JSONObject data= new JSONObject();
        try {
            data.put("functionName", "releasePoll");
            data.put("pollID", myPoll.pollID);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Impossible generating JSON object for releasing poll. Check logs", Toast.LENGTH_LONG).show();
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
                        try {
                            if (response.getString("success").equals("true")) {
                                Toast.makeText(getApplicationContext(), "Poll released", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Server error in releasing poll. Check logs", Toast.LENGTH_SHORT).show();
                                Log.d("<<DEBUG>>", response.getString("info"));
                            }
                        } catch (JSONException error) {
                            Toast.makeText(getApplicationContext(), "Server error in parsing JSON. Check logs", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getApplicationContext(), "Some error releasing poll. Check logs", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });


        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);

        releaseAnswerBtn.setVisibility(View.GONE);
        deletePollBtn.setVisibility(View.GONE);

        fetchResults();
    }

    public void deletePoll(View v) {
        JSONObject data= new JSONObject();
        try {
            data.put("functionName", "deletePoll");
            data.put("pollID", myPoll.pollID);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Impossible generating JSON object for deleting poll. Check logs", Toast.LENGTH_LONG).show();
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
                        try {
                            if (response.getString("success").equals("true")) {
                                Toast.makeText(getApplicationContext(), "Poll deleted", Toast.LENGTH_SHORT).show();

                                PollsEventManager.getInstance().removeMyPoll(myPoll.pollID);
                                PollsDictionary.getInstance().remove(myPoll.pollID);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Server error in deleting poll. Check logs", Toast.LENGTH_SHORT).show();
                                Log.d("<<DEBUG>>", response.getString("info"));
                            }
                        } catch (JSONException error) {
                            Toast.makeText(getApplicationContext(), "Server error in parsing JSON. Check logs", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getApplicationContext(), "Some error deleting poll. Check logs", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });


        // Add a request (in this example, called stringRequest) to your RequestQueue.
        RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(x2-x1)> MIN_DISTANCE & x1 > x2)
                //Swipe right to left
                {
                    break;
                }
                else if (Math.abs(x2-x1)> MIN_DISTANCE & x2 > x1)
                //swipe left to right
                {
                    finish();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
