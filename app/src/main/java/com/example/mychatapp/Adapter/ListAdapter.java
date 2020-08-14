package com.example.mychatapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.R;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Localchat> chats = new ArrayList<>();

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        Localchat currentChat = chats.get(position);
        holder.user_id.setText(String.valueOf(currentChat.getConversation_id()));
        holder.user_message.setText(currentChat.getTxt_message());
        holder.sender.setText(currentChat.getSender());
        holder.receiver.setText(currentChat.getReceiver());
        holder.status.setText(currentChat.getStatus());
        holder.identity.setText(currentChat.getIdentity());
        holder.seed.setText(currentChat.getSeed());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setChats(List<Localchat> chats){
        this.chats = chats;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView user_id, user_message, sender, receiver, status, identity, seed;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_id = itemView.findViewById(R.id.user_id);
            user_message = itemView.findViewById(R.id.user_message);
            sender = itemView.findViewById(R.id.sender);
            receiver = itemView.findViewById(R.id.receiver);
            status = itemView.findViewById(R.id.status);
            identity = itemView.findViewById(R.id.identity);
            seed = itemView.findViewById(R.id.seed);
        }
    }

}
