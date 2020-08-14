package com.example.mychatapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mychatapp.Model.ChattedUser;
import com.example.mychatapp.repository.Repository;

import java.util.List;

//helps the user to access the Room database
public class ChattedUserViewModel extends AndroidViewModel {

    private static Repository repository;
    private LiveData<List<ChattedUser>> allUsers;

    public ChattedUserViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allUsers = repository.loadAllUsers();
    }

    //These are the function that will be used for the user directly
    public void addUser(ChattedUser chattedUser){
        repository.addUser(chattedUser);
    }

    public LiveData<List<ChattedUser>> loadAllUsers(){
        return allUsers;
    }

}
