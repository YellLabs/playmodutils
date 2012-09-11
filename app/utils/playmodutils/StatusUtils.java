package utils.playmodutils;

import models.playmodutils.StatusItem;

public class StatusUtils {

    public static StatusItem getDeps(){
        StatuItem deps = new StatuItem();
        deps.detected = true;
        deps.status = "OK";

        //Iterate dependencies and versions

        return deps;
    }

    public static StatusItem getAuthStatus(){
        //Lookup settings
        //Attempt user logon
        return null;
    }

    public static boolean usesAuth(){
        //Detect if playmodauthn is a dep
        return false;
    }

    public static StatusItem getEventsApiStatus(){
        //Lookup settings
        //Try to call eventsapi
        return null;
    }

    public static boolean usesEventsApi(){
        //Detect if eventsapiadspter is a dep
        return false;
    }

    public static StatusItem getPlacesApiStatus(){
        return null;
    }

    public static boolean usesPlacesApi(){
        return false;
    }

    public static StatusItem getMemcacheStatus(){
        return null;
    }

    public static boolean usesMemcache(){
        return false;
    }


    public static StatusItem getMongoStatus(){
        return null;
    }

    public static boolean usesMongo(){
        return false;
    }


    public static StatusItem getMySQLStatus(){
        return null;
    }

    public static boolean usesMySQL(){
        return false;
    }

}