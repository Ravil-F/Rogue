package domain.abstact;

import com.googlecode.lanterna.TextColor;

public abstract class Items extends Entity {
    private int increase;

    public Items(char symbol, String name, int increase, TextColor color, int x, int y) {
        super(name, symbol, color, x, y);
        this.increase = increase;
    }

    public int getIncrease() {
        return increase;
    }

    public void setIncrease(int increase) {
        this.increase = increase;
    }
}
