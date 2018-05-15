/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author karel
 */
public class ConfigurationFile {
    
    private String filePath;
    private Properties props;
            
    public ConfigurationFile(String path) throws FileNotFoundException, IOException {
        filePath = path;
        props = new Properties();
        props.load(new FileInputStream(filePath));
    }
    
    public Properties getProperties() {
        return props;
    }
    
    public void save() throws FileNotFoundException, IOException {
        props.store(new FileOutputStream(filePath), "description");        
    }
    
    public String get(String name) {
        return props.getProperty(name); 
    }
    
    public void set(String name, String value) {
        props.setProperty(name, value); 
    }
    
}
