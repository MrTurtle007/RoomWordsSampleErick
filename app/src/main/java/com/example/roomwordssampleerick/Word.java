package com.example.roomwordssampleerick;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entidad representada en una clase
 * Una palabra en una BD
 */

@Entity(tableName = "word_table")
public class Word {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "word")
    private String mWord;

    public Word(@NonNull String word) {
        this.mWord = word;
    }

    /*
     * Este constructor se anota usando @Ignore, porque Room espera sólo
     * un constructor de forma predeterminada en una clase de entidad.
     */

    @Ignore
    public Word(int id, @NonNull String word) {
        this.id = id;
        this.mWord = word;
    }

    public String getWord() {
        return this.mWord;
    }

    public int getId() {return id;}

    public void setId(int id) {
        this.id = id;
    }
}