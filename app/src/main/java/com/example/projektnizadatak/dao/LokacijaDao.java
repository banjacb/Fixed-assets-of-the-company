package com.example.projektnizadatak.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projektnizadatak.model.Lokacija;

import java.util.List;

@Dao
public interface LokacijaDao {
    @Query("SELECT * FROM Lokacija")
    List<Lokacija> getAllLokacije();

    @Insert
    void insert(Lokacija lokacija);

    @Update
    void update(Lokacija lokacija);

    @Delete
    void delete(Lokacija lokacija);

    @Query("SELECT * FROM Lokacija WHERE id = :id")
    Lokacija getLokacijaById(int id);
}
