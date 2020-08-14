package com.example.mychatapp.localdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mychatapp.Model.LocalUser;

import java.util.List;

@Dao
public interface LocalUserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addOfficialUser(LocalUser officialuser);

    @Query("SELECT * FROM LocalUser")
    LiveData<List<LocalUser>> getAll();

    @Query("UPDATE LocalUser SET status=:status WHERE phonenumber=:phonenumber")
    void updateStatus(String phonenumber, String status);

}
