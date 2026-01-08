package domain.enums;


public enum ElixirE {
    HEALTH_E('e', "health elixir", 20, ColorE.BLUE),
    AGILITY_E('e', "agility elixir",  30, ColorE.BLUE),
    STRENGTH_E('e', "strength elixir", 10, ColorE.BLUE);

    private final char symbol;
    private final String name;
    private final int increase;
    private final ColorE color;

    ElixirE(char symbol, String name, int increase, ColorE color) {
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

    public int getIncrease() {
        return increase;
    }

    public ColorE getColor() {
        return color;
    }
}
