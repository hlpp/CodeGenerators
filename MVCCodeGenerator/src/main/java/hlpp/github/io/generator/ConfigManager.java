package hlpp.github.io.generator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author hulang
 * @sice 2017年9月6日
 * @vesion 1.0
 */
public class ConfigManager {
    private Properties props;  
    
    public ConfigManager(String path) {
        props = new Properties();
        try {
            props.load(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
