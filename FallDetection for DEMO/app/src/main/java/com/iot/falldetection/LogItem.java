package com.iot.falldetection;

public class LogItem {

    String text;
    int color; // 0--> Black (OK), 1 --> Yellow (WARNING), 2 --> Red (ERROR), 3 --> Green (CONNECTION)
    boolean connection;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isConnection() {
        return connection;
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public LogItem(String text, int color, boolean connection) {
        this.text = text;
        this.color = color;
        this.connection = connection;
    }
}
