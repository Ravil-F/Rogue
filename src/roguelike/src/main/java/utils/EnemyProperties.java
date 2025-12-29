package utils;

public class EnemyProperties extends MainProperties {
    private final String name;
    private final char symbol;
    private final String color;
    private final int maxHealth;
    private final int health;
    private final int agality;
    private final int strength;
    private final String hostility;

    public EnemyProperties(String enemyID){
        super("enemy.properties");
        this.name = getStrProperty(enemyID + ".name", "Unknown");
        this.symbol = getCharProperty(enemyID + ".symbol", 'N' );
        this.color = getStrProperty(enemyID + ".color", "WHITE");
        this.maxHealth = getIntProperty(enemyID+ ".maxHealth", 100);
        this.health = getIntProperty(enemyID+ ".health", 100);
        this.agality = getIntProperty(enemyID+ ".agality", 0);
        this.strength = getIntProperty(enemyID + ".strength", 0);
        this.hostility = getStrProperty(enemyID + ".hostility", "LOW");
    }

    public String getName(){return name;}

    public char getSymbol() {
        return symbol;
    }

    public String getColor() {
        return color;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getAgality() {
        return agality;
    }

    public int getStrength() {
        return strength;
    }

    public String getHostility() {
        return hostility;
    }
}
