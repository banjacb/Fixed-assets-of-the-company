package com.example.projektnizadatak.database;

import android.os.AsyncTask;

import com.example.projektnizadatak.model.OsnovnoSredstvo;

import java.lang.ref.WeakReference;

public class DatabaseTaskOs extends AsyncTask<Void, Void, Boolean> {
    private WeakReference<DatabaseTaskListener> listenerReference;
    private DataBase dataBase;
    private OsnovnoSredstvo osnovnoSredstvo;
    private int operationType;


    public DatabaseTaskOs(DatabaseTaskListener listener, DataBase db, OsnovnoSredstvo osnovnoSredstvo, int operationType) {
        this.listenerReference = new WeakReference<>(listener);
        this.dataBase = db;
        this.osnovnoSredstvo = osnovnoSredstvo;
        this.operationType = operationType;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            switch (operationType) {
                case 0: // Insert
                    if (osnovnoSredstvo != null) {
                        dataBase.osnovnoSredstvoDao().insert(osnovnoSredstvo);
                    }
                    break;
                case 1: // Update
                    if (osnovnoSredstvo != null) {
                        dataBase.osnovnoSredstvoDao().update(osnovnoSredstvo);
                    }
                    break;
                case 2: // Delete
                    if (osnovnoSredstvo != null) {
                        dataBase.osnovnoSredstvoDao().delete(osnovnoSredstvo);
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
