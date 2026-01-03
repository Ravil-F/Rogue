package domain.interfaces;

public interface Action {
    public int[] move(int x, int y, char symbol);
    public int move(int xy, boolean sign);
}
