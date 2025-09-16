package domain.enums;

public enum HostilityE {
    LOW(2),
    MEDIUM(4),
    HIGH(6);

    private final int hostilityType;

    HostilityE(int  hostilityType) {
        this. hostilityType =  hostilityType;
    }

    public int getVisibilityCell() {
        return  hostilityType;
    }
}
