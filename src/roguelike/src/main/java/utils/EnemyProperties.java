package utils;

import domain.enums.ColorE;
import domain.enums.HostilityE;
import java.util.Properties;

public class EnemyProperties extends CommonProperties{
    private final String name;
    private final char symbol;
    private final String color;
    private final int maxHealth;
    private final int health;
    private final int agality;
    private final int strength;
    private final String hostility;

    EnemyProperties(String enemyID){
        super("enemy.properties");
        this.name = getStrProperty(enemyID + ".name", "Unknown");
    }

    public String getName(){return name;}
}
