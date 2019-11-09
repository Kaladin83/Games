package com.example.maratbe.listeners;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maratbe.dataBase.DataLayer;
import com.example.maratbe.dataBase.MySharedPreferences;
import com.example.maratbe.domain.Theme;
import com.example.maratbe.games.Kakuro;
import com.example.maratbe.games.R;
import com.example.maratbe.games.Sudoku;
import com.example.maratbe.games.TicTacToe;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.ThemeAdapter;
import com.example.maratbe.other.Utils;

import java.util.List;

public class MenuListenerImpl implements Constants, MenuListener {
    private Dialog dialog;
    private Context context;
    private static Tasks task;
    private List<String> listOfStrings;
    private Kakuro kakuro;
    private Sudoku sudoku;
    private TicTacToe ticTacToe;
    private ThemeAdapter themeAdapter;
    private final String AUTOSAVE = "Autosave";

    private enum Tasks{
        FETCH_NAMES, FETCH_DATA
    }

    MenuListenerImpl(MenuHandler menuHandler, Object gameInstance)
    {
        setMenuListener(menuHandler);
        getContext(gameInstance);
    }

    private void setMenuListener(MenuHandler menuHandler) {
        menuHandler.setMenuListener(this);

    }

    private void getContext(Object gameInstance) {
        if (gameInstance instanceof Kakuro)
        {
            kakuro = (Kakuro)gameInstance;
            context = kakuro;
        }
        else if (gameInstance instanceof Sudoku)
        {
            sudoku = (Sudoku)gameInstance;
            context = sudoku;
        }
        else
        {
            ticTacToe = (TicTacToe)gameInstance;
            context = ticTacToe;
        }
    }

    @Override
    public void startNewGame() {
        if (kakuro != null)
        {
            kakuro.resetBoard();
        }
        else if (ticTacToe != null)
        {
            ticTacToe.resetBoard();
        }
        else
        {
            sudoku.resetBoard(true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void saveGame(List<Object> list) {
//        if (gameInstance instanceof Kakuro)
//        {
//            //Kakuro kakuro = (Kakuro) gameInstance;
//           // kakuro.setMenuListener(this);
//            dialog = new Dialog(context);
//            dialog.setContentView(R.layout.save_game);
//            dialog.show();
//        }
        dialog = new Dialog(context, MainActivity.getCurrentTheme().getThemeDialogId());
        dialog.setContentView(R.layout.save_game);
        TextView saveText = dialog.findViewById(R.id.nameToSave);
        saveText.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());

        EditText editText = dialog.findViewById(R.id.saveNameEdit);
        editText.setBackground(Utils.createBorder(10, Color.WHITE, 1, Color.BLACK));
        editText.setText(AUTOSAVE);
        editText.setSelectAllOnFocus(true);
        editText.requestFocus();

        editText.setOnTouchListener((view, motionEvent) -> {
            EditText edit = (EditText) view;
            edit.setText("");
            return false;});

        Button saveButton = dialog.findViewById(R.id.secondButton);
        Button cancelButton = dialog.findViewById(R.id.firstButton);

        cancelButton.setText(context.getString(R.string.cancel_button));
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        saveButton.setText(context.getString(R.string.save_button));
        saveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        saveButton.setOnClickListener(view -> saveData());
        dialog.show();
    }

    private void saveData() {
    }

    @Override
    public void loadGame() {
        task = Tasks.FETCH_NAMES;
        new AsyncTaskAgent().execute(KAKURO);
    }

    @Override
    public void backToGame() {
    }

    @Override
    public void chooseTheme() {
        dialog = new Dialog(context, MainActivity.getCurrentTheme().getThemeDialogId());
        dialog.setContentView(R.layout.choose_theme);

        TextView title = dialog.findViewById(R.id.themeTitle);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        themeAdapter = new ThemeAdapter(MainActivity.getThemes());
        recyclerView.setAdapter(themeAdapter);
        DividerItemDecoration decor = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decor);
        recyclerView.setBackground(Utils.createBorder(10, Color.WHITE, 2, Color.BLACK));

        Button cancelButton = dialog.findViewById(R.id.firstButton);
        Button chooseButton = dialog.findViewById(R.id.secondButton);

        cancelButton.setText(context.getString(R.string.cancel_button));
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        chooseButton.setText(context.getString(R.string.choose_button));
        chooseButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        chooseButton.setOnClickListener(v -> setTheme());

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }


    @Override
    public void onVictory() {
        dialog = new Dialog(context, MainActivity.getCurrentTheme().getThemeDialogId());
        dialog.setContentView(R.layout.victory);

        TextView title = dialog.findViewById(R.id.victoryTitle);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        TextView body = dialog.findViewById(R.id.victoryBody);
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());

        Button returnButton = dialog.findViewById(R.id.firstButton);
        Button startNewButton = dialog.findViewById(R.id.secondButton);

        returnButton.setText(context.getString(R.string.return_button));
        returnButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        startNewButton.setText(context.getString(R.string.restart_button));
        startNewButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());

        returnButton.setOnClickListener(v -> {
            dialog.dismiss();
            restart();
        });
        startNewButton.setOnClickListener(v ->{startNewGame(); dialog.dismiss();});
        dialog.show();
    }


    private void loadListOfNames() {
        dialog = new Dialog(context, MainActivity.getCurrentTheme().getThemeDialogId());
        dialog.setContentView(R.layout.load_games);

        TextView titleTxt = dialog.findViewById(R.id.nameToLoadTitle);
        titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        TextView bodyTxt = dialog.findViewById(R.id.nameToLoadBody);
        bodyTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        Spinner spinner = dialog.findViewById(R.id.loadSpinner);

        Button cancelButton = dialog.findViewById(R.id.firstButton);
        Button loadButton = dialog.findViewById(R.id.secondButton);

        cancelButton.setText(context.getString(R.string.cancel_button));
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        loadButton.setText(context.getString(R.string.load_button));
        loadButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        loadButton.setOnClickListener(v -> dialog.dismiss());

        populateLoadFields(listOfStrings, titleTxt, bodyTxt, spinner, loadButton);
        dialog.show();
    }

    private void populateLoadFields(List<String> listOfNames, TextView title, TextView body, Spinner spinner, Button loadButton) {
        if (listOfNames.isEmpty())
        {
            title.setText(context.getString(R.string.sorry));
            body.setText(context.getString(R.string.no_loaded_games));
            body.setVisibility(View.VISIBLE);
            loadButton.setEnabled(false);
            loadButton.setTextColor(GRAY_1);
        }
        else
        {
            body.setVisibility(View.GONE);
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

    private void setTheme() {
        Theme theme = MainActivity.getThemes().get(themeAdapter.getSelectedPosition());
        MySharedPreferences sp = new MySharedPreferences(context);
        sp.saveTheme(theme);
        restart();
    }

    private void restart() {
        Intent i = context.getPackageManager().
                getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
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
