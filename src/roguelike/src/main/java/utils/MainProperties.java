package utils;

import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class MainProperties {
    private Properties properties;
    private final String fileName;

    public MainProperties(String fileName) {
        properties = new Properties();
        this.fileName = fileName;
        loadProperties(fileName);
    }

    private void loadProperties(String fileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                System.out.println("Unable to find " + fileName);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getStrProperty(String key, String defaultParam) {
        return properties.getProperty(key, defaultParam);
    }

    public int getIntProperty(String key, int defaultParam) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultParam;
        }
        try {
            return Integer.parseInt(value); // Десятичная система
        } catch (NumberFormatException e) {
            return defaultParam;
        }
    }

    public char getCharProperty(String key, char defaultParam) {
        String value = properties.getProperty(key, String.valueOf(defaultParam));
        return (value != null && !value.isEmpty()) ? value.charAt(0) : defaultParam;
    }

    public TextColor parseColor(String color) {
        if (color == null || color.isEmpty())
            return TextColor.ANSI.WHITE;

        switch (color.trim().toUpperCase()) {
            case "GREEN":
                return TextColor.ANSI.GREEN;
            case "RED":
                return TextColor.ANSI.RED;
            case "WHITE":
                return TextColor.ANSI.WHITE;
            case "YELLOW":
                return TextColor.ANSI.YELLOW;
        }
        return TextColor.ANSI.WHITE;
    }
}
