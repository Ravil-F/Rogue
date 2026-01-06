package domain.enemy;

import domain.abstact.Attributes;
import domain.interfaces.Action;
import domain.interfaces.Check;
import utils.CommonProperties;
import utils.EntityProperties;

import java.util.Random;

public class Orge extends Attributes implements Action, Check {
    private final EntityProperties properties;
    private CommonProperties common;
    private int attackRest;
    private static final int stepAttackRest = 1;

    public Orge(int x, int y) {
        this(createProperties(), x, y);
        this.common = new CommonProperties();
        this.attackRest = 0;
    }

    private Orge(EntityProperties properties, int x, int y){
        super(properties.getName(), properties.getSymbol(),
                properties.getColor(), properties.getMaxHealth(),
                properties.getHealth(), properties.getAgility(),
                properties.getStrength(), properties.getHostility(), x, y);
        this.properties = properties;
    }

    private static EntityProperties createProperties(){
        return new EntityProperties("orge");
    }

    public EntityProperties getProperties() {
        return properties;
    }

    public void updateAtackRest(){
        if (attackRest > 0)
            attackRest--;
    }

    public boolean isAttackRest(){
        return attackRest == 0;
    }

    @Override
    public int[] move(int x, int y, char symbol) {
        if (!isAttackRest())
            return new int[]{x,y};


        // Огр - на 2 клетки, если не может, то на одну
        Random random = new Random();
        int newX = x;
        int newY = y;
        int direction = random.nextInt(4);
        switch (direction) {
            case 0: newX = x + 2; break;
            case 1: newX = x - 2; break;
            case 2: newY = y + 2; break;
            case 3: newY = y - 2; break;
        }

        if (!isWithInBounds(newX, newY)) {
            switch (direction) {
                case 0: newX = x + 1; break;
                case 1: newX = x - 1; break;
                case 2: newY = y + 1; break;
                case 3: newY = y - 1; break;
            }
        }

        if(isWithInBounds(newX, newY))
            return new int[]{newX, newY};
        return new int[]{x, y};
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
        return (x >= 0 && x < common.getWidthHeight() && y >= 0 && y < common.getWidthHeight());
    }

    @Override
    public boolean checkingSymbols(char symbol){
        return symbol == 's' || symbol == 'w' || symbol == 'f' ||
                symbol == 'e' || symbol == 'Z' || symbol == 'G' ||
                symbol == 'S' || symbol == 'O' || symbol == 'V';
    }

    @Override
    public void attack(Attributes entity) {
        if(!isAttackRest())
            return;

        boolean isHit = (Math.random() * 100) <= this.getAgility();
        if(isHit){;
            entity.setHealth(entity.getHealth() - this.getStrength());
            this.attackRest = stepAttackRest;
        }
    }
}
