package domain.location;

import java.util.ArrayList;
import java.util.List;

public class Passage {
    private List<PassageSegment> segments = new ArrayList<>();

    public void addSegment(int startX, int startY, int endX, int endY) {
        segments.add(new PassageSegment(startX, startY, endX, endY));
    }

    public List<PassageSegment> getSegments() {
        return segments;
    }

    public static class PassageSegment {
        private int startX, startY, endX, endY;

        public PassageSegment(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public int getStartX() { return startX; }
        public int getStartY() { return startY; }
        public int getEndX() { return endX; }
        public int getEndY() { return endY; }

        public boolean isHorizontal() {
            return startY == endY;
        }

        public boolean isVertical() {
            return startX == endX;
        }
    }
}