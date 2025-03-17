package com.example.projektnizadatak.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projektnizadatak.model.Osoba;
import java.util.List;
@Dao
public interface OsobaDao {
    @Query("SELECT * FROM Osoba")
    List<Osoba> getAllOsobe();

    @Insert
    void insert(Osoba osoba);

    @Update
    void update(Osoba osoba);

    @Delete
    void delete(Osoba osoba);

    @Query("SELECT * FROM Osoba WHERE id = :id")
    Osoba getOsobaById(int id);
}
