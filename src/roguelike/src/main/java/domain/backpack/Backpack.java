package domain.backpack;

import java.util.ArrayList;
import java.util.List;
import domain.abstact.Items;
import domain.items.Elixir;
import domain.items.Food;
import domain.items.Scroll;
import domain.items.Weapon;

public class Backpack {
    private List<Items> items;
    private int counter;
    private final int maxSize = 9;

    //для каждого предмета нужно по 9 слотов
    private List<Food> packFoods;
    private List<Weapon> packWeapons;
    private List<Elixir> packElixirs;
    private List<Scroll> packScroll;

    public Backpack() {
        items = new ArrayList<>();
        packFoods = new ArrayList<>(9);
        packWeapons = new ArrayList<>(9);
        packElixirs = new ArrayList<>(9);
        packScroll = new ArrayList<>(9);
        this.counter = 0;
    }

    public Items getItems(int index){
        return items.get(index);
    }

    public List<Items> getItems() {
        return items;
    }

    public int getItemsSize(){
        return items.size();
    }

    public String getItemsName(){
        return items.get(0).getName();
    }

    public char getItemsSymbol(){
        return items.get(0).getSymbol();
    }

    public int getItemsIncrease(){
        return items.get(0).getIncrease();
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void add(Items item){
        if (counter < maxSize) {
            this.items.add(item);
            ++counter;
        }
    }

    public void remove(int index){
        if (counter != 0) {
            this.items.remove(index);
            --counter;
        }
    }

    public void printBackpack(){
        for(Items i : items) {
            System.out.println("Name: " + i.getName() +
                    " Symbol: " + i.getSymbol() +
                    " increase: " + i.getIncrease());
        }
    }

    public int getMaxSize() {
        return maxSize;
    }
}
