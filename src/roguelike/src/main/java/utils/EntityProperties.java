package utils;

import com.googlecode.lanterna.TextColor;

public class EntityProperties extends MainProperties {
    private String entityId;

    public EntityProperties(String entityId){
        super("enemy.properties");
        this.entityId = entityId;
    }

    public String getName(){
        return getStrProperty(this.entityId + ".name", "Unknown");
    }

    public char getSymbol(){
        return getCharProperty(this.entityId + ".symbol", 'N');
    }

    public String getColor(){
        return  getStrProperty(this.entityId + ".color", "WHITE");
    }

    public int getMaxHealth(){
        return getIntProperty(this.entityId + ".maxHealth", 0);
    }

    public int getHealth(){
        return getIntProperty(this.entityId + ".health", 0);
    }

    public int getAgility(){
        return getIntProperty(this.entityId + ".agility", 0);
    }

    public int getStrength(){
        return getIntProperty(this.entityId + ".strength", 0);
    }

    public int getHostility(){
        return getIntProperty(this.entityId + ".hostility", 2);
    }

    public TextColor getTextColor() {
        return parseColor(getColor());
    }

}
