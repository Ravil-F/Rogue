package domain.abstact;

//import domain.enums.ColorE;
import domain.common.Coord;

//сущность
public abstract class Entity {
    private String name;
    private char symbol;
    private String color;
    private Coord coord;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(int x, int y) {
        this.coord.setX(x);
        this.coord.setY(y);
    }

    public void setCoordX(int x){
        this.coord.setX(x);
    }
    public void setCoordY(int y){
        this.coord.setX(y);
    }

    public Entity(String name, char symbol, String color, int x, int y) {
        this.name = name;
        this.symbol = symbol;
        this.color = color;
        this.coord = new Coord(x, y);
    }
}
