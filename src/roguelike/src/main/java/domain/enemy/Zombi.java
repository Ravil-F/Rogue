package domain.enemy;
import domain.abstact.Enemy;
import domain.enums.EnemyE;
import domain.enums.HostilityE;

public class Zombi extends Enemy{
    private EnemyE zombi;

    public Zombi(EnemyE enemyType, int x, int y) {
        super(enemyType, x, y);
    }

    public EnemyE getZombi(){return zombi;}

    public int getHealth(){return zombi.getHealth();}
    public int getAgality(){return zombi.getAgality();}
    public int getStrength(){return zombi.getStrength();}
    public HostilityE getHostility(){return zombi.getHostility();}
}
