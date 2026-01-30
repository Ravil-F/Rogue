package domain.enums;

import com.googlecode.lanterna.TextColor;

public enum ScrollE {
    HEALTH_S('s', "health scroll", TextColor.ANSI.YELLOW, 20), AGILITY_S('s', "agility scroll",
            TextColor.ANSI.YELLOW,
            20), STRENGTH_S('s', "strength scroll", TextColor.ANSI.YELLOW, 20);

    private final char symbol;
    private final String name;
    private final int increase;
    private final TextColor color;

    ScrollE(char symbol, String name, TextColor color, int increase) {
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
