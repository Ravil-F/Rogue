package org.example;
import com.googlecode.lanterna.input.KeyType;
import domain.Model;
import domain.enums.StatusPlayer;
import presentation.Controller;
import presentation.View;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
                startGame();
    }

    private static void startGame() throws IOException, InterruptedException {
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(controller);
        view.startWindow();
        startGameLoop(view, controller);
        view.stopWidows();
    }

    private static void startGameLoop(View view, Controller controller) throws IOException, InterruptedException {
        while (controller.getModel().getPlayer().getStatus() != StatusPlayer.GAMEOVER) {
            view.setKey();
            if (view.getKey() != null) {
                if (view.getKey().getKeyType() == KeyType.Character) {
                    switch (view.getKey().getCharacter()) {
                        case '1':
                            view.getScreen().refresh();
                            String namePlayer = view.inputScan();
                            if (namePlayer.equals(" "))
                                view.passName(namePlayer);
                            else controller.passName(namePlayer);
                            view.gameLoop(true);
                            break;
                        case '2':
                            controller.getModel().loadGame();
                            view.gameLoop(false);
                            break;
                        default:
                            break;
                    }
                }
                if (view.getKey().getKeyType() == KeyType.Escape) {
                    controller.getModel().getPlayer().setStatus(StatusPlayer.GAMEOVER);
                    controller.getModel().saveGame();
                }
            }
        }
    }
}