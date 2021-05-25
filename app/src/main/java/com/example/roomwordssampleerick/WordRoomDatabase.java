package com.example.roomwordssampleerick;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * WordRoomDatabase. Incluye código para crear la base de datos.
 * Después de que la aplicación crea la base de datos, todas las interacciones
 * sucede a través de la WordViewModel.
 */

@Database(entities = {Word.class}, version = 2, exportSchema = false)
public abstract class WordRoomDatabase extends RoomDatabase {

    public abstract WordDao wordDao();

    private static WordRoomDatabase INSTANCE;

    public static WordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDatabase.class) {
                if (INSTANCE == null) {
                    //Crear base de datos aquí
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDatabase.class, "word_database")
                            //Borra y reconstruye en lugar de migrar si no hay ningún objeto Migration
                            //La migración no forma parte de esta práctica
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Se llama a esta devolución de llamada cuando se ha abierto la base de datos
     *
     * En este caso, use PopulateDbAsync para rellenar la base de datos
     * con el conjunto de datos inicial si la base de datos no tiene entradas
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    /**
     * Rellena la base de datos con el conjunto de datos inicial
     * sólo si la base de datos no tiene entradas
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final WordDao mDao;
        //Se insertan los datos (words) iniciales
        String [] words = {"Carla", "Ximena", "Natalia", "Julieta", "Ana"};

        PopulateDbAsync(WordRoomDatabase db) {
            mDao = db.wordDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //Si no tenemos palabras, entonces crea la lista inicial de palabras
            if (mDao.getAnyWord().length < 1) {
                for (int i = 0; i <= words.length - 1; i++) {
                    Word word = new Word(words[i]);
                    mDao.insert(word);
                }
            }
            return null;
        }
    }
}