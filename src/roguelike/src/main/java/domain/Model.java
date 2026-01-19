package domain;

import domain.abstact.Attributes;
import domain.abstact.Items;
import domain.backpack.Backpack;
import domain.enemy.GameEnemy;
import domain.enemy.Orge;
import domain.enums.StatusE;
import domain.enums.StatusPlayer;
import domain.interfaces.Action;
import domain.interfaces.Check;
import domain.items.GameItems;
import domain.items.Weapon;
import domain.location.Map;
import domain.player.Player;
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

    private static final String FOLDER = "save_json/";
    private static final String FILE_NAME_PLAYER = FOLDER + "player.json";
    private static final String FILE_NAME_BACKPACK = FOLDER + "backpack.json";
    private static final String FILE_NAME_GAMEITEMS = FOLDER + "game_items.json";
    private static final String FILE_NAME_GAMEENEMY = FOLDER + "game_enemy.json";
    private static final String FILE_NAME_WEAPONTAKEN = FOLDER + "weapon_taken.json";

    public Model(){
        player = new Player(5, 5);
        backpack = new Backpack();
        map = new Map();
        int[] startPos = map.getRandomPosition();
        player = new Player(startPos[0], startPos[1]);
        items = new GameItems();
        enemys = new GameEnemy();
        level = 1;
        this.weaponTaken = new Weapon(null, 0, 0);
    }

    public void gameInitialization(){
        player.setStatus(StatusPlayer.ACTION);
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());

        items.generateRandom(level);
        for(int i = 0; i < items.getItems().size(); ++i) {
            Items item = items.getItems().get(i);
            int[] roomPos = map.getFreePosition();
            item.setCoord(roomPos[0], roomPos[1]);
            map.setMap(item.getCoord().getX(), item.getCoord().getY(), item.getSymbol());
        }

        enemys.generateRandom(level);
        for(int i = 0; i < enemys.getEnemy().size(); ++i){
            Attributes enemy = enemys.getEnemy().get(i);
            int[] roomPos = map.getFreePosition();
            enemy.setCoord(roomPos[0], roomPos[1]);
            map.setMap(enemy.getCoord().getX(), enemy.getCoord().getY(), enemy.getSymbol());
        }
    }

    public void gameSession(){
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());
        if(!player.getStatus().equals(StatusPlayer.PAUSE))
            enemyMovement();
//        if(player.getStatus().equals(StatusPlayer.GAMEOVER)){
//           System.out.println("save Player");
//            savePlayer();
//        }
    }

    public void passName(String line){
        player.setName(line);
    }

    public void movePlayer(final StatusE status) {
        if(player.getStatus().equals(StatusPlayer.SLEEP)){
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
        }
    }

    private void playerAction(int tmpX, int tmpY, int oldX, int oldY){
        if(isWithInBounds(tmpX, tmpY)) {
            if (checkEnemy(tmpX, tmpY)) {
                int index = enemys.getIndex(tmpX, tmpY);
                player.attack(enemys.getEnemy().get(index));
                if(enemys.getEnemy().get(index).getHealth() <= 0) {
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
            } else if (!checkItems(tmpX, tmpY)) {
                map.putZero(oldX, oldY);
                map.putZero(tmpX, tmpY);
                player.setCoord(tmpX, tmpY);
                map.setMap(tmpX, tmpY, player.getSymbol());
            }
        }
    }

    // все что связано с предметами
    private boolean checkItems(int x, int y){
        if(items.getItems() == null || items.getItems().isEmpty()) return false;

        boolean flag = false;

        char cellChar = map.getMapChar(x, y);
        int index = equalsMapItems(x, y, items);
        if(index != -1) {
            Items item = items.getItems().get(index);
            if (checkingSymbols(cellChar)) {
                backpack.add(item, item.getSymbol());
                flag = true;
            }

            if (cellChar == 't') {
                player.increaseTreasure(item.getIncrease());
                flag = true;
            }
        }

        if(flag){
            map.putZero(x, y);
            items.getItems().remove(index);
            map.putZero(player.getCoord().getX(), player.getCoord().getY());
            player.setCoord(x, y);
        }

        return flag;
    }

    private int equalsMapItems(int x, int y, GameItems items){
            for (int i = 0; i < items.getItems().size(); i++) {
                if (items.getItems().get(i).getCoord().getX() == x &&
                        items.getItems().get(i).getCoord().getY() == y)
                    return i;
            }
        return -1;
    }

    public void openBackpack(final char symbol){
        player.setStatus(StatusPlayer.PAUSE);
        getBackpack().getScreenOutput().clear();
        getBackpack().getScreenOutput().addAll(getBackpack().getPackItems(symbol));
        player.setStatus(StatusPlayer.ACTION);
    }

    //get - set metod
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

    // действия предметов из рюкзака
    public void actionOfItems(final char symbol, final int index){
        List<Items> item = getBackpack().getPackItems(symbol);
        int value = item.get(index).getIncrease();
        switch (symbol){
            case 'w':
                int resIncrease = getPlayer().getStrength() - weaponTaken.getIncrease();
                getPlayer().setStrength(resIncrease);
                getPlayer().increaseStrenght(value);
                if(getBackpack().getPackItems('w').size() >= 1 &&  weaponTaken.getClass() != null){
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
                if (getPlayer().getHealth() <= 100)
                    getPlayer().increaseHealth(value);
                break;
            case 'e':
                actionWithElixirScroll(item.get(index).getName(), value);
                break;
            case 's':
                actionWithElixirScroll(item.get(index).getName(), value);
                break;
        }

        getBackpack().getPackItems(symbol).remove(index);
        getPlayer().setStatus(StatusPlayer.ACTION);
    }

    public void actionWithElixirScroll(final String name, final int value){
        String tmpName = name.split(" ")[0];
        switch (tmpName){
            case "health":
                if(getPlayer().getHealth() <= 100)
                    getPlayer().increaseHealth(value);
                break;
            case "agility":
                getPlayer().increaseAgility(value);
                break;
            case "strength":
                getPlayer().increaseStrenght(value);
                break;
        }
    }

    private int[] isThereAnEmptyCellNearby(int x, int y){
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        int checkX = 0;
        int checkY = 0;

        for (int[] dir : directions) {
            checkX = x + dir[0];
            checkY = y + dir[1];

            if (!checkEnemy(checkX, checkY) &&
                    !checkingSymbols(map.getMapChar(checkX, checkY)) &&
                    map.getMap(checkX, checkY) != player.getSymbol()) {
                return new int[]{checkX, checkY};
            }

        }
        return new int[]{x, y};
    }

    //все что связано с врагами
     private boolean checkEnemy(int x, int y){
        char c = getMap().getMapChar(x, y);
        return c == 'Z' || c == 'V' || c == 'G' || c == 'O' || c == 'S' || !isWithInBounds(x, y);
     }

    private void enemyMovement(){
        for(int i = 0; i < enemys.getEnemy().size() && !player.getStatus().equals(StatusPlayer.GAMEOVER); ++i){
            Attributes enemy = enemys.getEnemy().get(i);
            if(enemy instanceof Orge){
                Orge orge = (Orge) enemy;
                orge.updateAtackRest();
            }

            if(enemy instanceof Action moveEnemy) {
                int currentX = enemy.getCoord().getX();
                int currentY = enemy.getCoord().getY();

                if (isPlayerAdjacent(currentX, currentY)) {
                    ((Action) enemy).attack(player);
                    if(player.getHealth() <= 0)
                        player.setStatus(StatusPlayer.GAMEOVER);
                    continue;
                }

                int[] newXY;
                if(canSeePlayer(enemy ,currentX, currentY)){
                    newXY = moveTowardsPlayer(currentX, currentY, player.getCoord().getX(), player.getCoord().getY());
                }else
                    newXY = moveEnemy.move(currentX, currentY, enemy.getSymbol());

                int newX = newXY[0];
                int newY = newXY[1];

                if (isWithInBounds(newX, newY) && !isCellBlocked(newX,newY) && !checkEnemy(newX, newY)) {
                    map.putZero(currentX, currentY);
                    map.setMap(newX, newY, enemy.getSymbol());
                    enemy.setCoord(newX, newY);
                }
            }
        }
    }

    private boolean isPlayerAdjacent(int enemyX, int enemyY) {
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

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

    private boolean canSeePlayer(Attributes enemy, int x, int y){
       int distance = Math.max(
               Math.abs(x - player.getCoord().getX()),
               Math.abs(y - player.getCoord().getY())
       );
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
            return new int[]{newX, newY};
        }

        if (moveX != 0) {
            newX = enemyX;
            newY = enemyY + Integer.compare(diffY, 0);
            if (isWithInBounds(newX, newY) && !isCellBlocked(newX, newY)) {
                return new int[]{newX, newY};
            }
        } else {
            newX = enemyX + Integer.compare(diffX, 0);
            newY = enemyY;
            if (isWithInBounds(newX, newY) && !isCellBlocked(newX, newY)) {
                return new int[]{newX, newY};
            }
        }

        return new int[]{enemyX, enemyY};
    }

    private boolean isCellBlocked(int x, int y) {
        char cell = (char) map.getMap(x, y);
          return cell != 0 && cell != ' ' && cell != '.' && cell != '@' && !checkingSymbols(cell);
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
    public boolean checkingSymbols(char symbol){
        return symbol == 's' || symbol == 'w' ||
                symbol == 'f' || symbol == 'e';
    }


    //для работы с json
    public void saveGame(){
        System.out.println("Save Player function in Model");
        new File(FOLDER).mkdirs();
        SaveGame.savePlayer(player, FILE_NAME_PLAYER);
        SaveGame.saveBackpack(backpack, FILE_NAME_BACKPACK);
        SaveGame.saveGameItems(items, FILE_NAME_GAMEITEMS);
        SaveGame.saveGameEnemy(enemys, FILE_NAME_GAMEENEMY);
        SaveGame.saveWeaponTaken(weaponTaken, FILE_NAME_WEAPONTAKEN);
    }

    public void loadGame() {
        Player loadedPlayer = SaveGame.loadPlayer(FILE_NAME_PLAYER);
        Backpack loadedBackpack = SaveGame.loadBackpack(FILE_NAME_BACKPACK);

        if ((loadedPlayer != null) && (loadedBackpack != null)) {
            this.player = loadedPlayer;
            this.backpack = loadedBackpack;
            restoreGameAfterLoad();
        } else {
            System.out.println("Not JSON file");
            player = new Player(5, 5);
        }
    }

    private void restoreGameAfterLoad() {
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());

        if (player.getHealth() <= 0) {
            player.setStatus(StatusPlayer.GAMEOVER);
        } else {
            player.setStatus(StatusPlayer.ACTION);
        }
    }
}
