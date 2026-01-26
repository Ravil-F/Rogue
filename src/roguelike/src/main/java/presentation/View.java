package presentation;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import domain.abstact.Attributes;
import com.googlecode.lanterna.TextColor;
import domain.abstact.Items;
import domain.enums.StatusPlayer;
import domain.location.Rooms;
import domain.location.Passage;

import java.io.IOException;
import java.util.List;


public class View {
    private static final int MENU_WIDTH = 81;
    private static final int MENU_HEIGHT = 30;
    private static final String VERSION = "v1.0";

    private Terminal terminal;
    private TerminalScreen screen;
    private TextGraphics textGraphics;

    private Controller controller;
    private KeyStroke key;

    public View(Controller controller) {
        try {
            this.controller = controller;
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setInitialTerminalSize(new TerminalSize(100, 40)); // ширина x высота
            this.terminal = factory.createTerminal();
            this.screen = new TerminalScreen(terminal);
            textGraphics = screen.newTextGraphics();
            screen.startScreen();
            screen.setCursorPosition(null);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // VIEW WINDOWS
    public void startWindow(){
        try {
            screen.clear();
            int offsetX = 1;
            int offsetY = 1;
            drawRectangle(textGraphics,
                    offsetY,
                    MENU_HEIGHT + offsetY,
                    offsetX,
                    MENU_WIDTH + offsetX);
            String title = "ROGUELIKE";
            int titleX = offsetX + (MENU_WIDTH - title.length()) / 2;
            int titleY = offsetY + 3;
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(titleX, titleY, title);
            int menuStartY = titleY + 3;
            int menuX = offsetX + (MENU_WIDTH - 30) / 2;
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(menuX, menuStartY, "1 - New game");
            textGraphics.putString(menuX, menuStartY + 1, "2 - Load save game");
            textGraphics.putString(menuX, menuStartY + 2, "3 - Load game statistics");
            textGraphics.putString(menuX, menuStartY + 3, "Escape - Exit game");
            int versionX = offsetX + MENU_WIDTH - VERSION.length() - 1;
            int versionY = offsetY + MENU_HEIGHT - 1;
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(versionX, versionY, VERSION);
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            screen.refresh();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String inputScan() throws IOException, InterruptedException {
        StringBuilder res = new StringBuilder();
        screen.clear();
        char symbol;
        int i = 23;
        textGraphics.putString(4, 2, "Enter name Player:");
        screen.refresh();
        do {
            setKey();
            if ((key.getCharacter() != ' ') && (key.getKeyType() == KeyType.Enter))
                break;
            symbol = key.getCharacter();
            textGraphics.putString(i, 2, String.valueOf(symbol));
            res.append(symbol);
            screen.refresh();
            ++i;
        } while (true);
        return res.toString().trim();
    }

    private void viewMap() {
        MapInfo info = initInfo();
        drawRectangle(textGraphics,
                info.offsetY - 1,
                info.mapHeight + info.offsetY,
                info.offsetX - 1,
                info.mapWidth + info.offsetX);
        drawRooms(info);
        drawPassages(info);
        drawEntities(info);
    }

    private MapInfo initInfo() {
        int mapWidth = controller.getModel().getMap().getWidth();
        int mapHeight = controller.getModel().getMap().getHeight();
        int offsetX = 1;
        int offsetY = 1;
        int playerX = controller.getModel().getPlayer().getCoord().getX();
        int playerY = controller.getModel().getPlayer().getCoord().getY();
        int playerRoomIndex = controller.getModel().getMap().determineRoom(playerX, playerY);
        return new MapInfo(mapWidth, mapHeight, offsetX, offsetY,
                playerX, playerY, playerRoomIndex);
    }

    private void drawRooms(MapInfo info) {
        for (int i = 0; i < controller.getModel().getMap().getRooms().size(); i++) {
            Rooms room = controller.getModel().getMap().getRooms().get(i);
            if (!controller.getModel().getMap().isRoomVisited(i)) {
                continue;
            }
            drawRectangle(textGraphics,
                    room.getTopY() + info.offsetY,
                    room.getBottomY() + info.offsetY,
                    room.getLeftX() + info.offsetX,
                    room.getRightX() + info.offsetX);
            if (i == info.playerRoomIndex) {
                drawRoomContent(textGraphics, room, info.offsetX, info.offsetY);
            }
        }
    }

    private void drawPassages(MapInfo info) {
        for (int i = 0; i < controller.getModel().getMap().getPassages().size(); i++) {
            Passage passage = controller.getModel().getMap().getPassages().get(i);
            if (controller.getModel().getMap().isPassageVisited(i)) {
                drawPassageSegments(textGraphics, passage, info.offsetX, info.offsetY);
            }
        }
    }

    private void drawEntities(MapInfo info) {
        for (int x = 0; x < info.mapWidth; ++x) {
            for (int y = 0; y < info.mapHeight; ++y) {
                int roomIndex = controller.getModel().getMap().determineRoom(x, y);
                if (roomIndex != info.playerRoomIndex) {
                    continue;
                }
                char cellChar = controller.getModel().getMap().getMapChar(x, y);
                if (cellChar != 0 && cellChar != ' ' && cellChar != '#' && cellChar != '.') {
                    TextColor color = getCellColor(x, y, cellChar);
                    textGraphics.setForegroundColor(color);
                    textGraphics.putString(x + info.offsetX, y + info.offsetY, String.valueOf(cellChar));
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
            }
        }
    }

    private TextColor getCellColor(int x, int y, char symbol) {
        if (controller.getModel().getPlayer().getCoord().getX() == x
                && controller.getModel().getPlayer().getCoord().getY() == y) {
            return controller.getModel().getPlayer().getColor();
        }

        for (Attributes enemy : controller.getModel().getEnemys().getEnemy()) {
            if (enemy.getCoord().getX() == x && enemy.getCoord().getY() == y) {
                return enemy.getColor();
            }
        }

        for (Items item : controller.getModel().getItems().getItems()) {
            if (item.getCoord().getX() == x && item.getCoord().getY() == y) {
                return item.getColor();
            }
        }

        return TextColor.ANSI.WHITE;
    }


    private void drawRoomContent(TextGraphics tg, Rooms room, int offsetX, int offsetY) {
        for (int y = room.getTopY() + 1; y < room.getBottomY(); y++) {
            for (int x = room.getLeftX() + 1; x < room.getRightX(); x++) {
                tg.putString(x + offsetX, y + offsetY, ".");
            }
        }
    }

    private void drawPassageSegments(TextGraphics tg, Passage passage, int offsetX, int offsetY) {
        for (Passage.PassageSegment segment : passage.getSegments()) {
            if (segment.isHorizontal()) {
                int y = segment.getStartY() + offsetY;
                for (int x = Math.min(segment.getStartX(), segment.getEndX());
                     x <= Math.max(segment.getStartX(), segment.getEndX()); x++) {
                    tg.putString(x + offsetX, y, "#");
                }
            } else {
                int x = segment.getStartX() + offsetX;
                for (int y = Math.min(segment.getStartY(), segment.getEndY());
                     y <= Math.max(segment.getStartY(), segment.getEndY()); y++) {
                    tg.putString(x, y + offsetY, "#");
                }
            }
        }
    }

    public void drawRectangle(TextGraphics tg, int topY, int bottomY, int leftX, int rightX) {
        tg.putString(leftX, topY, "┌");
        tg.putString(rightX, topY, "┐");
        tg.putString(leftX, bottomY, "└");
        tg.putString(rightX, bottomY, "┘");

        for (int x = leftX + 1; x < rightX; x++) {
            tg.putString(x, topY, "─");
            tg.putString(x, bottomY, "─");
        }

        for (int y = topY + 1; y < bottomY; y++) {
            tg.putString(leftX, y, "│");
            tg.putString(rightX, y, "│");
        }
    }

    private void viewInfo(){
        int mapWidth = controller.getModel().getMap().getWidth();
        int mapHeight = controller.getModel().getMap().getHeight();
        int offsetX = 1;
        int offsetY = 1;
        int infoY = mapHeight + offsetY + 2;

        String info = String.format(
            "Level: %d     Health: %d/%d     Agility: %d     Strength: %d     Treasure: %d",
            controller.getModel().getLevel(),
            controller.getModel().getPlayer().getHealth(),
            controller.getModel().getPlayer().getMaxHealth(),
            controller.getModel().getPlayer().getAgility(),
            controller.getModel().getPlayer().getStrength(),
            controller.getModel().getPlayer().getTreasure()
        );
        int infoRectWidth = mapWidth + offsetX;
        drawRectangle(textGraphics, infoY - 1, infoY + 1, offsetX - 1, infoRectWidth);
        int infoTextWidth = info.length();
        int rectWidth = infoRectWidth - offsetX + 1;
        int centerX = (rectWidth - infoTextWidth) / 2 + 1;
        textGraphics.putString(centerX, infoY, info);
    }

    private void viewGameStatus(String message) {
        try {
            screen.clear();
            textGraphics.putString(4, 2, message + controller.getModel().getPlayer().getName());
            screen.refresh();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewSingleItemtype(final char symbol) throws IOException {
        int tmpX = controller.getModel().getMap().getHeight();
        textGraphics.putString(tmpX + 2, 1, "Enter number items (0-8), Escape - exit");
        if (controller.getModel().getBackpack().getScreenOutput().isEmpty())
            textGraphics.putString(tmpX + 2, 2, "Not Items in Backpack");
        else {
            List<Items> item = controller.getModel().getBackpack().getScreenOutput();
            for (int i = 0; i < item.size(); ++i) {
                textGraphics.putString(tmpX + 4, 2 + i, +i + "." + " name-" + item.get(i).getName()
                        + " increase-" + item.get(i).getIncrease());
            }
        }
        screen.refresh();
    }
    // END VIEW WINDOWS

    // GET-SET METOD
    public KeyStroke getKey() {
        return key;
    }

    public void setKey() throws IOException, InterruptedException {
        KeyStroke tmp = screen.readInput();
        if (tmp != null) {
            this.key = tmp;
        }
    }

    public TerminalScreen getScreen() {
        return screen;
    }

    public void setScreen(TerminalScreen screen) {
        this.screen = screen;
    }
    // END GET-SET METOD

    public void passName(String namePlayer) {
        controller.passName(namePlayer);
    }

    public void gameLoop(boolean flag) throws IOException {
        if (flag)
            controller.getModel().gameInitialization();
        try {
            while (controller.getModel().getPlayer().getStatus() != StatusPlayer.GAMEOVER
                    && controller.getModel().getPlayer().getStatus() != StatusPlayer.VICTORY) {
                screen.clear();
                if (this.key != null) {
                    if (this.key.getKeyType() == KeyType.Escape) {
                        controller.getModel().saveGame();
                        controller.getModel().saveStatistics();
                        controller.getModel().getPlayer().setStatus(StatusPlayer.ACTION);
                        return;
                    }

                    if (this.key.getKeyType() == KeyType.Character) {
                        controller.userInput(this.key, true);
                        viewController();
                    }
                    viewMap();
                    viewInfo();
                    screen.refresh();
                    this.key = null;
                    setKey();
                }
            }
            controller.getModel().saveStatistics();
            if (controller.getModel().getPlayer().getStatus().equals(StatusPlayer.GAMEOVER)) {
                viewGameStatus("Game Over, ");
                controller.getModel().saveStatistics();
            } else if (controller.getModel().getPlayer().getStatus().equals(StatusPlayer.VICTORY)) {
                viewGameStatus("Victory! You completed the game, ");
                controller.getModel().saveStatistics();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewController() throws IOException, InterruptedException {
        switch (Character.toLowerCase(this.key.getCharacter())) {
            case 'h':
                viewBackpack('w');
                break;
            case 'j':
                viewBackpack('f');
                break;
            case 'k':
                viewBackpack('e');
                break;
            case 'e':
                viewBackpack('s');
                break;
            default:
                break;
        }
    }

    private void viewBackpack(final char symbol) throws IOException, InterruptedException {
        viewSingleItemtype(symbol);
        setKey();
        while (this.key != null) {
            if (this.key.getKeyType() == KeyType.Escape) {
                screen.clear();
                return;
            }
            if (this.key != null && this.key.getKeyType() == KeyType.Character) {
                controller.userInputBackpack(this.key, symbol);
                screen.clear();
                return;
            }
            setKey();
        }
    }

    public void stopWidows() throws IOException {
        screen.stopScreen();
    }

    public void gameStatisticsView() {
        try {
            screen.clear();

            domain.player.Statistics statsManager = controller.getModel().getStatistics();
            List<utils.GameStatistics> allStatistics = statsManager.getAllStatistics();

            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

            textGraphics.putString(10, 2, "=== GAME STATISTICS ===");

            if (allStatistics.isEmpty()) {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(10, 4, "The statistics are empty for now. Play some games!");
            } else {
                textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                textGraphics.putString(10, 4, "Top 10 players:");

                int startY = 6;
                int columnX = 1;

                textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA);
                textGraphics.putString(columnX, startY, "№");
                textGraphics.putString(columnX + 3, startY, "Name");
                textGraphics.putString(columnX + 15, startY, "Treasure");
                textGraphics.putString(columnX + 24, startY, "Level");
                textGraphics.putString(columnX + 30, startY, "Enemies");
                textGraphics.putString(columnX + 38, startY, "Foods");
                textGraphics.putString(columnX + 44, startY, "Elixirs");
                textGraphics.putString(columnX + 52, startY, "Scrolls");
                textGraphics.putString(columnX + 60, startY, "At_Made");
                textGraphics.putString(columnX + 68, startY, "At_Received");
                textGraphics.putString(columnX + 80, startY, "CellMoved");
                textGraphics.putString(columnX + 90, startY, "Victory");

                for (int x = columnX; x <= columnX + 96; x++) {
                    textGraphics.putString(x, startY + 1, "-");
                }

                int displayCount = Math.min(10, allStatistics.size());
                int currentY = startY + 3;

                for (int i = 0; i < displayCount; i++) {
                    utils.GameStatistics statistics = allStatistics.get(i);

                    if (i < 3) {
                        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
                    } else {
                        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    }

                    String place = (i + 1) + ".";
                    textGraphics.putString(columnX, currentY, place);

                    String name = statistics.getName();
                    if (name.length() > 10) {
                        name = name.substring(0, 7) + "...";
                    }
                    textGraphics.putString(columnX + 3, currentY, name);

                    textGraphics.putString(columnX + 15, currentY,
                            String.format("%8d", statistics.getTreasure()));

                    textGraphics.putString(columnX + 24, currentY,
                            String.format("%5d", statistics.getMaxLevel()));

                    textGraphics.putString(columnX + 30, currentY,
                            String.format("%7d", statistics.getEnemyKilled()));

                    textGraphics.putString(columnX + 38, currentY,
                            String.format("%5d", statistics.getFoodEaten()));

                    textGraphics.putString(columnX + 44, currentY,
                            String.format("%7d", statistics.getElixirDrink()));

                    textGraphics.putString(columnX + 52, currentY,
                            String.format("%7d", statistics.getScrollUse()));

                    textGraphics.putString(columnX + 60, currentY,
                            String.format("%7d", statistics.getAttacksMade()));

                    textGraphics.putString(columnX + 68, currentY,
                            String.format("%11d", statistics.getAttacksReceived()));

                    textGraphics.putString(columnX + 80, currentY,
                            String.format("%9d", statistics.getCellMoved()));

                    String victory = statistics.isVictory() ? "✓" : "✗";
                    if (statistics.isVictory()) {
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                    } else {
                        textGraphics.setForegroundColor(TextColor.ANSI.RED);
                    }
                    textGraphics.putString(columnX + 96, currentY, victory);

                    if (i < 3) {
                        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
                    } else {
                        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    }

                    currentY++;
                }
            }

            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);

            String instruction = "Escape - Exit game";

            textGraphics.putString(0, screen.getTerminalSize().getRows() - 2, instruction);

            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

            screen.refresh();

            boolean viewingStats = true;
            while (viewingStats) {
                KeyStroke key = screen.readInput();
                if (key.getKeyType() == KeyType.Escape) {
                    viewingStats = false;
                    screen.clear();
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static class MapInfo {
        final int mapWidth;
        final int mapHeight;
        final int offsetX;
        final int offsetY;
        final int playerX;
        final int playerY;
        final int playerRoomIndex;

        MapInfo(int mapWidth, int mapHeight, int offsetX, int offsetY,
                         int playerX, int playerY, int playerRoomIndex) {
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.playerX = playerX;
            this.playerY = playerY;
            this.playerRoomIndex = playerRoomIndex;
        }
    }
}
