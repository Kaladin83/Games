package com.example.maratbe.games;

import android.content.res.Configuration;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maratbe.customViews.KakuroButton;
import com.example.maratbe.customViews.TrianglesView;
import com.example.maratbe.domain.*;
import com.example.maratbe.listeners.ClickHandler;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Kakuro extends AppCompatActivity implements Constants {
    private TableLayout kakuroBoard;
    private Chronometer chronometer;
    private Button timeButton;
    private RadioButton easyRadio, moderateRadio, hardRadio;
    private ArrayList<Integer> allNumbers = new ArrayList<>();
    private ArrayList<Integer> missing = new ArrayList<>();
    private HashMap<String, Integer> numberColors = new HashMap<>();
    private Coordinates cor = new Coordinates();
    private int conflictNumber = 0, difficultyStart = DIFFICULTY_START_MODERATE, difficultyRange = DIFFICULTY_RANGE_MODERATE;
    private int emptyCells = 0;
    private long pauseOffset, timeToAdd;

    private int MAX_COLS = 10, KAKURO_CELL;
    private int[][] matrix = new int[MAX_COLS][MAX_COLS];
    private int[][] hintMatrix = new int[MAX_COLS][MAX_COLS];
    private ClickHandler clickHandler;

    private String prevConflict = "No Conflict";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakuro);
        initColorMap();
        timeButton = findViewById(R.id.timeBtn);
        timeButton.setOnClickListener(v ->
                clickHandler.onTimeButtonClicked(v.isSelected())
        );
        buildGui(savedInstanceState);

        TextView moreBtn = findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(v ->
                clickHandler.onMenuButtonClicked()
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listOfCells", clickHandler.getListOfCells());
        outState.putParcelableArrayList("turns", clickHandler.getTurns());
        outState.putParcelableArrayList("hintMatrix", Utils.translateMatrixIntoList(hintMatrix, MAX_COLS));
        outState.putParcelable("currentCell", clickHandler.getCurrentCell());
        outState.putParcelable("previousCell", clickHandler.getPreviousCell());
        outState.putInt("numOfHints", clickHandler.getNumberOfHints());
        outState.putBoolean("isHintPressed", clickHandler.isHintPressed());
        outState.putBoolean("isTimePressed", timeButton.isSelected());
        outState.putLong("timeToAdd", chronometer.getBase() - SystemClock.elapsedRealtime());
        outState.putLong("pauseOffset", pauseOffset);
    }

    private void setupClickHandler() {
        RelativeLayout relativeLayout = findViewById(R.id.mainRelativeLayout);
        clickHandler = new ClickHandler(findViewById(R.id.numbersLayout), findViewById(R.id.revert), findViewById(R.id.hintLayout)) {
            @Override
            protected void colorCellsForHints() {
                iterateTableView(FUNCTION_COLOR_CELLS_HINT);
            }

            @Override
            protected void updateCellRevertValue(Cell previous) {
                Coordinates coordinates = previous.getCoordinates();
                KakuroButton b = getButton(coordinates.getX(), coordinates.getY());
                b.setText(previous.getValue().get(previous.getValue().size() - 1) == 0? "":
                        String.valueOf(previous.getValue().get(previous.getValue().size() - 1)));
                //b.setTextColor(BLUE_1);
            }

            @Override
            protected void updateCellNewValue(boolean isPreviousExists) {
                Coordinates coordinates = clickHandler.getCurrentCell().getCoordinates();
                String chosenNumber = Utils.updateValueWhenHintPressed(clickHandler,
                        getValueFromHintMatrix(coordinates.getX(), coordinates.getY()));

                KakuroButton button = getButton(coordinates.getX(), coordinates.getY());
                button.setText(chosenNumber);
                button.setSelected(false);
                matrix[coordinates.getX()][coordinates.getY()] =
                        clickHandler.getCurrentCell().getValue().get(clickHandler.getCurrentCell().getValue().size() - 1);
            }

            @Override
            protected void handleChronometer(boolean isPaused) {
                pauseOffset = Utils.handleChronometer(chronometer, timeButton, pauseOffset, isPaused);
            }
        };
        clickHandler.setupMenuLayout(relativeLayout, this);
        clickHandler.createControlPanel();
    }

    private void initGlobalVariables() {
        matrix = new int[MAX_COLS][MAX_COLS];
        clickHandler.getListOfCells().clear();
        initMatrix();
    }

    private void initMatrix() {
        for (int i = 0; i < MAX_COLS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                if (i == 0 || j == 0)
                {
                    updateValue(new int[]{i, j}, "", false);
                }
                else {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    private int calculateCellSize(int screenWidth) {
        return screenWidth / (MAX_COLS + 1);
    }

    public void resetBoard() {
        initGlobalVariables();
        outlineBoard();
        createBoard(false);
        populateButtons();
        fillUpHintMatrix();
        populateSums();
        deleteValues();
    }

    private void outlineBoard() {
        for (int i = 1; i < MAX_COLS; i++) {
            addEmptyCells(i, HORIZONTAL);
            addEmptyCells(i, VERTICAL);
        }
    }

    private void addEmptyCells(int i, String direction) {
        Random rand = new Random();
        emptyCells = rand.nextInt(2) + 1;
        for (int j = 0; j < emptyCells; j++) {
            int[] coordinates = findRandomCoordinates(rand, i, direction);
            updateValue(coordinates, "", false);
        }
    }

    private void updateValue(int[] coordinates, String value, boolean enabled) {
        int i = coordinates[0], j = coordinates[1];
        matrix[i][j] = enabled? (value.equals("")? 0: Integer.parseInt(value)): -1;
        Cell c = new Cell(new Coordinates(i, j), matrix[i][j], BLUE_1, enabled);
        c.setIndex(clickHandler.getListOfCells().size());
        clickHandler.getListOfCells().add(c);
    }

    private int[] findRandomCoordinates(Random rand, int row, String direction) {
        int num = rand.nextInt(MAX_COLS - 1) + 1;
        int[] coordinates;

        if (direction.equals(HORIZONTAL)) {
            coordinates = new int[]{row, num};
            if (matrix[row][num] == -1) {
                coordinates = findRandomCoordinates(rand, row, direction);
            }
        } else {
            coordinates = new int[]{num, row};
            if (matrix[num][row] == -1) {
                coordinates = findRandomCoordinates(rand, row, direction);
            }
        }
        return coordinates;
    }

    private void createBoard(boolean loadFromList) {
        if (kakuroBoard.getChildCount() > 0)
        {
            kakuroBoard.removeAllViews();
        }
        createButtons(loadFromList);
    }

    private void populateSums() {
        clickHandler.getListOfCells().forEach(c -> {if (!c.isEnabled()) populateSums(c);});
    }

    private void populateSums(Cell cell) {
        ArrayList<Sum> sums = new ArrayList<>();
        Coordinates c = cell.getCoordinates();
        if (c.getX() == 0 && c.getY() > 0) {
            if (matrix[c.getX() + 1][c.getY()] > -1) {
                int sum = sumVerticalNumbers(c.getX() + 1, c.getY());
                sums = cell.getSums();
                sums.add(new Sum(sum, LOWER));
            }
        }
        if (c.getX() > 0) {
            if (c.getY() > 0 && c.getX() < MAX_COLS - 1 && matrix[c.getX() + 1][c.getY()] > -1) {
                int sum = sumVerticalNumbers(c.getX() + 1, c.getY());
                sums = cell.getSums();
                sums.add(new Sum(sum, LOWER));
            }
            if (c.getY() > 0 && c.getY() < MAX_COLS - 1 && matrix[c.getX()][c.getY() + 1] > -1) {
                int sum = sumHorizontalNumbers(c.getX(), c.getY() + 1);
                sums.add(new Sum(sum, UPPER));
            }
            if (c.getY() == 0 && matrix[c.getX()][c.getY() + 1] > -1) {
                int sum = sumHorizontalNumbers(c.getX(), c.getY() + 1);
                sums = cell.getSums();
                sums.add(new Sum(sum, UPPER));
            }
        }

        cell.setSums(sums);
        createTriangles(cell, (TrianglesView) getCell(getBoardRow(c.getX()), c.getY()));
    }

    private int sumHorizontalNumbers(int x, int y) {
        int sum = 0;
        for (int i = y; i < MAX_COLS; i++) {
            if (matrix[x][i] == -1) {
                return sum;
            } else {
                sum += matrix[x][i];
            }
        }
        return sum;
    }

    private int sumVerticalNumbers(int x, int y) {
        int sum = 0;
        for (int i = x; i < MAX_COLS; i++) {
            if (matrix[i][y] == -1) {
                return sum;
            } else {
                sum += matrix[i][y];
            }
        }
        return sum;
    }

    private void createButtons(boolean loadFromList) {
        for (int i = 0; i < MAX_COLS; i++) {
            TableRow row = new TableRow(this);
            kakuroBoard.addView(row);
            for (int j = 0; j < MAX_COLS; j++) {
                row.addView(createButton(loadFromList, i, j, KAKURO_CELL, KAKURO_CELL));
            }
        }
    }

    private TableRow getBoardRow(int i) {
        return (TableRow) kakuroBoard.getChildAt(i);
    }

    private View createButton(boolean loadFromList, int i, int j, int width, int height) {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(width, height);
        if (loadFromList)
        {
            return createCellsFromList(i, j, layoutParams);
        }
        else
        {
            return createNewCells(i, j, layoutParams);
        }
    }

    private View createCellsFromList(int i, int j, TableRow.LayoutParams layoutParams) {
        String name = "c00" + i + "" + j;
        Cell cell = clickHandler.getCoordinatesFromList(name);
        matrix[i][j] = cell.getValue().get(cell.getValue().size() - 1);
        if (cell.isEnabled()) {
            return populateButtonFromList(matrix[i][j], name, layoutParams);
        } else {
            return populateTrianglesViewFromCell(cell, name, layoutParams);
        }
    }

    private KakuroButton populateButtonFromList(int value, String name, TableRow.LayoutParams layoutParams) {
        KakuroButton b = createButton(name, layoutParams);
        b.setText(value == 0? "": String.valueOf(value));
        return b;
    }

    private View populateTrianglesViewFromCell(Cell cell, String name, TableRow.LayoutParams layoutParams) {
        TrianglesView trianglesView = (TrianglesView)createTrianglesView(name, layoutParams);
        if (cell.getSums().size() > 0) {
            createTriangles(cell, trianglesView);
        }
        return trianglesView;
    }

    private View createNewCells(int i, int j, TableRow.LayoutParams layoutParams) {
        if (matrix[i][j] == 0) {
            updateValue(new int[] {i,j}, "", true);
            return createButton("c00" + i + "" + j, layoutParams);
        } else {
            return createTrianglesView("c00" + i + "" + j, layoutParams);
        }
    }

    private View createTrianglesView(String name, TableRow.LayoutParams layoutParams) {
        TrianglesView trianglesView = new TrianglesView(this);
        trianglesView.setLayoutParams(layoutParams);
        trianglesView.setTag(name);
        return trianglesView;
    }

    private void createTriangles(Cell cell, TrianglesView view) {
        view.setLayoutParams(new TableRow.LayoutParams(KAKURO_CELL, KAKURO_CELL));
        view.setSize(KAKURO_CELL);
        view.setDirections(cell.getSums());
        View v = getCell(getBoardRow(cell.getCoordinates().getX()), cell.getCoordinates().getY() -1);
        view.isToDrawVerticalLine(v instanceof KakuroButton);
        view.buildCell();
        view.setTag("r"+cell.getCoordinates().getX()+"rl"+cell.getCoordinates().getY());
    }

    private KakuroButton createButton(String name, TableRow.LayoutParams layoutParams) {
        KakuroButton button = new KakuroButton(this, null, 0,R.style.KakuroButtonStyle);
        button.setTag(name);
        button.setGravity(Gravity.CENTER);
        button.setPadding(0, 0, 0, 0);
        button.setLayoutParams(layoutParams);
        button.setActivated(true);
        button.setOnClickListener(v -> clickHandler.onCellClick(name));
        return button;
    }

    private void populateButtons() {
        for (int i = 1; i < MAX_COLS; i++) {
            for (int j = 1; j < MAX_COLS; j++) {
                populateButton(i, j);
            }
        }
    }

    private void populateButton(int i, int j) {
        View v = getCell(getBoardRow(i), j);
        if (v instanceof KakuroButton) {
            KakuroButton b = (KakuroButton) v;
            if (b.getText().toString().equals("")) {
                putValue(i, j, b);
                putBorder(j, b);
            }
        }
    }

    private View getCell(TableRow row, int j) {
        return row.getChildAt(j);
    }

    private void deleteValues() {
        for (int i = 1; i < MAX_COLS; i++) {
            for (int j = 1; j < MAX_COLS; j++) {
                if (matrix[i][j] > -1)
                {
                    matrix[i][j] = 0;
                    getButton(i, j).setText("");
                }
            }
        }
    }

    private void buildGui(Bundle savedInstanceState) {
        kakuroBoard = findViewById(R.id.kakuroBoard);
        KAKURO_CELL = calculateCellSize(MainActivity.getScreenWidth());
        setupClickHandler();
        if (savedInstanceState != null) {
            getValuesFromBundle(savedInstanceState);
        } else {
            resetBoard();
        }
        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(chronometer.getBase()+timeToAdd);
        chronometer.start();
        timeButton.setSelected(true);
    }

    @SuppressWarnings("unchecked")
    private void getValuesFromBundle(Bundle savedInstanceState) {
        clickHandler.setNumberOfHints((int) savedInstanceState.get("numOfHints"));
        timeToAdd = (long) savedInstanceState.get("timeToAdd");
        timeButton.setSelected((boolean) savedInstanceState.get("isTimePressed"));
        pauseOffset = (long) savedInstanceState.get("pauseOffset");
        clickHandler.setCurrentCell((Cell) savedInstanceState.get("currentCell"));
        clickHandler.setPreviousCell((Cell) savedInstanceState.get("previousCell"));
        clickHandler.setListOfCells((ArrayList<Cell>) savedInstanceState.get("listOfCells"));
        clickHandler.setTurns((ArrayList<Cell>) savedInstanceState.get("turns"));
        Utils.translateListIntoMatrix((ArrayList<Cell>) savedInstanceState.get("hintMatrix"), hintMatrix);

        createBoard(true);
        clickHandler.setHintPressed((boolean) savedInstanceState.get("isHintPressed"));
    }

    private void initAllNumbers() {
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

    private void iterateTableView(int function) {
        for (int i = 0; i < MAX_COLS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                handleFunction(function, i, j);
            }
        }
    }

    private void handleFunction(int function, int i, int j) {
        switch (function) {
            case FUNCTION_COLOR_CELLS_HINT:
                setBackgroundColor(i, j);
                break;
            case FUNCTION_POPULATE_LIST_OF_SAVED_INSTANCE:
                populateList(i, j);
                break;
        }
    }

    private void setBackgroundColor(int i, int j) {
        View  view = getCell(getBoardRow(i), j);
        if (view instanceof KakuroButton)
        {
            KakuroButton b = (KakuroButton) view;
            if (b.getText().toString().equals(""))
            {
//                b.setBackground(Utils.createBorder(1,
////                        clickHandler.isHintPressed()? YELLOW_4: GREEN_6, 1, BLUE_1));
                b.setSelected(clickHandler.isHintPressed());
            }
        }
    }

    private void populateList(int i, int j) {
        View  view = getCell(getBoardRow(i), j);
        if (view instanceof Button)
        {
            populateListFromButton((KakuroButton) view, new Coordinates(i, j));
        }
//        else {
//            populateListFromRelativeLayout((RelativeLayout)view, new Coordinates(i, j));
//        }
    }

    private void populateListFromButton(KakuroButton b, Coordinates c) {
        if (!b.getText().toString().equals(""))
        {
            Cell cell = new Cell();
            cell.setCoordinates(c);
            cell.setValue(Integer.parseInt(b.getText().toString()));
            cell.setColor(BLUE_1);
            cell.setEnabled(true);
            cell.setIndex(clickHandler.getListOfCells().size());
            clickHandler.getListOfCells().add(cell);
        }
    }

    private void populateListFromRelativeLayout(RelativeLayout rLayout, Coordinates c) {
        ArrayList<Sum> sums = new ArrayList<>();
        for (int i = 0; i < rLayout.getChildCount(); i++)
        {
            if (rLayout.getChildAt(i) instanceof TextView)
            {
                String sum = ((TextView)rLayout.getChildAt(i)).getText().toString();
                String direction = rLayout.getChildAt(i).getTag().toString();
                sums.add(new Sum(Integer.parseInt(sum), direction));
                Cell cell = new Cell(c, new ArrayList<>(), sums, false);
                cell.setIndex(clickHandler.getListOfCells().size());
                clickHandler.getListOfCells().add(cell);
            }
        }
    }

    private void putBorder(int j, KakuroButton b) {
        if (j == MAX_COLS -1) {
            b.setBackground(addToSelector(R.drawable.kakuro_button_selected_2, R.drawable.kakuro_button_not_selected_2));
        }
        else {
            b.setBackground(addToSelector(R.drawable.kakuro_button_selected_1, R.drawable.kakuro_button_not_selected_1));
        }
    }

    public StateListDrawable addToSelector(int selected, int notSelected) {
        StateListDrawable res = new StateListDrawable();
        res.addState(new int[]{android.R.attr.state_pressed}, getDrawable(selected));
        res.addState(new int[]{android.R.attr.state_selected}, getDrawable(selected));
        res.addState(new int[]{-android.R.attr.state_pressed}, getDrawable(notSelected));
        res.addState(new int[]{-android.R.attr.state_selected}, getDrawable(notSelected));
        return res;
    }

    private void putValue(int x, int y, KakuroButton b) {
        missing.clear();
        cor.setX(x);
        cor.setY(y);
        getPossibleNumbers(x, y);
        if (allNumbers.size() > 0) {
            Random rand = new Random();
            int n = rand.nextInt(allNumbers.size());
            int number = allNumbers.get(n);

            b.setText(String.valueOf(number));
            matrix[x][y] = number;
        } else {
            conflictNumber = findConflictNumber(x, y);
            backTrack();
        }
    }

    private void getPossibleNumbers(int x, int y) {
        checkHorizontal(x, y);
        checkVertical(x, y);
        fillUpMissing();
    }

    private int findConflictNumber(int x, int y) {
        initAllNumbers();
        missing.clear();
        checkHorizontal(x, y);
        fillUpMissing();
        return allNumbers.get(0);
    }

    private String findConflict(int x, int y, int conflictNumber) {
        if (checkVerticalConflict(x, y, conflictNumber)) {
            return VERTICAL;
        }
        if (checkHorizontalConflict(x, y, conflictNumber)) {
            return HORIZONTAL;
        }

        return NO_CONFLICT;
    }

    private boolean checkVerticalConflict(int x, int y, int conflictNumber) {
        if (checkVerticalConflictBehind(x, y, conflictNumber)) {
            return true;
        } else {
            return checkVerticalConflictForward(x, y, conflictNumber);
        }
    }

    private boolean checkVerticalConflictBehind(int x, int y, int conflictNumber) {
        if (x == 1) {
            return false;
        }

        for (int i = x - 1; i > 0; i--) {
            if (matrix[i][y] == -1) {
                return false;
            }
            if (matrix[i][y] == conflictNumber) {
                return true;
            }
        }
        return false;
    }

    private boolean checkVerticalConflictForward(int x, int y, int conflictNumber) {
        if (x == MAX_COLS - 1) {
            return false;
        }

        for (int i = x + 1; i < MAX_COLS; i++) {
            if (matrix[i][y] == -1) {
                return false;
            }
            if (matrix[i][y] == conflictNumber) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHorizontalConflict(int x, int y, int conflictNumber) {
        if (checkHorizontalConflictBehind(x, y, conflictNumber)) {
            return true;
        } else {
            return checkHorizontalConflictForward(x, y, conflictNumber);
        }
    }

    private boolean checkHorizontalConflictBehind(int x, int y, int conflictNumber) {
        if (y == 1) {
            return false;
        }

        for (int i = y - 1; y > 0; y--) {
            if (matrix[x][i] == -1) {
                return false;
            }
            if (matrix[x][i] == conflictNumber) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHorizontalConflictForward(int x, int y, int conflictNumber) {
        if (y == MAX_COLS - 1) {
            return false;
        }

        for (int i = y + 1; i < MAX_COLS; i++) {
            if (matrix[x][i] == -1) {
                return false;
            }
            if (matrix[x][i] == conflictNumber) {
                return true;
            }
        }
        return false;
    }

    private void backTrack() {
        prevConflict = findConflict(cor.getX(), cor.getY(), conflictNumber);
        switchData(conflictNumber, new int[]{cor.getX(), cor.getY()}, -1);
    }

    private void putNumberInButton(int x, int y, String number) {
        getButton(x, y).setText(number);
        matrix[x][y] = number.equals("") ? 0 : Integer.parseInt(number);
    }

    private KakuroButton getButton(int x, int y) {
        return (KakuroButton) getCell(getBoardRow(x), y);
    }

    private void switchData(int conflictNumber, int[] currCoordinates, int toSkip) {
        String numberToSwitch2;
        int[] coordinates, newSearchCoor;
        prevConflict = findConflict(currCoordinates[0], currCoordinates[1], conflictNumber);
        if (prevConflict.equals(NO_CONFLICT)) {
            return;
        }

        if (prevConflict.equals(VERTICAL)) {
            coordinates = findNumberInLineVertically(conflictNumber, currCoordinates[1], toSkip);
            numberToSwitch2 = String.valueOf(matrix[coordinates[0]][coordinates[2]]);
            newSearchCoor = new int[]{coordinates[0], coordinates[2]};
            toSkip = coordinates[0];
        } else {
            coordinates = findNumberInLineHorizontally(conflictNumber, currCoordinates[0], toSkip);
            numberToSwitch2 = String.valueOf(matrix[coordinates[2]][coordinates[1]]);
            newSearchCoor = new int[]{coordinates[2], coordinates[1]};
            toSkip = coordinates[1];
        }
        String numberToSwitch = String.valueOf(matrix[coordinates[0]][coordinates[1]]);
        if (numberToSwitch.equals("0")) {
            putValue(coordinates[0], coordinates[1], getButton(coordinates[0], coordinates[1]));
            numberToSwitch = String.valueOf(matrix[coordinates[0]][coordinates[1]]);
        }
        putNumberInButton(currCoordinates[0], currCoordinates[1], numberToSwitch2);
        putNumberInButton(coordinates[0], coordinates[1], numberToSwitch2);
        putNumberInButton(newSearchCoor[0], newSearchCoor[1], numberToSwitch);
        //printMatrix();

        switchData(Integer.parseInt(numberToSwitch), new int[]{newSearchCoor[0], newSearchCoor[1]}, toSkip);
    }

    private int[] findNumberInLineVertically(int conflictNumber, int y, int toSkip) {
        ArrayList<Integer> values;
        for (int i = 0; i < MAX_COLS; i++) {
            if (conflictNumber == matrix[i][y] && i != toSkip) {
                if (y == MAX_COLS - 1) {
                    values = findNextAvailableNumbersFromEndInRow(i);
                } else {
                    values = findNextAvailableNumbersFromStartInRow(i);

                }
                return checkVerticalConflict(values.get(0), y, conflictNumber) ? new int[]{i, values.get(1), y} : new int[]{i, values.get(0), y};
            }
        }
        return new int[]{0, 0, 0};
    }

    private int[] findNumberInLineHorizontally(int conflictNumber, int x, int toSkip) {
        ArrayList<Integer> values;
        for (int i = 0; i < MAX_COLS; i++) {
            if (conflictNumber == matrix[x][i] && i != toSkip) {
                if (x == MAX_COLS - 1) {
                    values = findNextAvailableNumbersFromEndInColumn(i);
                } else {
                    values = findNextAvailableNumbersFromStartInColumn(i);
                }
                return checkHorizontalConflict(values.get(0), x, conflictNumber) ?
                        new int[]{i, values.get(1), x} : new int[]{i, values.get(0), x};
            }
        }
        return new int[]{0, 0, 0};
    }

    private ArrayList<Integer> findNextAvailableNumbersFromStartInRow(int y) {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < MAX_COLS; i++) {
            if (matrix[y][i] > -1) {
                values.add(i);
            }
            if (values.size() == 2) {
                return values;
            }
        }
        return values;
    }

    private ArrayList<Integer> findNextAvailableNumbersFromEndInRow(int y) {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = MAX_COLS - 1; i >= 0; i--) {
            if (matrix[y][i] > -1) {
                values.add(i);
            }
            if (values.size() == 2) {
                return values;
            }
        }
        return values;
    }

    private ArrayList<Integer> findNextAvailableNumbersFromStartInColumn(int x) {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < MAX_COLS; i++) {
            if (matrix[i][x] > -1) {
                values.add(i);
            }
            if (values.size() == 2) {
                return values;
            }
        }
        return values;
    }

    private ArrayList<Integer> findNextAvailableNumbersFromEndInColumn(int x) {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = MAX_COLS - 1; i >= 0; i--) {
            if (matrix[i][x] > -1) {
                values.add(i);
            }
            if (values.size() == 2) {
                return values;
            }
        }
        return values;
    }

    private void fillUpMissing() {
        initAllNumbers();
        for (int i = 0; i < missing.size(); i++) {
            for (int j = 0; j < allNumbers.size(); j++) {
                if (allNumbers.get(j).intValue() == missing.get(i).intValue()) {
                    allNumbers.remove(j);
                }
            }
        }
    }

    private void checkHorizontal(int x, int y) {
        getHorizontalNumbersBefore(x, y);
        getHorizontalNumbersAfter(x, y);
    }

    private void getHorizontalNumbersBefore(int x, int y) {
        if (y == 0) {
            return;
        }
        for (int i = y - 1; i > 0; i--) {
            if (matrix[x][i] == -1) {
                return;
            }
            if (matrix[x][i] > 0) {
                missing.add(matrix[x][i]);
            }
        }
    }

    private void getHorizontalNumbersAfter(int x, int y) {
        if (y == MAX_COLS - 1) {
            return;
        }
        for (int i = y + 1; i < MAX_COLS; i++) {
            if (matrix[x][i] == -1) {
                return;
            }
            if (matrix[x][i] > 0) {
                missing.add(matrix[x][i]);
            }
        }
    }

    private void checkVertical(int x, int y) {
        getVerticalNumbersBefore(x, y);
        getVerticalNumbersAfter(x, y);
    }

    private void getVerticalNumbersBefore(int x, int y) {
        if (x == 0) {
            return;
        }
        for (int i = x - 1; i > 0; i--) {
            if (matrix[i][y] == -1) {
                return;
            }
            if (matrix[i][y] > 0) {
                missing.add(matrix[i][y]);
            }
        }
    }

    private void getVerticalNumbersAfter(int x, int y) {
        if (x == MAX_COLS - 1) {
            return;
        }
        for (int i = x + 1; i < MAX_COLS; i++) {
            if (matrix[i][y] == -1) {
                return;
            }
            if (matrix[i][y] > 0) {
                missing.add(matrix[i][y]);
            }
        }
    }

    private String getValueFromHintMatrix(int i, int j)
    {
        return String.valueOf(hintMatrix[i][j]);
    }

    private void fillUpHintMatrix() {
        for(int i = 0; i < NUMBER_OF_COLS; i++)
        {
            for(int j = 0; j < NUMBER_OF_COLS; j++)
            {
                hintMatrix[i][j] = matrix[i][j];
            }
        }
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
        for (int i = 0; i < 9; i++) {
            int sum = 0;
            for (int j = 0; j < 9; j++) {
                sum += matrix[i][j];
            }

            if (sum != 45) {
                return false;
            }
        }
        return true;
    }
}