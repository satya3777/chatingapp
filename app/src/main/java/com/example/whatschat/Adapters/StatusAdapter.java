package com.example.whatschat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatschat.Models.User_status;
import com.example.whatschat.R;
import com.example.whatschat.databinding.ItemStatusBinding;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
   Context context ;
   ArrayList<User_status> userStatuses;

   public StatusAdapter(Context context, ArrayList<User_status> userStatuses){
       this.context = context;
       this.userStatuses = userStatuses;

   }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);

        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class  StatusViewHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
