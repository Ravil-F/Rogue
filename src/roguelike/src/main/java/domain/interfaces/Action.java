package domain.interfaces;

import domain.abstact.Attributes;

public interface Action {
    public int[] move(int x, int y, char symbol);
    public int move(int xy, boolean sign);
    public void attack(Attributes enemy);
}
