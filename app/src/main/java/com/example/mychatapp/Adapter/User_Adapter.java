package com.example.mychatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychatapp.MessageActivity;
import com.example.mychatapp.Model.ChattedUser;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.R;

import java.util.List;

public class User_Adapter extends RecyclerView.Adapter<User_Adapter.ViewHolder> {

    private Context mContext;
    private List<ChattedUser> mUsers;

    //Encapsulation to make sure we can easily use the User class
    public User_Adapter(Context mContext, List<ChattedUser> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    //load the specific xml file as a view.
    public User_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_items, parent, false);
        return new User_Adapter.ViewHolder(view);
    }

    @Override
    //bind the name and the icon for each user in the database
    public void onBindViewHolder(@NonNull User_Adapter.ViewHolder holder, int position) {
        final ChattedUser user = mUsers.get(position);
        holder.username.setText(user.getPhonenumber());

        //pass the current device's phone number and the current user's user type to the MessageActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("username", user.getPhonenumber());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_icon;

        ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_icon = itemView.findViewById(R.id.profile_icon);
        }
    }
}
