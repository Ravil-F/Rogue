package utils;

import com.googlecode.lanterna.TextColor;

public class PlayerProperties extends MainProperties {
    private String entityId;

    public PlayerProperties(String entityId) {
        super("player.properties");
        this.entityId = entityId;
    }

    public String getName() {
        return getStrProperty(this.entityId + ".name", "player");
    }

    public char getSymbol() {
        return getCharProperty(this.entityId + ".symbol", '@');
    }

    public String getColor() {
        return getStrProperty(this.entityId + ".color", "RED");
    }

    public int getMaxHealth() {
        return getIntProperty(this.entityId + ".maxHealth", 100);
    }

    public int getHealth() {
        return getIntProperty(this.entityId + ".health", 100);
    }

    public int getAgility() {
        return getIntProperty(this.entityId + ".agility", 90);
    }

    public int getStrength() {
        return getIntProperty(this.entityId + ".strength", 40);
    }

    public TextColor getTextColor() {
        return parseColor(getColor());
    }
}
