package com.example.mychatapp.localdatabase;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mychatapp.Model.ChattedUser;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.Model.LocalUser;

//this class is represent the database
@Database(entities = {Localchat.class, ChattedUser.class, LocalUser.class}, version = 9, exportSchema = false)
public abstract class MyAppDatabase extends RoomDatabase {
    public abstract LocalChatDAO myDao();
    public abstract ChattedUserDAO userDao();
    public abstract LocalUserDAO officialuserDao();

    private static String DB_NAME = "user_db";
    private static MyAppDatabase INSTANCE;

    //build the database "user_db"
    public static synchronized MyAppDatabase getMyAppDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyAppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return INSTANCE;
    }

    //*****************************
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new TheAsyncTask(INSTANCE).execute();
        }
    };

    private static class TheAsyncTask extends AsyncTask<Void, Void, Void>{
        private LocalChatDAO localChatDAO;
        private ChattedUserDAO chattedUserDAO;
        private LocalUserDAO localUser_DAO;

        private TheAsyncTask(MyAppDatabase database){
            localChatDAO = database.myDao();
            chattedUserDAO = database.userDao();
            localUser_DAO = database.officialuserDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //myDao.addChat(new Localchat("ww1", "sender1", "receiver1", "Unknown"));
            //chattedUserDAO.addUser(new ChattedUser("user1"));
            return null;
        }
    }

}
