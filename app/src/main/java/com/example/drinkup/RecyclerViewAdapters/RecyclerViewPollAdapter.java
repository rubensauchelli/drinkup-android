package com.example.drinkup.RecyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drinkup.PollManager;
import com.example.drinkup.objects.PollsDictionary;
import com.example.drinkup.R;
import com.example.drinkup.objects.Poll;

import java.util.ArrayList;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RecyclerViewPollAdapter extends RecyclerView.Adapter<RecyclerViewPollAdapter.ViewHolder> {
    /*
    Holds each list item individually in memory
     */
    private static final String TAG = "RecyclerViewPollAdapter";

    private ArrayList<String> pollsIDs;
    private PollsDictionary polls_dict;
    private Boolean isRecyclerMyPolls; //Used to differentiate between my polls and other users polls
    private Context context;
    String previousColor;
    int prevColorIndex =-1;


    String[] colors = {"#FF1493","#9400D3","#6A5ACD","#4169E1","#20B2AA","#3CB371","#FF8C00","#DC143C","#212121"};

    public RecyclerViewPollAdapter(ArrayList<String> polls_ids, PollsDictionary polls_dictionary, Boolean isRecMyPolls, Context c) {
        pollsIDs=polls_ids;
        polls_dict=polls_dictionary;
        isRecyclerMyPolls=isRecMyPolls;

        context=c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        Grab the .xml file that defines the layout of list items. Pass it to the ViewHolder class to instantiate
        the list elements.
         */

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.pool_list_item, parent, false);
        ViewHolder holder=new ViewHolder(view);

        return holder;
    }
    private static int onCreateViewHolder(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private static int randomNum(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
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

    //discolor poll
    static void discolorButton(ViewHolder holder, Context context, GradientDrawable gradientDrawable) {

        gradientDrawable.setColor(ContextCompat.getColor(context,R.color.greyWeak));
        holder.questionName.setTextColor(ContextCompat.getColor(context,R.color.greyWeak));

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        /*
        Here the polls data are used to fill the list items defined in the ViewHolder
         */

        //Get polls data
        String poll_id=pollsIDs.get(position);
        Poll poll_obj=polls_dict.getPoll(poll_id);

        Boolean isAnswered=!String.valueOf(poll_obj.playerAnswer).equals("null");

        //Fill list item
        holder.pollID=poll_id; //keep the dictionary poll id to be open later
        holder.questionName.setText(poll_obj.question); //set the widget text using the poll question

        //Set list item background color
        GradientDrawable pollItemDrawable = (GradientDrawable) holder.questionName.getBackground().mutate();
        pollItemDrawable.setColor(Color.parseColor(colourOrdered()));
        //Set poll item color to <grey>
        if (!isAnswered && !isRecyclerMyPolls && poll_obj.isReleased) {
            //poll not answered but released

            discolorButton(holder,context,pollItemDrawable);

        } else if (isAnswered && !isRecyclerMyPolls && !poll_obj.isReleased) {
            //poll answered but not released
            discolorButton(holder,context,pollItemDrawable);

        }

        //poll released and owner
        if (poll_obj.isReleased && isRecyclerMyPolls) {
            discolorButton(holder,context,pollItemDrawable);
        }

        holder.itemLayoutWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PollManager.class);

                intent.putExtra("poolID", holder.pollID);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pollsIDs.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        /*
        Here define the variables needed to be accessed and modified for each item in the list.
         */
        TextView questionName;
        String pollID;

        ConstraintLayout itemLayoutWrapper;
        public ViewHolder(View itemView) {
            super(itemView);

            questionName=itemView.findViewById(R.id.poolName);
            itemLayoutWrapper=itemView.findViewById(R.id.item_layout_wrapper);
        }
    }
}
