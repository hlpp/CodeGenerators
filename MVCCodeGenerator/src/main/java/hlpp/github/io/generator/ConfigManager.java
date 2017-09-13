package hlpp.github.io.generator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private Properties props = new Properties();  
    
    public ConfigManager(String path) {
        /*
        try {
            props.load(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    
    public void set(String key, String value) {  
        props.setProperty(key, value);
    }
    
    public String get(String key) {  
        return props.getProperty(key);  
    }  
    
    public int getInt(String key) {  
        return Integer.parseInt(props.getProperty(key));  
    }  
      
    public boolean getBool(String key) {
        return Boolean.parseBoolean(props.getProperty(key));  
    }
}
