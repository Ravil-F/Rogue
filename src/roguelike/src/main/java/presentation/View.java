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
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(menuX, menuStartY - 1, "SELECT THE GAME MODE:");
            textGraphics.putString(menuX, menuStartY, "1 - New game");
            textGraphics.putString(menuX, menuStartY + 1, "2 - Load game");
            textGraphics.putString(menuX, menuStartY + 2, "3 - Load game statistics");
            textGraphics.putString(menuX, menuStartY + 4, "Escape - Exit game");
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
        textGraphics.putString(pos + 1, 2, "ENTER THE PLAYER'S NAME:");
        screen.refresh();
        do {
            setKey();
            if ((key.getCharacter() != ' ') && (key.getKeyType() == KeyType.Enter))
                break;
            symbol = key.getCharacter();
            textGraphics.putString(pos + 10, 4, String.valueOf(symbol));
            res.append(symbol);
            screen.refresh();
            ++pos;
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
        int mapWidth = controller.getModel().getMap().getWidth();
        int mapHeight = controller.getModel().getMap().getHeight();
        int offsetX = 1;
        int offsetY = 1;
        int infoY = mapHeight + offsetY + 2;
        int navigatorY = infoY + 2;
        int backpackY = navigatorY + 2;

        String info = String.format(
                "Level: %d     Health: %d/%d     Agility: %d     Strength: %d     Treasure: %d",
                controller.getModel().getLevel(), controller.getModel().getPlayer().getHealth(),
                controller.getModel().getPlayer().getMaxHealth(),
                controller.getModel().getPlayer().getAgility(),
                controller.getModel().getPlayer().getStrength(),
                controller.getModel().getPlayer().getTreasure());
        String navigatorInfo = String.format("W - Up     S - Down     A - Left     D - Right");
        String backpackInfo = String.format("H - Weapon     J - Food     K - Elixir     E - Scroll");

        int infoRectWidth = mapWidth + offsetX;

        drawRectangle(textGraphics, infoY - 1, backpackY + 1, offsetX - 1, infoRectWidth);
        int centerInfo = (infoRectWidth - info.length()) / 2 + 1;
        int centerNavigator = (infoRectWidth - navigatorInfo.length()) / 2 + 1;
        int centerBackpack = (infoRectWidth - backpackInfo.length()) / 2 + 1;


        textGraphics.putString(centerInfo, infoY, info);
        textGraphics.putString(centerNavigator, navigatorY, navigatorInfo);
        textGraphics.putString(centerBackpack, backpackY, backpackInfo);

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
        textGraphics.putString(pos + 3, MENU_HEIGHT - 2, "press Escape to exit");
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

            domain.player.Statistics statsManager = controller.getModel().getStatistics();
            List<utils.GameStatistics> allStatistics = statsManager.getAllStatistics();

            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

            textGraphics.putString((MENU_WIDTH - 3) / 2, 2, "=== GAME STATISTICS ===");

            if (allStatistics.isEmpty()) {
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                textGraphics.putString(10, 4, "The statistics are empty for now. Play some games!");
            } else {
                textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
                textGraphics.putString(1, 4, "Top 10 players:");

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
}
