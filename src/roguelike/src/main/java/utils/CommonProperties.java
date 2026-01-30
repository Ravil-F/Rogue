package utils;

public class CommonProperties extends MainProperties {
    private final int maxlevel;
    private final int differenceLevel;
    private final int widthHeight;
    private final int countEnemy;

    public CommonProperties() {
        super("common.properties");
        this.maxlevel = getIntProperty("max_level", 21);
        this.differenceLevel = getIntProperty("difference_level", 16);
        this.widthHeight = getIntProperty("width_heigth", 20);
        this.countEnemy = getIntProperty("count_enemy", 5);
    }

    public int getDifferenceLevel() {
        return differenceLevel;
    }

    public int getWidthHeight() {
        return widthHeight;
    }

    public int getMaxlevel() {
        return maxlevel;
    }

    public int getCountEnemy() {
        return countEnemy;
    }
}
