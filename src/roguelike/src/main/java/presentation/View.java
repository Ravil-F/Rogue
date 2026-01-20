package presentation;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import domain.abstact.Attributes;
import com.googlecode.lanterna.TextColor;
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
            screen.setCursorPosition(null);
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
        int mapWidth = controller.getModel().getMap().getWidth();
        int mapHeight = controller.getModel().getMap().getHeight();
        int offsetX = 1;
        int offsetY = 1;
        drawRectangle(textGraphics, offsetY - 1, mapHeight + offsetY,
                offsetX - 1, mapWidth + offsetX);
        for (Rooms room : controller.getModel().getMap().getRooms()) {
            drawRectangle(textGraphics,
                    room.getTopY() + offsetY, room.getBottomY() + offsetY,
                    room.getLeftX() + offsetX, room.getRightX() + offsetX);
            drawRoomContent(textGraphics, room, offsetX, offsetY);
        }
        for (Passage passage : controller.getModel().getMap().getPassages()) {
            drawPassageSegments(textGraphics, passage, offsetX, offsetY);
        }
        for(int x = 0; x < mapWidth; ++x){
            for (int y = 0; y < mapHeight; ++y){
                char cellChar = controller.getModel().getMap().getMapChar(x, y);
                if (cellChar != 0 && cellChar != ' ' && cellChar != '#' && cellChar != '.') {
                    TextColor color = getCellColor(x, y, cellChar);
                    textGraphics.setForegroundColor(color);
                    textGraphics.putString(x + offsetX, y + offsetY, String.valueOf(cellChar));
                    textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                }
            }
        }
    }

    private TextColor getCellColor(int x, int y, char symbol) {
        if (controller.getModel().getPlayer().getCoord().getX() == x &&
                controller.getModel().getPlayer().getCoord().getY() == y) {
            return controller.getModel().getPlayer().getColor();
        }

        for (Attributes enemy : controller.getModel().getEnemys().getEnemy()) {
            if (enemy.getCoord().getX() == x && enemy.getCoord().getY() == y) {
                return enemy.getColor();
            }
        }

        for (Items item : controller.getModel().getItems().getItems()) {
            if (item.getCoord().getX() == x && item.getCoord().getY() == y) {
                return item.getColor();
            }
        }

        // Цвет по умолчанию для символа
        return TextColor.ANSI.WHITE;
    }


    private void drawRoomContent(TextGraphics tg, Rooms room, int offsetX, int offsetY) {
        for (int y = room.getTopY() + 1; y < room.getBottomY(); y++) {
            for (int x = room.getLeftX() + 1; x < room.getRightX(); x++) {
                tg.putString(x + offsetX, y + offsetY, ".");
            }
        }
    }

    private void drawPassageSegments(TextGraphics tg, Passage passage, int offsetX, int offsetY) {
        for (Passage.PassageSegment segment : passage.getSegments()) {
            if (segment.isHorizontal()) {
                int y = segment.getStartY() + offsetY;
                for (int x = Math.min(segment.getStartX(), segment.getEndX());
                     x <= Math.max(segment.getStartX(), segment.getEndX()); x++) {
                    tg.putString(x + offsetX, y, "#");
                }
            } else {
                int x = segment.getStartX() + offsetX;
                for (int y = Math.min(segment.getStartY(), segment.getEndY());
                     y <= Math.max(segment.getStartY(), segment.getEndY()); y++) {
                    tg.putString(x, y + offsetY, "#");
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
        int mapWidth = controller.getModel().getMap().getWidth();
        int mapHeight = controller.getModel().getMap().getHeight();
        // int offsetX = 1;
        // int offsetY = 1;
        // int infoY = mapHeight + offsetY + 2;
        int infoY = mapHeight;
        String info = String.format(
                "Level: %d     Health: %d/%d     Agility: %d     Strength: %d     Treasure: %d",
                controller.getModel().getLevel(),
                controller.getModel().getPlayer().getHealth(),
                controller.getModel().getPlayer().getMaxHealth(),
                controller.getModel().getPlayer().getAgility(),
                controller.getModel().getPlayer().getStrength(),
                controller.getModel().getPlayer().getTreasure()
        );
        // int infoRectWidth = mapWidth + offsetX;
        // drawRectangle(textGraphics, infoY - 1, infoY + 1,
        //         offsetX - 1, infoRectWidth);
        // int infoTextWidth = info.length();
        // int rectWidth = infoRectWidth - offsetX + 1;
        // int centerX = offsetX + (rectWidth - infoTextWidth) / 2;
        int infoRectWidth = mapWidth;
        drawRectangle(textGraphics, infoY - 1, infoY + 1,
                1, infoRectWidth);
        int infoTextWidth = info.length();
        int rectWidth = infoRectWidth + 1;
        int centerX = (rectWidth - infoTextWidth) / 2;

        textGraphics.putString(centerX, infoY, info);
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

    public void gameLoop(boolean flag) throws IOException {
        if (flag)
            controller.getModel().gameInitialization();
        try{
            while (controller.getModel().getPlayer().getStatus() != StatusPlayer.GAMEOVER){
                screen.clear();
                if (this.key != null) {
                    if (this.key.getKeyType() == KeyType.Escape) {
                        viewGameOver();
                        break;
                    }

                    if (this.key.getKeyType() == KeyType.Character) {
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

            if(controller.getModel().getPlayer().getStatus().equals(StatusPlayer.GAMEOVER))
                viewGameOver();

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
