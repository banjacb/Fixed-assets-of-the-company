package com.example.projektnizadatak.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DataBaseMigrations {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL("ALTER TABLE Lokacija ADD COLUMN postanskiBroj INTEGER NOT NULL DEFAULT 0");
         //  database.execSQL("CREATE TABLE IF NOT EXISTS `Lista` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE IF NOT EXISTS `Lista` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`naziv` TEXT)");
            database.execSQL("ALTER TABLE OsnovnoSredstvo ADD COLUMN lista_id INTEGER");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE OsnovnoSredstvo ADD COLUMN lista_id INTEGER");
        }
    };


}
