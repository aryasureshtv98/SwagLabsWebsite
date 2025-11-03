package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private final String propertyFilePath = "src/test/resources/Config.properties";


    public ConfigReader() {
        properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(propertyFilePath)) 
        {
            properties.load(inputStream);
        } 
        catch (FileNotFoundException e) 
        {
            throw new RuntimeException("Configuration file not found at " + propertyFilePath, e);
        } 
        catch (IOException e) 
        {
            System.err.println("Error reading configuration file: " + propertyFilePath);
            e.printStackTrace();
        }
    }

    public String getBaseUrl() 
    {
        String url = properties.getProperty("application.url");
        if (url != null) 
        {
            return url;
        } 
        else 
        {
            throw new RuntimeException("Application URL not specified in the config.properties file.");
        }
    }


    public String getBrowser() 
    {
        String browser = properties.getProperty("browser.name");
        if (browser != null) 
        {
            return browser;
        } 
        else 
        {
            return "chrome"; 
        }
    }

    public int getImplicitWaitTime() 
    {
        String wait = properties.getProperty("implicit.wait");
        if (wait != null) 
        {
            try 
            {
                return Integer.parseInt(wait);
            } 
            catch (NumberFormatException e) 
            {
                System.err.println("Warning: Invalid number format for implicit.wait. Using default 10 seconds.");
                return 10;
            }
        }
        return 10;
    }


}
