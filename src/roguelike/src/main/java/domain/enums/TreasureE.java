package domain.enums;

import com.googlecode.lanterna.TextColor;

public enum TreasureE {
    TREASURES_T('t', "treasures", TextColor.ANSI.MAGENTA, 10);

    private final char symbol;
    private final String name;
    private final int increase;
    private final TextColor color;

    TreasureE(char symbol, String name, TextColor color, int increase) {
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
