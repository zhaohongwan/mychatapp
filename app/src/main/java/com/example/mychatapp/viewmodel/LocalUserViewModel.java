package com.example.mychatapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mychatapp.Model.LocalUser;
import com.example.mychatapp.repository.Repository;

import java.util.List;

public class LocalUserViewModel extends AndroidViewModel {
    private static Repository repository;
    private LiveData<List<LocalUser>> allOfficialUsers;

    public LocalUserViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allOfficialUsers = repository.getAll();
    }

    //These are the function that will be used for the user directly
    public void addLocalUser(LocalUser officialuser){
        repository.addLocalUser(officialuser);
    }

    public LiveData<List<LocalUser>> getAll(){
        return allOfficialUsers;
    }

    public void updateUserStatus(String phonenumber, String status){
        repository.updateUserStatus(phonenumber, status);
    }

}
