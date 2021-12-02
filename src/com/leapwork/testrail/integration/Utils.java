package com.leapwork.testrail.integration;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class Utils {

    public static String defaultStringIfNull(JsonElement jsonElement)
    {

        if(jsonElement != null)
            return jsonElement.getAsString();
        else
            return "";
    }

    public static String defaultStringIfNull(JsonElement jsonElement, String defaultValue)
    {

        if(jsonElement != null)
            return jsonElement.getAsString();
        else
            return defaultValue;
    }

    public static Integer defaultIntegerIfNull(JsonElement jsonElement, Integer defaultValue)
    {

        if(jsonElement != null && jsonElement != JsonNull.INSTANCE)
            return jsonElement.getAsInt();
        else
            return defaultValue;
    }

    public static int defaultIntegerIfNull(JsonElement jsonElement)
    {

        if(jsonElement != null && jsonElement != JsonNull.INSTANCE)
            return jsonElement.getAsInt();
        else
            return 0;
    }
}
