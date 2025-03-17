package com.example.projektnizadatak.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projektnizadatak.model.Lista;

import java.util.List;

@Dao
public interface ListaDao {

    @Insert
    void insert(Lista lista);

    @Query("SELECT * FROM Lista")
    List<Lista> getAll();

    @Query("DELETE FROM Lista")
    void deleteAll();
}
