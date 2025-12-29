package domain.enemy;

import utils.CommonProperties;
import utils.EnemyProperties;

import java.util.ArrayList;
import java.util.List;

public class GameEnemy {
    private List<EnemyProperties> enemy;
    CommonProperties common;
    Zombi zombi;

    public GameEnemy(int x, int y){
        enemy = new ArrayList<>();
        common = new CommonProperties();
        zombi = new Zombi(x, y);
    }

    public List<EnemyProperties> getEnemy() {
        return enemy;
    }
}
