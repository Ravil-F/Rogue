package domain.player;

import domain.abstact.Attributes;

import domain.enums.StatusPlayer;
import domain.interfaces.Action;

public class Player extends Attributes implements Action {
    private StatusPlayer status;

    public Player() {
        super("Player", '@', "RED", 100, 100, 0, 0, 5, 5);
        status = StatusPlayer.START;
    }

    public Player(String name, char symbol, String color, int maxHealth, int health, int agility, int strength, int x, int y) {
        super(name, symbol, color, maxHealth, health, agility, strength, x, y);
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
}
