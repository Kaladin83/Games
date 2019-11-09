package com.example.maratbe.other;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maratbe.domain.Theme;
import com.example.maratbe.games.R;

import java.util.List;
import java.util.Optional;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> implements Constants {

    private List<Theme> themes;
    private int selectedPosition = -1;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public ThemeAdapter(List<Theme> themes)
    {
        this.themes = themes;
        selectedPosition = getPositionOfCurrentTheme();
    }

    private int getPositionOfCurrentTheme() {
        for (int i = 0; i < MainActivity.getThemes().size(); i++)
        {
            if (MainActivity.getThemes().get(i).getThemeId() == MainActivity.getCurrentTheme().getThemeId())
            {
                return i;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.themeName.setText(themes.get(position).getThemeName());
        //holder.themeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainActivity.getCurrentTheme().getFontSize());
        holder.themeColor.setBackgroundColor(getColorAccordingToTheme(themes.get(position).getThemeId()));
        holder.layout.setBackgroundColor(selectedPosition == position? GRAY_4: Color.WHITE);
        holder.layout.setOnClickListener(view -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });
    }

    private int getColorAccordingToTheme(int themeId) {
        if (themeId == R.style.SummerHeat)
        {
            return RED_1;
        }
        return GREEN_2;
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView themeName;
        TextView themeColor;
        RelativeLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            themeName = itemView.findViewById(R.id.themeName);
            themeColor = itemView.findViewById(R.id.themeColor);
            layout = itemView.findViewById(R.id.themeItem);
        }
    }
}
