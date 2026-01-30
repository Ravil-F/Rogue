package domain.location;

public class Edge {
    private int roomA;
    private int roomB;

    public Edge(int roomA, int roomB) {
        this.roomA = roomA;
        this.roomB = roomB;
    }

    public int getRoomA() {
        return roomA;
    }

    public int getRoomB() {
        return roomB;
    }

    public boolean isHorizontal() {
        return Math.abs(roomA - roomB) == 1;
    }

    public boolean isVertical() {
        return Math.abs(roomA - roomB) == 3;
    }
}
