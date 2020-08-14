package com.example.mychatapp.localdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mychatapp.Model.Localchat;

import java.util.List;

//Dao = Database access object
@Dao
public interface LocalChatDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addChat(Localchat chat);

    @Query("SELECT * FROM Localchat")
    LiveData<List<Localchat>> loadAllChats();

    @Query("Delete FROM Localchat")
    void deleteAll();

    @Query("UPDATE Localchat SET status=:status WHERE conversation_id=:conversation_id")
    void updateStatus(int conversation_id, String status);

    @Query("UPDATE Localchat SET identity=:identity WHERE conversation_id=:conversation_id")
    void updateIdentity(int conversation_id, String identity);

    @Query("UPDATE Localchat SET txt_message=:txt_message WHERE conversation_id=:conversation_id")
    void updateMessage(int conversation_id, String txt_message);

    @Query("UPDATE Localchat SET seed=:seed WHERE conversation_id=:conversation_id")
    void updateSeed(int conversation_id, String seed);

}
