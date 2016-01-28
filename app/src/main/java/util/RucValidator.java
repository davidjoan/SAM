package util;

/**
 * Created by David on 28/01/16.
 */
public class RucValidator {

    /**
     * @param ruc
     * @return
     */
    public static boolean validate(String ruc){

        boolean result = false;

        if(ruc.length() > 11){
            return  false;
        }

        int dig01 = Integer.valueOf(ruc.substring(0,1))*5;
        int dig02 = Integer.valueOf(ruc.substring(1,1))*4;
        int dig03 = Integer.valueOf(ruc.substring(2,1))*3;
        int dig04 = Integer.valueOf(ruc.substring(3,1))*2;
        int dig05 = Integer.valueOf(ruc.substring(4,1))*7;
        int dig06 = Integer.valueOf(ruc.substring(5,1))*6;
        int dig07 = Integer.valueOf(ruc.substring(6,1))*5;
        int dig08 = Integer.valueOf(ruc.substring(7,1))*4;
        int dig09 = Integer.valueOf(ruc.substring(8,1))*3;
        int dig10 = Integer.valueOf(ruc.substring(9,1))*2;
        int dig11 = Integer.valueOf(ruc.substring(10,1));

        int suma = dig01 + dig02 + dig03 + dig04 + dig05 + dig06 + dig07 + dig08 + dig09 + dig10;
        int residuo =suma%11;
        int resta = 11-residuo;

        int digChk = 0;
        if(resta == 10){
            digChk = 0;
        }else if(resta == 11){
            digChk = 1;
        }else{
            digChk = resta;
        }

        if(dig11 == digChk){
            result = true;
        }else{
            result = false;
        }

        return result;
    }
}
