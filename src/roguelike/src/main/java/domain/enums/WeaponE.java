package domain.enums;

import com.googlecode.lanterna.TextColor;

public enum WeaponE {
    GUN_W('w', "gun", TextColor.ANSI.RED, 20),
    SWORD_W('w', "sword", TextColor.ANSI.RED, 10);    //меч

    private final char symbol;
    private final String name;
    private final int increase;
    private final TextColor color;

    WeaponE(char symbol, String name, TextColor color, int increase) {
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
