package domain.items;

import domain.abstact.Items;
import domain.common.Coord;
import domain.enums.ScrollE;

public class Scroll extends Items {
    private ScrollE scroll;

    public Scroll(ScrollE scroll, int x, int y){
        super(scroll.getSymbol(), scroll.getName(), scroll.getIncrease(), x, y);
    }

    public ScrollE getScroll() {
        return scroll;
    }
}
