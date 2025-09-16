package domain.abstact;

import domain.common.Coord;

public abstract class Items {
    private final char symbol;
    private final String name;
    private  final int increase;
    private Coord coord;

    public Items(char symbol, String name, int increase, int x, int y) {
        this.symbol = symbol;
        this.name = name;
        this.increase = increase;
        coord = new Coord(x, y);
    }

    public String getName() {
        return name;
    }

    public int getIncrease() {
        return increase;
    }

    public char getSymbol() {
        return symbol;
    }

    public Coord getCoord() {
        return coord;
    }
}
