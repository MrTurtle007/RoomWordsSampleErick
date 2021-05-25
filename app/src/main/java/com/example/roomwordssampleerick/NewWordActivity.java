package com.example.roomwordssampleerick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.roomwordssampleerick.MainActivity.EXTRA_DATA_ID;
import static com.example.roomwordssampleerick.MainActivity.EXTRA_DATA_UPDATE_WORD;

/**
 * Esta clase muestra una pantalla donde el usuario escribe una nueva palabra.
 * NewWordActivity devuelve la palabra introducida a la actividad de llamada
 * (MainActivity), que luego almacena la nueva palabra y actualiza la lista de
 * palabras mostradas.
 */

public class NewWordActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.roomwordssampleerick.REPLY";
    public static final String EXTRA_REPLY_ID = "com.android.example.roomwordssampleerick.REPLY_ID";

    private EditText mEditWordView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);

        mEditWordView = findViewById(R.id.edit_word);
        int id = -1 ;

        final Bundle extras = getIntent().getExtras();

        //Si se pasa el contenido, se pone para editarlo
        if (extras != null) {
            String word = extras.getString(EXTRA_DATA_UPDATE_WORD, "");
            if (!word.isEmpty()) {
                mEditWordView.setText(word);
                mEditWordView.setSelection(word.length());
                mEditWordView.requestFocus();
            }
        } //Si no, comienza vacío


        final Button button = findViewById(R.id.button_save);

        //Cuando el usuario presione el Save button, se crea un new Intent para la respuesta
        //El reply Intent se enviará de vuelta a la actividad de llamada (en este caso, MainActivity).
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Crear un new Intent para la respuesta
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWordView.getText())) {
                    //No se introdujo ninguna palabra, se establece el resultado en consecuencia
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    //Se obtiene la nueva palabra que ha especificado el usuario
                    String word = mEditWordView.getText().toString();
                    //Se pone la nueva palabra en los extras para los reply Intent.
                    replyIntent.putExtra(EXTRA_REPLY, word);
                    if (extras != null && extras.containsKey(EXTRA_DATA_ID)) {
                        int id = extras.getInt(EXTRA_DATA_ID, -1);
                        if (id != -1) {
                            replyIntent.putExtra(EXTRA_REPLY_ID, id);
                        }
                    }
                    //Se inserta del Result con el resultado con éxito
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}