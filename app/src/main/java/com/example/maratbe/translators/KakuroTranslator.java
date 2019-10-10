package com.example.maratbe.translators;

import com.example.maratbe.dataBase.dto.Kakuro;
import com.example.maratbe.domain.Cell;
import com.example.maratbe.domain.Sum;

import java.util.ArrayList;

public class KakuroTranslator {

    public Kakuro daoObjectFromKakuroCell(Cell cell, int id)
    {
        Kakuro kakuro = new Kakuro();
        kakuro.setX(cell.getX());
        kakuro.setY(cell.getY());
        kakuro.setId(id);
        kakuro.setValue(getKakuroValue(cell.getValue(), cell.getSum()));
        return kakuro;
    }

    private String getKakuroValue(String value, ArrayList<Sum> sums) {
        int index = 0;
        StringBuilder sb = new StringBuilder();
        for (Sum sum: sums)
        {
            if (index > 0)
            {
                sb.append(";");
            }
            sb.append(sum.getValue()).append(":").append(sum.getDirection());
            index++;
        }
        return sums.size() > 0? sb.toString(): value;
    }

    public Cell cellFromDaoObject(Kakuro daoCell)
    {
        Cell cell = new Cell();
        cell.setX(daoCell.getX());
        cell.setY(daoCell.getY());
        splitValue(cell, daoCell.getValue());
        return cell;
    }

    private void splitValue(Cell cell, String daoValue) {
        String[] numberOfValues = daoValue.split(";");
        for (String value: numberOfValues) {
            String[] sumsAndDirections = value.split(":");
            if (sumsAndDirections.length > 1) {
                ArrayList<Sum> sums = new ArrayList<>();
                sums.add(new Sum(Integer.parseInt(sumsAndDirections[0]), sumsAndDirections[1]));
                cell.setSum(sums);
                cell.setEnable(false);
            } else {
                cell.setEnable(true);
                cell.setValue(sumsAndDirections[0]);
            }
        }
    }
}
