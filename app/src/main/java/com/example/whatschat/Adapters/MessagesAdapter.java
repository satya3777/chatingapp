package com.example.whatschat.Adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatschat.R;
import com.example.whatschat.databinding.ItemReceiveBinding;
import com.example.whatschat.databinding.ItemSentBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.example.whatschat.Models.Message;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages ;
    // IF VIEW TYPE COME 1 THEN SENT AND IF 2 THEN ITS RECIVE
    final int ITEM_SENT=1;
    final int ITEM_RECEIVE=2;

    String senderRoom;
    String reciverRoom;


    public MessagesAdapter(Context context,ArrayList<Message> messages,String senderRoom,String reciverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.reciverRoom = reciverRoom;


    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType==ITEM_SENT){
           View view = LayoutInflater.from(context).inflate(R.layout.item_sent,parent,false);
                   return new SentViewHolder(view);
       }else{
           View view = LayoutInflater.from(context).inflate(R.layout.item_receive,parent,false);
           return new ReceiverViewHolder(view);
       }

    }
    //to sent messenge
    @Override
    public int getItemViewType(int position) {
        //object define
        Message message = messages.get(position);
        //ItemSender
        // if current user id matchces with sender id then the message will sent
        //getSenderID
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        //for giving reaction to message
        //array for reaction or images
        int reactions[] = new int[]{
                        R.drawable.sticker_like,
                        R.drawable.sticker_love,
                        R.drawable.sticer_laugh,
                        R.drawable.amaze_wow,
                        R.drawable.ic_fb_sad,
                        R.drawable.sticker_angry

        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
       // to attach emo's
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass()== SentViewHolder.class) {
                SentViewHolder viewHolder = (SentViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }
            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(reciverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);


            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass()== SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder)holder;
            ////error can be issued here
            viewHolder.binding.messagesnt.setText(message.getMessage());

            if (message.getFeeling()>=0) {
                viewHolder.binding.feeling.setImageResource(reactions[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);

            }
            // for emoji
            viewHolder.binding.messagesnt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);

                    return false;
                }
            });

        }else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            ////error can be issued here
            viewHolder.binding.messagerev.setText(message.getMessage());
            if (message.getFeeling()>=0) {
                //message.setFeeling(reactions[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);

            }


            viewHolder.binding.messagerev.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);


                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return messages.size();
    }
//ViewHolder
    public class SentViewHolder extends RecyclerView.ViewHolder{

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding =ItemSentBinding.bind(itemView);
        }
    }
    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        ItemReceiveBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
