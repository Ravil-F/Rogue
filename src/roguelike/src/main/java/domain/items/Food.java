package domain.items;

import domain.abstact.Items;
import domain.common.Coord;
import domain.enums.FoodE;

public class Food extends Items {
    private FoodE food;

    public Food(FoodE food, int x, int y){
        super(food.getSymbol(), food.getName(), food.getIncrease(), x, y);
    }

    public FoodE getFood() {
        return food;
    }

}
