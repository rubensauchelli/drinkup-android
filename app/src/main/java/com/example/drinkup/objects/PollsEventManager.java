package com.example.drinkup.objects;

import android.util.Log;

import java.util.ArrayList;

public class PollsEventManager {
    private static final PollsEventManager ourInstance = new PollsEventManager();

    public ArrayList<String> IDOpenPolls= new ArrayList<>();
    public ArrayList<String> IDReleasedPolls= new ArrayList<>();
    public ArrayList<String> IDMyPolls= new ArrayList<>();

    public static PollsEventManager getInstance() {
        return ourInstance;
    }


    private PollsEventManager() {
    }

    public void clear(){
        IDOpenPolls= new ArrayList<>();
        IDReleasedPolls= new ArrayList<>();
        IDMyPolls= new ArrayList<>();
    }

    public void removeMyPoll(String pollID) {
        IDMyPolls.remove(IDMyPolls.indexOf(pollID));
    }

    public void removeReleasedPoll(String pollID) {
        IDReleasedPolls.remove(IDReleasedPolls.indexOf(pollID));
    }


    public void addPoll(Poll poll_obj, String playerID) {
        if (poll_obj.pollOwnerID.equals(playerID)) {
            //If released append at the end of list. Otherwise at the beginning
            if (poll_obj.isReleased) {
                IDMyPolls.add(poll_obj.pollID);
            } else {
                IDMyPolls.add(0, poll_obj.pollID);
            }
        } else if (poll_obj.isReleased) {
            if (String.valueOf(poll_obj.playerAnswer).equals("null")) {
                IDReleasedPolls.add(poll_obj.pollID);
            } else {
                IDReleasedPolls.add(0, poll_obj.pollID);
            }
        } else {
            if (String.valueOf(poll_obj.playerAnswer).equals("null")) {
                IDOpenPolls.add(0, poll_obj.pollID);
            } else {
                IDOpenPolls.add(poll_obj.pollID);
            }
        }
    }
}
