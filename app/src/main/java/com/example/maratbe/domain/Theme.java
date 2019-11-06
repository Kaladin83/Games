package com.example.maratbe.domain;

public class Theme {
    private String themeName;
    private String themeDialogTheme;
    private int themeId;
    private int themeDialogId;
    private int fontSize;

    public Theme(String themeName, int themeId, String themeDialogTheme, int themeDialogId, int fontSize) {
        this.themeName = themeName;
        this.themeDialogTheme = themeDialogTheme;
        this.themeId = themeId;
        this.themeDialogId = themeDialogId;
        this.fontSize = fontSize;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeDialogTheme() {
        return themeDialogTheme;
    }

    public void setThemeDialogTheme(String themeDialogTheme) {
        this.themeDialogTheme = themeDialogTheme;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public int getThemeDialogId() {
        return themeDialogId;
    }

    public void setThemeDialogId(int themeDialogId) {
        this.themeDialogId = themeDialogId;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
