package domain.interfaces;

public interface Check {
    public boolean isWithInBounds(int x);

    public boolean isWithInBounds(int x, int y);

    public boolean checkingSymbols(char symbol);
}
