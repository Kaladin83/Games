package com.example.maratbe.games;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maratbe.domain.*;
import com.example.maratbe.other.Constants;
import com.example.maratbe.other.MainActivity;
import com.example.maratbe.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Kakuro extends AppCompatActivity implements Constants {
    private TableLayout kakuroBoard;
    private RadioButton easyRadio, moderateRadio, hardRadio;
    private ArrayList<Integer> allNumbers = new ArrayList<>();
    private ArrayList<Integer> missing = new ArrayList<>();
    private ArrayList<String> opened = new ArrayList<>();
    private ArrayList<String> closed = new ArrayList<>();
    private ArrayList<Coordinates> coordinatesList = new ArrayList<>();
    private HashMap<String, Coordinates> cellsMap = new HashMap<>();
    private HashMap<String, Cell> mapOfCells = new HashMap<>();
    private HashMap<String, Button> numbersMap = new HashMap<>();
    private HashMap<String, Integer> numberColors = new HashMap<>();
    private Coordinates cor = new Coordinates();
    private Coordinates currentCoordinates = new Coordinates();
    private Coordinates previousChoice = new Coordinates();
    private int conflictNumber = 0, yIndex = 0, difficultyStart = DIFFICULTY_START_MODERATE, difficultyRange = DIFFICULTY_RANGE_MODERATE;
    private int emptyCells = 0;
    private String chosenNumber = "";
    private String previousNumber = "";
    private int[][] matrix = new int[0][0];
    private int MAX_COLS = 10, KAKURO_CELL;
    private int[] newCoordinatesToCompare = new int[]{-1, -1, -1, -1};

    private String prevConflict = "No Conflict";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.kakuro);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }
        LinearLayout l = findViewById(R.id.mainLayout);
        l.setBackgroundColor(WHITE);

        initColorMap();
        buildGui(savedInstanceState);

        Button newGameBtn = findViewById(R.id.newGameBtn);
        newGameBtn.setOnClickListener(v ->
                resetBoard(true)
        );
    }

    private void initGlobalVariables() {
        KAKURO_CELL = calculateCellSize(MainActivity.getScreenWidth());
        matrix = new int[MAX_COLS][MAX_COLS];
        initMatrix();
    }

    private void initMatrix() {
        for (int i = 0; i < MAX_COLS; i++) {
            matrix[i][0] = -1;
            matrix[0][i] = -1;
            mapOfCells.put("r" + 0 + "b" + i, new Cell(0, i, "", false));
            mapOfCells.put("r" + i + "b" + 0, new Cell(i, 0, "", false));
        }
    }

    private int calculateCellSize(int screenWidth) {
        return screenWidth / (MAX_COLS + 1);
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
        outState.putParcelableArrayList("coordinatesList", coordinatesList);
        outState.putParcelable("currentCoordinates", currentCoordinates);
        outState.putParcelable("previousChoice", previousChoice);
    }

    private void resetBoard(boolean clearBoard) {
        outlineBoard();
        createBoard();
        deleteValues();
        //createBoard(clearBoard);

        //populateCoordinatesList();
//        if (checkForVictory())
//        {
//
//            colorGrid();
//        }
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
            mapOfCells.put("r" + coordinates[0] + "b" + coordinates[1], new Cell(coordinates[0], coordinates[1], "", false));
        }
    }

    private int[] findRandomCoordinates(Random rand, int row, String direction) {
        int num = rand.nextInt(MAX_COLS - 1) + 1;
        int[] coordinates;

        if (direction.equals(HORIZONTAL)) {
            coordinates = new int[]{row, num};
            if (mapOfCells.get("r" + row + "b" + num) != null) {
                coordinates = findRandomCoordinates(rand, row, direction);
            }
        } else {
            coordinates = new int[]{num, row};
            if (mapOfCells.get("r" + num + "b" + row) != null) {
                coordinates = findRandomCoordinates(rand, row, direction);
            }
        }
        return coordinates;
    }

    private void createBoard() {
        createButtons();
        populateButtons();
        populateSums();
    }

    private void populateSums() {
        for (HashMap.Entry<String, Cell> entry : mapOfCells.entrySet()) {
            Cell cell = entry.getValue();
            if (cell.isEnable())
            {
                continue;
            }
            ArrayList<Sum> sums = new ArrayList<>();

            if (cell.getX() == 0 && cell.getY() > 0) {
                if (matrix[cell.getX() + 1][cell.getY()] > -1) {
                    int sum = sumVerticalNumbers(cell.getX() + 1, cell.getY());
                    sums = cell.getSum();
                    sums.add(new Sum(sum, LOWER));
                }
            }
            if (cell.getX() > 0) {
                if (cell.getY() > 0 && cell.getX() < MAX_COLS - 1 && matrix[cell.getX() + 1][cell.getY()] > -1) {
                    int sum = sumVerticalNumbers(cell.getX() + 1, cell.getY());
                    sums = cell.getSum();
                    sums.add(new Sum(sum, LOWER));
                }
                if (cell.getY() > 0 && cell.getY() < MAX_COLS - 1 && matrix[cell.getX()][cell.getY() + 1] > -1) {
                    int sum = sumHorizontalNumbers(cell.getX(), cell.getY() + 1);
                    sums.add(new Sum(sum, UPPER));
                }
                if (cell.getY() == 0 && matrix[cell.getX()][cell.getY() + 1] > -1) {
                    int sum = sumHorizontalNumbers(cell.getX(), cell.getY() + 1);
                    sums = cell.getSum();
                    sums.add(new Sum(sum, UPPER));
                }
            }

            cell.setSum(sums);
            RelativeLayout relativeLayout = (RelativeLayout) getCell(getBoardRow(cell.getX()), cell.getY());
            createTriangles(entry, getBoardRow(cell.getX()).getLayoutParams(), relativeLayout);
        }
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

    private void createButtons() {
        for (int i = 0; i < MAX_COLS; i++) {
            TableRow row = new TableRow(this);
            kakuroBoard.addView(row);
            for (int j = 0; j < MAX_COLS; j++) {
                row.addView(createButton(i, j, KAKURO_CELL, KAKURO_CELL));
            }
        }
    }

    private TableRow getBoardRow(int i) {
        return (TableRow) kakuroBoard.getChildAt(i);
    }

    private View createButton(int i, int j, int width, int height) {
        ViewGroup.LayoutParams layoutParams = new TableRow.LayoutParams(width, height);
        if (mapOfCells.get("r" + i + "b" + j) == null) {
            matrix[i][j] = 0;
            mapOfCells.put("r" + i + "b" + j, new Cell(i, j, "", true));
            return createButton("r" + i + "b" + j, layoutParams);
        } else {
            matrix[i][j] = -1;
            return createRelativeLayout("r" + i + "rl" + j, layoutParams);
        }
    }

    private View createRelativeLayout(String name, ViewGroup.LayoutParams layoutParams) {
        RelativeLayout rLayout = new RelativeLayout(this);
        rLayout.setLayoutParams(layoutParams);
        return rLayout;
    }

    private void createTriangles(HashMap.Entry<String, Cell> entry, ViewGroup.LayoutParams layoutParams, RelativeLayout relativeLayout) {
        Triangles triangles = new Triangles(this);
        triangles.setSize(relativeLayout.getLayoutParams().height);
        triangles.setDirections(entry.getValue().getSum());
        triangles.buildCell();
        triangles.setTag(entry.getKey());
        triangles.setLayoutParams(layoutParams);
        combineIntoLayout(triangles, relativeLayout, entry.getValue());
    }

    private void combineIntoLayout(Triangles triangles, RelativeLayout relativeLayout, Cell cell) {
        relativeLayout.addView(triangles);
        cell.getSum().forEach(s -> {
            if (s.getDirection().equals(UPPER)) {
                addTextViewToRelativeLayout(s, relativeLayout, new int[]{RelativeLayout.ALIGN_PARENT_END, RelativeLayout.CENTER_VERTICAL});
            } else {
                addTextViewToRelativeLayout(s, relativeLayout, new int[]{RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_HORIZONTAL});
            }
        });
    }

    private void addTextViewToRelativeLayout(Sum sum, RelativeLayout relativeLayout, int[] rules) {
        TextView txt = new TextView(this);
        txt.setText(String.valueOf(sum.getValue()));
        txt.setTextSize(10);
        txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        int size = (int) (KAKURO_CELL / 2.5);
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(size, size);
        rParams.addRule(rules[0]);
        rParams.addRule(rules[1]);
        relativeLayout.addView(txt, rParams);
    }

    private Button createButton(String name, ViewGroup.LayoutParams layoutParams) {
        Button button = new Button(this);
        button.setTag(name);
        button.setPadding(0, 0, 0, 0);
        button.setLayoutParams(layoutParams);
        button.setBackground(Utils.createBorder(1, GREEN_6, 1, Color.BLACK));
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
        if (v instanceof Button) {
            Button b = (Button) v;
            if (b.getText().toString().equals("")) {
                putValue(i, j, b);
                b.setOnClickListener(v1 -> handleClick(i, j));
            } else {
                yIndex++;
            }
        }
    }

    private View getCell(TableRow row, int j) {
        return row.getChildAt(j);
    }

    private int getStartDifficulty() {
        if (easyRadio.isChecked()) {
            return DIFFICULTY_START_EASY;
        }
        if (moderateRadio.isChecked()) {
            return DIFFICULTY_START_MODERATE;
        }
        return DIFFICULTY_START_HARD;
    }

    private int getRangeDifficulty() {
        if (easyRadio.isChecked()) {
            return DIFFICULTY_RANGE_EASY;
        }
        if (moderateRadio.isChecked()) {
            return DIFFICULTY_RANGE_MODERATE;
        }
        return DIFFICULTY_RANGE_HARD;
    }

    private void populateCoordinatesList() {
        coordinatesList.clear();
        iterateTableView(FUNCTION_POPULATE_LIST_OF_SAVED_INSTANCE, new ArrayList<>());
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

    private int findCoordinatesToClose(Integer[] closedArray) {
        Random rand = new Random();
        int m = rand.nextInt(9);
        if (closedArray[m] != null) {
            m = findCoordinatesToClose(closedArray);
        }
        closedArray[m] = m;
        return m;
    }

    private void buildGui(Bundle savedInstanceState) {
        kakuroBoard = findViewById(R.id.kakuroBoard);
        initGlobalVariables();
        //setRadios();
//        if (savedInstanceState != null)
//        {
//            currentCoordinates = (Coordinates) savedInstanceState.get("currentCoordinates");
//            previousChoice = (Coordinates) savedInstanceState.get("previousChoice");
//            coordinatesList = (ArrayList<Coordinates>) savedInstanceState.get("coordinatesList");
//            iterateTableView(FUNCTION_READ_LIST_FROM_SAVED_INSTANCE, new ArrayList<>());
//        }
//        else
//        {
//            resetBoard(false);
//        }
        resetBoard(false);
        createChoiceNumbers();
    }

    private void setRadios() {
        easyRadio = findViewById(R.id.easyRadio);
        easyRadio.setOnClickListener(v ->
                setDifficulty(DIFFICULTY_START_EASY, DIFFICULTY_RANGE_EASY, true));
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
        if (toReset) {
            resetBoard(true);
        }
    }

    private void createChoiceNumbers() {
        TableLayout numberLayout = findViewById(R.id.choiceButtonsLayout);
        TableRow row;
        Button button;
        for (int i = 0; i < numberLayout.getChildCount(); i++) {
            if (numberLayout.getChildAt(i) instanceof TableRow) {
                row = (TableRow) numberLayout.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    if (row.getChildAt(j) instanceof Button) {
                        button = (Button) row.getChildAt(j);
                        numbersMap.put(button.getTag().toString(), button);
                        button.setOnClickListener(v -> {
                            if (!previousNumber.equals("")) {
                                Objects.requireNonNull(numbersMap.get(previousNumber)).setBackgroundColor(BLUE_6);
                            }

                            if (v.getTag().toString().equals("revert")) {
                                if (previousChoice.getValue() > 0) {
                                    putNumberInButton(previousChoice.getPartRow(), previousChoice.getPartCol(), previousChoice.getX(), previousChoice.getY(), String.valueOf(previousChoice.getValue()));
                                    Button b = getButton(previousChoice.getPartRow(), previousChoice.getPartCol(), previousChoice.getX(), previousChoice.getY());
                                    b.setTextColor(BLUE_1);
                                    ((FrameLayout) b.getParent()).setBackgroundColor(GRAY_2);
                                }

                                v.setSelected(!v.isSelected());
                            } else {
                                openButtons();
                                v.setBackgroundColor(GREEN_5);
                                Objects.requireNonNull(numbersMap.get("revert")).setSelected(false);
                                chosenNumber = ((Button) v).getText().toString();
                            }
                            previousNumber = ((Button) v).getText().toString();
                        });
                    }
                }
            }
        }
    }

    private void openButtons() {
        cellsMap.forEach((key, value) ->
                getButton(value.getPartRow(), value.getPartCol(), value.getX(), value.getY()).setEnabled(true));
    }

    private void closeButtons() {
        cellsMap.forEach((key, value) -> {
            //   Coordinates c = value.getCoordinates();
            Button b = getButton(value.getPartRow(), value.getPartCol(), value.getX(), value.getY());
            b.setEnabled(false);
        });

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

    private void iterateTableView(int function, List<Object> params) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        Button button = getButton(i, j, k, l);
                        Coordinates coordinates = new Coordinates(k, l, i, j);
                        handleFunction(function, button, coordinates, params);
                    }
                }
            }
        }
    }

    private void handleFunction(int function, Button b, Coordinates c, List<Object> params) {
        switch (function) {
            case FUNCTION_FILL_UP_CELL_MAP:
                fillUpCellsMap(b, c, false);
                break;
            case FUNCTION_COLOR_CELL_VICTORY:
                b.setTextColor(numberColors.get(b.getText().toString()));
                break;
            case FUNCTION_EMPTY_BOARD:
                b.setText("");
                break;
            case FUNCTION_POPULATE_LIST_OF_SAVED_INSTANCE:
                populateList(b, c);
                break;
            case FUNCTION_READ_LIST_FROM_SAVED_INSTANCE:
                populateBoardFromList(b, c);
                break;
            case FUNCTION_POPULATE_MATRIX:
                fillUpTempMatrix(c.getPartRow(), c.getPartCol(), c.getX(), c.getY(), b.getText().toString().equals("") ? 0 : Integer.parseInt(b.getText().toString()));
                break;
        }
    }

    private void populateList(Button button, Coordinates c) {
        if (!button.getText().toString().equals("")) {
            c.setValue(Integer.parseInt(button.getText().toString()));
            c.setColor(Color.BLACK);
            coordinatesList.add(c);
        }
    }

    private void populateBoardFromList(Button button, Coordinates c1) {
        button.setOnClickListener(v ->
                handleClick(c1.getX(), c1.getY()));
        fillUpCellsMap(button, c1, true);
        fillUpTempMatrix(c1.getPartRow(), c1.getPartCol(), c1.getX(), c1.getY(), 0);
        button.setBackground(Utils.createBorder(1, Color.WHITE, 1, Color.BLACK));
        coordinatesList.stream().filter(c -> c.getX() == c1.getX() && c.getY() == c1.getY() && c.getPartRow() == c1.getPartRow() && c.getPartCol() == c1.getPartCol()).
                forEach(c -> populateBoard(button, c));
    }

    private void populateBoard(Button button, Coordinates c) {
        button.setText(String.valueOf(c.getValue()));
        button.setTextColor(c.getColor());
        fillUpCellsMap(button, c, c.getColor() == BLUE_1);
        fillUpTempMatrix(c.getPartRow(), c.getPartCol(), c.getX(), c.getY(), Integer.parseInt(button.getText().toString()));
        if (currentCoordinates != null && currentCoordinates.equals(c)) {
            colorFrame(currentCoordinates, GREEN_1);
        }
    }

    private void fillUpCellsMap(Button b, Coordinates c, boolean enabled) {
        c.setEnabled(enabled);
        cellsMap.put(b.getTag().toString(), c);
    }

    private void putValue(int x, int y, Button b) {
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
            yIndex++;
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

    private void printMatrix() {
        System.out.print("\n\n***************start****************\n");
        for (int m = 0; m < 9; m++) {
            for (int n = 0; n < 9; n++) {
                System.out.print(matrix[m][n]);
            }
            System.out.print("\n");
        }
        System.out.print("\n*******************end*****************\n");
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

    private void putNumberInButton(int partRow, int partCol, int x, int y, String number) {
        Button b = getButton(partRow, partCol, x, y);
        b.setText(number);
    }

    private void putNumberInButton(int x, int y, String number) {
        getButton(x, y).setText(number);
        matrix[x][y] = number.equals("") ? 0 : Integer.parseInt(number);
    }

    private Button getButton(int partRow, int partCol, int x, int y) {
        return ((Button) ((FrameLayout) ((TableRow) ((TableLayout) ((TableRow) kakuroBoard.getChildAt(partRow))
                .getChildAt(partCol)).getChildAt(x)).getChildAt(y)).getChildAt(0));
    }

    private Button getButton(int x, int y) {
        return (Button) getCell(getBoardRow(x), y);
    }

    private void switchData(int conflictNumber, int[] currCoordinates, int toSkip) {
        String numberToSwitch2;
        int[] coordinates, newSearchCoor;
        newCoordinatesToCompare = currCoordinates;
        prevConflict = findConflict(currCoordinates[0], currCoordinates[1], conflictNumber);
        if (prevConflict.equals(NO_CONFLICT)) {
            newCoordinatesToCompare = new int[]{-1, -1};
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

    private void fillUpTempMatrix(int partRow, int partColumn, int i, int j, int value) {
        int x = partColumn * 3 + j;
        int y = partRow * 3 + i;
        matrix[y][x] = value;
    }

    private void handleClick(int i, int j) {
        currentCoordinates = new Coordinates(i, j);
        currentCoordinates.setValue(matrix[i][j]);
//        if (previousChoice.getValue() > 0) {
//            colorFrame(previousChoice, GREEN_4);
//        }

        if (!chosenNumber.equals("")) {
            Button button = getButton(i, j);
            button.setText(chosenNumber);
            button.setTextColor(BLUE_1);
//            colorFrame(currentCoordinates, GREEN_1);
            previousChoice = currentCoordinates;
            previousChoice.setValue(Integer.parseInt(chosenNumber));
            currentCoordinates.setValue(Integer.parseInt(chosenNumber));
            coordinatesList.add(currentCoordinates);
            if (checkForVictory()) {
                colorGrid();
            }
        }
    }

    private void colorFrame(Coordinates coordinates, int color) {
        Button b = getButton(coordinates.getPartRow(), coordinates.getPartCol(), coordinates.getX(), coordinates.getY());
        ((FrameLayout) b.getParent()).setBackgroundColor(color);
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