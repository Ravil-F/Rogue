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
        player = new Player();
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
        enemyMovement();
    }

    public void passName(String line){
        player.setName(line);
    }

    public void movePlayer(final StatusE status) {
        if (getPlayer().getStatus() == StatusPlayer.MOVE) {
            int tmpX = player.getCoord().getX();
            int tmpY = player.getCoord().getY();
            int oldX = tmpX;
            int oldY = tmpY;

            switch (status) {
                case DOWN:
                    player.move(tmpY, true);
//                    ++tmpY;
                    break;
                case UP:
                    player.move(tmpY, false);
//                    --tmpY;
                    break;
                case LEFT:
                    player.move(tmpX, false);
//                    --tmpX;
                    break;
                case RIGHT:
                    player.move(tmpX, true);
//                    ++tmpX;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }

            if(checkEnemy(tmpX, tmpY)){
                player.setStatus(StatusPlayer.ATTAC);
                System.out.println("Enemy");
//                enemyAttac();
                player.setStatus(StatusPlayer.MOVE);
            }
            else if (tryMove(tmpX, tmpY)) {
                map.putZero(oldX, oldY);
                map.putZero(tmpX, tmpY);
                player.setCoord(tmpX, tmpY);
            }
        }
    }

    private boolean tryMove(int x, int y) {
        if (map.isWithInBounds(x, y) && !checkItems(x, y)) {
            return true;
        }
        return false;
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

    private boolean isItemSymbol(char c) {
        return c == 'w' || c == 'f' || c == 's' || c == 'e';
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
//        player.setStatus();
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
    //чекает есть ли враг
     private boolean checkEnemy(int x, int y){
        char c = getMap().getMapChar(x, y);
     return c == 'Z' || c == 'V' || c == 'G' || c == 'O' || c == 'S';
     }

    private void enemyMovement(){
        for(int i = 0; i < enemys.getEnemy().size(); ++i){
            Attributes enemy = enemys.getEnemy().get(i);
            if(enemy instanceof Action moveEnemy) {
                System.out.println("enemy.get(i) = " + enemy.getSymbol());
                int currentX = enemy.getCoord().getX();
                int currentY = enemy.getCoord().getY();

                System.out.println("currentX = " + currentX);
                System.out.println("currentY = " + currentY);

                int[] newXY = moveEnemy.move(currentX, currentY, enemy.getSymbol());
                int newX = newXY[0];
                int newY = newXY[1];
                if (isWithInBounds(newX, newY)) { //возможно нужно поменять проверки
                    System.out.println("Not move enemy");
                } else {
                    System.out.println("newX = " + newX);
                    System.out.println("newY = " + newY);
                    map.putZero(currentX, currentY);
                    map.setMap(newX, newY, enemy.getSymbol());
                    enemy.setCoord(newX, newY);
                }
            }

        }
    }

    @Override
    public boolean isWithInBounds(int x) {
        return true;
    }

    @Override
    public boolean isWithInBounds(int x, int y) {
        return (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight());
    }

    @Override
    public boolean checkingSymbols(char symbol){
        return symbol == 's' || symbol == 'w' ||
                symbol == 'f' || symbol == 'e';
    }
}
