package domain.items;

import domain.abstact.Items;
import domain.enums.ElixirE;

public class Elixir extends Items {
    private ElixirE elixir;

    public Elixir(ElixirE elixir, int duration, int x, int y){
        super(elixir.getSymbol(), elixir.getName(), elixir.getIncrease(), x, y);
    }

    public ElixirE getElixir() {
        return elixir;
    }

    public int getDuration() {
        return 60;
    }
}
