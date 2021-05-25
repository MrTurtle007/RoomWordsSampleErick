package com.example.roomwordssampleerick;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

/**
 * Esta clase muestra una lista de palabras en un RecyclerView.
 * Las palabras se guardan en una base de datos de la habitación.
 * El diseño de esta actividad también muestra una FAB que
 * permite a los usuarios iniciar NewWordActivity para agregar nuevas palabras.
 * Los usuarios pueden eliminar una palabra deslizándola o eliminando todas las palabras
 * a través del menú Opciones.
 * Cada vez que se agrega, elimina o actualiza una nueva palabra, el RecyclerView
 * mostrar la lista de palabras se actualiza automáticamente.
 */

public class MainActivity extends AppCompatActivity {

    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_WORD_ACTIVITY_REQUEST_CODE = 2;

    public static final String EXTRA_DATA_UPDATE_WORD = "extra_word_to_be_updated";
    public static final String EXTRA_DATA_ID = "extra_data_id";

    private WordViewModel mWordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Configuración para el recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Configuración para el WordViewModel
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        //Se obtienen todas las words de la BD
        //y se asocian al adaptador
        mWordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable final List<Word> words) {
                //Actualiza la copia almacenada en caché de las palabras del adaptador.
                adapter.setWords(words);
            }
        });

        //Configuración del Floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        //Agregue la funcionalidad para deslizar elementos en el
        //RecyclerView para eliminar el elemento deslizado
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    //No se implementa onMove() en esta aplicación.
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    //Cuando se desliza una palabra,
                    //se elimina esa palabra de la base de datos
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Word myWord = adapter.getWordAtPosition(position);
                        Toast.makeText(MainActivity.this,
                                getString(R.string.delete_word_preamble) + " " +
                                        myWord.getWord(), Toast.LENGTH_LONG).show();

                        //Borrar la palabra
                        mWordViewModel.deleteWord(myWord);
                    }
                });
        //Se conecta el elemento táctil auxiliar a la vista de reciclador
        helper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new WordListAdapter.ClickListener()  {

            @Override
            public void onItemClick(View v, int position) {
                Word word = adapter.getWordAtPosition(position);
                launchUpdateWordActivity(word);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflar el menú; esto agrega elementos a la barra de acciones si está presente
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //El menú de opciones tiene un solo elemento "Clear all data now"
    //que elimina todas las entradas de la base de datos
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Los clics del elemento de la barra de acciones se manejan aquí. La barra de acción
        //manejar automáticamente los clics en el Home/Up button, siempre y cuando
        //a medida que especifica una parent activity en AndroidManifest.xml
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_data) {
            //Toast de confirmación
            Toast.makeText(this, R.string.clear_data_toast_text, Toast.LENGTH_LONG).show();

            //Borrar los datos existentes
            mWordViewModel.deleteAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Cuando el usuario escribe una nueva palabra en el NewWordActivity,
     * esa actividad devuelve el resultado a esta actividad.
     * Si el usuario ha introducido una nueva palabra, guárdela en la base de datos.

     * @param requestCode ID para la solicitud
     * @param resultCode indica éxito o fracaso
     * @param data La intención enviada de nuevo desde el NewWordActivity,
     * que incluye la palabra que el usuario introdujo
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Word word = new Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY));
            //Guardar la info
            mWordViewModel.insert(word);
        } else if (requestCode == UPDATE_WORD_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            String word_data = data.getStringExtra(NewWordActivity.EXTRA_REPLY);
            int id = data.getIntExtra(NewWordActivity.EXTRA_REPLY_ID, -1);

            if (id != -1) {
                mWordViewModel.update(new Word(id, word_data));
            } else {
                Toast.makeText(this, R.string.unable_to_update,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(
                    this, R.string.empty_not_saved, Toast.LENGTH_LONG).show();
        }
    }

    public void launchUpdateWordActivity( Word word) {
        Intent intent = new Intent(this, NewWordActivity.class);
        intent.putExtra(EXTRA_DATA_UPDATE_WORD, word.getWord());
        intent.putExtra(EXTRA_DATA_ID, word.getId());
        startActivityForResult(intent, UPDATE_WORD_ACTIVITY_REQUEST_CODE);
    }
}