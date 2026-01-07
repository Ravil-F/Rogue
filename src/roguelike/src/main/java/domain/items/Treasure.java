package domain.items;

import domain.abstact.Items;
import domain.enums.TreasureE;

public class Treasure extends Items {
    private TreasureE treasure;

    public Treasure(TreasureE treasure, int x, int y){
        super(treasure.getSymbol(), treasure.getName(), treasure.getIncrease(), x, y);
        this.treasure = treasure;
    }
}
