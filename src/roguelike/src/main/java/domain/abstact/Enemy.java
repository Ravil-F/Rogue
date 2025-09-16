package domain.abstact;

import domain.enums.EnemyE;
import domain.enums.HostilityE;

//враг
public abstract class Enemy extends Attributes {
    private final HostilityE hostility;


    public Enemy(EnemyE enemyType, int x, int y){
        super(enemyType.getName(), enemyType.getSymbol(), enemyType.getColor(), enemyType.getMaxHealth(), enemyType.getHealth(), enemyType.getAgality(), enemyType.getStrength(), x, y);
        this.hostility = enemyType.getHostility();
    }

    public HostilityE getHostility() {
        return hostility;
    }
}
