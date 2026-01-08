package domain.abstact;

import com.googlecode.lanterna.TextColor;
import domain.enums.ColorE;

public abstract class Items extends Entity{
    private  final int increase;

    public Items(char symbol, String name, int increase, TextColor color, int x, int y) {
        super(name, symbol, color, x, y);
        this.increase = increase;
    }

    public int getIncrease() {
        return increase;
    }
}
