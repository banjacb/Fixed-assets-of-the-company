package com.example.projektnizadatak.database;



import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.projektnizadatak.dao.ListaDao;
import com.example.projektnizadatak.dao.LokacijaDao;
import com.example.projektnizadatak.dao.OsnovnoSredstvoDao;
import com.example.projektnizadatak.dao.OsobaDao;
import com.example.projektnizadatak.model.Lista;
import com.example.projektnizadatak.model.Lokacija;
import com.example.projektnizadatak.model.OsnovnoSredstvo;
import com.example.projektnizadatak.model.Osoba;

@Database(entities = {OsnovnoSredstvo.class, Osoba.class, Lokacija.class, Lista.class}, version = 6)
@TypeConverters({DataBaseConvertor.class})
public abstract class DataBase extends RoomDatabase {

    public abstract OsnovnoSredstvoDao osnovnoSredstvoDao();
    public abstract OsobaDao osobaDao();
    public abstract LokacijaDao lokacijaDao();

    public  abstract ListaDao listaDao();

    private static DataBase dataBase;

    public static DataBase getInstance(Context context){
        if (dataBase == null) {
            dataBase = Room.databaseBuilder(context,
                            DataBase.class, "DataBase")
                    .addMigrations(DataBaseMigrations.MIGRATION_1_2, DataBaseMigrations.MIGRATION_2_3, DataBaseMigrations.MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dataBase;
    }



}
