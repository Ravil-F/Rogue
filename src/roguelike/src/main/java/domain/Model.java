package domain;

import domain.abstact.Items;
import domain.backpack.Backpack;
import domain.enemy.GameEnemy;
import domain.enums.StatusE;
import domain.enums.StatusPlayer;
import domain.items.GameItems;
import domain.location.Map;
import domain.player.Player;

import java.util.*;

public class Model {
    private Player player;
    private Backpack backpack;
    private Map map;
    private GameItems items;
    private GameEnemy enemy;
    private int level;

    public Model(){
        player = new Player();
        backpack = new Backpack();
        map = new Map();
        items = new GameItems();
        enemy = new GameEnemy();
        level = 1;
    }

    public void gameInitialization(){
        player.setStatus(StatusPlayer.ACTION);
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());

        items.generateRandom(level);
        for(int i = 0; i < items.getItems().size(); ++i) {
            map.setMap(items.getItems().get(i).getCoord().getX(), items.getItems().get(i).getCoord().getY(), items.getItems().get(i).getSymbol());
        }

        enemy.generateRandom(level);
        for(int i = 0; i < enemy.getEnemy().size(); ++i){
            map.setMap(enemy.getEnemy().get(i).getCoord().getX(), enemy.getEnemy().get(i).getCoord().getY(), enemy.getEnemy().get(i).getSymbol());
        }
    }

    public void gameSession(){
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());
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
                    ++tmpY;
                    break;
                case UP:
                    --tmpY;
                    break;
                case LEFT:
                    --tmpX;
                    break;
                case RIGHT:
                    ++tmpX;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }


            if (tryMove(tmpX, tmpY)) {
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

    private boolean checkItems(int x, int y){
        if(items.getItems() == null || items.getItems().isEmpty()) return false;
        int index = equalsMapItems(x, y, items);
        if (index != -1) {
            backpack.add(items.getItems().get(index), items.getItems().get(index).getSymbol());
            map.putZero(x, y);
            map.putZero(player.getCoord().getX(), player.getCoord().getY());
            player.setCoord(x, y);
            return true;
        }
        return false;
    }

    private int equalsMapItems(int x, int y, GameItems items){
        for(int i = 0; i < items.getItems().size(); i++){
            if (items.getItems().get(i).getCoord().getX() == x && items.getItems().get(i).getCoord().getY() == y)
                return i;
        }
        return -1;
    }

    public void openBackpack(final char symbol){
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


}
