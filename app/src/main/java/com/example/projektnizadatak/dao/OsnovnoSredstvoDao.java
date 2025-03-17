package com.example.projektnizadatak.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projektnizadatak.model.OsnovnoSredstvo;

import java.util.List;
@Dao
public interface OsnovnoSredstvoDao {
    @Query("SELECT * FROM OsnovnoSredstvo")
    List<OsnovnoSredstvo> getAllOsnovnoSredstvo();

    @Query("SELECT id, naziv, opis, barkod, cijena, datum, from_osoba_id, to_osoba_id, from_lokacija_id,to_lokacija_id, lista_id FROM OsnovnoSredstvo")
    List<OsnovnoSredstvo> getAllOsnovnoSredstvoWithoutImages();

    @Query("SELECT slika FROM OsnovnoSredstvo WHERE Id = :id")
    byte[] getImageForOS(int id);
    @Insert
    void insert(OsnovnoSredstvo osnovoSredstvo);

    @Update
    void update(OsnovnoSredstvo osnovoSredstvo);

    @Delete
    void delete(OsnovnoSredstvo osnovoSredstvo);

    @Query("SELECT * FROM OsnovnoSredstvo WHERE id = :id")
    OsnovnoSredstvo getOsnovnoSredstvoById(int id);

    @Query("SELECT * FROM OsnovnoSredstvo WHERE barkod = :barkod")
    OsnovnoSredstvo getOsnovnoSredstvoByBarkod(String barkod);

    @Query("SELECT * FROM OsnovnoSredstvo WHERE lista_id = :listaId")
    List<OsnovnoSredstvo> getSredstvaByListaId(int listaId);

}
