package domain.player;

import com.googlecode.lanterna.TextColor;
import domain.abstact.Attributes;
import domain.enums.StatusPlayer;
import domain.interfaces.Action;
import utils.PlayerProperties;

public class Player extends Attributes implements Action {
    private transient PlayerProperties properties;
    private StatusPlayer status;
    private int treasure;
    private int sleep;


    public Player(int x, int y) {
        this(createProperties(), x, y);
        status = StatusPlayer.START;
        this.sleep = 0;
        this.treasure = 0;
    }

    private Player(PlayerProperties properties, int x, int y) {
        super(properties.getName(), properties.getSymbol(), properties.getTextColor(),
                properties.getMaxHealth(), properties.getHealth(), properties.getAgility(),
                properties.getStrength(), 0, x, y);
        this.properties = properties;
    }

    public Player() {
        super("Player", '@', TextColor.ANSI.RED, 100, 100, 90, 40, 0, 0, 0);
    }


    private static PlayerProperties createProperties() {
        return new PlayerProperties("player");
    }

    public void increaseStrength(int xp) {
        setStrength(getStrength() + xp);
    }

    public void increaseHealth(int xp) {
        setHealth(getHealth() + xp);
        if (getHealth() > 100)
            setHealth(100);
    }

    public void increaseTreasure(int xp) {
        setTreasure(getTreasure() + xp);
    }

    public void increaseAgility(int xp) {
        setAgility(getAgility() + xp);
    }

    public StatusPlayer getStatus() {
        return status;
    }

    public void setStatus(StatusPlayer status) {
        this.status = status;
    }

    public int getTreasure() {
        return treasure;
    }

    public void setTreasure(int treasure) {
        this.treasure = treasure;
    }


    public void updateSleep() {
        if (sleep > 0) {
            --sleep;
            if (sleep <= 0) {
                status = StatusPlayer.ACTION;
            }
        }
    }

    public void putSleep(int sleep) {
        this.sleep = sleep;
        this.status = StatusPlayer.SLEEP;
    }

    @Override
    public int[] move(int x, int y, char symbol) {
        return new int[] {x, y};
    }

    @Override
    public int move(int xy, boolean sign) { // true ++, false --
        return sign ? ++xy : --xy;
    }

    @Override
    public void attack(Attributes entity) {
        if (entity instanceof domain.enemy.Vampire) {
            domain.enemy.Vampire vampire = (domain.enemy.Vampire) entity;
            if (vampire.isFirstAttack()) {
                vampire.setFirstAttack(false);
                return;
            }
        }
        boolean isHit = (Math.random() * 100) <= this.getAgility();
        if (isHit) {
            entity.setHealth(entity.getHealth() - this.getStrength());
            entity.setAgility(entity.getAgility() - 5);
        }
    }
}
