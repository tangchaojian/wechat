package com.tcj.sunshine.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Sharedpreferences 存取
 */
public class PreferenceUtils {

    private static PreferenceUtils INSTANCE;
    private SharedPreferences preferences;
    private PreferenceUtils(){}

    public static PreferenceUtils getInstance(){
        if(INSTANCE == null) {
            INSTANCE = new PreferenceUtils();
        }

        return INSTANCE;
    }

    public void init(Context context){
        preferences = context.getSharedPreferences("SUNSHINE", Context.MODE_PRIVATE);
    }

    public static <T> void put(String key, T t) {
        if(INSTANCE != null && INSTANCE.preferences != null) {
            SharedPreferences.Editor editor = INSTANCE.preferences.edit();
            if(t instanceof String) {
                editor.putString(key, (String) t);
            }else if(t instanceof Integer) {
                editor.putInt(key, (Integer) t);
            }else if(t instanceof Long) {
                editor.putLong(key, (Long) t);
            }else if(t instanceof Float) {
                editor.putFloat(key, (Float) t);
            }else if(t instanceof Boolean) {
                editor.putBoolean(key, (Boolean) t);
            }
            editor.commit();
        }
    }


    public static <T> void batchPut(String key, T t) {
        if(INSTANCE != null && INSTANCE.preferences != null) {
            SharedPreferences.Editor editor = INSTANCE.preferences.edit();
            if(t instanceof String) {
                editor.putString(key, (String) t);
            }else if(t instanceof Integer) {
                editor.putInt(key, (Integer) t);
            }else if(t instanceof Long) {
                editor.putLong(key, (Long) t);
            }else if(t instanceof Float) {
                editor.putFloat(key, (Float) t);
            }else if(t instanceof Boolean) {
                editor.putBoolean(key, (Boolean) t);
            }
        }
    }

    public static void commit(){
        if(INSTANCE != null && INSTANCE.preferences != null) {
            SharedPreferences.Editor editor = INSTANCE.preferences.edit();
            editor.commit();
        }
    }

    public static <T> T get(String key, Class<T> clzz) {
        if(INSTANCE != null && INSTANCE.preferences != null) {

            if(clzz == String.class) {
                return (T)INSTANCE.preferences.getString(key, "");
            }else if(clzz == Integer.class) {
                return (T)(Integer)INSTANCE.preferences.getInt(key, 0);
            }else if(clzz == Long.class) {
                return (T)(Long)INSTANCE.preferences.getLong(key, 0L);
            }else if(clzz == Float.class) {
                return (T)(Float)INSTANCE.preferences.getFloat(key, 0f);
            }else if(clzz == Boolean.class) {
                return (T)(Boolean)INSTANCE.preferences.getBoolean(key, false);
            }
        }
        return null;
    }

    public static String getString(String key){
        if(INSTANCE != null && INSTANCE.preferences != null) {
            return INSTANCE.preferences.getString(key, "");
        }
        return "";
    }

    public static int getInt(String key) {
        if(INSTANCE != null && INSTANCE.preferences != null) {
            return INSTANCE.preferences.getInt(key, 0);
        }
        return 0;
    }

    public static long getLong(String key) {
        if(INSTANCE != null && INSTANCE.preferences != null) {
            return INSTANCE.preferences.getLong(key, 0L);
        }
        return 0L;
    }

    public static float getFloat(String key) {
        if(INSTANCE != null && INSTANCE.preferences != null) {
            return INSTANCE.preferences.getFloat(key, 0f);
        }
        return 0f;
    }

    public static boolean getBoolean(String key) {
        if(INSTANCE != null && INSTANCE.preferences != null) {
            return INSTANCE.preferences.getBoolean(key, false);
        }
        return false;
    }

    public static boolean getBoolean(String key,boolean def) {
        if(INSTANCE != null && INSTANCE.preferences != null) {
            return INSTANCE.preferences.getBoolean(key, def);
        }
        return false;
    }
}