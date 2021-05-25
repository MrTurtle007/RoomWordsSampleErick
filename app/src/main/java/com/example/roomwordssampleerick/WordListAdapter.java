package com.example.roomwordssampleerick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * Adaptador para el RecyclerView que muestra una lista de palabras
 */

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    private final LayoutInflater mInflater;
    private List<Word> mWords; //Copia almacenada en caché de palabras
    private static ClickListener clickListener;

    WordListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        if (mWords != null) {
            Word current = mWords.get(position);
            holder.wordItemView.setText(current.getWord());
        } else {
            //Cubre el caso de que los datos aún no estén listos
            holder.wordItemView.setText(R.string.no_word);
        }
    }

    /**
     * Asocia una lista de palabras con este adaptador
     */
    void setWords(List<Word> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    /**
     * getItemCount() se llama muchas veces, y cuando se llama por primera vez,
     * mWords no se ha actualizado (significa inicialmente, es null, y no podemos devolver null).
     */
    @Override
    public int getItemCount() {
        if (mWords != null)
            return mWords.size();
        else return 0;
    }

    /**
     * Obtiene la palabra en una posición dada.
     * Este método es útil para identificar qué palabra
     * se ha clic o deslizado en métodos que controlan los eventos del usuario.
     * @param position
     * @return
     */
    public Word getWordAtPosition(int position) {
        return mWords.get(position);
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        WordListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

}