package readerAdvisor.environment;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GlobalProperties {
    // TODO : Pass all hardcoded folder names into this path
    private static volatile GlobalProperties globalProperties = new GlobalProperties();
    private ConcurrentMap<String,String> properties = new ConcurrentHashMap<String,String>();
    // The name of the configurationFile is defined in the build file.
    private static final String SOFTWARE_PROPERTIES = "configurationFile";
    // In case that the software properties file is not found - HardCode its name
    private static final String HARD_CODED_SOFTWARE_PROPERTIES = "software.properties";

    private GlobalProperties(){
        loadProperties();
    }

    public static GlobalProperties getInstance(){
        return globalProperties;
    }

    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    public void loadProperties(){
        try{
            Properties props = new Properties();
            String softwareProperties = System.getProperty(SOFTWARE_PROPERTIES);
            props.load(new FileInputStream(softwareProperties != null ? softwareProperties : HARD_CODED_SOFTWARE_PROPERTIES));

            Set<String> propertyNames = props.stringPropertyNames();
            for(String name : propertyNames){
                properties.put(name,props.getProperty(name));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadPropertiesForClass(String className){
        try{
            Properties props = new Properties();
            String softwareProperties = System.getProperty(SOFTWARE_PROPERTIES);
            props.load(new FileInputStream(softwareProperties != null ? softwareProperties : HARD_CODED_SOFTWARE_PROPERTIES));

            Set<String> propertyNames = props.stringPropertyNames();
            for(String name : propertyNames){
                // Load only properties that match this given class name
                if(name.indexOf('.') > -1 && name.substring(0, name.indexOf('.')).equalsIgnoreCase(className)){
                    properties.put(name,props.getProperty(name));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // TODO: Function to be tested
    public void loadPropertiesForClasses(LinkedList<String> classes){
        try{
            Properties props = new Properties();
            String softwareProperties = System.getProperty(SOFTWARE_PROPERTIES);
            props.load(new FileInputStream(softwareProperties != null ? softwareProperties : HARD_CODED_SOFTWARE_PROPERTIES));

            Set<String> propertyNames = props.stringPropertyNames();
            for(String name : propertyNames){
                // Load only properties that match this given class name
                if(name.indexOf('.') > -1){
                    if(classes.contains(name.substring(0, name.indexOf('.')))){
                        properties.put(name,props.getProperty(name));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Retrieve the properties map for this software
     * It contains all the properties of the software
     */
    public Map<String,String> getPropertiesMap(){
        return properties;
    }

    public String getProperty(String property){
        String value = null;
        if(property != null){
            value = properties.get(property);
        }
        return value;
    }

    public String getProperty(String property, String val){
        if(val == null) val = "";
        String value = getProperty(property);
        return (value != null ? value : val);
    }

    public double getPropertyAsDouble(String property, double val){
        double value = getPropertyAsDouble(property);
        return (value != 0.0 ? value : val);
    }

    public double getPropertyAsDouble(String property){
        double value = 0;
        if(property != null){
            value = NumberUtils.getDouble(properties.get(property));
        }
        return value;
    }

    public int getPropertyAsInteger(String property, int val){
        int value = getPropertyAsInteger(property);
        return (value != 0 ? value : val);
    }

    public int getPropertyAsInteger(String property){
        int value = 0;
        if(property != null){
            value = NumberUtils.getInteger(properties.get(property));
        }
        return value;
    }

    // If this property does not exists, return false
    public Boolean getPropertyAsBoolean(String property){
        return NumberUtils.getBoolean(properties.get(property));
    }
}
