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
            factory.setInitialTerminalSize(new TerminalSize(110, 35)); // ширина x высота
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
    public void startWindow() {
        try {
            screen.clear();
            int offsetX = 0;
            int offsetY = 0;
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            drawRectangle(textGraphics, offsetY, MENU_HEIGHT + offsetY + 1, offsetX,
                    MENU_WIDTH + offsetX + 1);
            String title = "===== ROGUELIKE =====";
            int titleX = offsetX + (MENU_WIDTH - title.length()) / 2;
            int titleY = offsetY + 3;
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(titleX + 2, titleY - 1, "=================");
            textGraphics.putString(titleX, titleY, title);
            textGraphics.putString(titleX + 2, titleY + 1, "=================");
            int menuStartY = titleY + 4;
            int menuX = offsetX + (MENU_WIDTH - 30) / 2 + 4;
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(menuX, menuStartY - 1, "SELECT THE GAME MODE:");
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(menuX, menuStartY, "1 - New game");
            textGraphics.putString(menuX, menuStartY + 1, "2 - Load game");
            textGraphics.putString(menuX, menuStartY + 2, "3 - Game statistics");
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
            textGraphics.putString(menuX, menuStartY + 4, "ESCAPE - Exit the game");
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            int versionX = offsetX + MENU_WIDTH - VERSION.length();
            int versionY = offsetY + MENU_HEIGHT;
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
        int offsetX = 0;
        int offsetY = 0;
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        drawRectangle(textGraphics, offsetY, MENU_HEIGHT + offsetY + 1, offsetX,
                MENU_WIDTH + offsetX + 1);
        char symbol;
        int pos = MENU_WIDTH / 3 + 1;
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(pos + 1, 2, "ENTER THE PLAYER'S NAME:");
        textGraphics.putString(MENU_WIDTH / 4 - 3, 6, "Press BACKSPACE to delete, ENTER to start the game");
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        int cursorPos = pos + 7;
        screen.refresh();
        do {
            setKey();
            if (key.getKeyType() == KeyType.Enter)
                break;

            if(key.getKeyType() == KeyType.Backspace){
                if(res.length() > 0){
                    res.deleteCharAt(res.length() - 1);
                    textGraphics.putString(cursorPos - 1, 4, " ");
                    cursorPos--;
                    screen.refresh();
                }
            }

            if(key.getKeyType() == KeyType.Escape)
                return " ";

            if(key.getKeyType() == KeyType.Character) {
                symbol = key.getCharacter();
                textGraphics.putString(cursorPos, 4, String.valueOf(symbol));
                res.append(symbol);
                screen.refresh();
                cursorPos++;
            }
        } while (true);
        return res.toString().trim();
    }

    private void viewMap() {
        MapInfo info = initInfo();
        drawRectangle(textGraphics, info.offsetY - 1, info.mapHeight + info.offsetY,
                info.offsetX - 1, info.mapWidth + info.offsetX);
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
        int playerInRoom = controller.getModel().getMap().determineRoom(playerX, playerY);
        return new MapInfo(mapWidth, mapHeight, offsetX, offsetY, playerX, playerY, playerInRoom);
    }

    private void drawRooms(MapInfo info) {
        int playerInPassage =
                controller.getModel().getMap().determinePassage(info.playerX, info.playerY);
        boolean playerIn = playerInPassage != -1;

        // Определяем "текущую комнату игрока" - комнату, в которой находится игрок,
        // или комнату, рядом с которой находится игрок в коридоре
        int currentRoom =
                controller.getModel().getMap().determineCurrentRoom(info.playerX, info.playerY);

        for (int i = 0; i < controller.getModel().getMap().getRooms().size(); i++) {
            Rooms room = controller.getModel().getMap().getRooms().get(i);
            boolean isRoomVisited = controller.getModel().getMap().isRoomVisited(i);
            boolean isCurrentRoom = (i == currentRoom);

            // Пропускаем комнату, если она не посещена и не является текущей
            if (!isRoomVisited && !isCurrentRoom) {
                continue;
            }

            // Если комната посещена или является текущей - рисуем стены
            if (isRoomVisited || isCurrentRoom) {
                drawRectangle(textGraphics, room.getTopY() + info.offsetY,
                        room.getBottomY() + info.offsetY, room.getLeftX() + info.offsetX,
                        room.getRightX() + info.offsetX);
            }

            // Если игрок внутри комнаты - показываем весь контент
            if (i == info.playerInRoom) {
                drawRoomContent(textGraphics, room, info.offsetX, info.offsetY);
            }
            // Если комната является текущей и игрок находится в коридоре рядом с ней -
            // применяем частичный туман (работает как для посещенных, так и для непосещенных
            // комнат)
            else if (isCurrentRoom && playerIn) {
                drawRoomContentWithFog(textGraphics, room, info.offsetX, info.offsetY, info.playerX,
                        info.playerY);
            }
        }
    }

    private void drawPassages(MapInfo info) {
        for (int i = 0; i < controller.getModel().getMap().getPassages().size(); i++) {
            Passage passage = controller.getModel().getMap().getPassages().get(i);
            if (controller.getModel().getMap().isPassageVisited(i)) {
                // Рисуем весь коридор, если он посещен
                drawPassageSegments(textGraphics, passage, info.offsetX, info.offsetY);
            } else {
                // Если коридор не посещен, но комната посещена - показываем только дверь
                drawPassageDoors(i, info);
            }
        }
    }

    private void drawPassageDoors(int passageIndex, MapInfo info) {
        // Проверяем, находится ли игрок в комнате
        if (info.playerInRoom == -1) {
            return; // Игрок не в комнате, двери не отображаем
        }

        // Рисуем двери только для комнаты, в которой находится игрок
        int[] doorCell =
                controller.getModel().getMap().getPassageDoorCell(passageIndex, info.playerInRoom);
        if (doorCell != null) {
            // Рисуем дверь (первую клетку коридора) символом '#'
            textGraphics.putString(doorCell[0] + info.offsetX, doorCell[1] + info.offsetY, "#");
        }
    }

    private void drawEntities(MapInfo info) {
        int playerInPassage =
                controller.getModel().getMap().determinePassage(info.playerX, info.playerY);
        drawPlayer(info);
        drawEnemies(info, playerInPassage);
        drawItems(info);
        drawExit(info);
    }

    private void drawEntitiesSymbol(int x, int y, char symbol, TextColor color, MapInfo info) {
        textGraphics.setForegroundColor(color);
        textGraphics.putString(x + info.offsetX, y + info.offsetY, String.valueOf(symbol));
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    private void drawPlayer(MapInfo info) {
        int playerX = info.playerX;
        int playerY = info.playerY;
        drawEntitiesSymbol(playerX, playerY, controller.getModel().getPlayer().getSymbol(),
                controller.getModel().getPlayer().getColor(), info);
    }

    private void drawEnemies(MapInfo info, int playerInPassage) {
        for (Attributes enemy : controller.getModel().getEnemys().getEnemy()) {
            int enemyX = enemy.getCoord().getX();
            int enemyY = enemy.getCoord().getY();
            int enemyInRoom = controller.getModel().getMap().determineRoom(enemyX, enemyY);
            int enemyInPassage = controller.getModel().getMap().determinePassage(enemyX, enemyY);
            boolean isVisible = false;

            // Если игрок в комнате и враг в той же комнате - видно
            if (info.playerInRoom != -1 && enemyInRoom == info.playerInRoom) {
                isVisible = true;
            }
            // Если игрок в коридоре и враг в том же коридоре - видно
            else if (playerInPassage != -1 && enemyInPassage == playerInPassage) {
                isVisible = true;
            }
            // Если игрок в коридоре, а враг в комнате - проверяем видимость через алгоритм тумана
            else if (playerInPassage != -1 && enemyInRoom != -1) {
                // Проверяем, находится ли комната рядом с позицией игрока
                if (controller.getModel().getMap().isRoomNearPosition(enemyInRoom, info.playerX,
                        info.playerY)) {
                    Rooms enemyRoom = controller.getModel().getMap().getRooms().get(enemyInRoom);
                    // Проверяем, видна ли ячейка врага из коридора
                    if (controller.getModel().getMap().isRoomCellVisibleFromPassage(enemyX, enemyY,
                            info.playerX, info.playerY, enemyRoom)) {
                        // Дополнительно проверяем прямую видимость (Bresenham)
                        isVisible = controller.getModel().getMap().hasLineOfSight(info.playerX,
                                info.playerY, enemyX, enemyY);
                    }
                }
            }

            if (isVisible) {
                drawEntitiesSymbol(enemyX, enemyY, enemy.getSymbol(), enemy.getColor(), info);
            }
        }
    }

    private void drawItems(MapInfo info) {
        int playerInPassage =
                controller.getModel().getMap().determinePassage(info.playerX, info.playerY);
        for (Items item : controller.getModel().getItems().getItems()) {
            int itemX = item.getCoord().getX();
            int itemY = item.getCoord().getY();
            int itemInRoom = controller.getModel().getMap().determineRoom(itemX, itemY);
            boolean isVisible = false;

            // Если игрок в комнате и предмет в той же комнате - видно
            if (info.playerInRoom != -1 && itemInRoom == info.playerInRoom) {
                isVisible = true;
            }
            // Если игрок в коридоре, а предмет в комнате - проверяем видимость через алгоритм
            // тумана
            else if (playerInPassage != -1 && itemInRoom != -1) {
                // Проверяем, находится ли комната рядом с позицией игрока
                if (controller.getModel().getMap().isRoomNearPosition(itemInRoom, info.playerX,
                        info.playerY)) {
                    Rooms itemRoom = controller.getModel().getMap().getRooms().get(itemInRoom);
                    // Проверяем, видна ли ячейка предмета из коридора
                    if (controller.getModel().getMap().isRoomCellVisibleFromPassage(itemX, itemY,
                            info.playerX, info.playerY, itemRoom)) {
                        // Дополнительно проверяем прямую видимость (Bresenham)
                        isVisible = controller.getModel().getMap().hasLineOfSight(info.playerX,
                                info.playerY, itemX, itemY);
                    }
                }
            }

            if (isVisible) {
                drawEntitiesSymbol(itemX, itemY, item.getSymbol(), item.getColor(), info);
            }
        }
    }

    private void drawExit(MapInfo info) {
        int playerInPassage =
                controller.getModel().getMap().determinePassage(info.playerX, info.playerY);
        int[] exitCoords = controller.getModel().getMap().getFinalRoomCoords();
        int exitX = exitCoords[0];
        int exitY = exitCoords[1];
        int exitInRoom = controller.getModel().getMap().determineRoom(exitX, exitY);
        boolean isVisible = false;

        // Если игрок в комнате и выход в той же комнате - видно
        if (info.playerInRoom != -1 && exitInRoom == info.playerInRoom) {
            isVisible = true;
        }
        // Если игрок в коридоре, а выход в комнате - проверяем видимость через алгоритм тумана
        else if (playerInPassage != -1 && exitInRoom != -1) {
            // Проверяем, находится ли комната рядом с позицией игрока
            if (controller.getModel().getMap().isRoomNearPosition(exitInRoom, info.playerX,
                    info.playerY)) {
                Rooms exitRoom = controller.getModel().getMap().getRooms().get(exitInRoom);
                // Проверяем, видна ли ячейка выхода из коридора
                if (controller.getModel().getMap().isRoomCellVisibleFromPassage(exitX, exitY,
                        info.playerX, info.playerY, exitRoom)) {
                    // Дополнительно проверяем прямую видимость (Bresenham)
                    isVisible = controller.getModel().getMap().hasLineOfSight(info.playerX,
                            info.playerY, exitX, exitY);
                }
            }
        }

        if (isVisible) {
            drawEntitiesSymbol(exitX, exitY, '■', TextColor.ANSI.CYAN, info);
        }
    }

    private void drawRoomContent(TextGraphics tg, Rooms room, int offsetX, int offsetY) {
        for (int y = room.getTopY() + 1; y < room.getBottomY(); y++) {
            for (int x = room.getLeftX() + 1; x < room.getRightX(); x++) {
                tg.putString(x + offsetX, y + offsetY, ".");
            }
        }
    }

    private void drawRoomContentWithFog(TextGraphics tg, Rooms room, int offsetX, int offsetY,
            int playerX, int playerY) {
        for (int y = room.getTopY() + 1; y < room.getBottomY(); y++) {
            for (int x = room.getLeftX() + 1; x < room.getRightX(); x++) {
                // Проверяем видимость ячейки на основе направления (Ray Casting)
                // Для пола не используем проверку прямой видимости (Bresenham),
                // так как это блокирует отображение из-за стен комнаты
                if (controller.getModel().getMap().isRoomCellVisibleFromPassage(x, y, playerX,
                        playerY, room)) {
                    // Рисуем точку в видимой области
                    tg.putString(x + offsetX, y + offsetY, ".");
                }
            }
        }
    }

    private void drawPassageSegments(TextGraphics tg, Passage passage, int offsetX, int offsetY) {
        for (Passage.PassageSegment segment : passage.getSegments()) {
            if (segment.isHorizontal()) {
                int y = segment.getStartY() + offsetY;
                for (int x = Math.min(segment.getStartX(), segment.getEndX()); x <= Math
                        .max(segment.getStartX(), segment.getEndX()); x++) {
                    tg.putString(x + offsetX, y, "#");
                }
            } else {
                int x = segment.getStartX() + offsetX;
                for (int y = Math.min(segment.getStartY(), segment.getEndY()); y <= Math
                        .max(segment.getStartY(), segment.getEndY()); y++) {
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

    private void viewInfo() {
        viewPlayerStats();
        viewControlInfo();
    }

    private void viewPlayerStats() {
        int mapWidth = controller.getModel().getMap().getWidth();
        int mapHeight = controller.getModel().getMap().getHeight();
        int offsetX = 1;
        int offsetY = 1;
        int statsY = mapHeight + offsetY + 2;
        int statsWidth = mapWidth + offsetX;
        drawRectangle(textGraphics, statsY - 1, statsY + 1,
                offsetX - 1, statsWidth);
        String stats = String.format(
                "Level: %d     Health: %d/%d     Agility: %d     Strength: %d     Treasures: %d",
                controller.getModel().getLevel(),
                controller.getModel().getPlayer().getHealth(),
                controller.getModel().getPlayer().getMaxHealth(),
                controller.getModel().getPlayer().getAgility(),
                controller.getModel().getPlayer().getStrength(),
                controller.getModel().getPlayer().getTreasure()
        );
        int centerX = (statsWidth - stats.length()) / 2 + 1;
        textGraphics.putString(centerX, statsY, stats);
    }

    private void viewControlInfo() {
        int mapWidth = controller.getModel().getMap().getWidth();
        int mapHeight = controller.getModel().getMap().getHeight();
        int offsetX = 1;
        int offsetY = 1;
        int panelX = mapWidth + offsetX + 2;
        int panelY = offsetY - 1;
        int panelWidth = 25;
        int panelHeight = mapHeight + 4;
        drawRectangle(textGraphics, panelY, panelY + panelHeight,
                panelX, panelX + panelWidth);

        String title = "CONTROL:";
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(panelX + 2, panelY + 1, title);

        int controlX = panelX + 2;
        int controlY = panelY + 3;
        textGraphics.putString(controlX, controlY, "Movement:");
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.putString(controlX + 2, controlY + 1, "W - Up");
        textGraphics.putString(controlX + 2, controlY + 2, "A - Left");
        textGraphics.putString(controlX + 2, controlY + 3, "S - Down");
        textGraphics.putString(controlX + 2, controlY + 4, "D - Right");

        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(controlX, controlY + 6, "Backpack:");
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.putString(controlX + 2, controlY + 7, "H - Weapon");
        textGraphics.putString(controlX + 2, controlY + 8, "J - Food");
        textGraphics.putString(controlX + 2, controlY + 9, "K - Elixirs");
        textGraphics.putString(controlX + 2, controlY + 10, "E - Scrolls");

        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(controlX, controlY + 12, "ESC - Save & Exit");
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
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

    private void viewSingleItemtype() throws IOException {
        int offsetX = 0;
        int offsetY = 0;
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        drawRectangle(textGraphics, offsetY, MENU_HEIGHT + offsetY + 1, offsetX,
                MENU_WIDTH + offsetX + 1);
        int pos = MENU_WIDTH / 3 + 1;
        textGraphics.putString(pos, 2, "ENTER THE ITEM NUMBER (0-8):");
        textGraphics.putString(pos + 3, MENU_HEIGHT - 2, "press ESCAPE to return");
        if (controller.getModel().getBackpack().getScreenOutput().isEmpty())
            textGraphics.putString(pos + 5, 4, "backpack is empty");
        else {
            List<Items> item = controller.getModel().getBackpack().getScreenOutput();
            for (int i = 0; i < item.size(); ++i) {
                textGraphics.putString(pos, 4 + i, +i + ". " + item.get(i).getName() + " (increase "
                        + item.get(i).getIncrease() + ")");
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
        viewSingleItemtype();
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
            List<utils.GameStatistics> allStatistics =
                    controller.getModel().getStatistics().getAllStatistics();
            StatsOffsets info = initStatsOffsets();
            drawOuterBorder(info);
            drawTitle(info);
            if (allStatistics.isEmpty()) {
                drawEmptyMessage(info);
            } else {
                drawInnerBorder(info);
                drawStatsHeader(info);
                drawStatsData(info, allStatistics);
            }
            drawFooter(info);
            screen.refresh();
            waitForEscape();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private StatsOffsets initStatsOffsets() {
        int screenWidth = screen.getTerminalSize().getColumns();
        int screenHeight = screen.getTerminalSize().getRows();
        int outerLeft = 1;
        int outerRight = screenWidth - 2;
        int outerTop = 1;
        int outerBottom = screenHeight - 2;
        int innerLeft = outerLeft + 2;
        int innerRight = outerRight - 2;
        int innerTop = outerTop + 5;
        int innerBottom = outerBottom - 3;
        return new StatsOffsets(
                screenWidth, screenHeight,
                outerLeft, outerRight, outerTop, outerBottom,
                innerLeft, innerRight, innerTop, innerBottom
        );
    }

    private void drawOuterBorder(StatsOffsets info) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        drawRectangle(textGraphics, info.outerTop - 1, info.outerBottom + 1,
                info.outerLeft - 1, info.outerRight+ 1);
    }

    private void drawTitle(StatsOffsets info) {
        String title = "GAME STATISTICS";
        int titleX = (info.screenWidth - title.length()) / 2;
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(titleX, info.outerTop, title);
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.putString(titleX, info.outerTop + 3, "TOP 10 PLAYERS:");
    }

    private void drawEmptyMessage(StatsOffsets info) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        String emptyMsg = "The statistics are empty for now. Play some games!";
        int msgX = (info.screenWidth - emptyMsg.length()) / 2;
        textGraphics.putString(msgX, info.screenHeight / 2, emptyMsg);
    }

    private void drawInnerBorder(StatsOffsets info) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        drawRectangle(textGraphics, info.innerTop, info.innerBottom,
                info.innerLeft, info.innerRight);
        for (int x = info.innerLeft + 1; x <= info.innerRight - 1; x++) {
            textGraphics.putString(x, info.innerTop + 2, "─");
        }
    }

    private void drawStatsHeader(StatsOffsets info) {
        int headerY = info.innerTop + 1;
        int[] cols = calculateColumnPositions(info.innerLeft);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.putString(cols[0], headerY, "№");
        textGraphics.putString(cols[1], headerY, "Name");
        textGraphics.putString(cols[2], headerY, "Treasures");
        textGraphics.putString(cols[3], headerY, "Level");
        textGraphics.putString(cols[4], headerY, "Enemies");
        textGraphics.putString(cols[5], headerY, "Food");
        textGraphics.putString(cols[6], headerY, "Elixirs");
        textGraphics.putString(cols[7], headerY, "Scrolls");
        textGraphics.putString(cols[8], headerY, "AtMade");
        textGraphics.putString(cols[9], headerY, "AtReceived");
        textGraphics.putString(cols[10], headerY, "CellMoved");
        textGraphics.putString(cols[11], headerY, "Victory");
    }

    private void drawStatsData(StatsOffsets info, List<utils.GameStatistics> stats) {
        int displayCount = Math.min(10, stats.size());
        int dataY = info.innerTop + 4;
        int[] cols = calculateColumnPositions(info.innerLeft);
        for (int i = 0; i < displayCount; i++) {
            utils.GameStatistics s = stats.get(i);
            setRowColor(i);
            textGraphics.putString(cols[0], dataY, (i + 1) + ".");
            String name = shortenLongName(s.getName(), 10);
            textGraphics.putString(cols[1], dataY, name);
            textGraphics.putString(cols[2] - 2, dataY, String.format("%7d", s.getTreasure()));
            textGraphics.putString(cols[3] - 2, dataY, String.format("%5d", s.getMaxLevel()));
            textGraphics.putString(cols[4] - 2, dataY, String.format("%6d", s.getEnemyKilled()));
            textGraphics.putString(cols[5] - 2, dataY, String.format("%5d", s.getFoodEaten()));
            textGraphics.putString(cols[6] - 2, dataY, String.format("%6d", s.getElixirDrink()));
            textGraphics.putString(cols[7] - 2, dataY, String.format("%6d", s.getScrollUse()));
            textGraphics.putString(cols[8] - 2, dataY, String.format("%6d", s.getAttacksMade()));
            textGraphics.putString(cols[9] - 3, dataY, String.format("%9d", s.getAttacksReceived()));
            textGraphics.putString(cols[10] - 1, dataY, String.format("%7d", s.getCellMoved()));
            drawGameResult(cols[11] + 3, dataY, s.isVictory());
            dataY++;
        }
    }

    private void drawFooter(StatsOffsets info) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        String instruction = "press ESCAPE to return to the main menu";
        int instructionX = (info.screenWidth - instruction.length()) / 2;
        textGraphics.putString(instructionX, info.outerBottom - 1, instruction);
    }

    // Расчёт позиций столбцов
    private int[] calculateColumnPositions(int innerLeft) {
        int col1 = innerLeft + 2;
        int col2 = col1 + 3;
        int col3 = col2 + 7;
        int col4 = col3 + 11;
        int col5 = col4 + 7;
        int col6 = col5 + 9;
        int col7 = col6 + 6;
        int col8 = col7 + 9;
        int col9 = col8 + 9;
        int col10 = col9 + 8;
        int col11 = col10 + 12;
        int col12 = col11 + 11;

        return new int[]{col1, col2, col3, col4, col5, col6,
                col7, col8, col9, col10, col11, col12};
    }

    private void setRowColor(int index) {
        if (index < 3) {
            textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        } else {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        }
    }

    // Обрезка длинного имени
    private String shortenLongName(String name, int maxLength) {
        if (name.length() > maxLength) {
            return name.substring(0, 7) + "...";
        }
        return name;
    }

    private void drawGameResult(int x, int y, boolean isVictory) {
        String victory = isVictory ? "✓" : "✗";
        if (isVictory) {
            textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        } else {
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
        }
        textGraphics.putString(x, y, victory);
    }

    private void waitForEscape() throws IOException {
        boolean viewingStats = true;
        while (viewingStats) {
            KeyStroke key = screen.readInput();
            if (key.getKeyType() == KeyType.Escape) {
                viewingStats = false;
            }
        }
        screen.clear();
    }

    private static class MapInfo {
        final int mapWidth;
        final int mapHeight;
        final int offsetX;
        final int offsetY;
        final int playerX;
        final int playerY;
        final int playerInRoom;

        MapInfo(int mapWidth, int mapHeight, int offsetX, int offsetY, int playerX, int playerY,
                int playerInRoom) {
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.playerX = playerX;
            this.playerY = playerY;
            this.playerInRoom = playerInRoom;
        }
    }

    private static class StatsOffsets {
        final int screenWidth;
        final int screenHeight;
        final int outerLeft;
        final int outerRight;
        final int outerTop;
        final int outerBottom;
        final int innerLeft;
        final int innerRight;
        final int innerTop;
        final int innerBottom;

        StatsOffsets(int screenWidth, int screenHeight,
                        int outerLeft, int outerRight, int outerTop, int outerBottom,
                        int innerLeft, int innerRight, int innerTop, int innerBottom) {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.outerLeft = outerLeft;
            this.outerRight = outerRight;
            this.outerTop = outerTop;
            this.outerBottom = outerBottom;
            this.innerLeft = innerLeft;
            this.innerRight = innerRight;
            this.innerTop = innerTop;
            this.innerBottom = innerBottom;
        }
    }

}
