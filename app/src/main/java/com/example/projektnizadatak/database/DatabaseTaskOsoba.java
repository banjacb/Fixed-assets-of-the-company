package com.example.projektnizadatak.database;
import android.os.AsyncTask;

import com.example.projektnizadatak.model.Osoba;

import java.lang.ref.WeakReference;

public class DatabaseTaskOsoba extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<DatabaseTaskListener> listenerReference;
    private DataBase dataBase;
    private Osoba osoba;
    private int operationType;

    public DatabaseTaskOsoba(DatabaseTaskListener listener, DataBase db, Osoba person, int operationType) {
        this.listenerReference = new WeakReference<>(listener);
        this.dataBase = db;
        this.osoba = person;
        this.operationType = operationType;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            switch (operationType) {
                case 0: // Insert
                    if (osoba != null) {
                        dataBase.osobaDao().insert(osoba);
                    }
                    break;
                case 1:
                    if (osoba != null) {
                        dataBase.osobaDao().update(osoba);
                    }
                    break;
                case 2:
                    if (osoba != null) {
                        dataBase.osobaDao().delete(osoba);
                    }
                case 3:
                    if (osoba != null) {
                        dataBase.osobaDao().getAllOsobe();
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
