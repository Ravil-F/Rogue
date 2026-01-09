package domain.items;

import domain.abstact.Items;
import domain.common.Coord;
import domain.enums.WeaponE;

public class Weapon extends Items {
    private WeaponE weapon;

    public Weapon(WeaponE weapon, int x, int y){
        super(weapon.getSymbol(), weapon.getName(), weapon.getIncrease(), weapon.getColor(), x, y);
        this.weapon = weapon;
    }

    public WeaponE getWeapon() {
        return weapon;
    }
}
