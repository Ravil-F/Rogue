package domain.enums;

import com.googlecode.lanterna.TextColor;

public enum FoodE {
    BREAD_F('f', "bread", TextColor.ANSI.GREEN, 10),
    MEAT_F('f', "meat", TextColor.ANSI.GREEN, 20);

    private final char symbol;
    private final String name;
    private final int increase;
    private final TextColor color;

    FoodE(char symbol, String name, TextColor color, int increase) {
        this.symbol = symbol;
        this.name = name;
        this.increase = increase;
        this.color = color;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public TextColor getColor() {
        return color;
    }

    public int getIncrease() {
        return increase;
    }
}
