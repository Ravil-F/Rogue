package domain;

import domain.abstact.Attributes;
import domain.abstact.Items;
import domain.backpack.Backpack;
import domain.enemy.GameEnemy;
import domain.enemy.Ogre;
import domain.enums.StatusE;
import domain.enums.StatusPlayer;
import domain.interfaces.Action;
import domain.interfaces.Check;
import domain.items.GameItems;
import domain.items.Weapon;
import domain.location.Map;
import domain.player.Player;
import domain.player.Statistics;
import utils.GameStatistics;
import utils.SaveGame;
import java.io.File;

import java.util.*;

public class Model implements Check {
    private Player player;
    private Backpack backpack;
    private Map map;
    private GameItems items;
    private GameEnemy enemys;
    private int level;
    private Weapon weaponTaken;
    private Statistics statistics;
    private GameStatistics gameStatistics;

    private static final int MAX_LEVEL = 21;
    private static int timeAgility;
    private static int timeStrenght;
    private static int tmpStrenght;

    private static final String FOLDER =
            System.getProperty("user.dir") + File.separator + "save_json" + File.separator;
    private static final String FILE_NAME_PLAYER = FOLDER + "player.json";
    private static final String FILE_NAME_BACKPACK = FOLDER + "backpack.json";
    private static final String FILE_NAME_GAMEITEMS = FOLDER + "game_items.json";
    private static final String FILE_NAME_GAMEENEMY = FOLDER + "game_enemy.json";
    private static final String FILE_NAME_WEAPONTAKEN = FOLDER + "weapon_taken.json";
    private static final String FILE_NAME_MAP = FOLDER + "map.json";
    private static final String FILE_NAME_LEVEL = FOLDER + "level.json";

    public Model() {
        backpack = new Backpack();
        map = new Map();
        int[] startPos = map.getStartRoomCoords();
        player = new Player(startPos[0], startPos[1]);
        items = new GameItems();
        enemys = new GameEnemy();
        level = 1;
        timeAgility = 0;
        timeStrenght = 0;
        tmpStrenght = 0;
        this.weaponTaken = new Weapon(null, 0, 0);
        this.statistics = Statistics.getStatistics();
        this.gameStatistics = null;
    }

    public void gameInitialization() {
        player.setStatus(StatusPlayer.ACTION);
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());
        map.markAVisit(player.getCoord().getX(), player.getCoord().getY());
        generateItems();
        generateExit();
        generateEnemies();
        gameStatistics = new GameStatistics(player.getName());
        gameStatistics.setMaxLevel(level);
    }

    private void generateItems() {
        items.generateRandom(level);
        for (int i = 0; i < items.getItems().size(); ++i) {
            Items item = items.getItems().get(i);
            int[] roomPos = map.getFreePosition();
            item.setCoord(roomPos[0], roomPos[1]);
            map.setMap(item.getCoord().getX(), item.getCoord().getY(), item.getSymbol());
        }
    }

    private void generateExit() {
        int[] exitPos = map.getFinalRoomCoords();
        map.setMap(exitPos[0], exitPos[1], '■');
    }

    private void generateEnemies() {
        enemys.generateRandom(level);
        for (int i = 0; i < enemys.getEnemy().size(); ++i) {
            Attributes enemy = enemys.getEnemy().get(i);
            int[] roomPos = map.excludeStartRoom();
            enemy.setCoord(roomPos[0], roomPos[1]);
            map.setMap(enemy.getCoord().getX(), enemy.getCoord().getY(), enemy.getSymbol());
        }
    }

    public void gameSession() {
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());
        map.markAVisit(player.getCoord().getX(), player.getCoord().getY());
        if (!player.getStatus().equals(StatusPlayer.PAUSE))
            enemyMovement();
    }

    public void passName(String line) {
        player.setName(line);
    }

    public void movePlayer(final StatusE status) {
        if (player.getStatus().equals(StatusPlayer.SLEEP)) {
            player.updateSleep();
            return;
        }
        if (getPlayer().getStatus() == StatusPlayer.ACTION) {
            int tmpX = player.getCoord().getX();
            int tmpY = player.getCoord().getY();
            int oldX = tmpX;
            int oldY = tmpY;

            switch (status) {
                case DOWN:
                    tmpY = player.move(tmpY, true);
                    break;
                case UP:
                    tmpY = player.move(tmpY, false);
                    break;
                case LEFT:
                    tmpX = player.move(tmpX, false);
                    break;
                case RIGHT:
                    tmpX = player.move(tmpX, true);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }
            playerAction(tmpX, tmpY, oldX, oldY);
            checkTimeAgalityStrenght();
        }
    }

    private void playerAction(int tmpX, int tmpY, int oldX, int oldY) {
        if (!isWithInBounds(tmpX, tmpY)) {
            return;
        }
        char cellChar = map.getMapChar(tmpX, tmpY);
        if (cellChar == '#' || cellChar == ' ' || cellChar == 0) {
            return;
        }
        if (cellChar == '■') {
            if (level < MAX_LEVEL) {
                goToNextLevel();
            } else {
                player.setStatus(StatusPlayer.VICTORY);
            }
            return;
        }
        if (checkEnemy(tmpX, tmpY)) {
            int index = enemys.getIndex(tmpX, tmpY);
            if (index >= 0 && index < enemys.getEnemy().size()) {
                incrementAttacksMade();
                player.attack(enemys.getEnemy().get(index));
                if (enemys.getEnemy().get(index).getHealth() <= 0) {
                    incrementEnemyKilled();
                    int enemyX = enemys.getEnemy().get(index).getCoord().getX();
                    int enemyY = enemys.getEnemy().get(index).getCoord().getY();
                    map.putZero(enemyX, enemyY);
                    enemys.getEnemy().remove(index);
                    Items singleItem = items.generateTreasure(enemyX, enemyY);
                    if (singleItem != null) {
                        items.getItems().add(singleItem);
                        map.setMap(enemyX, enemyY, singleItem.getSymbol());
                    }
                }
            }
            checkPlayerStatus();
            return;
        }
        if (!checkItems(tmpX, tmpY)) {
            incrementCellMoved();
            map.putZero(oldX, oldY);
            player.setCoord(tmpX, tmpY);
            map.setMap(tmpX, tmpY, player.getSymbol());
        }
    }

    private void goToNextLevel() {
        updateMaxLevel();
        level++;
        enemys.getEnemy().clear();
        items.getItems().clear();
        map = new Map();
        int[] startPos = map.getStartRoomCoords();
        player.setCoord(startPos[0], startPos[1]);
        gameInitialization();
    }

    private void checkTimeAgalityStrenght(){
        if (timeAgility != 0){
            timeAgility = timeAgility - 1;
            System.out.println("timeAgility = " + timeAgility);
            if (timeAgility == 0) {
                player.setAgility(90);
                System.out.println("timeAgility = " + timeAgility);
            }
        }
        if (timeStrenght != 0) {
            timeStrenght = timeStrenght - 1;
            System.out.println("timeStregnht = " + timeStrenght);
            if (timeStrenght == 0) {
                player.setStrength(player.getStrength() - tmpStrenght);
                System.out.println("timeAgility = " + timeAgility);
            }
        }
    }

    // все что связано с предметами
    private boolean checkItems(int x, int y) {
        if (items.getItems() == null || items.getItems().isEmpty())
            return false;

        boolean flag = false;

        char cellChar = map.getMapChar(x, y);
        int index = equalsMapItems(x, y, items);
        if (index != -1) {
            Items item = items.getItems().get(index);
            if (checkingSymbols(cellChar)) {
                backpack.add(item, item.getSymbol());
                flag = true;
            }

            if (cellChar == 't') {
                incrementTreasure(item.getIncrease());
                player.increaseTreasure(item.getIncrease());
                flag = true;
            }
        }

        if (flag) {
            map.putZero(x, y);
            items.getItems().remove(index);
            map.putZero(player.getCoord().getX(), player.getCoord().getY());
            player.setCoord(x, y);
        }

        return flag;
    }

    private void checkPlayerStatus() {
        if (player.getHealth() <= 0 && player.getStatus() != StatusPlayer.GAMEOVER) {
            player.setStatus(StatusPlayer.GAMEOVER);
            saveStatistics();
        }
    }

    private int equalsMapItems(int x, int y, GameItems items) {
        for (int i = 0; i < items.getItems().size(); i++) {
            if (items.getItems().get(i).getCoord().getX() == x
                    && items.getItems().get(i).getCoord().getY() == y)
                return i;
        }
        return -1;
    }

    public void openBackpack(final char symbol) {
        player.setStatus(StatusPlayer.PAUSE);
        getBackpack().getScreenOutput().clear();
        getBackpack().getScreenOutput().addAll(getBackpack().getPackItems(symbol));
        player.setStatus(StatusPlayer.ACTION);
    }

    // get - set metod
    public Player getPlayer() {
        return player;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public GameItems getItems() {
        return items;
    }

    public GameEnemy getEnemys() {
        return enemys;
    }

    public Weapon getWeaponTaken() {
        return weaponTaken;
    }

    public void setWeaponTaken(Weapon weaponTaken) {
        this.weaponTaken = weaponTaken;
    }

    public int getLevel() {
        return level;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public GameStatistics getGameStatistics() {
        return gameStatistics;
    }

    // действия предметов из рюкзака
    public void actionOfItems(final char symbol, final int index) {
        List<Items> item = getBackpack().getPackItems(symbol);
        int value = item.get(index).getIncrease();
        switch (symbol) {
            case 'w':
                int resIncrease = getPlayer().getStrength() - weaponTaken.getIncrease();
                getPlayer().setStrength(resIncrease);
                getPlayer().increaseStrength(value);
                if (!getBackpack().getPackItems('w').isEmpty() && weaponTaken.getClass() != null) {
                    int xPlayer = getPlayer().getCoord().getX();
                    int yPlayer = getPlayer().getCoord().getY();
                    int XY[] = isThereAnEmptyCellNearby(xPlayer, yPlayer);
                    weaponTaken.setCoord(XY[0], XY[1]);
                    items.getItems().add(weaponTaken);
                    map.setMap(XY[0], XY[1], weaponTaken.getSymbol());
                }
                weaponTaken = (domain.items.Weapon) item.get(index);
                break;
            case 'f':
                incrementFoodEaten();
                if (getPlayer().getHealth() <= getPlayer().getMaxHealth())
                    getPlayer().increaseHealth(value);
                break;
            case 'e':
                incrementElixirDrink();
                actionWithElixirScroll(item.get(index).getName(), value, true);
                break;
            case 's':
                incrementScrollUse();
                actionWithElixirScroll(item.get(index).getName(), value, false);
                break;
        }

        getBackpack().getPackItems(symbol).remove(index);
        getPlayer().setStatus(StatusPlayer.ACTION);
    }

    public void actionWithElixirScroll(final String name, final int value, boolean flag) {
        String tmpName = name.split(" ")[0];
        switch (tmpName) {
            case "health":
                if (getPlayer().getHealth() < 100)
                    getPlayer().increaseHealth(value);
                break;
            case "agility":
                getPlayer().increaseAgility(value);
                if (flag)
                    timeAgility = timeAgility + value;
                break;
            case "strength":
                getPlayer().increaseStrength(value);
                if (flag) {
                    timeStrenght = timeStrenght + value;
                    tmpStrenght = timeStrenght + value;
                }
                break;
        }
        checkPlayerStatus();
    }

    private int[] isThereAnEmptyCellNearby(int x, int y) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        int checkX = 0;
        int checkY = 0;

        for (int[] dir : directions) {
            checkX = x + dir[0];
            checkY = y + dir[1];

            if (!checkEnemy(checkX, checkY) && !checkingSymbols(map.getMapChar(checkX, checkY))
                    && map.getMap(checkX, checkY) != player.getSymbol()) {
                return new int[] {checkX, checkY};
            }

        }
        return new int[] {x, y};
    }

    // все что связано с врагами
    private boolean checkEnemy(int x, int y) {
        if (!isWithInBounds(x, y)) {
            return false;
        }
        char c = getMap().getMapChar(x, y);
        return c == 'Z' || c == 'V' || c == 'G' || c == 'O' || c == 'S';
    }

    private void enemyMovement() {
        for (int i = 0; i < enemys.getEnemy().size()
                && !player.getStatus().equals(StatusPlayer.GAMEOVER); ++i) {
            Attributes enemy = enemys.getEnemy().get(i);
            if (enemy instanceof Ogre) {
                Ogre ogre = (Ogre) enemy;
                ogre.updateAtackRest();
            }
            if (enemy instanceof Action moveEnemy) {
                int currentX = enemy.getCoord().getX();
                int currentY = enemy.getCoord().getY();
                if (isPlayerAdjacent(currentX, currentY)) {
                    incrementAttacksReceived();
                    ((Action) enemy).attack(player);
                    checkPlayerStatus();
                    if (player.getStatus() == StatusPlayer.GAMEOVER)
                        continue;
                    continue;
                }

                int[] newXY;
                if (canSeePlayer(enemy, currentX, currentY)) {
                    newXY = moveTowardsPlayer(currentX, currentY, player.getCoord().getX(),
                            player.getCoord().getY());
                } else {
                    newXY = findValidMove(enemy, moveEnemy, currentX, currentY);
                }
                int newX = newXY[0];
                int newY = newXY[1];
                if (newX == currentX && newY == currentY) {
                    continue;
                }
                if (isWithInBounds(newX, newY) && !isCellBlocked(newX, newY)
                        && !checkEnemy(newX, newY)) {
                    map.putZero(currentX, currentY);
                    map.setMap(newX, newY, enemy.getSymbol());
                    enemy.setCoord(newX, newY);
                }
            }
        }
    }

    private int[] findValidMove(Attributes enemy, Action moveEnemy, int currentX, int currentY) {
        int maxAttempts = 10;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int[] newXY = moveEnemy.move(currentX, currentY, enemy.getSymbol());
            int newX = newXY[0];
            int newY = newXY[1];
            if (isWithInBounds(newX, newY) && !isCellBlocked(newX, newY)
                    && !checkEnemy(newX, newY)) {
                return new int[] {newX, newY};
            }
        }
        return new int[] {currentX, currentY};
    }

    private boolean isPlayerAdjacent(int enemyX, int enemyY) {
        int[][] directions = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

        for (int[] dir : directions) {
            int checkX = enemyX + dir[0];
            int checkY = enemyY + dir[1];

            if (!isWithInBounds(checkX, checkY)) {
                continue;
            }

            if (map.getMap(checkX, checkY) == player.getSymbol()) {
                return true;
            }
        }
        return false;
    }

    private boolean canSeePlayer(Attributes enemy, int x, int y) {
        int distance = Math.max(Math.abs(x - player.getCoord().getX()),
                Math.abs(y - player.getCoord().getY()));
        return distance <= enemy.getHostility();
    }

    private int[] moveTowardsPlayer(int enemyX, int enemyY, int playerX, int playerY) {
        int diffX = playerX - enemyX;
        int diffY = playerY - enemyY;

        int moveX = 0;
        int moveY = 0;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            moveX = Integer.compare(diffX, 0);
        } else {
            moveY = Integer.compare(diffY, 0);
        }

        int newX = enemyX + moveX;
        int newY = enemyY + moveY;

        if (isWithInBounds(newX, newY) && !isCellBlocked(newX, newY)) {
            return new int[] {newX, newY};
        }

        if (moveX != 0) {
            newX = enemyX;
            newY = enemyY + Integer.compare(diffY, 0);
            if (isWithInBounds(newX, newY) && !isCellBlocked(newX, newY)) {
                return new int[] {newX, newY};
            }
        } else {
            newX = enemyX + Integer.compare(diffX, 0);
            newY = enemyY;
            if (isWithInBounds(newX, newY) && !isCellBlocked(newX, newY)) {
                return new int[] {newX, newY};
            }
        }

        return new int[] {enemyX, enemyY};
    }

    private boolean isCellBlocked(int x, int y) {
        if (isWithInBounds(x, y)) {
            char cell = (char) map.getMap(x, y);
            if (cell == '.') {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isWithInBounds(int x) {
        return true;
    }

    @Override
    public boolean isWithInBounds(int x, int y) {
        return (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight());
    }

    @Override
    public boolean checkingSymbols(char symbol) {
        return symbol == 's' || symbol == 'w' || symbol == 'f' || symbol == 'e' || symbol == '■';
    }

    // для работы с json
    public void saveGame() {
        File folder = new File(FOLDER);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                System.err.println("Not create folder: " + FOLDER);
                return;
            }
        }
        SaveGame.savePlayer(player, FILE_NAME_PLAYER);
        SaveGame.saveBackpack(backpack, FILE_NAME_BACKPACK);
        SaveGame.saveGameItems(items, FILE_NAME_GAMEITEMS);
        SaveGame.saveGameEnemy(enemys, FILE_NAME_GAMEENEMY);
        SaveGame.saveWeaponTaken(weaponTaken, FILE_NAME_WEAPONTAKEN);
        SaveGame.saveMap(map, FILE_NAME_MAP);
        SaveGame.saveLevel(level, FILE_NAME_LEVEL);
    }

    public void loadGame() {
        Player loadedPlayer = SaveGame.loadPlayer(FILE_NAME_PLAYER);
        Backpack loadedBackpack = SaveGame.loadBackpack(FILE_NAME_BACKPACK);
        GameItems loadedItems = SaveGame.loadGameItems(FILE_NAME_GAMEITEMS);
        GameEnemy loadedEnemies = SaveGame.loadGameEnemy(FILE_NAME_GAMEENEMY);
        Weapon loadedWeaponTaken = SaveGame.loadWeaponTaken(FILE_NAME_WEAPONTAKEN);
        Map loadedMap = SaveGame.loadMap(FILE_NAME_MAP);
        Integer loadedLevel = SaveGame.loadLevel(FILE_NAME_LEVEL);

        if (loadedPlayer != null) {
            this.player = loadedPlayer;

            if (loadedLevel != null)
                this.level = loadedLevel;
            else
                this.level = 1;

            if (loadedBackpack != null)
                this.backpack = loadedBackpack;
            else
                this.backpack = new Backpack();

            if (loadedItems != null) {
                this.items = loadedItems;
                if (this.items.getItems() != null)
                    this.items.getItems().removeIf(Objects::isNull);
            } else
                this.items = new GameItems();

            if (loadedEnemies != null) {
                this.enemys = loadedEnemies;
                if (this.enemys.getEnemy() != null)
                    this.enemys.getEnemy().removeIf(Objects::isNull);
            } else
                this.enemys = new GameEnemy();

            if (loadedWeaponTaken != null)
                this.weaponTaken = loadedWeaponTaken;
            else
                this.weaponTaken = new Weapon(null, 0, 0);

            if (loadedMap != null) {
                this.map = loadedMap;
                restoreAllGameObjects();
            } else
                this.map = new Map();

            restoreAllGameObjects();
            if (gameStatistics == null) {
                GameStatistics stat = statistics.findStatisticsByName(player.getName());
                if (stat != null) {
                    this.gameStatistics = stat;
                    gameStatistics.setMaxLevel(Math.max(stat.getMaxLevel(), level));
                } else {
                    gameStatistics = new GameStatistics(player.getName());
                    gameStatistics.setMaxLevel(level);
                }
            }
        } else {
            System.out.println("Not JSON file");
            int[] startPos = map.getStartRoomCoords();
            player.setCoord(startPos[0], startPos[1]);
            gameInitialization();
        }
    }

    private void restoreItemsOnMap() {
        for (Items item : items.getItems()) {
            if (item != null && item.getCoord() != null) {
                int x = item.getCoord().getX();
                int y = item.getCoord().getY();
                if (isWithInBounds(x, y)) {
                    map.setMap(x, y, item.getSymbol());
                }
            }
        }
    }


    private void restoreEnemiesOnMap() {
        for (Attributes enemy : enemys.getEnemy()) {
            if (enemy != null && enemy.getCoord() != null) {
                int x = enemy.getCoord().getX();
                int y = enemy.getCoord().getY();
                if (isWithInBounds(x, y)) {
                    map.setMap(x, y, enemy.getSymbol());
                }
            }
        }
    }

    private void clearGameObjectsFromMap() {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                char c = map.getMapChar(x, y);
                // Очищаем все символы игровых объектов кроме стен и пола
                if (c != '#' && c != '.' && c != 0 && c != ' ' && c != '■') {
                    map.putZero(x, y);
                }
            }
        }
    }

    private void restoreAllGameObjects() {
        clearGameObjectsFromMap();
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());
        restoreEnemiesOnMap();
        restoreItemsOnMap();
    }

    // для работы по статистике в игре
    private void incrementEnemyKilled() {
        if (gameStatistics != null)
            gameStatistics.addEnemyKilled();
    }

    public void incrementFoodEaten() {
        if (gameStatistics != null) {
            gameStatistics.addFoodEaten();
        }
    }

    public void incrementTreasure(int amount) {
        if (gameStatistics != null) {
            gameStatistics.addTreasure(amount);
        }
    }

    public void incrementElixirDrink() {
        if (gameStatistics != null) {
            gameStatistics.addElixirDrink();
        }
    }

    public void incrementScrollUse() {
        if (gameStatistics != null) {
            gameStatistics.addScrollUse();
        }
    }

    public void incrementAttacksMade() {
        if (gameStatistics != null) {
            gameStatistics.addAttacksMade();
        }
    }

    public void incrementAttacksReceived() {
        if (gameStatistics != null) {
            gameStatistics.addAttacksReceived();
        }
    }

    public void incrementCellMoved() {
        if (gameStatistics != null) {
            gameStatistics.addCellMoved();
        }
    }

    public void updateMaxLevel() {
        if (gameStatistics != null && level > gameStatistics.getMaxLevel()) {
            gameStatistics.setMaxLevel(level);
        }
    }

    public void saveStatistics() {
        if (gameStatistics != null) {
            boolean isVictory = player.getStatus() == StatusPlayer.VICTORY;
            gameStatistics.setVictory(isVictory);
            if (level > gameStatistics.getMaxLevel()) {
                gameStatistics.setMaxLevel(level);
            }
            statistics.updateStatistics(gameStatistics);
        }
    }
}
