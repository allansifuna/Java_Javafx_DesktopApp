package ml.allansifuna.validations;

import java.util.UUID;
import java.util.regex.Pattern;

public class Validations {
    public static boolean validateEmail(String email){
        boolean b = Pattern.matches("[a-z1-9]*@[a-z]*.[a-z]{2}.[a-z]{2}", email);
        boolean d = Pattern.matches("[a-z1-9]*@[a-z]*.[a-z]{3}", email);
        return (b||d)&& email.contains(".");
    }
    public static boolean validateNatId(String id) {
        return Pattern.matches("[0-9]{8}", id);
    }
    public static boolean validateRent(String rent) {
        return Pattern.matches("[0-9]*", rent);
    }

}
