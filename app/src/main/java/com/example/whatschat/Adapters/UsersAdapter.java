package com.example.whatschat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatschat.Activities.ChatActivity;
import com.example.whatschat.R;
import com.example.whatschat.Models.User;
import com.example.whatschat.databinding.RowConversBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersAdapter extends  RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{
    Context context;
    ArrayList<User> users;


    //Building constructor
    public UsersAdapter(Context context,ArrayList<User> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View View = LayoutInflater.from(context).inflate(R.layout.row_convers,parent,false);
        return new UsersViewHolder(View);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
            User user = users .get(position);
            //tos show  last message
            String senderId = FirebaseAuth.getInstance().getUid();
            String senderRoom = senderId + user.getUid();
        FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                                .child(senderRoom)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String lastmsg = snapshot.child("lastMsg").getValue(String.class);
                                                    long time = snapshot.child("lastMsgTime").getValue(long.class);
                                                    holder.binding.lastMsg.setText(lastmsg);
                                                }else {
                                                    holder.binding.lastMsg.setText("start chat");
                                                }
                                                }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

            holder.binding.username.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profilepic);
        //to go on chat layout activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("uid", user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        RowConversBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            //adapting or taking id's from row converse
            binding = RowConversBinding.bind(itemView);

        }
    }
}
