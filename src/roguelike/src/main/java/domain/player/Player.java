package domain.player;

import domain.abstact.Attributes;
import domain.enums.StatusPlayer;
import domain.interfaces.Action;
import utils.PlayerProperties;

public class Player extends Attributes implements Action {
    private final PlayerProperties properties;
    private StatusPlayer status;

    public Player(int x, int y) {
        this(createProperties(), x, y);
        status = StatusPlayer.START;
    }

    private Player(PlayerProperties properties, int x, int y){
        super(properties.getName(), properties.getSymbol(),
                properties.getColor(), properties.getMaxHealth(),
                properties.getHealth(), properties.getAgility(),
                properties.getStrength(), x, y);
        this.properties = properties;
    }

    private static PlayerProperties createProperties(){
        return new PlayerProperties("player");
    }

    public void increaseStrenght(int xp){
        setStrength(getStrength() + xp);
    }

    public void increaseHealth(int xp){
        setHealth(getHealth() + xp);
    }

    public void increaseAgility(int xp){
        setAgility(getAgility() + xp);
    }

    public StatusPlayer getStatus() {
        return status;
    }

    public void setStatus(StatusPlayer status) {
        this.status = status;
    }

    @Override
    public int[] move(int x, int y, char symbol) {
        return new int[]{x, y};
    }

    @Override
    public int move(int xy, boolean sign){ //true ++, false --
        return sign ? ++xy : --xy;
    }

    @Override
    public void attack(Attributes entity) {
        boolean isHit = (Math.random() * 100) <= this.getAgility();
        if(isHit){
            entity.setHealth(entity.getHealth() - this.getStrength());
        }
    }


}
