package com.rmat.fusen.sessionvoid.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperty {

    //add property here
	private static Properties prop;

    static {

        InputStream namefile = null;

        try{
            namefile = AppProperty.class.getClassLoader().getResourceAsStream("/com/rmat/fusen/sessionvoid/util/app.properties");
            if(namefile == null){
                throw new IllegalArgumentException("failed to read property-file...");
            }

            prop = new Properties();
            prop.load(namefile);

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key){
    	return prop.getProperty(key);
    }
}