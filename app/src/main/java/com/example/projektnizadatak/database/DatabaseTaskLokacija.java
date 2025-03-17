package com.example.projektnizadatak.database;

import android.os.AsyncTask;

import com.example.projektnizadatak.model.Lokacija;
import com.example.projektnizadatak.model.OsnovnoSredstvo;
import com.example.projektnizadatak.model.Osoba;

import java.lang.ref.WeakReference;
import java.util.List;

public class DatabaseTaskLokacija extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<DatabaseTaskListener> listenerReference;
    private DataBase dataBase;
    private Lokacija lokacija;
    private int operationType; //
    public DatabaseTaskLokacija(DatabaseTaskLokacija.DatabaseTaskListener listener, DataBase db, Lokacija lokacija, int operationType) {
        this.listenerReference = new WeakReference<>(listener);
        this.dataBase = db;
        this.lokacija = lokacija;
        this.operationType = operationType;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            switch (operationType) {
                case 0:
                    if (lokacija != null) {
                        dataBase.lokacijaDao().insert(lokacija);
                    }
                    break;
                case 1:
                    if (lokacija != null) {
                        dataBase.lokacijaDao().update(lokacija);
                    }
                    break;
                case 2:
                    if (lokacija != null) {
                        dataBase.lokacijaDao().delete(lokacija);
                    }
                case 3:
                    if (lokacija != null) {
                         dataBase.lokacijaDao().getAllLokacije();
                    }
                    break;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        DatabaseTaskListener listener = listenerReference.get();
        if (listener != null) {
            listener.onTaskCompleted(result);
        }
    }


    public interface DatabaseTaskListener {
        void onTaskCompleted(boolean success);
    }
}
