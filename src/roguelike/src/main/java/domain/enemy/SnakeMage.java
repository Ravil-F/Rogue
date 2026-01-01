package domain.enemy;

import domain.abstact.Attributes;
import utils.EntityProperties;

public class SnakeMage extends Attributes {
    private final EntityProperties properties;
    private final int hostility;

    public SnakeMage(int x, int y) {
        this(createProperties(), x, y);
    }

    private SnakeMage(EntityProperties properties, int x, int y){
        super(properties.getName(), properties.getSymbol(),
                properties.getColor(), properties.getMaxHealth(),
                properties.getHealth(), properties.getAgility(),
                properties.getStrength(), x, y);
        this.properties = properties;
        this.hostility = properties.getHostility();
    }

    private static EntityProperties createProperties(){
        return new EntityProperties("snake_mage");
    }

    public EntityProperties getProperties() {
        return properties;
    }
}
