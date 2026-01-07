package domain.enums;

public enum TreasureE {
    TREASURES_T('t', "treasures", 10);

    private final char symbol;
    private final String name;
    private final int increase;

    TreasureE(char symbol, String name, int increase){
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
