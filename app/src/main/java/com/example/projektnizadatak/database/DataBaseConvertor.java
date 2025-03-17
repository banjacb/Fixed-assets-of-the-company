package com.example.projektnizadatak.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DataBaseConvertor {

    @TypeConverter
    public static Date vrijednostDatum(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long datumVrijednost(Date date) {
        return date == null ? null : date.getTime();
    }
}
