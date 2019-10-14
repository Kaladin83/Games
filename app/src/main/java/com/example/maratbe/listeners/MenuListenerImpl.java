package com.example.maratbe.listeners;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maratbe.dataBase.DataLayer;
import com.example.maratbe.games.Kakuro;
import com.example.maratbe.games.R;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.Utils;

import java.util.List;

public class MenuListenerImpl implements Constants, MenuListener {
    private Dialog dialog;
    private Context context;
    private static Tasks task;
    private List<String> listOfStrings;

    private enum Tasks{
        FETCH_NAMES, FETCH_DATA
    }

    public MenuListenerImpl(Object gameInstance)
    {
        setMenuListener(gameInstance);
    }

    private void setMenuListener(Object gameInstance) {
        if (gameInstance instanceof Kakuro)
        {
            Kakuro kakuro = (Kakuro) gameInstance;
            kakuro.setMenuListener(this);
        }
    }

    private void startGame(Object gameInstance) {
        if (gameInstance instanceof Kakuro)
        {
            Kakuro kakuro = (Kakuro) gameInstance;
            kakuro.resetBoard(true);
        }
    }

    @Override
    public void startNewGame(Object gameInstance, Context context) {
        startGame(gameInstance);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void saveGame(Object gameInstance, Context context, List<Object> list) {
//        if (gameInstance instanceof Kakuro)
//        {
//            //Kakuro kakuro = (Kakuro) gameInstance;
//           // kakuro.setMenuListener(this);
//            dialog = new Dialog(context);
//            dialog.setContentView(R.layout.save_game);
//            dialog.show();
//        }
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.save_game);
        Button saveButton = dialog.findViewById(R.id.saveButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        EditText editText = dialog.findViewById(R.id.saveNameEdit);
        editText.setBackground(Utils.createBorder(10, Color.WHITE, 1, Color.BLACK));
        editText.setText("Autosave");
        editText.setSelection(0, editText.getText().length());
        editText.setOnTouchListener((view, motionEvent) -> {
            EditText edit = (EditText) view;
            edit.setText("");
            return false;});
        dialog.show();
        cancelButton.setOnClickListener(view -> dialog.dismiss());
        saveButton.setOnClickListener(view -> dialog.dismiss());

    }

    @Override
    public void loadGame(Object gameInstance, Context context) {
        this.context = context;
        task = Tasks.FETCH_NAMES;
        new AsyncTaskAgent().execute(KAKURO);
    }

    private void loadListOfNames() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.load_games);

        TextView txtView = dialog.findViewById(R.id.nameToLoad);
        Spinner spinner = dialog.findViewById(R.id.loadSpinner);
        Button loadButton = dialog.findViewById(R.id.loadButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        populateLoadFields(listOfStrings, txtView, spinner, loadButton);
        dialog.show();
        cancelButton.setOnClickListener(view -> dialog.dismiss());
    }

    private void populateLoadFields(List<String> listOfNames, TextView txtView, Spinner spinner, Button loadButton) {
        if (listOfNames.isEmpty())
        {
            txtView.setText(context.getString(R.string.no_loaded_games));
            loadButton.setEnabled(false);
            loadButton.setTextColor(GRAY_1);
        }
        else
        {
            loadSpinner(listOfNames, spinner);
            loadButton.setEnabled(true);
        }
    }

    private void loadSpinner(List<String> listOfNames, Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, listOfNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private class AsyncTaskAgent extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... object) {
            DataLayer dl = new DataLayer();
            switch (task)
            {
                case FETCH_NAMES:
                    String game = (String) object[0];
                    listOfStrings = dl.loadAllGameNames(game); break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (task)
            {
                case FETCH_NAMES:
                    loadListOfNames(); break;
            }
        }
    }
}
