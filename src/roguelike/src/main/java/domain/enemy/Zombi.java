package domain.enemy;

import utils.EntityProperties;
import domain.abstact.Attributes;

public class Zombi extends Attributes {
    private final EntityProperties properties;

    public Zombi(int x, int y) {
        this(createProperties(), x, y);
    }

    private Zombi(EntityProperties properties, int x, int y){
        super(properties.getName(), properties.getSymbol(),
                properties.getColor(), properties.getMaxHealth(),
                properties.getHealth(), properties.getAgility(),
                properties.getStrength(), x, y);
        this.properties = properties;
    }
    
    private static EntityProperties createProperties(){
        return new EntityProperties("zombi");
    }

    public EntityProperties getProperties() {
        return properties;
    }
}
