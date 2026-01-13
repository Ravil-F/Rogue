package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.player.Player;

import java.io.*;

public class SavePlayer {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting().create();

    public static void savePlayer(Player player, String fileName){
        try (FileWriter writer = new FileWriter(fileName)){
            gson.toJson(player, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Player loadPlayer(String fileName){
        try(FileReader reader = new FileReader(fileName)){
            Player player = gson.fromJson(reader, Player.class);
            return player;
        } catch (IOException e){
            return null;
        }
    }
}

