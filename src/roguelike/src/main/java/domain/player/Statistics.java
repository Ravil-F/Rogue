package domain.player;

import utils.GameStatistics;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Statistics {
    private static Statistics statistics;
    private List<GameStatistics> allStatistics;
    private static final String STATISTICS_FILE = "game_statistics.dat";

    public Statistics(){
        allStatistics = new ArrayList<>();
        loadStatistics();
    }

    public static synchronized Statistics getStatistics(){
        if(statistics == null)
            statistics = new Statistics();
        return statistics;
    }

    public void addStatistics(GameStatistics gameStatistics){
        if(gameStatistics == null) return;
        allStatistics.add(gameStatistics);
        sortStatistics();
        saveStatistics();
    }

    public List<GameStatistics> getAllStatistics() {
        return allStatistics;
    }

    public void setAllStatistics(List<GameStatistics> allStatistics) {
        this.allStatistics = allStatistics;
    }

    private void saveStatistics(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATISTICS_FILE))){
            oos.writeObject(allStatistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadStatistics(){
        File file = new File(STATISTICS_FILE);
        if (!file.exists()) {
            allStatistics = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STATISTICS_FILE))) {
            allStatistics = (List<GameStatistics>) ois.readObject();
            sortStatistics();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Not load statistics: " + e.getMessage());
            allStatistics = new ArrayList<>();
        }
    }

    private  void sortStatistics(){
        Collections.sort(allStatistics);
    }

    public GameStatistics findStatisticsByName(String namePlayer){
        for(GameStatistics name : allStatistics){
            if(name.getName().equals(namePlayer))
                return name;
        }
        return null;
    }

    public void updateStatistics(GameStatistics newStat){
        for(int i =0; i < allStatistics.size(); ++i){
            if(allStatistics.get(i).getName().equals(newStat.getName())){
                allStatistics.set(i, newStat);
                sortStatistics();
                saveStatistics();
                return;
            }
        }
        addStatistics(newStat);
    }
}