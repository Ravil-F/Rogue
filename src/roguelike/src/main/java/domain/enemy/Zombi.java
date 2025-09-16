package domain.enemy;
import domain.abstact.Enemy;
import domain.enums.EnemyE;

public class Zombi extends Enemy{
    public Zombi(EnemyE enemyType, int x, int y) {
        super(enemyType, x, y);
    }
}
