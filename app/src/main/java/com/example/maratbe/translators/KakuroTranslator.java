package com.example.maratbe.translators;

import com.example.maratbe.dataBase.dto.KakuroTable;
import com.example.maratbe.domain.Cell;
import com.example.maratbe.domain.Sum;

import java.util.ArrayList;

public class KakuroTranslator {

    public KakuroTable daoObjectFromKakuroCell(Cell cell, int id)
    {
        KakuroTable kakuro = new KakuroTable();
        kakuro.setX(cell.getCoordinates().getX());
        kakuro.setY(cell.getCoordinates().getY());
        kakuro.setId(id);
        kakuro.setValue(getKakuroValue(cell.getValue(), cell.getSums()));
        return kakuro;
    }

    private String getKakuroValue(ArrayList<Integer> value, ArrayList<Sum> sums) {
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
        return sums.size() > 0? sb.toString(): String.valueOf(value.get(value.size() - 1));
    }

    public Cell cellFromDaoObject(KakuroTable daoCell)
    {
        Cell cell = new Cell();
        cell.getCoordinates().setX(daoCell.getX());
        cell.getCoordinates().setY(daoCell.getY());
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
                cell.setSums(sums);
                cell.setEnabled(false);
            } else {
                cell.setEnabled(true);
                cell.setValue(sumsAndDirections[0].equals("")? 0: Integer.parseInt(sumsAndDirections[0]));
            }
        }
    }
}
