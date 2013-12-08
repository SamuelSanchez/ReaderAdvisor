package readerAdvisor.environment;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/15/13
 * Time: 11:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberUtils {

    public static double getDouble(String str){
        double value = 0;
        try{
            value = Double.parseDouble(str);
        }catch(Exception e){
            /* Do nothing */
        }
        return value;
    }

    public static int getInteger(String str){
        int value = 0;
        try{
            value = Integer.parseInt(str);
        }catch(Exception e){
            /* Do nothing */
        }
        return value;
    }

    public static Boolean getBoolean(String str){
         return Boolean.parseBoolean(str);
    }

    public static boolean isInteger(String text){
        boolean isInteger = false;
        try{
            Integer.parseInt(text);
            isInteger = true;
        }catch(Exception e){
            // Do nothing
        }
        return isInteger;
    }

    public static boolean isDouble(String text){
        boolean isDouble = false;
        try{
            Double.parseDouble(text);
            isDouble = true;
        }catch(Exception e){
            // Do nothing
        }
        return isDouble;
    }

    public static boolean isLong(String text){
        boolean isLong = false;
        try{
            Long.parseLong(text);
            isLong = true;
        }catch(Exception e){
            // Do nothing
        }
        return isLong;
    }
}
