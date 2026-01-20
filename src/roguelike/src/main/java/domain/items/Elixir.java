package domain.items;

import com.googlecode.lanterna.TextColor;
import domain.abstact.Items;
import domain.enums.ElixirE;

public class Elixir extends Items {
    private ElixirE elixir;

    public Elixir(ElixirE elixir, int duration, int x, int y){
        super(elixir.getSymbol(), elixir.getName(), elixir.getIncrease(), elixir.getColor(), x, y);
        this.elixir = elixir;
    }

    public ElixirE getElixir() {
        return elixir;
    }

}
