package domain.enums;

public enum WeaponE {
    GUN_W('w', "gun", 20),
    SWORD_W('w', "sword", 10);    //меч

    private final char symbol;
    private final String name;
    private final int increase;

    WeaponE(char symbol, String name, int increase) {
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
