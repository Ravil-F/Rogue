package domain.enemy;

import utils.EntityProperties;
import domain.abstact.Attributes;

public class Vampire  extends Attributes {
    private final EntityProperties properties;
    private final int hostility;

    public Vampire(int x, int y) {
        this(createProperties(), x, y);
    }

    private Vampire(EntityProperties properties, int x, int y){
        super(properties.getName(), properties.getSymbol(),
                properties.getColor(), properties.getMaxHealth(),
                properties.getHealth(), properties.getAgility(),
                properties.getStrength(), x, y);
        this.properties = properties;
        this.hostility = properties.getHostility();
    }

    private static EntityProperties createProperties(){
        return new EntityProperties("vampire");
    }

    public EntityProperties getProperties() {
        return properties;
    }
}
