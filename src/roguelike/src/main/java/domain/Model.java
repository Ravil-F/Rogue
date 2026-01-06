package domain;

import domain.abstact.Attributes;
import domain.abstact.Items;
import domain.backpack.Backpack;
import domain.enemy.GameEnemy;
import domain.enums.StatusE;
import domain.enums.StatusPlayer;
import domain.interfaces.Action;
import domain.interfaces.Check;
import domain.items.GameItems;
import domain.location.Map;
import domain.player.Player;

import java.util.*;

public class Model implements Check {
    private Player player;
    private Backpack backpack;
    private Map map;
    private GameItems items;
    private GameEnemy enemys;
    private int level;

    public Model(){
        player = new Player(5, 5);
        backpack = new Backpack();
        map = new Map();
        items = new GameItems();
        enemys = new GameEnemy();
        level = 1;
    }

    public void gameInitialization(){
        player.setStatus(StatusPlayer.ACTION);
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());

        items.generateRandom(level);
        for(int i = 0; i < items.getItems().size(); ++i) {
            map.setMap(items.getItems().get(i).getCoord().getX(), items.getItems().get(i).getCoord().getY(), items.getItems().get(i).getSymbol());
        }

        enemys.generateRandom(level);
        for(int i = 0; i < enemys.getEnemy().size(); ++i){
            map.setMap(enemys.getEnemy().get(i).getCoord().getX(), enemys.getEnemy().get(i).getCoord().getY(), enemys.getEnemy().get(i).getSymbol());
        }
    }

    public void gameSession(){
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());
        if(!player.getStatus().equals(StatusPlayer.PAUSE))
            enemyMovement();
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

            if(isWithInBounds(tmpX, tmpY)) {
                if (checkEnemy(tmpX, tmpY)) {
                    int index = enemys.getIndex(tmpX, tmpY);
                    player.attack(enemys.getEnemy().get(index));
                    System.out.println("enemy health = " + enemys.getEnemy().get(index).getHealth());
                    if(enemys.getEnemy().get(index).getHealth() <= 0){
                        enemys.getEnemy().remove(index);
                    }
                } else if (!checkItems(tmpX, tmpY)) {
                    map.putZero(oldX, oldY);
                    map.putZero(tmpX, tmpY);
                    player.setCoord(tmpX, tmpY);
                }
            }
        }
    }

    // все что связано с предметами
    private boolean checkItems(int x, int y){
        if(items.getItems() == null || items.getItems().isEmpty()) return false;

        char cellChar = map.getMapChar(x, y);
        if(checkingSymbols(cellChar)) {
            int index = equalsMapItems(x, y, items);
            if (index != -1) {
                Items item = items.getItems().get(index);
                backpack.add(item, item.getSymbol());
                map.putZero(x, y);
                items.getItems().remove(index);
                map.putZero(player.getCoord().getX(), player.getCoord().getY());
                player.setCoord(x, y);
                return true;
            }
        }
        return false;
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

    // действия предметов из рюкзака
    public void actionOfItems(final char symbol, final int index){
        List<Items> item = getBackpack().getPackItems(symbol);
        int value = item.get(index).getIncrease();
        switch (symbol){
            case 'w':
                getPlayer().increaseStrenght(value);
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

    //все что связано с врагами
     private boolean checkEnemy(int x, int y){
        char c = getMap().getMapChar(x, y);
     return c == 'Z' || c == 'V' || c == 'G' || c == 'O' || c == 'S';
     }

    private void enemyMovement(){
        for(int i = 0; i < enemys.getEnemy().size() && !player.getStatus().equals(StatusPlayer.GAMEOVER); ++i){
            Attributes enemy = enemys.getEnemy().get(i);
            if(enemy instanceof Action moveEnemy) {
                int currentX = enemy.getCoord().getX();
                int currentY = enemy.getCoord().getY();

                if (isPlayerAdjacent(currentX, currentY)) {
                    ((Action) enemy).attack(player);
                    if(player.getHealth() <= 0)
                        player.setStatus(StatusPlayer.GAMEOVER);
                    System.out.println("health player = " + player.getHealth());
                    continue;
                }

                int[] newXY;
                if(canSeePlayer(enemy ,currentX, currentY)){
                    newXY = moveTowardsPlayer(currentX, currentY, player.getCoord().getX(), player.getCoord().getY());
                }else
                    newXY = moveEnemy.move(currentX, currentY, enemy.getSymbol());

                int newX = newXY[0];
                int newY = newXY[1];

                if (isWithInBounds(newX, newY)) {
                    map.putZero(currentX, currentY);
                    map.setMap(newX, newY, enemy.getSymbol());
                    enemy.setCoord(newX, newY);
                }
            }
        }
        System.out.println("End enemy move");
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
}
