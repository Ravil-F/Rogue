package domain.location;

import domain.interfaces.Utils;
import utils.CommonProperties;

import java.util.List;

public class Map implements Utils {
    private List<Rooms> room;
    private List<Passage> passages;
    private CommonProperties common;
    private int[][] map;
    
    public Map(){
        common = new CommonProperties();
        this.map = new int[this.common.getWidthHeight()][this.common.getWidthHeight()];
    }

    public void putZero(int x, int y) {
        if (isWithInBounds(x, y)) {
            map[x][y] = '0';
        }
    }

    public int getMap(int x, int y) {
        return map[x][y];
    }

    public char getMapChar(int x, int y) {
        return (char)map[x][y];
    }

    public void setMap(int x, int y, int value) {
        this.map[x][y] = value;
    }

    public int getWidth() {
        return common.getWidthHeight();
    }

    public int getHeight() {
        return common.getWidthHeight();
    }


    @Override
    public boolean isWithInBounds(int x) {
        return false;
    }

    @Override
    public boolean isWithInBounds(int x, int y) {
        return x > 0 && y > 0 && x < common.getWidthHeight() && y < common.getWidthHeight();
    }
}
