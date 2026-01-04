package domain.abstact;

public abstract class Items extends Entity{
    private  final int increase;

    public Items(char symbol, String name, int increase, int x, int y) {
        super(name, symbol, "WHITE", x, y);
        this.increase = increase;
    }

    public int getIncrease() {
        return increase;
    }
}
