package com.iot.falldetection;

public class RIItem {

    int text;
    int color; // 0--> Black (OK), 1 --> Yellow (WARNING), 2 --> Red (ERROR), 3 --> Green (CONNECTION)

    public int getText() {
        return text;
    }

    public void setText(int text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public RIItem(int text, int color) {
        this.text = text;
        this.color = color;
    }
}
