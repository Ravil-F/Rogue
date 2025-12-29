package utils;

public class MapProperties {
    private CommonProperties common;
    private final int maxlevel;
    private final int differenceLevel;
    private final int widthHeight;

    public MapProperties(){
        common = new CommonProperties("map.properties");
        this.maxlevel = common.getIntProperty("max_level", 21);
        this.differenceLevel = common.getIntProperty("difference_level", 16);
        this.widthHeight = common.getIntProperty("width_heigth", 20);
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
