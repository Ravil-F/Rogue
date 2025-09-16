package domain;

import domain.abstact.Items;
import domain.backpack.Backpack;
import domain.enums.StatusE;
import domain.enums.StatusPlayer;
import domain.items.GameItems;
import domain.location.Map;
import domain.player.Player;
import utils.CommonProperties;

import java.util.*;

public class Model {
    private Player player;
    private Backpack backpack;
    private Map map;
    private GameItems items;
    private List<Items> singleItemType;
    private int level;

    public Model(){
        player = new Player();
        backpack = new Backpack();
        map = new Map();
        items = new GameItems();
        singleItemType = new LinkedList<>();
        level = 1;
    }

    public void gameInitialization(){
        player.setStatus(StatusPlayer.ACTION);
        map.setMap(player.getCoord().getX(), player.getCoord().getY(), player.getSymbol());

        items.generateRandomItems(level);
        for(int i = 0; i < items.getItems().size(); ++i) {
            map.setMap(items.getItems().get(i).getCoord().getX(), items.getItems().get(i).getCoord().getY(), items.getItems().get(i).getSymbol());
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
            System.out.println("world");
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
        if(items.getItems() == null) return false;
        int index = equalsMapItems(x, y, items);
        if (index != -1) {
            backpack.add(items.getItems().get(index));
            backpack.printBackpack();
            System.out.println("-----------------------------------");
            map.putZero(x, y);
            map.putZero(player.getCoord().getX(), player.getCoord().getY());
            player.setCoord(x, y);
            return true;
        }
        return false;
    }

    private int equalsMapItems(int x, int y, GameItems items){
        for(int i = 0; i < items.getItems().size(); i++){
            if (map.convertIntToString(x, y).equals(String.valueOf(items.getItems().get(i).getSymbol())))
                return i;
        }
        return -1;
    }

    public void openBackpack(final char symbol){
        singleItemType.clear();
        for(int i = 0; i < backpack.getItemsSize(); ++i){
            if (backpack.getItems(i).getSymbol() == symbol){
                singleItemType.add(backpack.getItems(i));
            }
        }

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

    public List<Items> getSingleItemType() {
        return singleItemType;
    }

    public Backpack getBackpack() {
        return backpack;
    }
}
