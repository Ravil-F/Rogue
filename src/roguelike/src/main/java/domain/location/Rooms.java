package domain.location;

import java.util.Random;

public class Rooms {
    private int leftX, rightX, topY, bottomY, width, height;
    private static final Random rnd = new Random();

    // Генерация комнаты в конкретной зоне карты со случайным размером
    public void generateSingleRoom(int roomNumber, int minWidth, int maxWidth, int minHeight,
            int maxHeight, int regionWidth, int regionHeight) {
        this.width = getRandomPos(minWidth, maxWidth);
        this.height = getRandomPos(minHeight, maxHeight);
        int roomPosX = roomNumber % 3;
        int roomPosY = roomNumber / 3;
        int leftBorder = roomPosX * regionWidth + 1;
        int rightBorder = (roomPosX + 1) * regionWidth - width - 1;
        int topBorder = roomPosY * regionHeight + 1;
        int bottomBorder = (roomPosY + 1) * regionHeight - height - 1;
        leftX = getRandomPos(leftBorder, rightBorder);
        topY = getRandomPos(topBorder, bottomBorder);
        rightX = leftX + width - 1;
        bottomY = topY + height - 1;
    }

    private int getRandomPos(int min, int max) {
        if (max < min)
            return min;
        return min + rnd.nextInt(max - min + 1);
    }

    public int getLeftX() {
        return leftX;
    }

    public int getRightX() {
        return rightX;
    }

    public int getTopY() {
        return topY;
    }

    public int getBottomY() {
        return bottomY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setLeftX(int x) {
        this.leftX = x;
    }

    public void setRightX(int x) {
        this.rightX = x;
    }

    public void setTopY(int y) {
        this.topY = y;
    }

    public void setBottomY(int y) {
        this.bottomY = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int health) {
        this.height = height;
    }
}
