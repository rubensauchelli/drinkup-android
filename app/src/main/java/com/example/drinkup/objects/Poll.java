package com.example.drinkup.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;

public class Poll implements Serializable {

    public String pollID; //millisecond timestep to uniquely identify the poll
    public String pollOwnerID; //google id of who generated the poll
    public String question;
    public Hashtable<Character, String> optionsDict;
    public Character correctOption;
    public boolean isReleased=false;
    public Character playerAnswer;
    public HashMap<Character, Float> releasedResults;

    public Poll(String pID, String pOID, String q, Hashtable<Character, String> options, Character coi) {
        pollID = pID;
        pollOwnerID=pOID;
        question=q;
        optionsDict=options;
        correctOption=coi;
    }

    public HashMap<Character, Float> getResult() {
        return releasedResults;
    }

    public void setResult(HashMap<Character, Float> relRes) {
        this.releasedResults=relRes;
    }
}
