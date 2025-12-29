package domain.enemy;

import domain.common.Coord;
import utils.EnemyProperties;

public class Zombi {
    private EnemyProperties properties;
    private Coord coord;

    public Zombi(int x, int y) {
        this.properties = new EnemyProperties("zombi");
        this.coord = new Coord(x, y);
    }

    public void setCoord(int x, int y){
        coord.setCoord(x, y);
    }

    public int getX(){
        return coord.getX();
    }

    public int getY(){
        return coord.getY();
    }

    public EnemyProperties getProperties() {
        return properties;
    }

    public void setProperties(EnemyProperties properties) {
        this.properties = properties;
    }
}
