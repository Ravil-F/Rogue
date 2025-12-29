package utils;

import domain.enums.ColorE;
import domain.enums.HostilityE;
import java.util.Properties;

public class EnemyProperties {
    private CommonProperties common;
//    private final String name;
    private final char symbol;
    private final String color;
    private final int maxHealth;
    private final int health;
    private final int agality;
    private final int strength;
    private final String hostility;

    EnemyProperties(String enemyID){
        common = new CommonProperties("enemy.properties");
        this.name = common.getStrProperty(enemyID + ".name", "Unknown");
    }

    public String getName(){return name;}
}
