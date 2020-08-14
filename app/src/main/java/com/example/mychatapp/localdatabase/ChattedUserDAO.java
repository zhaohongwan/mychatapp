package com.example.mychatapp.localdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mychatapp.Model.ChattedUser;

import java.util.List;

@Dao
public interface ChattedUserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addUser(ChattedUser chattedUser);

    @Query("SELECT * FROM ChattedUser")
    LiveData<List<ChattedUser>> loadAllUsers();
}
