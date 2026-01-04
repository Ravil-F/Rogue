package domain.location;

import domain.interfaces.Check;
import utils.CommonProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;

public class Map implements Check {
    private List<Rooms> room;
    private List<Passage> passages;
    private CommonProperties common;
    private int[][] map;

    private static final int ROOMS_IN_WIDTH = 3;
    private static final int ROOMS_IN_HEIGHT = 3;
    private static final int REGION_WIDTH = 27;
    private static final int REGION_HEIGHT = 10;

    private static final int MIN_ROOM_WIDTH = 6;
    private static final int MAX_ROOM_WIDTH = REGION_WIDTH - 2;
    private static final int MIN_ROOM_HEIGHT = 5;
    private static final int MAX_ROOM_HEIGHT = REGION_HEIGHT - 2;

    public static final int MAP_WIDTH = ROOMS_IN_WIDTH * REGION_WIDTH;   // 81
    public static final int MAP_HEIGHT = ROOMS_IN_HEIGHT * REGION_HEIGHT; // 30

    private static final int NUM_ROOMS = 9;
    private static final Random rnd = new Random();

    private List<Rooms> rooms = new ArrayList<>();
    private List<Passage> passages = new ArrayList<>();

    public Map() {
        common = new CommonProperties();
        this.map = new int[MAP_WIDTH][MAP_HEIGHT];
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                map[x][y] = '0';
            }
        }
        generateRoomsAndPassages();
        sendRoomsIntoMap();
        sendPassagesIntoMap();
    }

    public void putZero(int x, int y) {
        if (isWithInBounds(x, y)) {
            map[x][y] = ' ';
        }
    }

    public int getMap(int x, int y) {
        return map[x][y];
    }

    public char getMapChar(int x, int y) {
        return (char)map[x][y];
    }

    public void setMap(int x, int y, int value) {
        if (isWithInBounds(x, y)) {
            this.map[x][y] = value;
        }
    }

    public int getWidth() {
        return MAP_WIDTH;
    }

    public int getHeight() {
        return MAP_HEIGHT;
    }

    @Override
    public boolean isWithInBounds(int x) {
        return false;
    }

    @Override
    public boolean isWithInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < common.getWidthHeight() && y < common.getWidthHeight();
    }

    @Override
    public boolean checkingSymbols(char symbol) {
        return false;
    }
}
