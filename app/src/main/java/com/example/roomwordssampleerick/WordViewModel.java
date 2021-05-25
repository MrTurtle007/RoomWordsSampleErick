package com.example.roomwordssampleerick;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * El WordViewModel proporciona la interfaz entre la interfaz de usuario y la capa de datos de la aplicación,
 * representado por el Repositorio
 */

public class WordViewModel extends AndroidViewModel {

    private WordRepository mRepository;

    private LiveData<List<Word>> mAllWords;

    public WordViewModel(Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert(Word word) {
        mRepository.insert(word);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteWord(Word word) {
        mRepository.deleteWord(word);
    }

    public void update(Word word) {
        mRepository.update(word);
    }
}