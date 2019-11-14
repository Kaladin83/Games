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
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maratbe.dataBase.DataLayer;
import com.example.maratbe.dataBase.MySharedPreferences;
import com.example.maratbe.dataBase.dto.SavedGame;
import com.example.maratbe.dataBase.dto.TransferData;
import com.example.maratbe.domain.Theme;
import com.example.maratbe.games.Kakuro;
import com.example.maratbe.games.R;
import com.example.maratbe.games.Sudoku;
import com.example.maratbe.games.TicTacToe;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.ThemeAdapter;
import com.example.maratbe.other.Utils;

import java.util.ArrayList;
import java.util.List;

public class MenuListenerImpl implements Constants, MenuListener {
    private Dialog dialog;
    private Context context;
    private static Tasks task;
    private List<String> listOfStrings = new ArrayList<>();
    private Kakuro kakuro;
    private Sudoku sudoku;
    private TicTacToe ticTacToe;
    private ThemeAdapter themeAdapter;
    private final String AUTOSAVE = "Autosave";
    private Object gameInstance;
    private String game;
    Spinner spinner;
    private TransferData transferData = new TransferData();

    private enum Tasks{
        FETCH_NAMES, SAVE_DATA, FETCH_DATA, IS_NAME_EXISTS
    }

    MenuListenerImpl(MenuHandler menuHandler, Object gameInstance)
    {
        this.gameInstance = gameInstance;
        setMenuListener(menuHandler);
        getContext();
    }

    private void setMenuListener(MenuHandler menuHandler) {
        menuHandler.setMenuListener(this);

    }

    private void getContext() {
        if (gameInstance instanceof Kakuro)
        {
            kakuro = (Kakuro)gameInstance;
            context = kakuro;
            game = KAKURO;
        }
        else if (gameInstance instanceof Sudoku)
        {
            sudoku = (Sudoku)gameInstance;
            context = sudoku;
            game = SUDOKU;
        }
        else
        {
            ticTacToe = (TicTacToe)gameInstance;
            context = ticTacToe;
            game = TIC_TAC_TOE;
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
    public void saveGame(TransferData transferData) {
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
        saveButton.setOnClickListener(view -> saveData(transferData, editText.getText().toString()));
        dialog.show();
    }

    private void saveData(TransferData transferData, String nameToSave) {
        dialog.dismiss();
        if (!nameToSave.equals(""))
        {
            transferData.setChosenName(nameToSave);
            task = Tasks.IS_NAME_EXISTS;
            new AsyncTaskAgent().execute(transferData, nameToSave);
        }
    }

    @Override
    public TransferData loadGame() {
        task = Tasks.FETCH_NAMES;
        new AsyncTaskAgent().execute();
        return transferData;
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
        Button[] buttons = build2ButtonsMessageDialog(R.string.well_done, R.string.victory_body,
                R.string.return_button, R.string.restart_button);
        buttons[0].setOnClickListener(v ->{restart(); dialog.dismiss();});
        buttons[1].setOnClickListener(v ->{startNewGame(); dialog.dismiss();});
    }


    private void loadListOfNames() {
        dialog = new Dialog(context, MainActivity.getCurrentTheme().getThemeDialogId());
        dialog.setContentView(R.layout.load_games);

        TextView titleTxt = dialog.findViewById(R.id.nameToLoadTitle);
        titleTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        TextView bodyTxt = dialog.findViewById(R.id.nameToLoadBody);
        bodyTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        spinner = dialog.findViewById(R.id.loadSpinner);

        Button cancelButton = dialog.findViewById(R.id.firstButton);
        Button loadButton = dialog.findViewById(R.id.secondButton);

        cancelButton.setText(context.getString(R.string.cancel_button));
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        loadButton.setText(context.getString(R.string.load_button));
        loadButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        loadButton.setOnClickListener(v -> loadSelectedGame());

        listOfStrings.add("String number 1");
        listOfStrings.add("Saved game number 2");
        listOfStrings.add("Kvazemodo");
        listOfStrings.add("I love mmy kids");
        populateLoadFields(listOfStrings, titleTxt, bodyTxt, spinner, loadButton);
        dialog.show();
    }

    private void loadSelectedGame() {
        dialog.dismiss();
        String selectedName = spinner.getSelectedItem().toString();
        task = Tasks.FETCH_DATA;
        new AsyncTaskAgent().execute(selectedName);
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

    private void handleNameValidation(boolean isNameExists, TransferData dataToSave) {
        if(isNameExists)
        {
            Button[] buttons = build2ButtonsMessageDialog(R.string.hi_there, R.string.game_exists_body,
                    R.string.rename_button, R.string.override_button);
            buttons[0].setOnClickListener(v -> dialog.dismiss());
            buttons[1].setOnClickListener(v -> saveTheGame(dataToSave));
        }
        else {
            saveTheGame(dataToSave);
        }
    }

    private void saveTheGame(TransferData dataToSave) {
        task = Tasks.SAVE_DATA;
        new AsyncTaskAgent().execute(dataToSave);
        dialog.dismiss();
    }

    private void handleSavedData() {
        dialog.dismiss();
        Button button = build1ButtonMessageDialog(R.string.well_done, R.string.loaded_successfully,
                R.string.ok);
        button.setOnClickListener(v ->
            dialog.dismiss());
    }


    private TransferData handleLoadedData(TransferData loadedData) {
        if (loadedData.getListOfHints().size() > 0)
        {
            Button button = build1ButtonMessageDialog(R.string.well_done, R.string.loaded_successfully,
                    R.string.ok);
            button.setOnClickListener(v -> {
                dialog.dismiss();
                if (gameInstance instanceof Sudoku)
                {
                    sudoku.getClickHandler().setFieldsFromTransferData(loadedData);
                    sudoku.buildGui(null, true);
                }
                else
                {
                    kakuro.getClickHandler().setFieldsFromTransferData(loadedData);
                    kakuro.buildGui(null, true);
                }

                });
        }
        else
        {
            Button button = build1ButtonMessageDialog(R.string.sorry, R.string.something_went_wrong,
                    R.string.ok);
            button.setOnClickListener(v -> dialog.dismiss());
        }

        return loadedData;
    }

    private Button build1ButtonMessageDialog(int titleTxt, int bodyTxt, int button1Txt) {
        dialog = new Dialog(context, MainActivity.getCurrentTheme().getThemeDialogId());
        dialog.setContentView(R.layout.dialog_1_button_message_template);

        TextView title = dialog.findViewById(R.id.messageTemplateTitle);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        title.setText(dialog.getContext().getString(titleTxt));
        TextView body = dialog.findViewById(R.id.messageTemplateBody);
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        body.setText(dialog.getContext().getString(bodyTxt));

        Button firstButton = dialog.findViewById(R.id.firstButton);

        firstButton.setText(context.getString(button1Txt));
        firstButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());

        dialog.show();
        return firstButton;
    }
    private Button[] build2ButtonsMessageDialog(int titleTxt, int bodyTxt, int button1Txt, int button2Txt) {
        dialog = new Dialog(context, MainActivity.getCurrentTheme().getThemeDialogId());
        dialog.setContentView(R.layout.dialog_2_buttons_message_template);

        TextView title = dialog.findViewById(R.id.messageTemplateTitle);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getTitleFontSize());
        title.setText(dialog.getContext().getString(titleTxt));
        TextView body = dialog.findViewById(R.id.messageTemplateBody);
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        body.setText(dialog.getContext().getString(bodyTxt));

        Button firstButton = dialog.findViewById(R.id.firstButton);
        Button secondButton = dialog.findViewById(R.id.secondButton);

        firstButton.setText(context.getString(button1Txt));
        firstButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());
        secondButton.setText(context.getString(button2Txt));
        secondButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getRegularFontSize());

        dialog.show();
        return new Button[]{firstButton, secondButton};
    }

    private class AsyncTaskAgent extends AsyncTask<Object, Void, Void> {
        private boolean isNameExists = false;
        @Override
        protected Void doInBackground(Object... object) {
            DataLayer dl = new DataLayer();
            switch (task)
            {
                case IS_NAME_EXISTS:
                    transferData = (TransferData) object[0];
                    isNameExists = dl.isNameExists(game, (String)object[1]); break;
                case FETCH_NAMES:
                    listOfStrings = dl.loadAllGameNames(game); break;
                case SAVE_DATA:
                    dl.saveGame(game, (TransferData)object[0]); break;
                case FETCH_DATA:
                    transferData = dl.loadGame(game, (String)object[0]); break;
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (task)
            {
                case IS_NAME_EXISTS:
                    handleNameValidation(isNameExists, transferData);break;
                case FETCH_NAMES:
                    loadListOfNames(); break;
                case SAVE_DATA:
                    handleSavedData();break;
                case FETCH_DATA:
                    handleLoadedData(transferData);break;
            }
        }
    }
}
