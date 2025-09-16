package presentation;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import domain.Model;
import domain.enums.StatusE;
import domain.enums.StatusPlayer;

public class Controller {
    private Model model;
    private KeyStroke key;

    public Controller(Model model){
        this.model = model;

    }

    public void userInput(KeyStroke key, boolean flag){
        if (flag) {
            if (key != null) {
                if (key.getKeyType() == KeyType.Character) {
                    if (Character.toLowerCase(key.getCharacter()) == 'w'  ||
                            Character.toLowerCase(key.getCharacter()) == 's'  ||
                            Character.toLowerCase(key.getCharacter()) == 'a'  ||
                            Character.toLowerCase(key.getCharacter()) == 'd' )
                        getModel().getPlayer().setStatus(StatusPlayer.MOVE);
                    switch (Character.toLowerCase(key.getCharacter())) {
                        case 'w':
                            model.movePlayer(StatusE.UP);
                            break;
                        case 's':
                            model.movePlayer(StatusE.DOWN);
                            break;
                        case 'a':
                            model.movePlayer(StatusE.LEFT);
                            break;
                        case 'd':
                            model.movePlayer(StatusE.RIGHT);
                            break;
                        case 'h':
                            model.openBackpack('w');
                            break;
                        case 'j':
                            System.out.println("world");
                            break;
                        case 'k':
                            System.out.println("world");
                            break;
                        case 'e':
                            System.out.println("world");
                            break;
                        default:
                            break;
                    }
                } else if (key.getKeyType() == KeyType.Escape) {
                    getModel().getPlayer().setStatus(StatusPlayer.OVER);
                }
            }

        }
        getModel().getPlayer().setStatus(StatusPlayer.ACTION);
        model.gameSession();
    }

    public void userInputBackpack(KeyStroke key){
        if (key != null){
            if (key.getKeyType() == KeyType.Character){
                switch (key.getCharacter()){
                    case '0', '1', '2',
                         '3', '4', '5',
                         '6', '7', '8' :
                        model.getPlayer().increaseStrenght(model.getSingleItemType().get(Character.getNumericValue(key.getCharacter())).getIncrease());
                        break;
                }
            }
        }
        model.getBackpack().getItems().remove(Character.getNumericValue(key.getCharacter()));
    }

    public void passName(String namePlayer){
        model.passName(namePlayer);
    }


    //GET-SET METOD
    public Model getModel() {
        return model;
    }
}
