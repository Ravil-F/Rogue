package domain.enums;

public enum ElixirE {
    HEALTH_E('e', "health elixir", 20),
    AGILITY_E('e', "agility elixir", 30),
    STRENGTH_E('e', "strength elixir", 10);

    private final char symbol;
    private final String name;
    private final int increase;

    ElixirE(char symbol, String name, int increase) {
        this.symbol = symbol;
        this.name = name;
        this.increase = increase;
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

}
