package com.example.maratbe.listeners;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.maratbe.domain.Cell;
import com.example.maratbe.domain.Coordinates;
import com.example.maratbe.other.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public abstract class ClickHandler implements Constants, ClickListener {
    private Button hintButton;
    private TextView hintTextView;
    private String chosenNumber ="", previousNumber = "";
    private Cell currentCell, previousCell;
    private Stack<Cell> turns = new Stack<>();
    private ArrayList<Cell> listOfCells = new ArrayList<>();
    private HashMap<String, Button> numbersMap = new HashMap<>();
    private boolean hintPressed = false;
    private MenuHandler menuHandler;

    protected ClickHandler(RelativeLayout relativeLayout, Object gameInstance) {
        setupMenuLayout(relativeLayout, gameInstance);
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

    private void setupMenuLayout(RelativeLayout relativeLayout, Object gameInstance) {
        menuHandler = new MenuHandler(relativeLayout, gameInstance) {
            @Override
            public void saveGame(MenuListener menuListener) {
                menuListener.saveGame(Collections.singletonList(listOfCells));
            }
        };
    }

    public void createControlPanel(TableLayout numberLayout) {
        TableRow row;
        Button button;
        for (int i = 0; i < numberLayout.getChildCount(); i++) {
            if (numberLayout.getChildAt(i) instanceof TableRow) {
                row = (TableRow) numberLayout.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    if (row.getChildAt(j) instanceof Button) {
                        button = (Button) row.getChildAt(j);
                        numbersMap.put(button.getTag().toString(), button);
                        button.setOnClickListener(this::onButtonControlClicked);
                    }
                    else{
                        RelativeLayout rLayout = (RelativeLayout) row.getChildAt(j);
                        hintButton = (Button) rLayout.getChildAt(0);
                        hintTextView = (TextView) rLayout.getChildAt(1);
                        hintButton.setOnClickListener(this::onLayoutControlClicked);
                        hintTextView.setOnClickListener(this::onLayoutControlClicked);
                    }
                }
            }
        }
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
            Objects.requireNonNull(numbersMap.get(previousNumber)).setBackgroundColor(BLUE_6);
        }
    }

    private void updateHints(boolean pressed) {
        hintPressed = false;
        if (pressed)
        {
            colorCellsForHints();
        }
        hintButton.setSelected(false);
        if (pressed)
        {
            int hints = Integer.parseInt(hintTextView.getText().toString());
            hintTextView.setText(String.valueOf(hints -1));
        }
    }

    @Override
    public void onCellClick(String name) {
        currentCell = getCoordinatesFromList(name);

        if (currentCell.isEnabled() && (!chosenNumber.equals("") || hintPressed)) {
            boolean isPreviousExists = false;
            if (!turns.isEmpty()) {
                previousCell = turns.get(turns.size()-1);
                isPreviousExists = true;
            }
            updateCellNewValue(isPreviousExists);
            turns.push(currentCell);
            if (hintPressed)
            {
                updateHints(true);
            }

            currentCell.setColor(BLUE_1);
            listOfCells.get(currentCell.getIndex()).getValue().add(Integer.parseInt(chosenNumber));
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
    public void onButtonControlClicked(View view) {
        updateHints(false);
        updatePreviousControl();

        if (view.getTag().toString().equals("revert")) {
            if (!turns.isEmpty()) {
                int index = turns.pop().getIndex();
                Cell previous = listOfCells.get(index);
                if (previous.getValue().size() > 1)
                {
                    previous.getValue().remove(previous.getValue().size() - 1);
                }
                else {
                    previous.setValue(0);
                }
                listOfCells.set(index, previous);
                updateCellRevertValue(previous);
                view.setSelected(!view.isSelected());
            }
        } else {
            view.setBackgroundColor(GREEN_5);
            Objects.requireNonNull(numbersMap.get("revert")).setSelected(false);
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
    public void onTimeButtonClicked(boolean isPaused) {
        handleChronometer(isPaused);
    }

    protected abstract void colorCellsForHints();

    protected abstract void updateCellRevertValue(Cell previous);

    protected abstract void updateCellNewValue(boolean isPreviousExists);

    protected abstract void handleChronometer(boolean isPaused);
}
