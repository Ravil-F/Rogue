package domain.enemy;

import domain.interfaces.Action;
import domain.interfaces.Check;
import utils.CommonProperties;
import utils.EntityProperties;
import domain.abstact.Attributes;

import java.util.Random;

public class Zombi extends Attributes implements Check, Action {
    private final EntityProperties properties;
    private final int hostility;
    private CommonProperties common;

    public Zombi(int x, int y) {
        this(createProperties(), x, y);
        this.common = new CommonProperties();
    }

    private Zombi(EntityProperties properties, int x, int y){
        super(properties.getName(), properties.getSymbol(),
                properties.getColor(), properties.getMaxHealth(),
                properties.getHealth(), properties.getAgility(),
                properties.getStrength(), x, y);
        this.properties = properties;
        this.hostility = properties.getHostility();
    }
    
    private static EntityProperties createProperties(){
        return new EntityProperties("zombi");
    }

    public EntityProperties getProperties() {
        return properties;
    }

    @Override
    public int[] move(int x, int y, char symbol) {
        // Зомби в 4 направления
        Random random = new Random();
        int newX = x;
        int newY = y;
        int direction = random.nextInt(4);
        switch (direction) {
            case 0: ++newX; break;
            case 1: --newX; break;
            case 2: ++newY; break;
            case 3: --newY; break;
        }

        if(isWithInBounds(newX, newY))
            return new int[]{x,y};
        return new int[]{newX, newY};
    }

    @Override
    public int move(int xy, boolean sign) {
        return 0;
    }

    @Override
    public boolean isWithInBounds(int x) {
        return false;
    }

    @Override
    public boolean isWithInBounds(int x, int y) {
        return (x < 0 || x >= common.getWidthHeight() || y < 0 || y >= common.getWidthHeight());
    }

    @Override
    public boolean checkingSymbols(char symbol){
        return symbol == 's' || symbol == 'w' || symbol == 'f' ||
                symbol == 'e' || symbol == 'Z' || symbol == 'G' ||
                symbol == 'S' || symbol == 'O' || symbol == 'V';
    }
}
