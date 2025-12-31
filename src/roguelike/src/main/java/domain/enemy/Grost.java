package domain.enemy;


import utils.EntityProperties;
import domain.abstact.Attributes;

public class Grost extends Attributes  {
    private final EntityProperties properties;
    private final int hostility;

    public Grost(int x, int y) {
        this(createProperties(), x, y);
    }

    private Grost(EntityProperties properties, int x, int y){
        super(properties.getName(), properties.getSymbol(),
                properties.getColor(), properties.getMaxHealth(),
                properties.getHealth(), properties.getAgility(),
                properties.getStrength(), x, y);
        this.properties = properties;
        this.hostility = properties.getHostility();
    }

    private static EntityProperties createProperties(){
        return new EntityProperties("grost");
    }

    public EntityProperties getProperties() {
        return properties;
    }
}
