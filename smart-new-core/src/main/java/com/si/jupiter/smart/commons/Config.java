package com.si.jupiter.smart.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Author: lizhipeng
 * Date: 2017/01/22 10:13
 */
public class Config {
    private static Properties prop;

    static {
        init();
    }

    private static void init() {
        InputStream is = null;
        prop = new Properties();

        try {
            is = Config.class.getClassLoader().getResourceAsStream("system.properties");
            prop.load(is);
        } catch (Exception var4) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException var3) {
                    var3.printStackTrace();
                }
            }
        }

    }

    public static String getValue(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    public static String getValue(String key) {
        return prop.getProperty(key);
    }

    public static int getValue(String key, int defaultValue) {
        String v = prop.getProperty(key);
        if (v == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(v);
        }
    }
}
