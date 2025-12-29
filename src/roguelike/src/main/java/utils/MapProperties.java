package utils;

public class MapProperties extends CommonProperties {
    private final int maxlevel;
    private final int differenceLevel;
    private final int widthHeight;

    public MapProperties(){
        super("map.properties");
        this.maxlevel = getIntProperty("max_level", 21);
        this.differenceLevel = getIntProperty("difference_level", 16);
        this.widthHeight = getIntProperty("width_heigth", 20);
    }

    public int getMaxLevel() {
        return maxlevel;
    }

    public int getDifferenceLevel() {
        return differenceLevel;
    }

    public int getWidthHeight() {
        return widthHeight;
    }
}
