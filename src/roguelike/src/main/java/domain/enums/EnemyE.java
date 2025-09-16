package domain.enums;

//типы врагов
public enum EnemyE {
    ZOMBI("Zombi", 'Z',  ColorE.GREEN ,100, 100, 25, 50, HostilityE.LOW),
    VAMPIRE("Vampire", 'V', ColorE.RED , 100, 100, 100, 50, HostilityE.HIGH),
    GROST("Grost", 'G', ColorE.WHITE, 25, 25, 100, 25, HostilityE.LOW),
    ORGE("Orge", 'O', ColorE.YELLOW, 100, 100, 25, 100, HostilityE.MEDIUM),
    SNAKE_MAGE("Sname Mage", 'S', ColorE.WHITE, 50, 50, 100, 50, HostilityE.HIGH);

//    private final String enemyType;
    private final String name;
    private final char symbol;
    private final ColorE color;
    private final int maxHealth;
    private final int health;
    private final int agality;
    private final int strength;
    private final HostilityE hostility;

    EnemyE(String name, char symbol, ColorE color, int maxHealth, int health, int agality, int strength, HostilityE hostility) {
        this.name = name;
        this.symbol = symbol;
        this.color = color;
        this.maxHealth = maxHealth;
        this.health = health;
        this.agality = agality;
        this.strength = strength;
        this.hostility = hostility;
    }


    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    public ColorE getColor() {
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

    public HostilityE getHostility() { return  hostility; }
}
