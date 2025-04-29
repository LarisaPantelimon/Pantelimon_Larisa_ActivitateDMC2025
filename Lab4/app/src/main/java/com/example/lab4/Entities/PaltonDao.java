package com.example.lab4.Entities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lab4.Palton;

import java.util.List;

@Dao
public interface PaltonDao {
    @Insert
    void insert(Palton palton);

    @Update
    void update(Palton palton);

    @Delete
    void delete(Palton palton);

    @Query("SELECT * FROM paltoane")
    LiveData<List<Palton>> getAll();
}
