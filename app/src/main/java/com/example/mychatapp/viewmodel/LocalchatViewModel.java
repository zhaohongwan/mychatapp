package com.example.mychatapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.repository.Repository;

import java.util.List;

//helps the user to access the Room database
public class LocalchatViewModel extends AndroidViewModel {
    private static Repository repository;
    private LiveData<List<Localchat>> allChats;

    public LocalchatViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allChats = repository.loadAllChats();
    }

    //These are the function that will be used for the user directly
    public void addChat(Localchat chat){
        repository.addChat(chat);
    }

    public LiveData<List<Localchat>> loadAllChats(){
        return allChats;
    }

    public static void deleteAll(){
        repository.deleteAll();
    }

    public void updateStatus(int id, String status) {
        repository.updateStatus(id, status);
    }

    public void updateIdentity(int id, String identity) {
        repository.updateIdentity(id, identity);
    }

    public void updateMessage(int id, String message){
        repository.updateMessage(id, message);
    }

    public void updateSeed(int id, String seed){
        repository.updateSeed(id, seed);
    }

}
