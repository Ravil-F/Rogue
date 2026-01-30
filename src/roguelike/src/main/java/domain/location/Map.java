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
    private int[][] floor;

    private boolean[] visitedRooms;
    private boolean[] visitedPassages;

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

    private int startRoomNum;
    private int finalRoomNum;

    private int exitX;
    private int exitY;

    public Map() {
        common = new CommonProperties();
        this.map = new int[MAP_WIDTH][MAP_HEIGHT];
        this.floor = new int[MAP_WIDTH][MAP_HEIGHT];
        this.visitedRooms = new boolean[NUM_ROOMS];
        this.visitedPassages = new boolean[NUM_ROOMS * 2];
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                map[x][y] = ' ';
                floor[x][y] = ' ';
            }
        }
        generateRoomsAndPassages();
        sendRoomsIntoMap();
        sendPassagesIntoMap();
        generateExitCoordinates();
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
            room.generateSingleRoom(i, MIN_ROOM_WIDTH, MAX_ROOM_WIDTH, MIN_ROOM_HEIGHT,
                    MAX_ROOM_HEIGHT, REGION_WIDTH, REGION_HEIGHT);
            rooms.add(room);
        }
        startRoomNum = rnd.nextInt(NUM_ROOMS);
        do {
            finalRoomNum = rnd.nextInt(NUM_ROOMS);
        } while (finalRoomNum == startRoomNum);
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
                floor[x][room.getTopY()] = '#';
                floor[x][room.getBottomY()] = '#';
            }
            for (int y = room.getTopY(); y <= room.getBottomY(); y++) {
                map[room.getLeftX()][y] = '#';
                map[room.getRightX()][y] = '#';
                floor[room.getLeftX()][y] = '#';
                floor[room.getRightX()][y] = '#';
            }
            for (int x = room.getLeftX() + 1; x < room.getRightX(); x++) {
                for (int y = room.getTopY() + 1; y < room.getBottomY(); y++) {
                    map[x][y] = '.';
                    floor[x][y] = '.';
                }
            }
        }
    }

    private void sendPassagesIntoMap() {
        for (Passage passage : passages) {
            for (Passage.PassageSegment segment : passage.getSegments()) {
                if (segment.isHorizontal()) {
                    int y = segment.getStartY();
                    for (int x = Math.min(segment.getStartX(), segment.getEndX()); x <= Math
                            .max(segment.getStartX(), segment.getEndX()); x++) {
                        map[x][y] = '.';
                        floor[x][y] = '.';
                    }
                } else {
                    int x = segment.getStartX();
                    for (int y = Math.min(segment.getStartY(), segment.getEndY()); y <= Math
                            .max(segment.getStartY(), segment.getEndY()); y++) {
                        map[x][y] = '.';
                        floor[x][y] = '.';
                    }
                }
            }
        }
    }

    private void generateExitCoordinates() {
        Rooms finalRoom = rooms.get(finalRoomNum);
        this.exitX = getRandomInRange(finalRoom.getLeftX() + 1, finalRoom.getRightX() - 1);
        this.exitY = getRandomInRange(finalRoom.getTopY() + 1, finalRoom.getBottomY() - 1);
    }

    public void markAVisit(int playerX, int playerY) {
        int roomIndex = determineRoom(playerX, playerY);
        if (roomIndex != -1) {
            visitedRooms[roomIndex] = true;
        }
        int passageIndex = determinePassage(playerX, playerY);
        if (passageIndex != -1 && passageIndex < visitedPassages.length) {
            visitedPassages[passageIndex] = true;
        }
    }

    public int determineRoom(int x, int y) {
        for (int i = 0; i < rooms.size(); i++) {
            Rooms room = rooms.get(i);
            if (x >= room.getLeftX() && x <= room.getRightX() && y >= room.getTopY()
                    && y <= room.getBottomY()) {
                return i;
            }
        }
        return -1;
    }

    public int determinePassage(int x, int y) {
        for (int i = 0; i < passages.size(); i++) {
            Passage passage = passages.get(i);
            for (Passage.PassageSegment segment : passage.getSegments()) {
                if (segment.isHorizontal()) {
                    int segY = segment.getStartY();
                    int minX = Math.min(segment.getStartX(), segment.getEndX());
                    int maxX = Math.max(segment.getStartX(), segment.getEndX());
                    if (y == segY && x >= minX && x <= maxX) {
                        return i;
                    }
                } else {
                    int segX = segment.getStartX();
                    int minY = Math.min(segment.getStartY(), segment.getEndY());
                    int maxY = Math.max(segment.getStartY(), segment.getEndY());
                    if (x == segX && y >= minY && y <= maxY) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public boolean isRoomVisited(int roomIndex) {
        return roomIndex >= 0 && roomIndex < visitedRooms.length && visitedRooms[roomIndex];
    }

    public boolean isPassageVisited(int passageIndex) {
        return passageIndex >= 0 && passageIndex < visitedPassages.length
                && visitedPassages[passageIndex];
    }

    /**
     * Находит первую клетку коридора, которая находится на границе указанной комнаты Возвращает
     * координаты [x, y] или null, если коридор не связан с комнатой
     */
    public int[] getPassageDoorCell(int passageIndex, int roomIndex) {
        if (passageIndex < 0 || passageIndex >= passages.size() || roomIndex < 0
                || roomIndex >= rooms.size()) {
            return null;
        }

        Passage passage = passages.get(passageIndex);
        Rooms room = rooms.get(roomIndex);

        if (passage.getSegments().isEmpty()) {
            return null;
        }

        // Проверяем все сегменты коридора, чтобы найти клетку на границе комнаты
        for (Passage.PassageSegment segment : passage.getSegments()) {
            // Проверяем начальную точку сегмента
            int doorX = segment.getStartX();
            int doorY = segment.getStartY();
            if (isPointOnRoomBorder(doorX, doorY, room)) {
                return new int[] {doorX, doorY};
            }

            // Проверяем конечную точку сегмента
            doorX = segment.getEndX();
            doorY = segment.getEndY();
            if (isPointOnRoomBorder(doorX, doorY, room)) {
                return new int[] {doorX, doorY};
            }
        }

        return null;
    }

    /**
     * Проверяет, находится ли точка на границе комнаты
     */
    private boolean isPointOnRoomBorder(int x, int y, Rooms room) {
        // Проверяем горизонтальные границы (сверху/снизу)
        boolean onHorizontalBorder = (y == room.getTopY() || y == room.getBottomY())
                && x >= room.getLeftX() && x <= room.getRightX();
        // Проверяем вертикальные границы (слева/справа)
        boolean onVerticalBorder = (x == room.getLeftX() || x == room.getRightX())
                && y >= room.getTopY() && y <= room.getBottomY();
        return onHorizontalBorder || onVerticalBorder;
    }

    /**
     * Проверяет, связан ли коридор с указанной комнатой (имеет ли дверь на границе комнаты)
     */
    public boolean isPassageConnectedToRoom(int passageIndex, int roomIndex) {
        return getPassageDoorCell(passageIndex, roomIndex) != null;
    }

    /**
     * Проверяет, находится ли точка внутри комнаты (включая границы)
     */
    public boolean isPointInsideRoom(int x, int y, Rooms room) {
        return x >= room.getLeftX() && x <= room.getRightX() && y >= room.getTopY()
                && y <= room.getBottomY();
    }

    /**
     * Проверяет, находится ли точка строго внутри комнаты (без границ)
     */
    public boolean isPointStrictlyInsideRoom(int x, int y, Rooms room) {
        return x > room.getLeftX() && x < room.getRightX() && y > room.getTopY()
                && y < room.getBottomY();
    }

    /**
     * Определяет, находится ли игрок сбоку от комнаты (вертикальное направление видимости) Игрок
     * находится сбоку, если он не может попасть в комнату, двигаясь влево или вправо Основано на
     * логике из C реализации: is_vertical_direction_fog
     */
    public boolean isVerticalDirectionFog(int playerX, int playerY, Rooms room) {
        // Проверяем, может ли игрок попасть в комнату, двигаясь влево или вправо
        // Аналогично C: new_coords.coordinates[X]++ и new_coords.coordinates[X] -= 2
        int checkX1 = playerX + 1;
        if (isPointInsideRoom(checkX1, playerY, room)) {
            return false; // Может попасть вправо - горизонтальное направление
        }
        int checkX2 = playerX - 1;
        if (isPointInsideRoom(checkX2, playerY, room)) {
            return false; // Может попасть влево - горизонтальное направление
        }
        return true; // Не может попасть влево/вправо - вертикальное направление (игрок сбоку)
    }

    /**
     * Проверяет, находится ли игрок рядом с комнатой и может ли видеть её из коридора Основано на
     * логике из C реализации: комната видна, если игрок находится в коридоре рядом с границей
     * комнаты (в пределах 1 клетки)
     */
    public boolean isRoomNearPosition(int roomIndex, int playerX, int playerY) {
        if (roomIndex < 0 || roomIndex >= rooms.size()) {
            return false;
        }

        Rooms room = rooms.get(roomIndex);

        // Проверяем, находится ли игрок ВНЕ комнаты (в коридоре)
        if (isPointInsideRoom(playerX, playerY, room)) {
            return false; // Игрок внутри комнаты, не применяем частичный туман
        }

        // Проверяем, находится ли игрок рядом с границами комнаты (в пределах 1 клетки)
        // Это соответствует логике из C: игрок должен быть в коридоре рядом с комнатой

        // Проверяем горизонтальные границы (сверху/снизу)
        boolean nearTop = (playerY == room.getTopY() - 1) && playerX >= room.getLeftX() - 1
                && playerX <= room.getRightX() + 1;
        boolean nearBottom = (playerY == room.getBottomY() + 1) && playerX >= room.getLeftX() - 1
                && playerX <= room.getRightX() + 1;

        // Проверяем вертикальные границы (слева/справа)
        boolean nearLeft = (playerX == room.getLeftX() - 1) && playerY >= room.getTopY() - 1
                && playerY <= room.getBottomY() + 1;
        boolean nearRight = (playerX == room.getRightX() + 1) && playerY >= room.getTopY() - 1
                && playerY <= room.getBottomY() + 1;

        return nearTop || nearBottom || nearLeft || nearRight;
    }

    /**
     * Проверяет, связана ли комната с коридором (коридор касается комнаты)
     * 
     * @deprecated Используйте isRoomNearPosition вместо этого метода
     */
    @Deprecated
    public boolean isRoomConnectedToPassage(int roomIndex, int passageIndex) {
        if (roomIndex < 0 || roomIndex >= rooms.size() || passageIndex < 0
                || passageIndex >= passages.size()) {
            return false;
        }

        Rooms room = rooms.get(roomIndex);
        Passage passage = passages.get(passageIndex);

        // Проверяем, находится ли хотя бы один сегмент коридора рядом с комнатой
        for (Passage.PassageSegment segment : passage.getSegments()) {
            // Проверяем горизонтальные сегменты
            if (segment.isHorizontal()) {
                int segY = segment.getStartY();
                int minX = Math.min(segment.getStartX(), segment.getEndX());
                int maxX = Math.max(segment.getStartX(), segment.getEndX());

                // Проверяем, касается ли сегмент комнаты или находится рядом
                if ((segY == room.getTopY() || segY == room.getBottomY()
                        || segY == room.getTopY() - 1 || segY == room.getBottomY() + 1)
                        && !(maxX < room.getLeftX() - 1 || minX > room.getRightX() + 1)) {
                    return true;
                }
            }
            // Проверяем вертикальные сегменты
            else {
                int segX = segment.getStartX();
                int minY = Math.min(segment.getStartY(), segment.getEndY());
                int maxY = Math.max(segment.getStartY(), segment.getEndY());

                // Проверяем, касается ли сегмент комнаты или находится рядом
                if ((segX == room.getLeftX() || segX == room.getRightX()
                        || segX == room.getLeftX() - 1 || segX == room.getRightX() + 1)
                        && !(maxY < room.getTopY() - 1 || minY > room.getBottomY() + 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Проверяет видимость ячейки комнаты из коридора на основе направления Основано на алгоритме из
     * C реализации
     */
    public boolean isRoomCellVisibleFromCorridor(int cellX, int cellY, int playerX, int playerY,
            Rooms room) {
        if (!isPointStrictlyInsideRoom(cellX, cellY, room)) {
            return false;
        }

        boolean isVertical = isVerticalDirectionFog(playerX, playerY, room);
        int deltaX = cellX - playerX;
        int deltaY = cellY - playerY;

        if (isVertical) {
            // Вертикальное направление: видно ячейки где |deltaX| >= |deltaY|
            return Math.abs(deltaX) >= Math.abs(deltaY);
        } else {
            // Горизонтальное направление: видно ячейки где |deltaX| <= |deltaY|
            return Math.abs(deltaX) <= Math.abs(deltaY);
        }
    }

    public boolean isExitVertical(int playerX, int playerY, Rooms room) {
        return isVerticalDirectionFog(playerX, playerY, room);
    }

    public boolean isCellVisible(int cellX, int cellY, int playerX, int playerY,
            boolean isVertical) {
        int deltaX = cellX - playerX;
        int deltaY = cellY - playerY;
        if (isVertical) {
            return Math.abs(deltaY) >= Math.abs(deltaX);
        } else {
            return Math.abs(deltaX) >= Math.abs(deltaY);
        }
    }

    public boolean hasLineOfSight(int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        int x = x0;
        int y = y0;
        while (true) {
            if (x == x1 && y == y1) {
                return true;
            }
            if (isWithInBounds(x, y)) {
                char cell = getMapChar(x, y);
                if (cell == '#') {
                    return false;
                }
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    private int getRandomInRange(int min, int max) {
        if (max < min)
            return min;
        return min + rnd.nextInt(max - min + 1);
    }

    public void putZero(int x, int y) {
        if (isWithInBounds(x, y)) {
            map[x][y] = floor[x][y];
        }
    }

    public int[] getRandomPosition() {
        Rooms randomRoom = rooms.get(rnd.nextInt(rooms.size()));
        int x = getRandomInRange(randomRoom.getLeftX() + 1, randomRoom.getRightX() - 1);
        int y = getRandomInRange(randomRoom.getTopY() + 1, randomRoom.getBottomY() - 1);
        return new int[] {x, y};
    }

    public int[] getFreePosition() {
        int maxAttempts = 100;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int[] pos = getRandomPosition();
            int x = pos[0];
            int y = pos[1];
            char cell = getMapChar(x, y);
            if (cell == '.') {
                return pos;
            }
        }
        return getRandomPosition();
    }

    public int[] getStartRoomCoords() {
        Rooms startRoom = rooms.get(startRoomNum);
        int x = getRandomInRange(startRoom.getLeftX() + 1, startRoom.getRightX() - 1);
        int y = getRandomInRange(startRoom.getTopY() + 1, startRoom.getBottomY() - 1);
        return new int[] {x, y};
    }

    public int[] excludeStartRoom() {
        int maxAttempts = 200;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int roomNum = rnd.nextInt(rooms.size());
            if (roomNum == startRoomNum) {
                continue;
            }
            Rooms room = rooms.get(roomNum);
            int x = getRandomInRange(room.getLeftX() + 1, room.getRightX() - 1);
            int y = getRandomInRange(room.getTopY() + 1, room.getBottomY() - 1);
            char cell = getMapChar(x, y);
            if (cell == '.') {
                return new int[] {x, y};
            }
        }
        return getFreePosition();
    }

    public int[] getFinalRoomCoords() {
        return new int[] {exitX, exitY};
    }

    public int getStartRoom() {
        return startRoomNum;
    }

    public int getFinalRoom() {
        return finalRoomNum;
    }

    public char getFloorChar(int x, int y) {
        if (isWithInBounds(x, y)) {
            return (char) floor[x][y];
        }
        return ' ';
    }

    public int getMap(int x, int y) {
        return map[x][y];
    }

    public char getMapChar(int x, int y) {
        return (char) map[x][y];
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
