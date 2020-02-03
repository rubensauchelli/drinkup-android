package com.example.drinkup.objects;

import java.util.HashMap;

public class PollsDictionary {
    private static final PollsDictionary ourInstance = new PollsDictionary();

    private HashMap<String, Poll> pollsDictionary=new HashMap<String, Poll>();

    public static PollsDictionary getInstance() {
        return ourInstance;
    }

    private PollsDictionary() {
    }

    public void clear() {
        pollsDictionary=new HashMap<>();
    }

    public void remove(String pollID) {
        pollsDictionary.remove(pollID);
    }

    public void addPoll(String pollId, Poll obj) {
        pollsDictionary.put(pollId, obj);
    }

    public Poll getPoll(String pollId) {
        return pollsDictionary.get(pollId);
    }
}
