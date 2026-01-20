package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.googlecode.lanterna.TextColor;
import domain.abstact.Attributes;
import domain.abstact.Items;
import domain.backpack.Backpack;
import domain.items.GameItems;
import domain.items.Weapon;
import domain.location.Passage;
import domain.location.Rooms;
import domain.location.Map;
import domain.player.Player;
import domain.items.*;
import domain.enemy.*;
import domain.*;

import java.io.*;
import java.util.*;

public class SaveGame {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TextColor.class, new TextColorTypeAdapter())
            .registerTypeAdapter(Items.class, new ItemsTypeAdapter())
            .registerTypeAdapter(Backpack.class, new BackpackTypeAdapter())
            .registerTypeAdapter(Attributes.class, new AttributesTypeAdapter())
            .registerTypeAdapter(GameEnemy.class, new GameEnemyTypeAdapter())
            .registerTypeAdapter(Weapon.class, new WeaponTypeAdapter())
            .registerTypeAdapter(domain.location.Map.class, new MapTypeAdapter())
            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
            .create();


    // SAVE
    public static void savePlayer(Player player, String fileName){
        try (FileWriter writer = new FileWriter(fileName, false)){
            gson.toJson(player, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBackpack(Backpack backpack, String fileName){
        try (FileWriter writer = new FileWriter(fileName, false)){
            gson.toJson(backpack, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveGameItems(GameItems gameItems, String fileName){
        try (FileWriter writer = new FileWriter(fileName, false)){
            gson.toJson(gameItems, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveGameEnemy(GameEnemy gameEnemy, String fileName){
        try (FileWriter writer = new FileWriter(fileName,false)){
            gson.toJson(gameEnemy, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveWeaponTaken(Weapon weapon, String fileName){
        try (FileWriter writer = new FileWriter(fileName, false)){
            gson.toJson(weapon, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMap(domain.location.Map map, String fileName){
        try (FileWriter writer = new FileWriter(fileName, false)){
            gson.toJson(map, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // LOAD
    public static Player loadPlayer(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            Player player = gson.fromJson(reader, Player.class);
            return player;
        } catch (IOException e){
            System.err.println("Error loading player from " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    public static Backpack loadBackpack(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            Backpack backpack = gson.fromJson(reader, Backpack.class);
            return backpack;
        } catch (IOException e){
            System.err.println("Error loading backpack from " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    public static GameItems loadGameItems(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            GameItems gameItems = gson.fromJson(reader, GameItems.class);
            return gameItems;
        } catch (IOException e){
            System.err.println("Error loading game items from " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    public static GameEnemy loadGameEnemy(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            GameEnemy gameEnemy = gson.fromJson(reader, GameEnemy.class);
            return gameEnemy;
        } catch (IOException e){
            System.err.println("Error loading game enemy from " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    public static Weapon loadWeaponTaken(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            Weapon weapon = gson.fromJson(reader, Weapon.class);
            return weapon;
        } catch (IOException e){
            System.err.println("Error loading weapon from " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    public static Map loadMap(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            Map map = gson.fromJson(reader, Map.class);
            return map;
        } catch (IOException e){
            System.err.println("Error loading map from " + fileName + ": " + e.getMessage());
            return null;
        }
    }
}

// Адаптер для TextColor
class TextColorTypeAdapter extends TypeAdapter<TextColor> {
    @Override
    public void write(JsonWriter out, TextColor value) throws IOException {
        if (value instanceof TextColor.ANSI) {
            out.value(((TextColor.ANSI) value).name());
        } else {
            out.value("WHITE");
        }
    }

    @Override
    public TextColor read(JsonReader in) throws IOException {
        String colorStr = in.nextString();

        if (colorStr == null || colorStr.isEmpty()) {
            return TextColor.ANSI.WHITE;
        }

        try {
            return TextColor.ANSI.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TextColor.ANSI.WHITE;
        }
    }
}


// Адаптер для Items
class ItemsTypeAdapter extends TypeAdapter<Items> {
    @Override
    public void write(JsonWriter out, Items item) throws IOException {
        out.beginObject();

        // Сохраняем тип предмета
        if (item instanceof domain.items.Food) {
            out.name("type").value("Food");
            domain.items.Food food = (domain.items.Food) item;
            out.name("foodEnum").value(food.getFood().name());
        } else if (item instanceof domain.items.Weapon) {
            out.name("type").value("Weapon");
            domain.items.Weapon weapon = (domain.items.Weapon) item;
            out.name("weaponEnum").value(weapon.getWeapon().name());
        } else if (item instanceof domain.items.Elixir) {
            out.name("type").value("Elixir");
            domain.items.Elixir elixir = (domain.items.Elixir) item;
            out.name("elixirEnum").value(elixir.getElixir().name());
        } else if (item instanceof domain.items.Scroll) {
            out.name("type").value("Scroll");
            domain.items.Scroll scroll = (domain.items.Scroll) item;
            out.name("scrollEnum").value(scroll.getScroll().name());
        } else if (item instanceof domain.items.Treasure) {
            out.name("type").value("Treasure");
            domain.items.Treasure treasure = (domain.items.Treasure) item;
            out.name("treasureEnum").value(treasure.getTreasure().name());
        } else {
            out.name("type").value("Items");
        }

        out.name("name").value(item.getName());
        out.name("symbol").value(String.valueOf(item.getSymbol()));
        out.name("increase").value(item.getIncrease());

        if (item.getColor() instanceof TextColor.ANSI) {
            out.name("color").value(((TextColor.ANSI) item.getColor()).name());
        }

        if (item.getCoord() != null) {
            out.name("coordX").value(item.getCoord().getX());
            out.name("coordY").value(item.getCoord().getY());
        }

        out.endObject();
    }

    @Override
    public Items read(JsonReader in) throws IOException {
        String type = null;
        String name = null;
        String symbolStr = "?";
        int increase = 0;
        String colorStr = null;
        int coordX = 0;
        int coordY = 0;

        String foodEnum = null;
        String weaponEnum = null;
        String elixirEnum = null;
        String scrollEnum = null;
        String treasureEnum = null;

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "type": type = in.nextString(); break;
                case "name": name = in.nextString(); break;
                case "symbol": symbolStr = in.nextString(); break;
                case "increase": increase = in.nextInt(); break;
                case "color": colorStr = in.nextString(); break;
                case "coordX": coordX = in.nextInt(); break;
                case "coordY": coordY = in.nextInt(); break;
                case "foodEnum": foodEnum = in.nextString(); break;
                case "weaponEnum": weaponEnum = in.nextString(); break;
                case "elixirEnum": elixirEnum = in.nextString(); break;
                case "scrollEnum": scrollEnum = in.nextString(); break;
                case "treasureEnum": treasureEnum = in.nextString(); break;

                case "food": foodEnum = in.nextString(); type = "Food"; break;
                case "weapon": weaponEnum = in.nextString(); type = "Weapon"; break;
                case "elixir": elixirEnum = in.nextString(); type = "Elixir"; break;
                case "scroll": scrollEnum = in.nextString(); type = "Scroll"; break;
                case "treasure": treasureEnum = in.nextString(); type = "Treasure"; break;

                case "coord":
                    in.beginObject();
                    while (in.hasNext()) {
                        String coordField = in.nextName();
                        if ("x".equals(coordField)) {
                            coordX = in.nextInt();
                        } else if ("y".equals(coordField)) {
                            coordY = in.nextInt();
                        } else {
                            in.skipValue();
                        }
                    }
                    in.endObject();
                    break;

                default: in.skipValue(); break;
            }
        }
        in.endObject();

        char symbol = symbolStr.isEmpty() ? '?' : symbolStr.charAt(0);
        TextColor color = getTextColor(colorStr);

        try {
            if ("Food".equals(type)) {
                domain.enums.FoodE foodE = getFoodEnum(foodEnum, name);
                return new domain.items.Food(foodE, coordX, coordY);
            } else if ("Weapon".equals(type)) {
                domain.enums.WeaponE weaponE = getWeaponEnum(weaponEnum, name);
                return new domain.items.Weapon(weaponE, coordX, coordY);
            } else if ("Elixir".equals(type)) {
                domain.enums.ElixirE elixirE = getElixirEnum(elixirEnum, name);
                return createElixir(elixirE, coordX, coordY);
            } else if ("Scroll".equals(type)) {
                domain.enums.ScrollE scrollE = getScrollEnum(scrollEnum, name);
                return createScroll(scrollE, coordX, coordY);
            } else if ("Treasure".equals(type)) {
                domain.enums.TreasureE treasureE = getTreasureEnum(treasureEnum, name);
                return createTreasure(treasureE, coordX, coordY);
            } else {
                System.err.println("Unknown item type: " + type);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error creating item: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private TextColor getTextColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            return TextColor.ANSI.WHITE;
        }
        try {
            return TextColor.ANSI.valueOf(colorStr.toUpperCase());
        } catch (Exception e) {
            return TextColor.ANSI.WHITE;
        }
    }

    private domain.enums.FoodE getFoodEnum(String foodEnum, String name) {
        if (foodEnum != null) {
            try {
                return domain.enums.FoodE.valueOf(foodEnum);
            } catch (Exception e) {
                System.err.println("Error backpack: food" + e.getMessage());
            }
        }
        for (domain.enums.FoodE food : domain.enums.FoodE.values()) {
            if (food.getName().equals(name)) {
                return food;
            }
        }
        return domain.enums.FoodE.BREAD_F;
    }

    private domain.enums.WeaponE getWeaponEnum(String weaponEnum, String name) {
        if (weaponEnum != null) {
            try {
                return domain.enums.WeaponE.valueOf(weaponEnum);
            } catch (Exception e) {
                System.err.println("Error backpack: weapon" + e.getMessage());
            }
        }
        for (domain.enums.WeaponE weapon : domain.enums.WeaponE.values()) {
            if (weapon.getName().equals(name)) {
                return weapon;
            }
        }
        return domain.enums.WeaponE.SWORD_W;
    }

    private domain.enums.ElixirE getElixirEnum(String elixirEnum, String name) {
        if (elixirEnum != null) {
            try {
                return domain.enums.ElixirE.valueOf(elixirEnum);
            } catch (Exception e) {
                System.err.println("Error backpack: elixir" + e.getMessage());            }
        }
        for (domain.enums.ElixirE elixir : domain.enums.ElixirE.values()) {
            if (elixir.getName().equals(name)) {
                return elixir;
            }
        }
        return domain.enums.ElixirE.HEALTH_E;
    }

    private domain.enums.ScrollE getScrollEnum(String scrollEnum, String name) {
        if (scrollEnum != null) {
            try {
                return domain.enums.ScrollE.valueOf(scrollEnum);
            } catch (Exception e) {
                System.err.println("Error backpack: scroll" + e.getMessage());            }
        }
        for (domain.enums.ScrollE scroll : domain.enums.ScrollE.values()) {
            if (scroll.getName().equals(name)) {
                return scroll;
            }
        }
        return domain.enums.ScrollE.STRENGTH_S;
    }

    private domain.enums.TreasureE getTreasureEnum(String treasureEnum, String name) {
        if (treasureEnum != null) {
            try {
                return domain.enums.TreasureE.valueOf(treasureEnum);
            } catch (Exception e) {
                System.err.println("Error backpack: food" + e.getMessage());            }
        }
        return domain.enums.TreasureE.TREASURES_T;
    }

    private domain.items.Elixir createElixir(domain.enums.ElixirE elixirE, int x, int y) {
        try {
            return new domain.items.Elixir(elixirE, 60, x, y);
        } catch (Exception e) {
            System.err.println("Error creating Elixir: " + e.getMessage());
            return null;
        }
    }

    private domain.items.Scroll createScroll(domain.enums.ScrollE scrollE, int x, int y) {
        try {
            return new domain.items.Scroll(scrollE, x, y);
        } catch (Exception e) {
            System.err.println("Error creating Scroll: " + e.getMessage());
            return null;
        }
    }

    private domain.items.Treasure createTreasure(domain.enums.TreasureE treasureE, int x, int y) {
        try {
            return new domain.items.Treasure(treasureE, x, y);
        } catch (Exception e) {
            System.err.println("Error creating Treasure: " + e.getMessage());
            return null;
        }
    }
}

// Адаптер для Backpack
class BackpackTypeAdapter extends TypeAdapter<Backpack> {
    @Override
    public void write(JsonWriter out, Backpack backpack) throws IOException {
        out.beginObject();

        out.name("packWeapon");
        writeItemsList(out, backpack.getPackItems('w'));

        out.name("packFood");
        writeItemsList(out, backpack.getPackItems('f'));

        out.name("packElixir");
        writeItemsList(out, backpack.getPackItems('e'));

        out.name("packScroll");
        writeItemsList(out, backpack.getPackItems('s'));

        out.name("screenOuptup");
        writeItemsList(out, backpack.getScreenOutput());

        out.endObject();
    }

    private void writeItemsList(JsonWriter out, List<Items> itemsList) throws IOException {
        out.beginArray();
        if (itemsList != null) {
            ItemsTypeAdapter itemsAdapter = new ItemsTypeAdapter();
            for (Items item : itemsList) {
                itemsAdapter.write(out, item);
            }
        }
        out.endArray();
    }

    @Override
    public Backpack read(JsonReader in) throws IOException {
        Backpack backpack = new Backpack();

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "packWeapon":
                    readItemsList(in, backpack, 'w');
                    break;
                case "packFood":
                    readItemsList(in, backpack, 'f');
                    break;
                case "packElixir":
                    readItemsList(in, backpack, 'e');
                    break;
                case "packScroll":
                    readItemsList(in, backpack, 's');
                    break;
                case "screenOuptup":
                    readScreenOutput(in, backpack);
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return backpack;
    }

    private void readItemsList(JsonReader in, Backpack backpack, char symbol) throws IOException {
        ItemsTypeAdapter itemsAdapter = new ItemsTypeAdapter();

        in.beginArray();
        while (in.hasNext()) {
            Items item = itemsAdapter.read(in);
            if (item != null) {
                backpack.add(item, symbol);
            }
        }
        in.endArray();
    }

    private void readScreenOutput(JsonReader in, Backpack backpack) throws IOException {
        ItemsTypeAdapter itemsAdapter = new ItemsTypeAdapter();
        List<Items> screenOutput = new ArrayList<>();

        in.beginArray();
        while (in.hasNext()) {
            Items item = itemsAdapter.read(in);
            if (item != null) {
                screenOutput.add(item);
            }
        }
        in.endArray();

        backpack.setScreenOutput(screenOutput);
    }
}


// Адаптер для Attributes (врагов)
class AttributesTypeAdapter extends TypeAdapter<Attributes> {
    @Override
    public void write(JsonWriter out, Attributes enemy) throws IOException {
        out.beginObject();
        out.name("type").value(enemy.getClass().getSimpleName());
        out.name("maxHealth").value(enemy.getMaxHealth());
        out.name("health").value(enemy.getHealth());
        out.name("agility").value(enemy.getAgility());
        out.name("strength").value(enemy.getStrength());
        out.name("hostility").value(enemy.getHostility());
        out.name("name").value(enemy.getName());
        out.name("symbol").value(String.valueOf(enemy.getSymbol()));

        if (enemy.getColor() instanceof TextColor.ANSI) {
            out.name("color").value(((TextColor.ANSI) enemy.getColor()).name());
        }

        if (enemy.getCoord() != null) {
            out.name("coordX").value(enemy.getCoord().getX());
            out.name("coordY").value(enemy.getCoord().getY());
        }

        out.endObject();
    }

    @Override
    public Attributes read(JsonReader in) throws IOException {
        String type = null;
        int maxHealth = 0, health = 0, agility = 0, strength = 0, hostility = 0;
        String name = "", symbolStr = "?", colorStr = null;
        int coordX = 0, coordY = 0;

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "type": type = in.nextString(); break;
                case "maxHealth": maxHealth = in.nextInt(); break;
                case "health": health = in.nextInt(); break;
                case "agility": agility = in.nextInt(); break;
                case "strength": strength = in.nextInt(); break;
                case "hostility": hostility = in.nextInt(); break;
                case "name": name = in.nextString(); break;
                case "symbol": symbolStr = in.nextString(); break;
                case "color": colorStr = in.nextString(); break;
                case "coordX": coordX = in.nextInt(); break;
                case "coordY": coordY = in.nextInt(); break;
                default: in.skipValue(); break;
            }
        }
        in.endObject();

        char symbol = symbolStr.isEmpty() ? '?' : symbolStr.charAt(0);
        TextColor color = getTextColor(colorStr);

        try {
            switch (type) {
                case "Zombi":
                    Zombi zombi = new Zombi(coordX, coordY);
                    zombi.setHealth(health);
                    zombi.setAgility(agility);
                    zombi.setStrength(strength);
                    return zombi;
                case "Vampire":
                    Vampire vampire = new Vampire(coordX, coordY);
                    vampire.setHealth(health);
                    vampire.setAgility(agility);
                    vampire.setStrength(strength);
                    return vampire;
                case "Grost":
                    Grost grost = new Grost(coordX, coordY);
                    grost.setHealth(health);
                    grost.setAgility(agility);
                    grost.setStrength(strength);
                    return grost;
                case "Orge":
                    Orge orge = new Orge(coordX, coordY);
                    orge.setHealth(health);
                    orge.setAgility(agility);
                    orge.setStrength(strength);
                    return orge;
                case "SnakeMage":
                    SnakeMage snakeMage = new SnakeMage(coordX, coordY);
                    snakeMage.setHealth(health);
                    snakeMage.setAgility(agility);
                    snakeMage.setStrength(strength);
                    return snakeMage;
                default:
                    System.err.println("Unknown enemy type: " + type);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error creating enemy: " + e.getMessage());
            return null;
        }
    }

    private TextColor getTextColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            return TextColor.ANSI.WHITE;
        }
        try {
            return TextColor.ANSI.valueOf(colorStr.toUpperCase());
        } catch (Exception e) {
            return TextColor.ANSI.WHITE;
        }
    }
}

// Адаптер для GameEnemy
class GameEnemyTypeAdapter extends TypeAdapter<GameEnemy> {
    @Override
    public void write(JsonWriter out, GameEnemy gameEnemy) throws IOException {
        out.beginObject();
        out.name("enemy");
        writeEnemyList(out, gameEnemy.getEnemy());
        out.endObject();
    }

    private void writeEnemyList(JsonWriter out, List<Attributes> enemyList) throws IOException {
        out.beginArray();
        if (enemyList != null) {
            AttributesTypeAdapter adapter = new AttributesTypeAdapter();
            for (Attributes enemy : enemyList) {
                adapter.write(out, enemy);
            }
        }
        out.endArray();
    }

    @Override
    public GameEnemy read(JsonReader in) throws IOException {
        GameEnemy gameEnemy = new GameEnemy();

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if ("enemy".equals(fieldName)) {
                readEnemyList(in, gameEnemy);
            } else {
                in.skipValue();
            }
        }
        in.endObject();

        return gameEnemy;
    }

    private void readEnemyList(JsonReader in, GameEnemy gameEnemy) throws IOException {
        AttributesTypeAdapter adapter = new AttributesTypeAdapter();

        in.beginArray();
        while (in.hasNext()) {
            Attributes enemy = adapter.read(in);
            if (enemy != null) {
                gameEnemy.getEnemy().add(enemy);
            }
        }
        in.endArray();
    }
}


// Специальный адаптер для Weapon
class WeaponTypeAdapter extends TypeAdapter<Weapon> {
    @Override
    public void write(JsonWriter out, Weapon weapon) throws IOException {
        out.beginObject();

        if (weapon.getWeapon() != null) {
            out.name("weaponEnum").value(weapon.getWeapon().name());
        } else {
            out.name("weaponEnum").nullValue();
        }

        out.name("name").value(weapon.getName());
        out.name("symbol").value(String.valueOf(weapon.getSymbol()));
        out.name("increase").value(weapon.getIncrease());
        out.name("color").value(((TextColor.ANSI) weapon.getColor()).name());

        if (weapon.getCoord() != null) {
            out.name("coordX").value(weapon.getCoord().getX());
            out.name("coordY").value(weapon.getCoord().getY());
        }

        out.endObject();
    }

    @Override
    public Weapon read(JsonReader in) throws IOException {
        String weaponEnum = null;
        String name = "none";
        String symbolStr = "?";
        int increase = 0;
        String colorStr = "RED";
        int coordX = 0;
        int coordY = 0;

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "weaponEnum":
                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                        in.nextNull();
                        weaponEnum = null;
                    } else {
                        weaponEnum = in.nextString();
                    }
                    break;
                case "name": name = in.nextString(); break;
                case "symbol": symbolStr = in.nextString(); break;
                case "increase": increase = in.nextInt(); break;
                case "color": colorStr = in.nextString(); break;
                case "coordX": coordX = in.nextInt(); break;
                case "coordY": coordY = in.nextInt(); break;
                default: in.skipValue(); break;
            }
        }
        in.endObject();

        domain.enums.WeaponE weaponE = null;
        if (weaponEnum != null) {
            try {
                weaponE = domain.enums.WeaponE.valueOf(weaponEnum);
            } catch (Exception e) {
                System.err.println("Invalid weapon enum: " + weaponEnum);
            }
        }

        return new domain.items.Weapon(weaponE, coordX, coordY);
    }
}

// Адаптер для Map
class MapTypeAdapter extends TypeAdapter<domain.location.Map> {
    @Override
    public void write(JsonWriter out, Map map) throws IOException {
        out.beginObject();
   
    out.name("mapData");
    out.beginArray();
    for (int y = 0; y < domain.location.Map.MAP_HEIGHT; y++) {
        out.beginArray();
        for (int x = 0; x < domain.location.Map.MAP_WIDTH; x++) {
            out.value(map.getMap(x, y));
        }
        out.endArray();
    }
    out.endArray();
   
    out.name("floorData");
    out.beginArray();
    for (int y = 0; y < domain.location.Map.MAP_HEIGHT; y++) {
        out.beginArray();
        for (int x = 0; x < domain.location.Map.MAP_WIDTH; x++) {
            out.value(map.getFloorChar(x, y));
        }
        out.endArray();
    }
    out.endArray();
        
        out.name("rooms");
        out.beginArray();
        for (Rooms room : map.getRooms()) {
            writeRoom(out, room);
        }
        out.endArray();

        out.name("passages");
        out.beginArray();
        for (Passage passage : map.getPassages()) {
            writePassage(out, passage);
        }
        out.endArray();
        out.endObject();
    }
    
    private void writeRoom(JsonWriter out, Rooms room) throws IOException {
        out.beginObject();
        out.name("leftX").value(room.getLeftX());
        out.name("rightX").value(room.getRightX());
        out.name("topY").value(room.getTopY());
        out.name("bottomY").value(room.getBottomY());
        out.endObject();
    }
    
    private void writePassage(JsonWriter out, Passage passage) throws IOException {
        out.beginArray();
        for (Passage.PassageSegment segment : passage.getSegments()) {
            out.beginObject();
            out.name("startX").value(segment.getStartX());
            out.name("startY").value(segment.getStartY());
            out.name("endX").value(segment.getEndX());
            out.name("endY").value(segment.getEndY());
            out.endObject();
        }
        out.endArray();
    }
    
    @Override
    public Map read(JsonReader in) throws IOException {
        int[][] mapData = new int[Map.MAP_WIDTH][Map.MAP_HEIGHT];
        int[][] floorData = new int[Map.MAP_WIDTH][Map.MAP_HEIGHT];
        List<Rooms> rooms = new ArrayList<>();
        List<Passage> passages = new ArrayList<>();
        
        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "mapData":
                    readMapData(in, mapData);
                    break;
                case "floorData":
                    readFloorData(in, floorData);
                    break;
                case "rooms":
                    readRooms(in, rooms);
                    break;
                case "passages":
                    readPassages(in, passages);
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
    
        Map map = new Map();
        restoreMapData(map, mapData, floorData);
        restoreRoomsAndPassages(map, rooms, passages);
        
        return map;
    }
    
    private void readMapData(JsonReader in, int[][] mapData) throws IOException {
        in.beginArray();
        int y = 0;
        while (in.hasNext()) {
            in.beginArray();
            int x = 0;
            while (in.hasNext()) {
                if (x < Map.MAP_WIDTH && y < Map.MAP_HEIGHT) {
                    mapData[x][y] = in.nextInt();
                } else {
                    in.skipValue();
                }
                x++;
            }
            in.endArray();
            y++;
        }
        in.endArray();
    }
    
    private void readFloorData(JsonReader in, int[][] floorData) throws IOException {
        in.beginArray();
        int y = 0;
        while (in.hasNext()) {
            in.beginArray();
            int x = 0;
            while (in.hasNext()) {
                if (x < Map.MAP_WIDTH && y < Map.MAP_HEIGHT) {
                    floorData[x][y] = in.nextInt();
                } else {
                    in.skipValue();
                }
                x++;
            }
            in.endArray();
            y++;
        }
        in.endArray();
    }
    
    private void readRooms(JsonReader in, List<Rooms> rooms) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            rooms.add(readRoom(in));
        }
        in.endArray();
    }
    
    private Rooms readRoom(JsonReader in) throws IOException {
        int leftX = 0, rightX = 0, topY = 0, bottomY = 0;
        
        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "leftX": leftX = in.nextInt(); break;
                case "rightX": rightX = in.nextInt(); break;
                case "topY": topY = in.nextInt(); break;
                case "bottomY": bottomY = in.nextInt(); break;
                default: in.skipValue(); break;
            }
        }
        in.endObject();
 
        Rooms room = new Rooms();
        try {
            java.lang.reflect.Field leftXField = Rooms.class.getDeclaredField("leftX");
            java.lang.reflect.Field rightXField = Rooms.class.getDeclaredField("rightX");
            java.lang.reflect.Field topYField = Rooms.class.getDeclaredField("topY");
            java.lang.reflect.Field bottomYField = Rooms.class.getDeclaredField("bottomY");
            
            leftXField.setAccessible(true);
            rightXField.setAccessible(true);
            topYField.setAccessible(true);
            bottomYField.setAccessible(true);
            
            leftXField.set(room, leftX);
            rightXField.set(room, rightX);
            topYField.set(room, topY);
            bottomYField.set(room, bottomY);
        } catch (Exception e) {
            System.err.println("Error restoring room: " + e.getMessage());
        }
        
        return room;
    }
    
    private void readPassages(JsonReader in, List<Passage> passages) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            passages.add(readPassage(in));
        }
        in.endArray();
    }
    
    private Passage readPassage(JsonReader in) throws IOException {
        Passage passage = new Passage();
        
        in.beginArray();
        while (in.hasNext()) {
            readPassageSegment(in, passage);
        }
        in.endArray();
        
        return passage;
    }
    
    private void readPassageSegment(JsonReader in, Passage passage) throws IOException {
        int startX = 0, startY = 0, endX = 0, endY = 0;
        
        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "startX": startX = in.nextInt(); break;
                case "startY": startY = in.nextInt(); break;
                case "endX": endX = in.nextInt(); break;
                case "endY": endY = in.nextInt(); break;
                default: in.skipValue(); break;
            }
        }
        in.endObject();
        
        passage.addSegment(startX, startY, endX, endY);
    }
    
    private void restoreMapData(Map map, int[][] mapData, int[][] floorData) {
        try {
            java.lang.reflect.Field mapField = Map.class.getDeclaredField("map");
            java.lang.reflect.Field floorField = Map.class.getDeclaredField("floor");
            
            mapField.setAccessible(true);
            floorField.setAccessible(true);
            
            mapField.set(map, mapData);
            floorField.set(map, floorData);
        } catch (Exception e) {
            System.err.println("Error restoring map data: " + e.getMessage());
        }
    }
    
    private void restoreRoomsAndPassages(Map map, List<Rooms> rooms, 
                                          List<Passage> passages) {
        try {
            java.lang.reflect.Field roomsField = Map.class.getDeclaredField("rooms");
            java.lang.reflect.Field passagesField = Map.class.getDeclaredField("passages");
            
            roomsField.setAccessible(true);
            passagesField.setAccessible(true);
            
            roomsField.set(map, rooms);
            passagesField.set(map, passages);
        } catch (Exception e) {
            System.err.println("Error restoring rooms and passages: " + e.getMessage());
        }
    }
}