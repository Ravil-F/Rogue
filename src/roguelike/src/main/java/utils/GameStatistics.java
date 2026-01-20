package utils;

import java.io.Serializable;

public class GameStatistics implements Serializable {
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
    
    
}
