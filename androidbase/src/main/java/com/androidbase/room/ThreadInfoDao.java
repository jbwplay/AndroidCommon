package com.androidbase.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ThreadInfoDao {

    @Query("SELECT * FROM threadinfo WHERE tag = :tag")
    public List<ThreadInfo> loadAll(String tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertThreadInfo(ThreadInfo threadInfo);

    @Query("DELETE FROM threadinfo WHERE tag = :tag")
    void deleteThreadInfo(String tag);

}
