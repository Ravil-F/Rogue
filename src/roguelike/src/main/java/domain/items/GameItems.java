package domain.items;

import domain.abstact.Items;
import domain.enums.*;
import domain.interfaces.GenerateRandom;
import domain.interfaces.Check;
import utils.CommonProperties;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameItems implements Check, GenerateRandom {
    transient CommonProperties common;
    private List<Items> items;
    private transient Random random;
    private transient final int countItems = 5;

    public GameItems(){
        items = new LinkedList<Items>();
        random = new Random();
        common = new CommonProperties();
    }

    @Override
    public void generateRandom(int level) {
        int tmpXY = common.getWidthHeight();
        int tmpDifference = checkDifference(level);
        int i = items.size();
        for (; i < common.getMaxlevel() - tmpDifference; i++) {
            int countRandom = random.nextInt(0, countItems);
            switch (countRandom){
                case 0:
                    ElixirE randomElixir = ElixirE.values()[random.nextInt(0, ElixirE.values().length)];
                    items.add(new Elixir(randomElixir, 60, randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 1:
                    FoodE randomFood = FoodE.values()[random.nextInt(0, FoodE.values().length)];
                    items.add(new Food(randomFood, randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 2:
                    ScrollE randomScroll = ScrollE.values()[random.nextInt(0, ScrollE.values().length)];
                    items.add(new Scroll(randomScroll, randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 3:
                    WeaponE randomWeapon = WeaponE.values()[random.nextInt(0, WeaponE.values().length)];
                    items.add(new Weapon(randomWeapon, randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 4:
                    TreasureE randomTreasure = TreasureE.values()[random.nextInt(0, TreasureE.values().length)];
                    items.add(new Treasure(randomTreasure, randomXY(tmpXY), randomXY(tmpXY)));
                    break;
            }
        }
    }

    public Items generateTreasure(int x, int y) {
        TreasureE randomTreasure = TreasureE.values()[random.nextInt(0, TreasureE.values().length)];
        return new Treasure(randomTreasure, x, y);
    }

    @Override
    public int checkDifference(int level){
        int result = 16;
        if (level <= 5) result = 16;
        else if (level <= 10) result = 17;
        else if (level <= 15) result = 18;
        else if (level <= 20) result = 19;
        return result;
    }

    @Override
    public int randomXY(int xy){
        int tmp =  random.nextInt(xy);
        if (isWithInBounds(tmp))
            return tmp;
        else return randomXY(xy);
    }

    @Override
    public boolean isWithInBounds(int x) {
        return x >= 0 && x < common.getWidthHeight();
    }

    @Override
    public boolean isWithInBounds(int x, int y) {
        return true;
    }

    @Override
    public boolean checkingSymbols(char symbol) {
        return false;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }
}
