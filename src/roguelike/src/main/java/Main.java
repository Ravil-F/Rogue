import com.googlecode.lanterna.input.KeyType;
import domain.Model;
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

    private static void startGameLoop(View view, Controller controller)
            throws IOException, InterruptedException {
        boolean flag = true;
        while (flag) {
            view.setKey();
            if (view.getKey() != null) {
                if (view.getKey().getKeyType() == KeyType.Escape) {
                    controller.getModel().saveGame();
                    if (controller.getModel().getGameStatistics() != null)
                        controller.getModel().saveStatistics();
                    flag = false;
                }
                if (view.getKey().getKeyType() == KeyType.Character && flag) {
                    startGameLoopSwitch(view, controller);
                }
                view.getScreen().refresh();
                view.startWindow();
            }
        }
    }

    private static void startGameLoopSwitch(View view, Controller controller)
            throws IOException, InterruptedException {
        switch (view.getKey().getCharacter()) {
            case '1':
                Model newModel = new Model();
                controller.setModel(newModel);
                String namePlayer = view.inputScan();
                if (namePlayer.equals(" "))
                    view.passName(namePlayer);
                else
                    controller.passName(namePlayer);
                view.gameLoop(true);
                if (controller.getModel().getGameStatistics() != null) {
                    controller.getModel().saveStatistics();
                }
                break;
            case '2':
                Model new2Model = new Model();
                controller.setModel(new2Model);
                controller.getModel().loadGame();
                view.gameLoop(false);
                if (controller.getModel().getGameStatistics() != null) {
                    controller.getModel().saveStatistics();
                }
                break;
            case '3':
                view.gameStatisticsView();
                break;
            default:
                break;
        }
    }
}
