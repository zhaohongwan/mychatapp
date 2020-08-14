package com.example.mychatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    //value to help us define the message is send from me or the others.
    public static final int MESSAGE_TYPE_OTHERS = 0;
    public static final int MESSAGE_TYPE_USER = 1;
    public static final int MESSAGE_TYPE_NONE = 2;
    public static final int MESSAGE_TYPE_OTHERS_SPOOFED = 3;
    public static final int MESSAGE_TYPE_OTHERS_UNKNOWN = 4;

    private String id_mine;
    private String id_user;

    //using to store the localdatabase's message
    private List<Localchat> mChat = new ArrayList<>();

    @NonNull
    @Override
    //upload different views by determine the the viewtype.
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_USER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_from_user, parent, false);
            return new ViewHolder(view);
        }else if (viewType == MESSAGE_TYPE_OTHERS){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_from_others, parent, false);
            return new ViewHolder(view);
        }else if (viewType == MESSAGE_TYPE_OTHERS_SPOOFED){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_from_others_spoofed, parent, false);
            return new ViewHolder(view);
        }else if(viewType == MESSAGE_TYPE_OTHERS_UNKNOWN){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_from_others_unknown, parent, false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_blank, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    //shows the message and the user's icon.
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Localchat chat = mChat.get(position);

        if (chat.getSender().equals(id_mine) && chat.getReceiver().equals(id_user)){
            holder.sent_message.setText(chat.getTxt_message());
        }else if (chat.getSender().equals(id_user) && chat.getReceiver().equals(id_mine)){
            holder.sent_message.setText(chat.getTxt_message());
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sent_message;
        public ImageView profile_icon;

        ViewHolder(View itemView){
            super(itemView);
            sent_message = itemView.findViewById(R.id.sent_message);
            profile_icon = itemView.findViewById(R.id.profile_icon);
        }
    }

    @Override
    //return the message type to let us know whether the message is from the user or the other users.
    public int getItemViewType(int position) {
        String chat_sender = mChat.get(position).getSender();
        String chat_receiver = mChat.get(position).getReceiver();
        //get this chat message's Authentication result.
        String chat_identity = mChat.get(position).getIdentity();

        //check if the current sender's id is equal to the current firebase user's id.
        if(chat_sender.equals(id_mine) && chat_receiver.equals(id_user)){
            return MESSAGE_TYPE_USER;
        }else if(chat_sender.equals(id_user) && chat_receiver.equals(id_mine)){
            if (chat_identity.equals("Verified")){
                return MESSAGE_TYPE_OTHERS;
            }else if (chat_identity.equals("Spoofed")){
                return MESSAGE_TYPE_OTHERS_SPOOFED;
            }else{
                return MESSAGE_TYPE_OTHERS_UNKNOWN;
            }
        }else{
            return MESSAGE_TYPE_NONE;
        }
    }

    public void setLocalChat(List<Localchat> chat, String id_mine, String id_user){
        this.mChat = chat;
        this.id_mine = id_mine;
        this.id_user = id_user;
        notifyDataSetChanged();
    }

}
