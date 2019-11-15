package com.example.maratbe.listeners;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.maratbe.dataBase.dto.TransferData;
import com.example.maratbe.domain.Cell;
import com.example.maratbe.domain.Coordinates;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import io.alterac.blurkit.BlurLayout;

public abstract class ClickHandler implements Constants, ClickListener {
    private Button hintButton, revertButton;
    private RelativeLayout hintLayout;
    private TextView hintTextView;
    private TableLayout numberLayout;
    private String chosenNumber ="", previousNumber = "";
    private Cell currentCell, previousCell;
    private Stack<Cell> turns = new Stack<>();
    private ArrayList<Cell> listOfCells = new ArrayList<>();
    private HashMap<String, Button> numbersMap = new HashMap<>();
    private int[][] hintMatrix = new int[0][0];
    private boolean hintPressed = false;
    private MenuHandler menuHandler;
    private long timeToAdd = 0;
    private int hintMatrixSize, sudokuLevel;

    protected ClickHandler(TableLayout numberLayout, Button revertButton, RelativeLayout hintLayout) {
        this.numberLayout = numberLayout;
        this.revertButton = revertButton;
        this.hintLayout = hintLayout;
    }

    public BlurLayout getBlurLayout()
    {
        return menuHandler.getBlurLayout();
    }

    public String getChosenNumber()
    {
        return chosenNumber;
    }

    public void setChosenNumber(String chosenNumber) {
        this.chosenNumber = chosenNumber;
    }

    public ArrayList<Cell> getListOfCells() {
        return listOfCells;
    }

    public void setListOfCells(ArrayList<Cell> listOfCells) {
        this.listOfCells = listOfCells;
    }

    public Cell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(Cell currentCell) {
        this.currentCell = currentCell;
    }

    public Cell getPreviousCell() {
        return previousCell;
    }

    public void setPreviousCell(Cell previousCell) {
        this.previousCell = previousCell;
    }

    public boolean isHintPressed() {
        return hintPressed;
    }

    public void setHintPressed(boolean hintPressed) {
        this.hintPressed = hintPressed;
        if (hintPressed)
        {
            hintButton.setSelected(true);
            colorCellsForHints();
        }
    }

    public int getNumberOfHints() {
        return Integer.parseInt(hintTextView.getText().toString());
    }

    public void setNumberOfHints(int number) {
        this.hintTextView.setText(String.valueOf(number));
    }

    public ArrayList<Cell> getTurns() {
        return new ArrayList<>(turns);
    }

    public void setTurns(ArrayList<Cell>  listOfTurns) {
        turns = listOfTurns.stream().collect(Collectors.toCollection(Stack::new));
    }

    public long getTimeToAdd() {
        return timeToAdd;
    }

    public void setTimeToAdd(long timeToAdd) {
        this.timeToAdd = timeToAdd;
    }

    public void setMatrixSize(int size)
    {
        hintMatrixSize = size;
        hintMatrix = new int[hintMatrixSize][hintMatrixSize];
    }

    public int[][] getMatrix()
    {
        return hintMatrix;
    }

    public void setMatrix(int[][] matrix)
    {
        hintMatrix = Arrays.stream(matrix).map(int[]::clone).toArray(int[][]::new);
    }

    public int getSudokuLevel() {
        return sudokuLevel;
    }

    public void setSudokuLevel(int sudokuLevel) {
        this.sudokuLevel = sudokuLevel;
    }

    public String getPreviousNumber() {
        return previousNumber;
    }

    public void setPreviousNumber(String previousNumber) {
        this.previousNumber = previousNumber;
        numbersMap.get(previousNumber).setSelected(true);
    }

    public void resetHandler()
    {
        updatePreviousControl();
        setHintPressed(false);
        hintTextView.setText(String.valueOf(NUMBER_OF_HINTS));
        numbersMap.get("revert").setSelected(false);
        chosenNumber = "";
        previousNumber = "";
        currentCell = null;
        previousCell = null;
        turns = new Stack<>();
        listOfCells = new ArrayList<>();
    }

    public void setupMenuLayout(RelativeLayout relativeLayout, Object gameInstance)
    {
        menuHandler = new MenuHandler(relativeLayout, numberLayout, gameInstance) {
            @Override
            public void saveGame(MenuListener menuListener) {

                TransferData transferData = populateDataToSave();
                menuListener.saveGame(transferData);
            }
        };
    }

    void setFieldsFromTransferData(TransferData loadGame) {
        resetHandler();
        Utils.translateListIntoMatrix((ArrayList<Cell>) loadGame.getListOfHints(), hintMatrix);
        listOfCells = (ArrayList<Cell>) loadGame.getListOfCells();
        hintTextView.setText(String.valueOf(loadGame.getNumberOfHintsLeft()));
        timeToAdd = loadGame.getTimeToAdd();
        sudokuLevel = loadGame.getLevel();
        menuHandler.setControlButtonsSize();
    }

    private TransferData populateDataToSave() {
        TransferData transferData = new TransferData();
        transferData.setListOfCells(listOfCells);
        transferData.setListOfHints(Utils.translateMatrixIntoList(hintMatrix, hintMatrixSize));
        transferData.setNumberOfHintsLeft(Integer.parseInt(hintTextView.getText().toString()));
        transferData.setTimeToAdd(timeToAdd - SystemClock.elapsedRealtime() );
        transferData.setLevel(sudokuLevel);
        return transferData;
    }

    public void createControlPanel() {

        createNumbers(numberLayout);
        createActions(revertButton, hintLayout);
    }

    private void createNumbers(TableLayout numberLayout) {
        TableRow row;
        Button button;
        for (int i = 0; i < numberLayout.getChildCount(); i++) {
            if (numberLayout.getChildAt(i) instanceof TableRow) {
                row = (TableRow) numberLayout.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    button = (Button) row.getChildAt(j);
                    numbersMap.put(button.getTag().toString(), button);
                    button.setOnClickListener(this::onButtonControlClicked);
                }
            }
        }
    }

    private void createActions(Button revertButton, RelativeLayout hintLayout) {
        numbersMap.put(revertButton.getTag().toString(), revertButton);
        revertButton.setOnClickListener(this::onButtonControlClicked);

        hintButton = (Button) hintLayout.getChildAt(0);
        hintTextView = (TextView) hintLayout.getChildAt(1);
        hintTextView.setText(String.valueOf(NUMBER_OF_HINTS));
        hintButton.setOnClickListener(this::onLayoutControlClicked);
        hintTextView.setOnClickListener(this::onLayoutControlClicked);
    }

    private void createHint() {
        updatePreviousControl();
        int numOfHints = Integer.parseInt(hintTextView.getText().toString());
        if (numOfHints > 0)
        {
            hintButton.setSelected(!hintPressed);
            hintPressed = !hintPressed;
            colorCellsForHints();
        }
    }

    private void updatePreviousControl() {
        if (!previousNumber.equals("")) {
            numbersMap.get(previousNumber).setSelected(false);
        }
    }

    private void updateHints(boolean pressed) {
        hintPressed = false;
        if (pressed)
        {
            colorCellsForHints();
            int hints = Integer.parseInt(hintTextView.getText().toString());
            hintTextView.setText(String.valueOf(hints -1));
            hintButton.setSelected(false);
        }
    }

    public Cell getCoordinatesFromList(String name) {
        int partRow = Integer.parseInt(name.substring(1,2));
        int partCol = Integer.parseInt(name.substring(2,3));
        int x = Integer.parseInt(name.substring(3,4));
        int y = Integer.parseInt(name.substring(4,5));
        Optional<Cell> result = listOfCells.stream().
                filter(c -> c.getCoordinates().equals(new Coordinates(x, y, partRow, partCol))).findFirst();
        return result.orElseGet(Cell::new);
    }

    @Override
    public void onCellClick(String name) {
        currentCell = getCoordinatesFromList(name);

        if ((currentCell.isEnabled() && (!chosenNumber.equals("")))||
                (hintPressed && currentCell.getValue().get(currentCell.getValue().size()-1).toString().equals("") )) {
            boolean isPreviousExists = false;
            if (!turns.isEmpty()) {
                previousCell = turns.get(turns.size()-1);
                isPreviousExists = true;
            }
            updateCellNewValue(isPreviousExists);

            if (hintPressed)
            {
                updateHints(true);
                listOfCells.get(currentCell.getIndex()).getValue().add(Integer.parseInt(chosenNumber));
                chosenNumber = "";
            }
            else
            {
                listOfCells.get(currentCell.getIndex()).getValue().add(Integer.parseInt(chosenNumber));
            }

            currentCell.setColor(BLUE_1);
            turns.push(currentCell);
        }
    }

    @Override
    public void onButtonControlClicked(View view) {
        updateHints(false);
        updatePreviousControl();

        if (view.getTag().toString().equals("revert")) {
            if (!turns.isEmpty()) {
                int index = turns.pop().getIndex();
                Cell previous = listOfCells.get(index);
                if (previous.getValue().size() > 1) {
                    previous.getValue().remove(previous.getValue().size() - 1);
                } else {
                    previous.setValue(0);
                }
                listOfCells.set(index, previous);
                updateCellRevertValue(previous);
                view.setSelected(true);
            }
            if (turns.isEmpty()) {
                view.setSelected(false);
            }
        } else {
            view.setSelected(true);
            System.out.println("initial MeasuredHeight = "+ view.getMeasuredHeight());
            System.out.println("initial height = "+ view.getHeight());
            System.out.println("logicalDensity = "+ MainActivity.getLogicalDensity());
            numbersMap.get("revert").setSelected(false);
            chosenNumber = ((Button) view).getText().toString();
        }
        previousNumber = ((Button) view).getText().toString();
    }

    @Override
    public void onLayoutControlClicked(View view) {
        createHint();
    }

    @Override
    public void onMenuButtonClicked() {
        menuHandler.showMenu();
    }

    @Override
    public void onVictory() {
        menuHandler.showVictoryDialog();
    }

    @Override
    public void onTimeButtonClicked(boolean isPaused) {
        handleChronometer(isPaused);
    }

    protected abstract void colorCellsForHints();

    protected abstract void updateCellRevertValue(Cell previous);

    protected abstract void updateCellNewValue(boolean isPreviousExists);

    protected abstract void handleChronometer(boolean isPaused);
}