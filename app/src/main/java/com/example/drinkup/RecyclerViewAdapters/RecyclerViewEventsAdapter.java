package com.example.drinkup.RecyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drinkup.EventActivity;
import com.example.drinkup.QRCodeActivity;
import com.example.drinkup.R;
import com.example.drinkup.objects.Event;
import com.example.drinkup.objects.EventsDictionary;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewEventsAdapter extends RecyclerView.Adapter<RecyclerViewEventsAdapter.ViewHolder>  {
    private ArrayList<String> eventsID=new ArrayList<>();
    private EventsDictionary eventsDict;
    private Context context;

    int prevColorIndex =-1;
    String[] colors = {"#FF1493","#9400D3","#6A5ACD","#4169E1","#20B2AA","#3CB371","#FF8C00","#DC143C","#212121"};

    public RecyclerViewEventsAdapter(EventsDictionary eventsDictionary, Context c) {
        this.eventsDict=eventsDictionary;

        for ( String key : eventsDictionary.eventsDictionary.keySet()) {
            this.eventsID.add(key);
        }

        context=c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        Grab the .xml file that defines the layout of list items. Pass it to the ViewHolder class to instantiate
        the list elements.
         */

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    public String colourOrdered() {

        String currentColour;
        if(prevColorIndex+1 == colors.length){
            currentColour = colors[0];
            prevColorIndex=0;
            return currentColour;
        }
        else{
            currentColour = colors[prevColorIndex+1];
            prevColorIndex=prevColorIndex+1;
            return currentColour;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String eventID=eventsID.get(position);
        Event eventObj=this.eventsDict.get(eventID);

        holder.eventID=eventID;
        String eventNameStr=eventObj.eventName;
        holder.eventName.setText(eventNameStr.substring(0, 1).toUpperCase() + eventNameStr.substring(1));
        holder.pollAmountTextView.setText(String.valueOf(eventObj.num_polls));
        holder.numParticipantTextView.setText(String.valueOf(eventObj.num_participants));
        holder.eventLocationTextView.setText(String.valueOf(eventObj.location));

        //Set list item background color
        GradientDrawable eventItemDrawable = (GradientDrawable) holder.eventWrapper.getBackground().mutate();
        eventItemDrawable.setColor(Color.parseColor(colourOrdered()));


        //Open QRCODE
        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QRCodeActivity.class);

                intent.putExtra("eventID", holder.eventID);

                context.startActivity(intent);
            }
        });

        //Open event page
        holder.eventAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventActivity.class);

                intent.putExtra("eventID", holder.eventID);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventsID.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        /*
        Here define the variables accessed later on for each item in the list.

        Es. store eventID so that when user clicks on the item, the eventID is used to retrieve the polls
        available in that event.
         */

        ConstraintLayout itemLayoutWrapper;
        TextView eventName;
        TextView pollAmountTextView;
        TextView numParticipantTextView;
        TextView eventLocationTextView;
        View eventAction;
        View eventWrapper;
        ImageButton shareIcon;
        String eventID;

        public ViewHolder(View itemView) {
            super(itemView);
            itemLayoutWrapper=itemView.findViewById(R.id.event_item_layout_wrapper);
            eventName=itemView.findViewById(R.id.item_event_name);
            pollAmountTextView=itemView.findViewById(R.id.polls_amount);
            numParticipantTextView=itemView.findViewById(R.id.participants_amount);
            eventLocationTextView=itemView.findViewById(R.id.event_location);

            //action
            eventAction = itemView.findViewById(R.id.event_button);

            //changed to layout as a wrapper
            eventWrapper = itemView.findViewById(R.id.event_item_layout_wrapper);
            shareIcon=itemView.findViewById(R.id.share_icon);




        }
    }
}
