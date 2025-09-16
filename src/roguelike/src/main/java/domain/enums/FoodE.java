package domain.enums;

public enum FoodE {
    BREAD_F('f', "bread", 10),
    MEAT_F('f', "meat", 20);

    private final char symbol;
    private final String name;
    private final int increase;

    FoodE(char symbol, String name, int increase) {
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
