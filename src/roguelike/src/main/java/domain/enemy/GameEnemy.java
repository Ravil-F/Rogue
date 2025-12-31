package domain.enemy;

import utils.CommonProperties;
import utils.EntityProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEnemy {
    private List<EntityProperties> enemy;
    CommonProperties common;
    private Random random;
//    Zombi zombi;

    public GameEnemy(int x, int y){
        enemy = new ArrayList<>();
        common = new CommonProperties();
//        zombi = new Zombi(x, y);
    }

    public List<EntityProperties> getEnemy() {
        return enemy;
    }

    public void createEnemy(String name){

    }
}
