package domain.items;

import com.googlecode.lanterna.TextColor;
import domain.abstact.Items;
import domain.common.Coord;
import domain.enums.WeaponE;

public class Weapon extends Items {
    private WeaponE weapon;

    public Weapon(WeaponE weapon, int x, int y){
        super(
            weapon != null ? weapon.getSymbol() : ' ',
            weapon != null ? weapon.getName() : "none",
            weapon != null ? weapon.getIncrease() : 0,
            weapon != null ? weapon.getColor() : TextColor.ANSI.RED,
            x, y);
        this.weapon = weapon;
    }

    public WeaponE getWeapon() {
        return weapon;
    }

}
