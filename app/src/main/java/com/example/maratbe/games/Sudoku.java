package com.example.maratbe.games;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maratbe.domain.Coordinates;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Sudoku extends AppCompatActivity implements Constants {
    private TableLayout sudokuBoard;
    private RadioButton easyRadio, moderateRadio, hardRadio;
    private ArrayList<Integer> allNumbers = new ArrayList<>();
    private ArrayList<Integer> missing = new ArrayList<>();
    private ArrayList<String> opened = new ArrayList<>();
    private ArrayList<String> closed = new ArrayList<>();
    private ArrayList<Coordinates> coordinatesList = new ArrayList<>();
    private HashMap<String, Coordinates> cellsMap = new HashMap<>();
    private HashMap<String, Button> numbersMap = new HashMap<>();
    private HashMap<String, Integer> numberColors = new HashMap<>();
    private Coordinates cor = new Coordinates();
    private Coordinates currentCoordinates = new Coordinates(); 
    private Coordinates previousChoice = new Coordinates();
    private int conflictNumber = 0, yIndex = 0, difficultyStart = DIFFICULTY_START_MODERATE, difficultyRange = DIFFICULTY_RANGE_MODERATE;
    private String chosenNumber = "";
    private String previousNumber = "";
    private int[][] matrix = new int[9][9];
    int[] newCoordinatesToCompare = new int[]{-1,-1,-1,-1};

    private String prevConflict = "No Conflict";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        }
        initColorMap();
        buildGui(savedInstanceState);

        Button newGameBtn = findViewById(R.id.newGameBtn);
        newGameBtn.setOnClickListener(v ->
                resetBoard(true)
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("coordinatesList", coordinatesList);
        outState.putParcelable("currentCoordinates",currentCoordinates);
        outState.putParcelable("previousChoice",previousChoice);
    }

    private void resetBoard(boolean clearBoard) {
        setDifficulty(getStartDifficulty(), getRangeDifficulty(), false);
        createBoard(clearBoard);
        deleteValues();
        populateCoordinatesList();
//        if (checkForVictory())
//        {
//
//            colorGrid();
//        }
    }

    private int getStartDifficulty() {
        if (easyRadio.isChecked())
        {
            return DIFFICULTY_START_EASY;
        }
        if (moderateRadio.isChecked())
        {
            return DIFFICULTY_START_MODERATE;
        }
        return DIFFICULTY_START_HARD;
    }

    private int getRangeDifficulty() {
        if (easyRadio.isChecked())
        {
            return DIFFICULTY_RANGE_EASY;
        }
        if (moderateRadio.isChecked())
        {
            return DIFFICULTY_RANGE_MODERATE;
        }
        return DIFFICULTY_RANGE_HARD;
    }

    private void populateCoordinatesList() {
        coordinatesList.clear();
        iterateTableView(FUNCTION_POPULATE_LIST_OF_SAVED_INSTANCE, new ArrayList<>());
    }

    private void deleteValues() {
        Random rand = new Random();
        for (int i = 0; i < 9; i++)
        {
            initOpened();
            closed.clear();

            int n = rand.nextInt(difficultyRange)+ difficultyStart;
            Integer[] closedArray = new Integer[9];
            for (int j = 0; j < n; j++)
            {
                int m = findCoordinatesToClose(closedArray);
                closed.add(opened.get(m));
            }
            closeButtons(i);
        }
    }

    private int findCoordinatesToClose(Integer[] closedArray) {
        Random rand = new Random();
        int m = rand.nextInt(9);
        if(closedArray[m] != null)
        {
            m = findCoordinatesToClose(closedArray);
        }
        closedArray[m] = m;
        return m;
    }

    private void closeButtons(int i) {
        for (String close: closed)
        {
            String[] c = close.split(",");
            getCoordinates(i, Integer.parseInt(c[0]), Integer.parseInt(c[1]));
            putNumberInButton(cor.getPartRow(), cor.getPartCol(), cor.getX(), cor.getY(), "");
            Button b = getButton(cor.getPartRow(), cor.getPartCol(), cor.getX(), cor.getY());
            b.setText("");
            Objects.requireNonNull(cellsMap.get(b.getTag().toString())).setEnabled(true);
            fillUpTempMatrix(cor.getPartRow(), cor.getPartCol(), cor.getX(), cor.getY(), 0);
        }
    }

    private void getCoordinates(int part, int x, int y) {
        switch (part)
        {
            case 0: cor = new Coordinates(x, y, 0, 0); break;
            case 1: cor = new Coordinates(x, y, 0, 1); break;
            case 2: cor = new Coordinates(x, y, 0, 2); break;
            case 3: cor = new Coordinates(x, y, 1, 0); break;
            case 4: cor = new Coordinates(x, y, 1, 1); break;
            case 5: cor = new Coordinates(x, y, 1, 2); break;
            case 6: cor = new Coordinates(x, y, 2, 0); break;
            case 7: cor = new Coordinates(x, y, 2, 1); break;
            default: cor = new Coordinates(x, y, 2, 2); break;
        }
    }

    private void buildGui(Bundle savedInstanceState) {
        sudokuBoard = findViewById(R.id.sudokuBoard);
        setRadios();
        if (savedInstanceState != null)
        {
            currentCoordinates = (Coordinates) savedInstanceState.get("currentCoordinates");
            previousChoice = (Coordinates) savedInstanceState.get("previousChoice");
            coordinatesList = (ArrayList<Coordinates>) savedInstanceState.get("coordinatesList");
            iterateTableView(FUNCTION_READ_LIST_FROM_SAVED_INSTANCE, new ArrayList<>());
        }
        else
        {
            resetBoard(false);
        }
        createChoiceNumbers();
    }

    private void setRadios() {
        easyRadio = findViewById(R.id.easyRadio);
        easyRadio.setOnClickListener(v ->
            setDifficulty(DIFFICULTY_START_EASY, DIFFICULTY_RANGE_EASY,true));
        moderateRadio = findViewById(R.id.moderateRadio);
        moderateRadio.setOnClickListener(v ->
            setDifficulty(DIFFICULTY_START_MODERATE, DIFFICULTY_RANGE_MODERATE, true));
        hardRadio = findViewById(R.id.hardRadio);
        hardRadio.setOnClickListener(v ->
            setDifficulty(DIFFICULTY_START_HARD, DIFFICULTY_RANGE_HARD, true));
    }

    private void setDifficulty(int start, int range, boolean toReset) {
        difficultyStart = start;
        difficultyRange = range;
        if (toReset)
        {
            resetBoard(true);
        }

    }

    private void createChoiceNumbers() {
        TableLayout numberLayout = findViewById(R.id.choiceButtonsLayout);
        TableRow row;
        Button button;
        for (int i = 0; i < numberLayout.getChildCount() ;i++)
        {
            if (numberLayout.getChildAt(i) instanceof TableRow)
            {
                row = (TableRow) numberLayout.getChildAt(i);
                for (int j = 0; j < row.getChildCount() ;j++) {
                    if (row.getChildAt(j) instanceof Button) {
                        button = (Button) row.getChildAt(j);
                        numbersMap.put(button.getTag().toString(), button);
                        button.setOnClickListener(v -> {
                            if (!previousNumber.equals(""))
                            {
                                Objects.requireNonNull(numbersMap.get(previousNumber)).setBackgroundColor(BLUE_6);
                            }

                            if (v.getTag().toString().equals("revert"))
                            {
                                if (previousChoice.getValue() > 0)
                                {
                                    putNumberInButton(previousChoice.getPartRow(), previousChoice.getPartCol(), previousChoice.getX(), previousChoice.getY(), String.valueOf(previousChoice.getValue()));
                                    Button b = getButton(previousChoice.getPartRow(), previousChoice.getPartCol(), previousChoice.getX(), previousChoice.getY());
                                    b.setTextColor(BLUE_1);
                                    ((FrameLayout)b.getParent()).setBackgroundColor(GRAY_2);
                                }

                                v.setSelected(!v.isSelected());
                            }
                            else
                            {
                                openButtons();
                                v.setBackgroundColor(GREEN_5);
                                Objects.requireNonNull(numbersMap.get("revert")).setSelected(false);
                                chosenNumber = ((Button)v).getText().toString();
                            }
                            previousNumber = ((Button)v).getText().toString();
                        });
                    }
                }
            }
        }
    }

    private void openButtons() {
        cellsMap.forEach((key,value) -> {
            getButton(value.getPartRow(), value.getPartCol(), value.getX(), value.getY()).setEnabled(true);
        });

    }

    private void closeButtons() {
        cellsMap.forEach((key,value) -> {
         //   Coordinates c = value.getCoordinates();
            Button b = getButton(value.getPartRow(), value.getPartCol(), value.getX(), value.getY());
            b.setEnabled(false);
        });

    }

    private void initAllNumbersNumbers() {
        allNumbers.clear();
        allNumbers.add(1);
        allNumbers.add(2);
        allNumbers.add(3);
        allNumbers.add(4);
        allNumbers.add(5);
        allNumbers.add(6);
        allNumbers.add(7);
        allNumbers.add(8);
        allNumbers.add(9);
    }

    private void initOpened() {
        opened.clear();
        opened.add("0,0");
        opened.add("0,1");
        opened.add("0,2");
        opened.add("1,0");
        opened.add("1,1");
        opened.add("1,2");
        opened.add("2,0");
        opened.add("2,1");
        opened.add("2,2");
    }

    private void createBoard(boolean clearBoard) {
        if (clearBoard)
        {
            if (previousChoice.getValue() > 0)
            {
                colorFrame(previousChoice, GREEN_4);
            }
            iterateTableView(FUNCTION_EMPTY_BOARD, new ArrayList<>());
        }

        initMatrix();
        for (int i = 0; i < sudokuBoard.getChildCount(); i++)
        {
            Object o =  sudokuBoard.getChildAt(i);
            if (o instanceof TableRow)
            {
                TableRow groupRow = (TableRow)o;
                for (int j = 0; j < groupRow.getChildCount();j++)
                {
                    o = groupRow.getChildAt(j);

                    if (o instanceof TableLayout)
                    {
                        TableLayout group = (TableLayout)o;
                        group.setBackground(Utils.createBorder(1, Color.WHITE, 1, Color.BLACK));
                        for (int k = 0; k < group.getChildCount(); k++)
                        {
                            yIndex = 0;
                            o = group.getChildAt(k);
                            if (o instanceof TableRow)
                            {
                                TableRow rowInGroup = (TableRow)o;
                                for (; yIndex < rowInGroup.getChildCount();)
                                {
                                    o = ((FrameLayout)rowInGroup.getChildAt(yIndex)).getChildAt(0);
                                    if (o instanceof Button)
                                    {
                                        Button b = (Button) o;
                                        b.setBackground(Utils.createBorder(1, Color.WHITE, 1, Color.BLACK));
                                        b.setTextColor(Color.BLACK);
                                        b.setEnabled(false);
                                        if (b.getText().toString().equals("") || b.getText().toString().equals("0") )
                                        {
                                            putValue(i, j, k, yIndex, b);
                                            b.setOnClickListener(v ->
                                                handleClick((String) v.getTag()));
                                        }
                                        else
                                        {
                                            yIndex++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        iterateTableView(FUNCTION_FILL_UP_CELL_MAP, new ArrayList<>());
        if (!checkForVictory())
        {
            createBoard(true);
        }
    }
    private void iterateTableView(int function, List<Object> params)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                for (int k = 0; k < 3; k++)
                {
                    for (int l = 0; l < 3; l++)
                    {
                        Button button = getButton(i, j, k, l);
                        Coordinates coordinates = new Coordinates(k, l, i, j);
                        handleFunction(function, button, coordinates, params);
                    }
                }
            }
        }
    }

    private void handleFunction(int function, Button b, Coordinates c, List<Object> params) {
        switch (function)
        {
            case FUNCTION_FILL_UP_CELL_MAP:
                fillUpCellsMap(b, c, false); break;
            case FUNCTION_COLOR_CELL_VICTORY:
                b.setTextColor(numberColors.get(b.getText().toString()));break;
            case FUNCTION_EMPTY_BOARD:
                b.setText("");break;
            case FUNCTION_POPULATE_LIST_OF_SAVED_INSTANCE:
                populateList(b, c); break;
            case FUNCTION_READ_LIST_FROM_SAVED_INSTANCE:
                populateBoardFromList(b, c); break;
            case FUNCTION_POPULATE_MATRIX:
                fillUpTempMatrix(c.getPartRow(), c.getPartCol(), c.getX(), c.getY(), b.getText().toString().equals("")? 0: Integer.parseInt(b.getText().toString())); break;
        }
    }

    private void populateList(Button button, Coordinates c) {
        if (!button.getText().toString().equals(""))
        {
            c.setValue(Integer.parseInt(button.getText().toString()));
            c.setColor(Color.BLACK);
            coordinatesList.add(c);
        }
    }

    private void populateBoardFromList(Button button, Coordinates c1) {
        button.setOnClickListener(v ->
                handleClick((String) v.getTag()));
        fillUpCellsMap(button, c1, true);
        fillUpTempMatrix(c1.getPartRow(), c1.getPartCol(), c1.getX(), c1.getY(), 0);
        button.setBackground(Utils.createBorder(1, Color.WHITE, 1, Color.BLACK));
        coordinatesList.stream().filter(c->c.getX() == c1.getX() && c.getY() == c1.getY() && c.getPartRow() == c1.getPartRow() && c.getPartCol() == c1.getPartCol()).
                forEach(c-> populateBoard(button, c));
    }

    private void populateBoard(Button button, Coordinates c) {
        button.setText(String.valueOf(c.getValue()));
        button.setTextColor(c.getColor());
        fillUpCellsMap(button, c, c.getColor() == BLUE_1);
        fillUpTempMatrix(c.getPartRow(), c.getPartCol(), c.getX(), c.getY(), Integer.parseInt(button.getText().toString()));
        if (currentCoordinates!= null && currentCoordinates.equals(c))
        {
            colorFrame(currentCoordinates, GREEN_1);
        }
    }

    private void fillUpCellsMap(Button b, Coordinates c, boolean enabled) {
        c.setEnabled(enabled);
        cellsMap.put(b.getTag().toString(), c);
    }

    private void initMatrix() {
        for (int m = 0; m < 9; m++)
        {
            for (int n = 0; n < 9; n++)
            {
                matrix[m][n] = 0;
            }
        }
    }

    private void putValue(int partRow, int partColumn, int x, int y, Button b) {
        missing.clear();
        cor.setPartRow(partRow);
        cor.setPartCol(partColumn);
        cor.setX(x);
        cor.setY(y);
        getPossibleNumbers();
        if (allNumbers.size() > 0 )
        {
            Random rand = new Random();
            int n = rand.nextInt(allNumbers.size());
            int number = allNumbers.get(n);

            b.setText(String.valueOf(number));
            fillUpTempMatrix(partRow, partColumn, x, y, number);
            yIndex++;
        }
        else
        {
            conflictNumber = findConflictNumber(cor.getPartRow(), cor.getPartCol());
            backTrack();
        }
    }

    private void getPossibleNumbers() {
        checkPart();
        checkHorizontal();
        checkVertical();
        fillUpMissing();
    }

    private int findConflictNumber(int i, int j) {
        initAllNumbersNumbers();
        missing.clear();
        checkPart();
        fillUpMissing();
        return allNumbers.get(0);
    }

    private void printMatrix() {
        System.out.print("\n\n***************start****************\n");
        for (int m = 0; m < 9; m++)
        {
            for (int n = 0; n < 9; n++)
            {
                System.out.print(matrix[m][n]);
            }
            System.out.print("\n");
        }
        System.out.print("\n*******************end*****************\n");
    }

    private String findConflict(int row, int col, int x, int y, int conflictNumber) {
        if(checkVerticalConflict(col, y, conflictNumber))
        {
            return VERTICAL;
        }
        if (checkHorizontalConflict(row, x, conflictNumber))
        {
            return HORIZONTAL;
        }

        return NO_CONFLICT;
    }

    private boolean checkHorizontalConflict(int row, int x, int conflictNumber) {
        for (int i = 0; i < 3; i++)
        {
            if (checkHorizontalConflict(row, i, x, conflictNumber))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkHorizontalConflict(int row, int col, int x, int conflictNumber) {
        TableRow currentRow = (TableRow)((TableLayout)((TableRow)sudokuBoard.getChildAt(row)).getChildAt(col)).getChildAt(x);
        for (int i = 0; i < 3; i++)
        {
            Button b = (Button) ((FrameLayout)currentRow.getChildAt(i)).getChildAt(0);
            if (b != null && !b.getText().toString().equals("") )
            {
                if (Integer.parseInt(b.getText().toString()) == conflictNumber && !(newCoordinatesToCompare[0] == row && newCoordinatesToCompare[1] == col && newCoordinatesToCompare[2] == x ))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkVerticalConflict(int col, int y, int conflictNumber) {
        for (int i = 0; i < 3; i++)
        {
            if (checkVerticalConflict(i, col, y, conflictNumber))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkVerticalConflict(int row, int col, int y, int conflictNumber) {
        TableLayout table = (TableLayout)((TableRow)sudokuBoard.getChildAt(row)).getChildAt(col);
        for (int i = 0; i < 3; i++)
        {
            Button b = (Button)((FrameLayout)((TableRow)table.getChildAt(i)).getChildAt(y)).getChildAt(0);
            if (b != null && !b.getText().toString().equals(""))
            {
                if (Integer.parseInt(b.getText().toString()) == conflictNumber  && !(newCoordinatesToCompare[0] == row && newCoordinatesToCompare[1] == col  && newCoordinatesToCompare[3] == y))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void backTrack() {
        prevConflict = findConflict(cor.getPartRow(), cor.getPartCol(), cor.getX(), cor.getY(), conflictNumber);
        switchData(conflictNumber, new int[]{cor.getPartRow(), cor.getPartCol(), cor.getX(), cor.getY()}, -1);
        try {
            Process process = new ProcessBuilder()
                    .command("logcat", "-c")
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putNumberInButton(int partRow, int partCol, int x, int y, String number) {
        Button b = getButton(partRow, partCol, x, y);
        b.setText(number);
    }

    private String getNumberFromButton(int partRow, int partCol, int x, int y)
    {
        Button b = getButton(partRow, partCol, x, y);
        return b.getText().toString();
    }

    private Button getButton(int partRow, int partCol, int x, int y)
    {
        return ((Button)((FrameLayout)((TableRow)((TableLayout)((TableRow)sudokuBoard.getChildAt(partRow))
                .getChildAt(partCol)).getChildAt(x)).getChildAt(y)).getChildAt(0));
    }

    private void switchData(int conflictNumber, int[] currCoordinates, int toSkip) {
        String numberToSwitch2;
        int[] coordinates, newSearchCoor;
        newCoordinatesToCompare = currCoordinates;
        prevConflict = findConflict(currCoordinates[0], currCoordinates[1], currCoordinates[2], currCoordinates[3], conflictNumber);
        if (prevConflict.equals(NO_CONFLICT))
        {
            newCoordinatesToCompare = new int[]{-1,-1,-1, -1};
            return;
        }

        if (prevConflict.equals(VERTICAL))
        {
            coordinates = findNumberInLineVertically(conflictNumber, currCoordinates[1], currCoordinates[3], toSkip);
            numberToSwitch2 =  getNumberFromButton(coordinates[0], coordinates[1], coordinates[2], coordinates[4]);
            newSearchCoor = new int[]{coordinates[0], coordinates[1],coordinates[2],coordinates[4]};
            toSkip = coordinates[0];
        }
        else
        {
            coordinates = findNumberInLineHorizontally(conflictNumber, currCoordinates[0], currCoordinates[2], toSkip);
            numberToSwitch2 =  getNumberFromButton(coordinates[0], coordinates[1], coordinates[4], coordinates[3]);
            newSearchCoor = new int[]{coordinates[0], coordinates[1],coordinates[4],coordinates[3]};
            toSkip = coordinates[1];
        }
        String numberToSwitch = getNumberFromButton(coordinates[0], coordinates[1],coordinates[2],coordinates[3]);
        if (numberToSwitch.equals(""))
        {
            putValue(coordinates[0], coordinates[1],coordinates[2],coordinates[3],
                    getButton(coordinates[0], coordinates[1],coordinates[2],coordinates[3]));
            numberToSwitch = getNumberFromButton(coordinates[0], coordinates[1],coordinates[2],coordinates[3]);
        }
        putNumberInButton(currCoordinates[0], currCoordinates[1], currCoordinates[2], currCoordinates[3], numberToSwitch2);
        fillUpTempMatrix(currCoordinates[0], currCoordinates[1], currCoordinates[2], currCoordinates[3], Integer.parseInt(numberToSwitch2));
        putNumberInButton(coordinates[0], coordinates[1], coordinates[2], coordinates[3], numberToSwitch2);
        fillUpTempMatrix(coordinates[0], coordinates[1], coordinates[2], coordinates[3], Integer.parseInt(numberToSwitch2));
        putNumberInButton(newSearchCoor[0], newSearchCoor[1],newSearchCoor[2],newSearchCoor[3], numberToSwitch);
        fillUpTempMatrix(newSearchCoor[0], newSearchCoor[1],newSearchCoor[2],newSearchCoor[3], Integer.parseInt(numberToSwitch));

        switchData(Integer.parseInt(numberToSwitch), new int[]{newSearchCoor[0], newSearchCoor[1],newSearchCoor[2],newSearchCoor[3]}, toSkip);
    }

    private int[] findNumberInLineVertically(int conflictNumber, int partCol, int y, int toSkip) {

        for (int i = 0; i < 3; i++) {
            if (i != toSkip)
            {
                for (int j = 0; j < 3; j++) {
                    String numberFromButton = getNumberFromButton(i, partCol, j, y);
                    int num = !numberFromButton.equals("")? Integer.parseInt(numberFromButton): 0;
                    if (num == conflictNumber) {
                        switch (y) {
                            case 0:
                                return checkVerticalConflict(partCol, 1, conflictNumber) ? new int[]{i, partCol, j, 2, y}: new int[]{i, partCol, j, 1, y};
                            case 1:
                                return checkVerticalConflict(partCol, 0, conflictNumber) ? new int[]{i, partCol, j, 2, y}: new int[]{i, partCol, j, 0, y};
                            default:
                                return checkVerticalConflict(partCol, 0, conflictNumber) ? new int[]{i, partCol, j, 1, y}: new int[]{i, partCol, j, 0, y};
                        }
                    }
                }
            }
        }
        return new int[]{0,0,0,0,0};
    }

    private int[] findNumberInLineHorizontally(int conflictNumber, int partRow, int x, int toSkip) {

        for (int i = 0; i < 3; i++) {
            if (i != toSkip)
            {
                for (int j = 0; j < 3; j++) {
                    String numberFromButton = getNumberFromButton(partRow, i, x, j);
                    int num = !numberFromButton.equals("")? Integer.parseInt(numberFromButton): 0;
                    if (num == conflictNumber) {
                        switch (x) {
                            case 0:
                                return checkHorizontalConflict(partRow,1, conflictNumber)? new int[]{partRow, i, 2, j, x}: new int[]{partRow, i, 1, j, x};
                            case 1:
                                return checkHorizontalConflict(partRow,0, conflictNumber)? new int[]{partRow, i, 2, j, x}: new int[]{partRow, i, 0, j, x};
                            default:
                                return checkHorizontalConflict(partRow,0, conflictNumber)? new int[]{partRow, i, 1, j, x}: new int[]{partRow, i, 0, j, x};
                        }
                    }
                }
            }
        }
        return new int[]{0,0,0,0,0};
    }

    private void fillUpMissing() {
        initAllNumbersNumbers();
        for (int i = 0; i < missing.size(); i++)
        {
            for (int j = 0; j < allNumbers.size(); j++)
            {
                if (allNumbers.get(j).intValue() == missing.get(i).intValue())
                {
                    allNumbers.remove(j);
                }
            }
        }
    }

    private void checkHorizontal() {
        checkMissingInRow(cor.getPartRow(), 0, cor.getX());

        if (cor.getPartCol() == 1)
        {
            checkMissingInRow(cor.getPartRow(), 1, cor.getX());
        }
        else if (cor.getPartCol() == 2)
        {
            checkMissingInRow(cor.getPartRow(), 1, cor.getX());
            checkMissingInRow(cor.getPartRow(), 2, cor.getX());
        }
    }

    private void checkVertical() {
        checkMissingInColumn(0, cor.getPartCol(), cor.getY());

        if (cor.getPartRow() == 1)
        {
            checkMissingInColumn(1, cor.getPartCol(), cor.getY());
        }
        else if (cor.getPartRow() == 2)
        {
            checkMissingInColumn(1, cor.getPartCol(), cor.getY());
            checkMissingInColumn(2, cor.getPartCol(), cor.getY());
        }
    }

    private void checkMissingInColumn(int partRow, int partCol, int x) {
        getFromRow(0, partRow, partCol, x);
        getFromRow(1, partRow, partCol, x);
        getFromRow(2, partRow, partCol, x);
    }

    private void getFromRow(int i, int partRow, int partCol, int x) {
        TableRow row = (TableRow) ((TableLayout)((TableRow) sudokuBoard.getChildAt(partRow)).
                getChildAt(partCol)).getChildAt(i);
        getMissingFromRow(row, x);
    }

    private void checkMissingInRow(int partRow, int partCol, int y) {
        TableRow row = (TableRow) ((TableLayout)((TableRow) sudokuBoard.getChildAt(partRow)).
                getChildAt(partCol)).getChildAt(y);
        getMissingFromRow(row);
    }

    private void checkPart() {
        TableLayout partTable = ((TableLayout)((TableRow)sudokuBoard.getChildAt(cor.getPartRow())).getChildAt(cor.getPartCol()));
        getMissingFromPart(partTable);
    }

    private void getMissingFromPart(TableLayout partTable) {
        for (int i = 0; i < 3; i++)
        {
            TableRow row = (TableRow) partTable.getChildAt(i);
            getMissingFromRow(row);
        }
    }

    private void getMissingFromRow(TableRow row) {
        for (int i = 0; i < 3; i++)
        {
            getMissingFromRow(row, i);
        }
    }

    private void getMissingFromRow(TableRow row, int i) {
        String num = ((Button)((FrameLayout)row.getChildAt(i)).getChildAt(0)).getText().toString();
        if (!num.equals(""))
        {
            missing.add(Integer.parseInt(num));
        }
    }

    private void fillUpTempMatrix(int partRow, int partColumn, int i, int j, int value) {
        int x = partColumn * 3 + j;
        int y = partRow * 3 + i;
        matrix[y][x] = value;
    }

    private void handleClick(String name) {
        currentCoordinates = Objects.requireNonNull(cellsMap.get(name));
        if (previousChoice.getValue() > 0)
        {
            colorFrame(previousChoice, GREEN_4);
        }

        if (Objects.requireNonNull(cellsMap.get(name)).isEnabled() && !chosenNumber.equals(""))
        {
            Button button = getButton(currentCoordinates.getPartRow(), currentCoordinates.getPartCol(), currentCoordinates.getX(), currentCoordinates.getY());
            button.setText(chosenNumber);
            button.setTextColor(BLUE_1);
            colorFrame(currentCoordinates, GREEN_1);
            previousChoice = currentCoordinates;
            previousChoice.setValue(Integer.parseInt(chosenNumber));
            currentCoordinates.setColor(BLUE_1);
            currentCoordinates.setValue(Integer.parseInt(chosenNumber));
            coordinatesList.add(currentCoordinates);
            closeButtons();
            fillUpTempMatrix(currentCoordinates.getPartRow(), currentCoordinates.getPartCol(), currentCoordinates.getX(), currentCoordinates.getY(),
                    Integer.parseInt(button.getText().toString()));
            if(checkForVictory())
            {
                colorGrid();
            }
        }
    }

    private void colorFrame(Coordinates coordinates, int color) {
        Button b = getButton(coordinates.getPartRow(), coordinates.getPartCol(), coordinates.getX(), coordinates.getY());
        ((FrameLayout)b.getParent()).setBackgroundColor(color);
    }

    private void colorGrid() {
        iterateTableView(FUNCTION_COLOR_CELL_VICTORY, new ArrayList<>());
    }

    private void initColorMap() {
        numberColors.put("1", GRAY_3);
        numberColors.put("2", GREEN_1);
        numberColors.put("3", BLUE_1);
        numberColors.put("4", PINK);
        numberColors.put("5", PURPLE_1);
        numberColors.put("6", YELLOW_1);
        numberColors.put("7", BLUE_3);
        numberColors.put("8", ORANGE);
        numberColors.put("9", RED_3);
    }

    private boolean checkForVictory() {
        for (int i = 0; i < 9; i++)
        {
            int sum = 0;
            for (int j = 0; j < 9; j++)
            {
               sum +=  matrix[i][j];
            }

            if (sum != 45)
            {
                return false;
            }
        }
        return true;
    }
}