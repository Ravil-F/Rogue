package utils;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonProperties {
    private Properties properties;
    private final String fileName;

    public CommonProperties(String fileName) {
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

    public int getIntProperty(String key, int defaultParam){
        return Integer.parseInt(properties.getProperty(key), defaultParam);
    }

}
