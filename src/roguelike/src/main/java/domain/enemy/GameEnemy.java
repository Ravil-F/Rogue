package domain.enemy;

import domain.abstact.Attributes;
import domain.interfaces.GenerateRandom;
import domain.interfaces.Check;
import utils.CommonProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEnemy implements Check, GenerateRandom {
    private List<Attributes> enemy;
    transient CommonProperties common;
    private transient Random random;
    private transient final int countEnemy = 5;

    public GameEnemy(){
        enemy = new ArrayList<>();
        common = new CommonProperties();
        random = new Random();
    }

    @Override
    public void generateRandom(int level){
        int tmpXY = common.getWidthHeight();
        int tmpDifference = checkDifference(level);

        for (int i = 0; i <  common.getMaxlevel() - tmpDifference; i++) {
            int countRandom = random.nextInt(0, countEnemy);
            switch (countRandom){
                case 0:
                    enemy.add(new Zombi(randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 1:
                    enemy.add(new Vampire(randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 2:
                    enemy.add(new Grost(randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 3:
                    enemy.add(new Orge(randomXY(tmpXY), randomXY(tmpXY)));
                    break;
                case 4:
                    enemy.add(new SnakeMage(randomXY(tmpXY), randomXY(tmpXY)));
                    break;
            }
        }
    }

    public int getIndex(int x, int y){
        int index = -1;
        for(int i = 0; i < enemy.size() && index == -1; ++i){
            if(enemy.get(i).getCoord().getX() == x &&
                enemy.get(i).getCoord().getY() == y){
                index = i;
            }
        }
        return index;
    }

    @Override
    public int checkDifference(int level) {
        int result = 16;
        if (level <= 5) result = 16;
        else if (level <= 10) result = 15;
        else if (level <= 15) result = 14;
        else if (level <= 20) result = 13;
        return result;
    }

    @Override
    public int randomXY(int xy){
        int tmp =  random.nextInt(xy);
        if (isWithInBounds(tmp))
            return tmp;
        else return randomXY(xy);
    }

    @Override
    public boolean isWithInBounds(int x) {
        return x >= 0 && x < common.getWidthHeight();
    }

    @Override
    public boolean isWithInBounds(int x, int y) {
        return true;
    }

    @Override
    public boolean checkingSymbols(char symbol) {
        return false;
    }

    public List<Attributes> getEnemy() {
        return enemy;
    }

}
