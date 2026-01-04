package presentation;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import domain.abstact.Items;
import domain.enums.StatusPlayer;
import domain.location.Rooms;
import domain.location.Passage;

import java.io.IOException;
import java.util.List;


public class View {
    private Terminal terminal;
    private TerminalScreen screen;
    private TextGraphics textGraphics;

    private Controller controller;
    private KeyStroke key;

    public View(Controller controller) {
        try {
            this.controller = controller;
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setInitialTerminalSize(new TerminalSize(100, 40)); // ширина x высота
            this.terminal = factory.createTerminal();
            this.screen = new TerminalScreen(terminal);
            textGraphics = screen.newTextGraphics();
            screen.startScreen();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    // VIEW WINDOWS
    public void startWindow(){
        try {
            textGraphics.putString(4, 2, "1      - New game");
            textGraphics.putString(4, 3, "2      - Load save game");
            textGraphics.putString(4, 4, "Escape - Exit game");
            screen.refresh();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public String inputScan() throws IOException, InterruptedException {
        StringBuilder res = new StringBuilder();
        screen.clear();
        char symbol;
        int i = 23;
        textGraphics.putString(4, 2, "Enter name Player:");
        screen.refresh();
        do{
                 setKey    ();
            if((key.getCharacter() != ' ') && (key.getKeyType() == KeyType.Enter))
                break;
            symbol = key.getCharacter();
            textGraphics.putString(i, 2, String.valueOf(symbol));
            res.append(symbol);
            screen.refresh();
            ++i;
        }while (true);
        return res.toString().trim();
    }

    private void viewMap(){
        for(int x = 0; x < controller.getModel().getMap().getWidth(); ++x){
            for (int y = 0; y < controller.getModel().getMap().getHeight(); ++y){
                int cellChar = controller.getModel().getMap().getMap(x, y);
                if (cellChar != 0)
                    textGraphics.putString(x, y, String.valueOf((char)cellChar));
            }
        }
        for (Rooms room : controller.getModel().getMap().getRooms()) {
            drawRectangle(textGraphics, room.getTopY(), room.getBottomY(),
                    room.getLeftX(), room.getRightX());
            drawRoomContent(textGraphics, room);
        }
        for (Passage passage : controller.getModel().getMap().getPassages()) {
            drawPassageSegments(textGraphics, passage);
        }
    }

    private void drawRoomContent(TextGraphics tg, Rooms room) {
        for (int y = room.getTopY() + 1; y < room.getBottomY(); y++) {
            for (int x = room.getLeftX() + 1; x < room.getRightX(); x++) {
                tg.putString(x, y, ".");
            }
        }
    }

    private void drawPassageSegments(TextGraphics tg, Passage passage) {
        for (Passage.PassageSegment segment : passage.getSegments()) {
            if (segment.isHorizontal()) {
                int y = segment.getStartY();
                for (int x = Math.min(segment.getStartX(), segment.getEndX());
                     x <= Math.max(segment.getStartX(), segment.getEndX()); x++) {
                    tg.putString(x, y, "#");
                }
            } else {
                int x = segment.getStartX();
                for (int y = Math.min(segment.getStartY(), segment.getEndY());
                     y <= Math.max(segment.getStartY(), segment.getEndY()); y++) {
                    tg.putString(x, y, "#");
                }
            }
        }
    }

    public void drawRectangle(TextGraphics tg, int topY, int bottomY, int leftX, int rightX) {
        tg.putString(leftX, topY, "┌");
        tg.putString(rightX, topY, "┐");
        tg.putString(leftX, bottomY, "└");
        tg.putString(rightX, bottomY, "┘");

        for (int x = leftX + 1; x < rightX; x++) {
            tg.putString(x, topY, "─");
            tg.putString(x, bottomY, "─");
        }

        for (int y = topY + 1; y < bottomY; y++) {
            tg.putString(leftX, y, "│");
            tg.putString(rightX, y, "│");
        }
    }


    private void viewInfo(){
        int count  = 2;
        textGraphics.putString( 2, controller.getModel().getMap().getHeight() + count,"Max health: " + controller.getModel().getPlayer().getMaxHealth());
        textGraphics.putString( 2, controller.getModel().getMap().getHeight() + (count + 1),"Health: " + controller.getModel().getPlayer().getHealth());
        textGraphics.putString( 20, controller.getModel().getMap().getHeight() + count,"Agility: " + controller.getModel().getPlayer().getAgility());
        textGraphics.putString( 20, controller.getModel().getMap().getHeight() + (count + 1),"Strength: " + controller.getModel().getPlayer().getStrength());
    }

    private void viewGameOver(){
        try {
            screen.clear();
            textGraphics.putString(4, 2, "Game Over, " + controller.getModel().getPlayer().getName());
            screen.refresh();
            Thread.sleep(2000);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void viewSingleItemtype(final char symbol) throws IOException {
        int tmpX = controller.getModel().getMap().getHeight();
        textGraphics.putString(tmpX + 2, 1, "Enter number items (0-8), Escape - exit");
        if (controller.getModel().getBackpack().getScreenOutput().isEmpty())
            textGraphics.putString(tmpX + 2, 2, "Not Items in Backpack");
        else {
            List<Items> item = controller.getModel().getBackpack().getScreenOutput();
            for (int i = 0; i < item.size(); ++i) {
                textGraphics.putString(tmpX + 4, 2 + i, +i + "." +
                        " name-" + item.get(i).getName() +
                        " increase-" + item.get(i).getIncrease());
            }
        }
        screen.refresh();
    }
    // END VIEW WINDOWS

    // GET-SET METOD
    public KeyStroke getKey() {
        return key;
    }

    public void setKey() throws IOException, InterruptedException {
        KeyStroke tmp = screen.readInput();
        if(tmp != null) {
            this.key = tmp;
        }
    }

    public TerminalScreen getScreen() {
        return screen;
    }

    public void setScreen(TerminalScreen screen) {
        this.screen = screen;
    }
    //END GET-SET METOD


    public void passName(String namePlayer){
        controller.passName(namePlayer);
    }

    public void gameLoop() throws IOException {
        controller.getModel().gameInitialization();
        try{
            while (controller.getModel().getPlayer().getStatus() != StatusPlayer.OVER){
                screen.clear();
                if (this.key != null) {
                    if (this.key.getKeyType() == KeyType.Escape) {
                        viewGameOver();
                        break;
                    }

                    if (this.key.getKeyType() == KeyType.Character && (controller.getModel().getPlayer().getStatus() == StatusPlayer.ACTION)) {
                        controller.userInput(this.key, true);
                        viewController();
                    }

                    viewMap();
                    viewInfo();
                    screen.refresh();
                    this.key = null;
                    setKey();
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void viewController() throws IOException, InterruptedException {
        switch (Character.toLowerCase(this.key.getCharacter())) {
            case 'h':
                viewBackpack('w');
                break;
            case 'j':
                viewBackpack('f');
                break;
            case 'k':
                viewBackpack('e');
                break;
            case 'e':
                viewBackpack('s');
                break;
            default:
                break;
        }
    }

    private void viewBackpack(final char symbol) throws IOException, InterruptedException {
        viewSingleItemtype(symbol);
        setKey();
        while (this.key != null){
            if(this.key.getKeyType() == KeyType.Escape){
                screen.clear();
                return;
            }
            if(this.key != null && this.key.getKeyType() == KeyType.Character){
                controller.userInputBackpack(this.key, symbol);
                screen.clear();
                return;
            }
            setKey();
        }
    }

    public void stopWidows() throws IOException {
        screen.stopScreen();
    }

}
