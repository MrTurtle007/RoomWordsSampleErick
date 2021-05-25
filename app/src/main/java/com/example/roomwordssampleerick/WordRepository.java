package com.example.roomwordssampleerick;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Esta clase contiene el código de implementación para los métodos que interactúan con la base de datos.
 * El uso de un repositorio nos permite agrupar los métodos de implementación,
 * y permite que el WordViewModel sea una interfaz limpia entre el resto de la aplicación
 * y la base de datos.
 * Para insertar, actualizar y eliminar, y consultas de ejecución prolongada,
 * debe ejecutar los métodos de interacción de la base de datos en segundo plano.
 *
 * Normalmente, todo lo que necesita hacer para implementar un método de base de datos
 * es llamarlo en el objeto de acceso a datos (DAO), en segundo plano si corresponde.
 */

public class WordRepository {

    private WordDao mWordDao;
    private LiveData<List<Word>> mAllWords;

    WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getAllWords();
    }

    LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert (Word word) {
        new insertAsyncTask(mWordDao).execute(word);
    }

    public void update(Word word)  {
        new updateWordAsyncTask(mWordDao).execute(word);
    }

    public void deleteAll()  {
        new deleteAllWordsAsyncTask(mWordDao).execute();
    }

    //Debe ejecutarse fuera del hilo principal
    public void deleteWord(Word word) {
        new deleteWordAsyncTask(mWordDao).execute(word);
    }

    //Clases internas estáticas a continuación aquí para ejecutar interacciones de base de datos en segundo plano

    /**
     * Inserta una palabra en la base de datos.
     */
    private static class insertAsyncTask extends AsyncTask<Word, Void, Void> {

        private WordDao mAsyncTaskDao;

        insertAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Word... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Elimina todas las palabras de la base de datos (no elimina la tabla).
     */
    private static class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao mAsyncTaskDao;

        deleteAllWordsAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    /**
     * Elimina una sola palabra de la base de datos.
     */
    private static class deleteWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mAsyncTaskDao;

        deleteWordAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Word... params) {
            mAsyncTaskDao.deleteWord(params[0]);
            return null;
        }
    }

    /**
     * Actualiza una palabra en la base de datos.
     */
    private static class updateWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mAsyncTaskDao;

        updateWordAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Word... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
