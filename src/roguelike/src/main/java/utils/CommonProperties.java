package utils;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonProperties {
    private Properties properties;
    private final String FILE_NAME = "common.properties";

    public CommonProperties() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (input == null) {
                System.out.println("Unable to find " + FILE_NAME);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getMaxLevel() {
        return Integer.parseInt(properties.getProperty("MAX_LEVEL"));
    }

    public int getDifferenceLevel() {
        return Integer.parseInt(properties.getProperty("DIFFERENCE_LEVEL"));
    }

    public int getWidthHeight() {
        return Integer.parseInt(properties.getProperty("WIDTH_HEIGHT"));
    }
}
