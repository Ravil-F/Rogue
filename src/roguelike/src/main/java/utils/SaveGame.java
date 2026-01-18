package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.googlecode.lanterna.TextColor;
import domain.backpack.Backpack;
import domain.player.Player;

import java.io.*;

public class SaveGame {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TextColor.class, new TextColorTypeAdapter()) // Добавляем кастомный адаптер
            .create();

    // SAVE
    public static void savePlayer(Player player, String fileName){
        try (FileWriter writer = new FileWriter(fileName)){
            gson.toJson(player, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBackpack(Backpack backpack, String fileName){
        try (FileWriter writer = new FileWriter(fileName)){
            gson.toJson(backpack, writer);
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
            return null;
        }
    }

    public static Backpack loadBackpack(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            Backpack backpack = gson.fromJson(reader, Backpack.class);
            return backpack;
        } catch (IOException e){
            return null;
        }
    }
}

class TextColorTypeAdapter extends TypeAdapter<TextColor> {
    @Override
    public void write(JsonWriter out, TextColor value) throws IOException {
        if (value instanceof TextColor.ANSI) {
            out.value(((TextColor.ANSI) value).name());
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

