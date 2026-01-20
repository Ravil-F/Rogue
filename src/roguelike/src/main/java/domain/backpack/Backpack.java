package domain.backpack;

import java.util.*;

import domain.abstact.Items;
import domain.items.Elixir;
import domain.items.Food;
import domain.items.Scroll;
import domain.items.Weapon;

public class Backpack {
    private List<Items> packWeapon;
    private List<Items> packFood;
    private List<Items> packElixir;
    private List<Items> packScroll;
    private List<Items> screenOuptup;

    public Backpack() {
       packWeapon = new ArrayList<>();
       packFood = new ArrayList<>();
       packElixir = new ArrayList<>();
       packScroll = new ArrayList<>();
       screenOuptup = new ArrayList<>();
    }

   public void add(Items item,  final char symbol ){
        switch (symbol){
            case 'w':
                packWeapon.add(item);
                break;
            case 'f':
                packFood.add(item);
                break;
            case 'e':
                packElixir.add(item);
                break;
            case 's':
                packScroll.add(item);
                break;
        }
   }

   public List<Items> getPackItems(final char symbol){
        switch (symbol){
            case 'w':
                return packWeapon;
            case 'f':
                return packFood;
            case 'e':
                return packElixir;
            case 's':
                return packScroll;
        }
        return null;
   }

   public List<Items> getScreenOutput(){
        return screenOuptup;
   }

    public void setScreenOutput(List<Items> screenOuptup) {
        this.screenOuptup = screenOuptup;
    }

}


