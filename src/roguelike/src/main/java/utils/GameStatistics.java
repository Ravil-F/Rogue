package utils;

import java.io.Serializable;

public class GameStatistics implements Serializable, Comparable<GameStatistics>{
    private int treasure;
    private int maxLevel;
    private int enemyKilled;
    private int foodEaten;
    private int elixirDrink;
    private int scrollUse;
    private int attacksMade;
    private int attacksReceived;
    private int cellMoved;
    private String name;
    private boolean isVictory;

    public GameStatistics(String name) {
        this.treasure = 0;
        this.maxLevel = 0;
        this.enemyKilled = 0;
        this.foodEaten = 0;
        this.elixirDrink = 0;
        this.scrollUse = 0;
        this.attacksMade = 0;
        this.attacksReceived = 0;
        this.cellMoved = 0;
        this.name = name;
        this.isVictory = false;
    }

    public int getTreasure() {
        return treasure;
    }

    public void setTreasure(int treasure) {
        this.treasure = treasure;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getEnemyKilled() {
        return enemyKilled;
    }

    public void setEnemyKilled(int enemyKilled) {
        this.enemyKilled = enemyKilled;
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public void setFoodEaten(int foodEaten) {
        this.foodEaten = foodEaten;
    }

    public int getElixirDrink() {
        return elixirDrink;
    }

    public void setElixirDrink(int elixirDrink) {
        this.elixirDrink = elixirDrink;
    }

    public int getScrollUse() {
        return scrollUse;
    }

    public void setScrollUse(int scrollUse) {
        this.scrollUse = scrollUse;
    }

    public int getAttacksMade() {
        return attacksMade;
    }

    public void setAttacksMade(int attacksMade) {
        this.attacksMade = attacksMade;
    }

    public int getAttacksReceived() {
        return attacksReceived;
    }

    public void setAttacksReceived(int attacksReceived) {
        this.attacksReceived = attacksReceived;
    }

    public int getCellMoved() {
        return cellMoved;
    }

    public void setCellMoved(int cellMoved) {
        this.cellMoved = cellMoved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVictory() {
        return isVictory;
    }

    public void setVictory(boolean victory) {
        isVictory = victory;
    }

    public void addTreasure(int treasure){
        this.treasure = this.treasure + treasure;
    }

    public void addEnemyKilled(){
        this.enemyKilled++;
    }

    public  void addFoodEaten() {this.foodEaten++;}

    public void addElixirDrink(){this.elixirDrink++; }

    public void addScrollUse(){this.scrollUse++;}

    public void addAttacksMade(){this.attacksMade++;}

    public void addAttacksReceived(){this.attacksReceived++;}

    public void addCellMoved(){this.cellMoved++;}

    @Override
    public int compareTo(GameStatistics o) {
        return Integer.compare(o.treasure, this.treasure);
    }
}
