package domain.location;

import domain.interfaces.Check;
import utils.CommonProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;

public class Map implements Check {
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

    public static final int MAP_WIDTH = ROOMS_IN_WIDTH * REGION_WIDTH;
    public static final int MAP_HEIGHT = ROOMS_IN_HEIGHT * REGION_HEIGHT;

    private static final int NUM_ROOMS = 9;
    private static final Random rnd = new Random();

    private List<Rooms> rooms = new ArrayList<>();
    private List<Passage> passages = new ArrayList<>();

    public Map() {
        common = new CommonProperties();
        this.map = new int[MAP_WIDTH][MAP_HEIGHT];
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                map[x][y] = ' ';
            }
        }
        generateRoomsAndPassages();
        sendRoomsIntoMap();
        sendPassagesIntoMap();
    }

    public List<Rooms> getRooms() {
        return rooms;
    }

    public List<Passage> getPassages() {
        return passages;
    }

    private void generateRoomsAndPassages() {
        rooms.clear();
        passages.clear();
        for (int i = 0; i < NUM_ROOMS; i++) {
            Rooms room = new Rooms();
            room.generateSingleRoom(i,
                    MIN_ROOM_WIDTH, MAX_ROOM_WIDTH,
                    MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT,
                    REGION_WIDTH, REGION_HEIGHT);
            rooms.add(room);
        }
        generatePassages();
    }

    private void generatePassages() {
        List<Edge> edges = new ArrayList<>();
        for (int row = 0; row < ROOMS_IN_HEIGHT; row++) {
            for (int col = 0; col < ROOMS_IN_WIDTH - 1; col++) {
                int roomA = row * ROOMS_IN_WIDTH + col;
                int roomB = roomA + 1;
                edges.add(new Edge(roomA, roomB));
            }
        }
        for (int row = 0; row < ROOMS_IN_HEIGHT - 1; row++) {
            for (int col = 0; col < ROOMS_IN_WIDTH; col++) {
                int roomA = row * ROOMS_IN_WIDTH + col;
                int roomB = roomA + ROOMS_IN_WIDTH;
                edges.add(new Edge(roomA, roomB));
            }
        }
        Collections.shuffle(edges, rnd);
        DisjointSetUnion dsu = new DisjointSetUnion(NUM_ROOMS);
        for (Edge edge : edges) {
            int roomA = edge.getRoomA();
            int roomB = edge.getRoomB();
            if (!dsu.isConnected(roomA, roomB)) {
                dsu.union(roomA, roomB);
                if (edge.isHorizontal()) {
                    generateHorizontalPassage(roomA, roomB);
                } else {
                    generateVerticalPassage(roomA, roomB);
                }
            }
        }
    }

    private void generateHorizontalPassage(int roomA, int roomB) {
        Rooms room1 = rooms.get(roomA);
        Rooms room2 = rooms.get(roomB);
        int x1 = room1.getRightX();
        int minY1 = room1.getTopY() + 1;
        int maxY1 = room1.getBottomY() - 1;
        int y1 = getRandomInRange(minY1, maxY1);
        int x2 = room2.getLeftX();
        int minY2 = room2.getTopY() + 1;
        int maxY2 = room2.getBottomY() - 1;
        int y2 = getRandomInRange(minY2, maxY2);
        Passage passage = new Passage();
        if (y1 == y2) {
            passage.addSegment(x1, y1, x2, y2);
        } else {
            int turnX = getRandomInRange(Math.min(x1, x2) + 1, Math.max(x1, x2) - 1);
            passage.addSegment(x1, y1, turnX, y1);
            passage.addSegment(turnX, Math.min(y1, y2), turnX, Math.max(y1, y2));
            passage.addSegment(turnX, y2, x2, y2);
        }
        passages.add(passage);
    }

    private void generateVerticalPassage(int roomA, int roomB) {
        Rooms room1 = rooms.get(roomA);
        Rooms room2 = rooms.get(roomB);
        int y1 = room1.getBottomY();
        int minX1 = room1.getLeftX() + 1;
        int maxX1 = room1.getRightX() - 1;
        int x1 = getRandomInRange(minX1, maxX1);
        int y2 = room2.getTopY();
        int minX2 = room2.getLeftX() + 1;
        int maxX2 = room2.getRightX() - 1;
        int x2 = getRandomInRange(minX2, maxX2);
        Passage passage = new Passage();
        if (x1 == x2) {
            passage.addSegment(x1, y1, x2, y2);
        } else {
            int turnY = getRandomInRange(Math.min(y1, y2) + 1, Math.max(y1, y2) - 1);
            passage.addSegment(x1, y1, x1, turnY);
            passage.addSegment(Math.min(x1, x2), turnY, Math.max(x1, x2), turnY);
            passage.addSegment(x2, turnY, x2, y2);
        }
        passages.add(passage);
    }

    private void sendRoomsIntoMap() {
        for (Rooms room : rooms) {
            for (int x = room.getLeftX(); x <= room.getRightX(); x++) {
                map[x][room.getTopY()] = '#';
                map[x][room.getBottomY()] = '#';
            }
            for (int y = room.getTopY(); y <= room.getBottomY(); y++) {
                map[room.getLeftX()][y] = '#';
                map[room.getRightX()][y] = '#';
            }
            for (int x = room.getLeftX() + 1; x < room.getRightX(); x++) {
                for (int y = room.getTopY() + 1; y < room.getBottomY(); y++) {
                    map[x][y] = '.';
                }
            }
        }
    }

    private void sendPassagesIntoMap() {
        for (Passage passage : passages) {
            for (Passage.PassageSegment segment : passage.getSegments()) {
                if (segment.isHorizontal()) {
                    int y = segment.getStartY();
                    for (int x = Math.min(segment.getStartX(), segment.getEndX());
                         x <= Math.max(segment.getStartX(), segment.getEndX()); x++) {
                        map[x][y] = '#';
                    }
                } else {
                    int x = segment.getStartX();
                    for (int y = Math.min(segment.getStartY(), segment.getEndY());
                         y <= Math.max(segment.getStartY(), segment.getEndY()); y++) {
                        map[x][y] = '#';
                    }
                }
            }
        }
    }

    private int getRandomInRange(int min, int max) {
        if (max < min) return min;
        return min + rnd.nextInt(max - min + 1);
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

    public boolean isWithInBounds(int x) {
        return false;
    }

    public boolean isWithInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < MAP_WIDTH && y < MAP_HEIGHT;
    }

    @Override
    public boolean checkingSymbols(char symbol) {
        return false;
    }
}
