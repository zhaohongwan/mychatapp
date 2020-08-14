package com.example.mychatapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.mychatapp.Model.ChattedUser;
import com.example.mychatapp.Model.LocalUser;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.localdatabase.ChattedUserDAO;
import com.example.mychatapp.localdatabase.LocalChatDAO;
import com.example.mychatapp.localdatabase.LocalUserDAO;
import com.example.mychatapp.localdatabase.MyAppDatabase;

import java.util.List;

public class Repository {
    private LocalChatDAO localChatDAO;
    private LiveData<List<Localchat>> allChats;

    private ChattedUserDAO chattedUserDAO;
    private LiveData<List<ChattedUser>> allUsers;

    private LocalUserDAO localUser_DAO;
    private LiveData<List<LocalUser>> allOfficialUsers;

    public Repository(Application application) {
        MyAppDatabase database = MyAppDatabase.getMyAppDatabase(application);
        localChatDAO = database.myDao();
        chattedUserDAO = database.userDao();
        localUser_DAO = database.officialuserDao();
        allChats = localChatDAO.loadAllChats();
        allUsers = chattedUserDAO.loadAllUsers();
        allOfficialUsers = localUser_DAO.getAll();
    }

    //functions to manipulate the Localchat(received and sent SMS message) Entity
    public void addChat(Localchat chat) {
        new AddNoteAsyncTask(localChatDAO).execute(chat);
    }

    public LiveData<List<Localchat>> loadAllChats() {
        return allChats;
    }

    public void deleteAll(){ new deleteAllAsyncTask(localChatDAO).execute(); }

    public void updateStatus(int id, String status) {
        Params params = new Params(id, status);
        new UpdateStatusAsyncTask(localChatDAO).execute(params);
    }

    public void updateIdentity(int id, String identity) {
        Params params = new Params(id, identity);
        new UpdateIdentityAsyncTask(localChatDAO).execute(params);
    }

    public void updateMessage(int id, String message){
        Params params = new Params(id, message);
        new UpdateMessageAsyncTask(localChatDAO).execute(params);
    }

    public void updateSeed(int id, String seed){
        Params params = new Params(id, seed);
        new UpdateSeedAsyncTask(localChatDAO).execute(params);
    }

    //****************************************************************************************
    //function to manipulate the Chatted_user Entity
    public void addUser(ChattedUser user){
        new AddUserAsyncTask(chattedUserDAO).execute(user);
    }

    public LiveData<List<ChattedUser>> loadAllUsers(){
        return allUsers;
    }

    //function to manipulate the OfficialUser_Private Entity
    public void addLocalUser(LocalUser officialuser){
        new AddOfficialUserAsyncTask(localUser_DAO).execute(officialuser);
    }

    public LiveData<List<LocalUser>> getAll(){
        return allOfficialUsers;
    }

    public void updateUserStatus(String phonenumber, String status) {
        Params2 params2 = new Params2(phonenumber, status);
        new UpdateUserStatusAsyncTask(localUser_DAO).execute(params2);
    }

    //*********************************************************************************
    //https://stackoverflow.com/questions/56088451/how-do-i-update-a-field-in-a-room-database-using-a-repository-viewmodel
    private static class Params{
        int id;
        String status;

        Params(int id, String status){
            this.id = id;
            this.status = status;
        }
    }

    private static class Params2{
        String phonenumber;
        String status;

        Params2(String phonenumber, String status){
            this.phonenumber = phonenumber;
            this.status = status;
        }
    }

    //The asynctask to manipulate the entity asynchronously
    private static class UpdateUserStatusAsyncTask extends AsyncTask<Params2, Void, Void> {
        private LocalUserDAO localUserDAO;
        private UpdateUserStatusAsyncTask(LocalUserDAO localUserDAO) {
            this.localUserDAO = localUserDAO;
        }
        @Override
        protected Void doInBackground(Params2... params2) {
            String phonenumber = params2[0].phonenumber;
            String status = params2[0].status;
            localUserDAO.updateStatus(phonenumber, status);
            return null;
        }
    }

    private static class AddOfficialUserAsyncTask extends AsyncTask<LocalUser, Void, Void>{
        private LocalUserDAO localUser_DAO;
        private AddOfficialUserAsyncTask(LocalUserDAO localUser_DAO){
            this.localUser_DAO = localUser_DAO;
        }

        @Override
        protected Void doInBackground(LocalUser... localUser_s) {
            localUser_DAO.addOfficialUser(localUser_s[0]);
            return null;
        }
    }

    private static class AddUserAsyncTask extends AsyncTask<ChattedUser, Void, Void> {
        private ChattedUserDAO chattedUserDAO;
        private AddUserAsyncTask(ChattedUserDAO chattedUserDAO) {
            this.chattedUserDAO = chattedUserDAO;
        }
        @Override
        protected Void doInBackground(ChattedUser... chattedUsers) {
            chattedUserDAO.addUser(chattedUsers[0]);
            return null;
        }
    }

    private static class AddNoteAsyncTask extends AsyncTask<Localchat, Void, Void> {
        private LocalChatDAO localChatDAO;
        private AddNoteAsyncTask(LocalChatDAO localChatDAO) {
            this.localChatDAO = localChatDAO;
        }
        @Override
        protected Void doInBackground(Localchat... chats) {
            localChatDAO.addChat(chats[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private LocalChatDAO localChatDAO;
        private deleteAllAsyncTask(LocalChatDAO localChatDAO) {
            this.localChatDAO = localChatDAO;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            localChatDAO.deleteAll();
            return null;
        }
    }

    private static class UpdateStatusAsyncTask extends AsyncTask<Params, Void, Void> {
        private LocalChatDAO localChatDAO;
        private UpdateStatusAsyncTask(LocalChatDAO localChatDAO) {
            this.localChatDAO = localChatDAO;
        }
        @Override
        protected Void doInBackground(Params... params) {
            int id = params[0].id;
            String status = params[0].status;
            localChatDAO.updateStatus(id, status);
            return null;
        }
    }

    private static class UpdateIdentityAsyncTask extends AsyncTask<Params, Void, Void> {
        private LocalChatDAO localChatDAO;
        private UpdateIdentityAsyncTask(LocalChatDAO localChatDAO) {
            this.localChatDAO = localChatDAO;
        }
        @Override
        protected Void doInBackground(Params... params) {
            int id = params[0].id;
            String identity = params[0].status;
            localChatDAO.updateIdentity(id, identity);
            return null;
        }
    }

    private static class UpdateMessageAsyncTask extends AsyncTask<Params, Void, Void> {
        private LocalChatDAO localChatDAO;
        private UpdateMessageAsyncTask(LocalChatDAO localChatDAO) {
            this.localChatDAO = localChatDAO;
        }
        @Override
        protected Void doInBackground(Params... params) {
            int id = params[0].id;
            String message = params[0].status;
            localChatDAO.updateMessage(id, message);
            return null;
        }
    }

    private static class UpdateSeedAsyncTask extends AsyncTask<Params, Void, Void> {
        private LocalChatDAO localChatDAO;
        private UpdateSeedAsyncTask(LocalChatDAO localChatDAO) {
            this.localChatDAO = localChatDAO;
        }
        @Override
        protected Void doInBackground(Params... params) {
            int id = params[0].id;
            String seed = params[0].status;
            localChatDAO.updateSeed(id, seed);
            return null;
        }
    }
}
