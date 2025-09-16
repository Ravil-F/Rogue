package domain.abstact;

import domain.enums.ColorE;

public abstract class Attributes extends Entity{
    private int maxHealth;
    private int health;
    private int agility;
    private int strength;

//    constructor
    public Attributes(String name, char symbol,
                      ColorE color, int maxHealth,
                      int health, int agility,
                      int strength, int x, int y){

        super(name, symbol, color, x, y);
        this.maxHealth = maxHealth;
        this.health= health;
        this.agility = agility;
        this.strength = strength;
    }

    //setter and getter
    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

 }
