package domain.backpack;

import java.util.*;

import domain.abstact.Items;
import domain.items.Elixir;
import domain.items.Food;
import domain.items.Scroll;
import domain.items.Weapon;

public class Backpack {
    private Items[][] matrix;
    private Map<Class<? extends Items>, Integer> typeToRow;
    private final int cols = 9;
    private final int rows = 4;

    public Backpack() {
        matrix = new Items[4][9];
        typeToRow = new HashMap<>();
        typeToRow.put(Weapon.class, 0);
        typeToRow.put(Food.class, 1);
        typeToRow.put(Elixir.class, 2);
        typeToRow.put(Scroll.class, 3);
    }

   public boolean add(Items item){
        Integer row = typeToRow.get(item.getClass());
        if(row == null) return false;

        for(int col = 0; col < cols; ++col){
            if(matrix[row][col] == null){
                matrix[row][col] = item;
                return true;
            }
        }
        return false;
   }

   public  List<Items> getItemsByType(Class<? extends Items> type){
        List<Items> res = new ArrayList<>();
        Integer row = typeToRow.get(type);
        if(row != null){
            for(int col = 0; col < cols; ++col){
                if(matrix[row][col] != null && matrix[row][col].getClass().equals(type)){
                    res.add(matrix[row][col]);
                }
            }
        }
        return res;
   }

   public List<Weapon> getPackWeapon(){
       List<Items> items = getItemsByType(Food.class);
       List<Weapon> res = new ArrayList<>();
       for(Items item : items){
           res.add((Weapon) item);
       }
        return res;
    }

    public List<Food> getPackFood(){
        List<Items> items = getItemsByType(Food.class);
        List<Food> res = new ArrayList<>();
        for(Items item : items){
            res.add((Food) item);
        }
        return res;
    }

    public List<Elixir> getPackElixir(){
        List<Items> items = getItemsByType(Food.class);
        List<Elixir> res = new ArrayList<>();
        for(Items item : items){
            res.add((Elixir) item);
        }
        return res;
    }

    public List<Scroll> getPackScroll(){
        List<Items> items = getItemsByType(Food.class);
        List<Scroll> res = new ArrayList<>();
        for(Items item : items){
            res.add((Scroll) item);
        }
        return res;
    }

//    public Items getItems(int row, int col){
//        if(row >= 0 && row < rows && col >=0 && col < cols)
//            return matrix[row][col];
//        return null;
//    }

    public int getCol(){
        return cols;
    }

    public int getRow(){
        return rows;
    }

    public void printBackpack() {

//            System.out.println("Name: " + i.getName() +
//                    " Symbol: " + i.getSymbol() +
//                    " increase: " + i.getIncrease());
//        }
        String[] rowNames = {"Еда", "Оружие", "Зелья", "Свитки"};

        // Заголовок
        System.out.println("=== РЮКЗАК ===");

        // Проходим по всем 4 строкам
        for (int row = 0; row < 4; row++) {
            // Печатаем название строки
            System.out.print(rowNames[row] + ": ");

            // Проходим по всем 9 ячейкам в строке
            for (int col = 0; col < 9; col++) {
                // Берем предмет из ячейки
                Items item = matrix[row][col];

                // Если предмет есть
                if (item != null) {
                    // Печатаем его символ (например 'F' для еды)
                    System.out.print(item.getSymbol() + " ");
                } else {
                    // Иначе печатаем точку '.' для пустой ячейки
                    System.out.print(". ");
                }
            }

            // Переход на новую строку после каждой строки рюкзака
            System.out.println();
        }
    }
}


