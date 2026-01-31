package domain.enums;

import com.googlecode.lanterna.TextColor;

public enum ElixirE {
    HEALTH_E('e', "strength elixir", TextColor.ANSI.BLUE, 20), AGILITY_E('e', "agility elixir",
            TextColor.ANSI.BLUE, 30), STRENGTH_E('e', "health elixir", TextColor.ANSI.BLUE, 10);

    private final char symbol;
    private final String name;
    private final int increase;
    private final TextColor color;

    ElixirE(char symbol, String name, TextColor color, int increase) {
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
