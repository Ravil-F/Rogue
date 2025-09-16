package domain.enums;

import com.googlecode.lanterna.TextColor;

public enum ColorE {
    GREEN (TextColor.ANSI.GREEN),
    RED(TextColor.ANSI.RED),
    WHITE(TextColor.ANSI.WHITE),
    YELLOW(TextColor.ANSI.YELLOW);

    private final TextColor.ANSI color;
    ColorE(TextColor.ANSI color) {
        this.color = color;
    }

    public TextColor.ANSI getColor() {
        return color;
    }
}
