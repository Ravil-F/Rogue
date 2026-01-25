package presentation;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import domain.Model;
import domain.abstact.Items;
import domain.enums.StatusE;
import domain.enums.StatusPlayer;

import java.util.List;

public class Controller {
    private Model model;
    private KeyStroke key;

    public Controller(Model model){
        this.model = model;
    }

    public void userInput(KeyStroke key, boolean flag) {
        if (flag) {
            if (key != null) {
                if (key.getKeyType() == KeyType.Character) {

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
                            model.openBackpack('f');
                            break;
                        case 'k':
                            model.openBackpack('e');
                            break;
                        case 'e':
                            model.openBackpack('s');
                            break;
                        default:
                            break;
                    }
                }
            }
            if(key.getCharacter() == 'w' || key.getCharacter() == 's' || key.getCharacter() == 'a' ||
                    key.getCharacter() == 'd' || key.getCharacter() == 'h' || key.getCharacter() == 'j' ||
                    key.getCharacter() == 'k' || key.getCharacter() == 'e')
                model.gameSession();
        }
    }

    public void userInputBackpack(KeyStroke key, final char symbol ){
        List<Items> item = model.getBackpack().getScreenOutput();
        if (key != null && !item.isEmpty()){

            if (key.getKeyType() == KeyType.Character){
                int index = Character.getNumericValue(key.getCharacter());
                switch (index){
                    case 0, 1, 2,
                         3, 4, 5,
                         6, 7, 8 :
                        model.actionOfItems(symbol, index);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    public void passName(String namePlayer){
        model.passName(namePlayer);
    }

    //GET-SET METOD
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
