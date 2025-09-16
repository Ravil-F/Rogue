package domain.enums;

public enum ScrollE {
    HEALTH_S('s', "health scroll", 20),
    AGILITY_S('s', "agility scroll", 20),
    STRENGTH_S('s', "strength scroll", 20);

    private final char symbol;
    private final String name;
    private final int increase;

    ScrollE(char symbol, String name, int increase) {
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
