package com.example.drinkup.objects;
import java.util.HashMap;

public class EventsDictionary {
    private static final EventsDictionary ourInstance = new EventsDictionary();
    public HashMap<String, Event> eventsDictionary=new HashMap<String, Event>();

    public static EventsDictionary getInstance() {
        return ourInstance;
    }

    private EventsDictionary() {
    }

    public void add(Event eventObj) {
        this.eventsDictionary.put(eventObj.ID, eventObj);
    }

    public Event get(String eventID) {
        return this.eventsDictionary.get(eventID);
    }

    public void clear() {
        eventsDictionary=new HashMap<>();
    }
}
