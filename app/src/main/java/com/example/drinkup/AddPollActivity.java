package com.example.drinkup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import java.util.Hashtable;

public class AddPollActivity extends AppCompatActivity {
    //Poll correct answer
    Character pollAnswer;
    //Poll question
    EditText editTextQuestion;

    //Poll options fields inputs
    EditText editTextA;
    EditText editTextB;
    EditText editTextC;
    EditText editTextD;

    CheckBox checkBoxA;
    CheckBox checkBoxB;
    CheckBox checkBoxC;
    CheckBox checkBoxD;

    LinearLayout answerAContainer;
    LinearLayout answerBContainer;
    LinearLayout answerCContainer;
    LinearLayout answerDContainer;

    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.transition.fadein, R.transition.fadeout);
        setContentView(R.layout.activity_add_poll);

        Intent i = getIntent();
        eventID=i.getStringExtra("eventID");

        //connect inputs to item IDs (create poll)
        editTextQuestion=(EditText) findViewById(R.id.editTextQuestion);

        answerAContainer= (LinearLayout) findViewById(R.id.answerA);
        answerBContainer= (LinearLayout) findViewById(R.id.answerB);
        answerCContainer= (LinearLayout) findViewById(R.id.answerC);
        answerDContainer= (LinearLayout) findViewById(R.id.answerD);

        editTextA=(EditText) findViewById(R.id.editTextA);
        editTextB=(EditText) findViewById(R.id.editTextB);
        editTextC=(EditText) findViewById(R.id.editTextC);
        editTextD=(EditText) findViewById(R.id.editTextD);

        checkBoxA=(CheckBox) findViewById(R.id.checkboxA);
        checkBoxB=(CheckBox) findViewById(R.id.checkboxB);
        checkBoxC=(CheckBox) findViewById(R.id.checkboxC);
        checkBoxD=(CheckBox) findViewById(R.id.checkboxD);

        //Disable checkbox untile relevant ones are satisfied
        checkBoxA.setEnabled(false);
        checkBoxB.setEnabled(false);
        checkBoxC.setEnabled(false);
        checkBoxD.setEnabled(false);

        //disable inputs until relevant ones are satisfied
        editTextA.setEnabled(false);
        editTextB.setEnabled(false);
        editTextC.setEnabled(false);
        editTextD.setEnabled(false);


        editTextQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(editTextQuestion.getText().toString().equals("null") || editTextQuestion.getText().toString().equals("")){
                    editTextA.setEnabled(false);
                    editTextB.setEnabled(false);
                    editTextC.setEnabled(false);
                    editTextD.setEnabled(false);

                    checkBoxA.setEnabled(false);
                    checkBoxB.setEnabled(false);
                    checkBoxC.setEnabled(false);
                    checkBoxD.setEnabled(false);


                    editTextA.setLongClickable(false);
                    editTextB.setLongClickable(false);
                    editTextC.setLongClickable(false);
                    editTextD.setLongClickable(false);
                } else {
                    editTextA.setEnabled(true);
                    checkBoxA.setEnabled(true);
                }
            }
        });

        editTextA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                if(editTextA.getText().toString().equals("null") || editTextA.getText().toString().equals("")){
                    checkBoxA.setEnabled(false);
                    editTextA.setLongClickable(false);

                    checkBoxA.setChecked(false);
                    answerAContainer.setBackgroundResource(R.drawable.answer_default_background);
                    editTextA.setTextAppearance(R.style.AnswerTextAddPollTheme);
                    pollAnswer = ((pollAnswer == 'a') ? null : pollAnswer);
                } else {
                    checkBoxA.setEnabled(true);
                    editTextA.setLongClickable(true);

                    editTextB.setEnabled(true);
                    checkBoxB.setEnabled(true);

                }
            }
        });

        editTextB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                if(editTextB.getText().toString().equals("null") || editTextB.getText().toString().equals("")){
                    checkBoxB.setEnabled(false);
                    editTextB.setLongClickable(false);

                    checkBoxB.setChecked(false);
                    answerBContainer.setBackgroundResource(R.drawable.answer_default_background);
                    editTextB.setTextAppearance(R.style.AnswerTextAddPollTheme);
                    pollAnswer = ((pollAnswer == 'b') ? null : pollAnswer);

                } else {
                    checkBoxB.setEnabled(true);
                    editTextB.setLongClickable(true);

                    editTextC.setEnabled(true);
                }
            }
        });

        editTextC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                if(editTextC.getText().toString().equals("null") || editTextC.getText().toString().equals("")){
                    checkBoxC.setEnabled(false);
                    editTextC.setLongClickable(false);

                    checkBoxC.setChecked(false);
                    answerCContainer.setBackgroundResource(R.drawable.answer_default_background);
                    editTextC.setTextAppearance(R.style.AnswerTextAddPollTheme);
                    pollAnswer = ((pollAnswer == 'c') ? null : pollAnswer);

                } else {
                    checkBoxC.setEnabled(true);
                    editTextC.setLongClickable(true);

                    editTextD.setEnabled(true);
                }
            }
        });

        editTextD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                if(editTextD.getText().toString().equals("null") || editTextD.getText().toString().equals("")){
                    checkBoxD.setEnabled(false);
                    editTextD.setLongClickable(false);

                    checkBoxD.setChecked(false);
                    answerDContainer.setBackgroundResource(R.drawable.answer_default_background);
                    editTextD.setTextAppearance(R.style.AnswerTextAddPollTheme);
                    pollAnswer = ((pollAnswer == 'd') ? null : pollAnswer);
                } else {
                    checkBoxD.setEnabled(true);
                    editTextD.setLongClickable(true);
                }
            }
        });

        editTextA.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View v) {
                toggleSelectedAnswer((View) checkBoxA);
                return true;
            }
        });
        editTextB.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View v) {
                toggleSelectedAnswer((View) checkBoxB);
                return true;
            }
        });
        editTextC.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View v) {
                toggleSelectedAnswer((View) checkBoxC);
                return true;
            }
        });
        editTextD.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View v) {
                toggleSelectedAnswer((View) checkBoxD);
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void toggleSelectedAnswer(View v) {
        String checkboxID=v.getResources().getResourceName(v.getId());
        //Save answer
        pollAnswer=Character.toLowerCase(checkboxID.charAt(checkboxID.length()-1));
        //Let just 1 checkbox selected
        checkBoxA.setChecked(false);
        checkBoxB.setChecked(false);
        checkBoxC.setChecked(false);
        checkBoxD.setChecked(false);

        CheckBox checkboxSelected=(CheckBox) v;
        checkboxSelected.setChecked(true);

        //Let just 1 answer selected
        answerAContainer.setBackgroundResource(R.drawable.answer_default_background);
        answerBContainer.setBackgroundResource(R.drawable.answer_default_background);
        answerCContainer.setBackgroundResource(R.drawable.answer_default_background);
        answerDContainer.setBackgroundResource(R.drawable.answer_default_background);

        editTextA.setTextAppearance(R.style.AnswerTextAddPollTheme);
        editTextB.setTextAppearance(R.style.AnswerTextAddPollTheme);
        editTextC.setTextAppearance(R.style.AnswerTextAddPollTheme);
        editTextD.setTextAppearance(R.style.AnswerTextAddPollTheme);

        switch (pollAnswer) {
            case 'a':
                answerAContainer.setBackgroundResource(R.drawable.answer_correct_background);
                editTextA.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 'b':
                answerBContainer.setBackgroundResource(R.drawable.answer_correct_background);
                editTextB.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 'c':
                answerCContainer.setBackgroundResource(R.drawable.answer_correct_background);
                editTextC.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            case 'd':
                answerDContainer.setBackgroundResource(R.drawable.answer_correct_background);
                editTextD.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }

    }

    public void createPoll(View v) {
        //get the pool options
        String answerA = editTextA.getText().toString();
        String answerB = editTextB.getText().toString();
        String answerC = editTextC.getText().toString();
        String answerD = editTextD.getText().toString();

        //get the question
        String pollQuestion = editTextQuestion.getText().toString();

        //Generate unique ID
        String pollID=eventID+Long.toString(System.currentTimeMillis());

        // close pop up after relevant variables are satisfied
        if(!(pollQuestion.isEmpty()||answerA.isEmpty()||answerB.isEmpty()) && pollAnswer!=null) {
            //Pack the options into a dictionary with key the character and value the option
            Hashtable<Character, String> optionsDict= new Hashtable<Character, String>();
            optionsDict.put('a', answerA);
            optionsDict.put('b', answerB);
            optionsDict.put('c', answerC);
            optionsDict.put('d', answerD);

            Poll poll_obj=new Poll(pollID, User.getInstance().getID(), pollQuestion, optionsDict, pollAnswer);

            PollsDictionary.getInstance().addPoll(poll_obj.pollID, poll_obj);
            PollsEventManager.getInstance().addPoll(poll_obj, User.getInstance().getID());

            JSONObject data= new JSONObject();
            try {
                data.put("functionName", "createPoll");
                data.put("poll_ID", pollID);
                data.put("owner_ID", User.getInstance().getID());
                data.put("question", pollQuestion);
                data.put("answer_A", answerA);
                data.put("answer_B", answerB);
                data.put("answer_C", answerC);
                data.put("answer_D", answerD);
                data.put("answer", pollAnswer);
                data.put("eventID", eventID);


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Impossible generating JSON object for adding poll. Check logs", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(getApplicationContext(), "Poll generated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Server error in generating poll. Check logs", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Some error adding polls. Check logs", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    });

            // Add a request (in this example, called stringRequest) to your RequestQueue.
            RequestsManager.getInstance(this).addToRequestQueue(jsonObjectRequest);

            onBackPressed();

        } else if (pollQuestion.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Question missing",Toast.LENGTH_LONG).show();
        } else if (answerA.isEmpty()||answerB.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Add at least two options",Toast.LENGTH_LONG).show();
        } else if (pollAnswer==null) {
            Toast.makeText(getApplicationContext(), "Correct option missing", Toast.LENGTH_LONG).show();
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
