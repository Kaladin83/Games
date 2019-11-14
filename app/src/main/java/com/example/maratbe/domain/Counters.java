package com.example.maratbe.domain;

import java.util.Arrays;

public class Counters {
    private char sign = ' ';
    private int i;
    private int j;
    private String result = "";
    private int counter = 0;
    private boolean firstMove = false;
                                     //r0 r1 r2 c0 c1 c2 d0 d1
    private int[] countersX = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
    private int[] counters0 = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
    private char[][] matrix = new char[3][3];

    public Counters()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                matrix[i][j] = ' ';
            }
        }
    }

    public void setNumOfTurns(int counter) {
        this.counter = counter;
    }

    public void setCountersX(int[] counters)
    {
        countersX = Arrays.copyOf(counters, counters.length);
    }

    public int[] getCountersX()
    {
        return countersX;
    }

    public void setCounters0(int[] counters)
    {
        counters0 = Arrays.copyOf(counters, counters.length);
    }

    public int[] getCounters0()
    {
        return counters0;
    }

    public char[][] getMatrix()
    {
        return matrix;
    }

    public char[] getArrayFromMatrix()
    {
        char[] arrayToReturn = new char[9];
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                arrayToReturn[getPosition(i,j)] = matrix[i][j];
            }
        }
        return arrayToReturn;
    }

    public void setMatrixFromArray(char[] array)
    {
        for (int i = 0; i < 9; i++)
        {
            int[] result = getPosition(i);
            matrix[result[0]][result[1]] = array[i];
        }
    }

    private int[] getPosition(int i) {
        int[] result = new int[2];
        result[0] = i / 3;
        result[1] = i % 3;
        return result;
    }

    private int getPosition(int i, int j) {
        return i * 3 + j;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setJ(int j) {
        this.j = j;
    }


    public void setSign(char sign) {
        counter++;
        this.sign = sign;
        matrix[i][j] = sign;
        setCounters();
    }

    public int[] setFirstMove(char sign)
    {
        if (sign == 'X' && counter == 0)
        {
            firstMove = true;
            return new int[]{1,1};
        }
        return new int[]{-1,-1};
    }

    public int[] setSpecialMove()
    {
        if (firstMove)
        {
            firstMove = false;
            if (matrix[0][2] == '0' || matrix[1][2] == '0' )
            {
                return new int[]{2,0};
            }
            if (matrix[2][2] == '0')
            {
                return new int[]{0,0};
            }
            if (matrix[0][0] == '0' || matrix[1][0] == '0' )
            {
                return new int[]{2,2};
            }
            if (matrix[2][0] == '0') {
                return new int[]{0, 2};
            }
        }
        return new int[]{-1,-1};
    }

    public int[] getBestChoice(char sign)
    {
        if (sign == 'X')
        {
            return getBestChoice(counters0, countersX);
        }
        return getBestChoice(countersX, counters0);
    }

    private int[] getBestChoice(int[] blockCounts, int[] counts) {
        int[] index = getBestChoice(counts, 2, counts.length -1);
        if (index[2] == 2)
        {
            return index;
        }

        int[] blockIndex = findBlocker(blockCounts);
        if (blockIndex[0]> -1)
        {
            return blockIndex;
        }
        return index;
    }

    private int[] findBlocker(int[] counts) {
        for (int i = 0; i< counts.length; i++)
        {
            if (counts[i] == 2)
            {
                int[] index = checkIfEmpty(parseResult(i), 2);
                if (index[0] > -1)
                {
                    return index;
                }
            }
        }
        return new int[]{-1};
    }

    private int[] getBestChoice(int[] counts, int num, int i) {
        if (i < 0)
        {
            return getBestChoice(counts, num - 1, counts.length - 1);
        }
        else
        {
            if (counts[i] < num) {
                return getBestChoice(counts, num, i - 1);
            }
            else
            {
                int[] index = checkIfEmpty(parseResult(i), num);
                if (index[0] > -1) {
                    return index;
                }
                return getBestChoice(counts, num, i - 1);
            }
        }
    }

    private int[] checkIfEmpty(String line, int num) {
        switch(line)
        {
            case "r0":
            return checkIfEmpty(new int[]{0,0}, new int[]{0,1}, new int[]{0,2}, num);
            case "r1":
                return checkIfEmpty(new int[]{1,0}, new int[]{1,1}, new int[]{1,2}, num);
            case "r2":
                return checkIfEmpty(new int[]{2,0}, new int[]{2,1}, new int[]{2,2}, num);
            case "c0":
                return checkIfEmpty(new int[]{0,0}, new int[]{1,0}, new int[]{2,0}, num);
            case "c1":
                return checkIfEmpty(new int[]{0,1}, new int[]{1,1}, new int[]{2,1}, num);
            case "c2":
                return checkIfEmpty(new int[]{0,2}, new int[]{1,2}, new int[]{2,2}, num);
            case "d0":
                return checkIfEmpty(new int[]{0,0}, new int[]{1,1}, new int[]{2,2}, num);
            default:
                return checkIfEmpty(new int[]{0,2}, new int[]{1,1}, new int[]{2,0}, num);
        }
    }

    private int[] checkIfEmpty(int[] index1, int[] index2, int[] index3, int num) {
        if (matrix[index1[0]][index1[1]] == ' ')
        {
            return new int[]{index1[0], index1[1], num};
        }
        else if (matrix[index2[0]][index2[1]] == ' ')
        {
            return new int[]{index2[0], index2[1], num};
        }
        else if (matrix[index3[0]][index3[1]] == ' ')
        {
            return new int[]{index3[0], index3[1], num};
        }
        else
        {
            return new int[]{-1, -1, -1};
        }
    }

    private void setCounters()
    {
        switch (i) {
            case 0:
                switch (j) {
                    case 0:  incCounters(0,3,6,-1); break;
                    case 1:  incCounters(0,4,-1,-1); break;
                    case 2:  incCounters(0,5,7,-1); break;
                }break;
            case 1: {
                switch (j) {
                    case 0: incCounters(1,3,-1,-1); break;
                    case 1: incCounters(1,4,6,7); break;
                    case 2: incCounters(1,5,-1,-1); break;
                }break;
            }
            case 2: {
                switch (j) {
                    case 0:  incCounters(2,3,7,-1); break;
                    case 1:  incCounters(2,4,-1,-1); break;
                    case 2:  incCounters(2,5,6,-1);break;
                }break;
            }
        }
    }

    private void incCounters(int i0, int i1, int i2, int i3) {
        if (sign == 'X')
        {
            incCounters(i0, i1, i2, i3, countersX);
        }
        else
        {
            incCounters(i0, i1, i2, i3, counters0);
        }
    }

    private void incCounters(int i0, int i1, int i2, int i3, int[] counts) {
        counts[i0]++;
        checkVictory(counts[i0], i0);
        counts[i1]++;
        checkVictory(counts[i1], i1);
        if (i2 > -1)
        {
            counts[i2]++;
            checkVictory(counts[i2], i2);
        }
        if (i3 > -1)
        {
            counts[i3]++;
            checkVictory(counts[i3], i3);
        }
    }

    private void checkVictory(int value, int index) {
        if (value == 3)
        {
            result += parseResult(index) +",";
        }
    }

    public String getResult()
    {
        return result;
    }

    public int getNumOfTurns()
    {
        return counter;
    }

    private String parseResult(int i)
    {
        switch (i)
        {
            case 0: return "r0";
            case 1: return "r1";
            case 2: return "r2";
            case 3: return "c0";
            case 4: return "c1";
            case 5: return "c2";
            case 6: return "d0";
            default: return "d1";
        }
    }
}
