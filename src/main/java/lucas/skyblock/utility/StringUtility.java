package lucas.skyblock.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StringUtility {

    public static String fancyNumber(Number number){
        double doubleValue = number.doubleValue();

        if(doubleValue == 0){
            return String.valueOf(doubleValue);
        }

        String plainString = new BigDecimal(doubleValue).toPlainString();
        return plainString.substring(0, Math.min(10, plainString.length() - 1));
    }

    public static String verboseNumber(Number number){
        long intValue = number.longValue();

        if(intValue == 0){
            return String.valueOf(intValue);
        }

        String string = String.valueOf(intValue);

        List<Character> characterList = new ArrayList<>();

        for (char c : string.toCharArray()) {
            characterList.add(c);
        }

        Collections.reverse(characterList);

        int c = 0;
        List<Character> newCharacters = new ArrayList<>();

        for (Character character : characterList) {
            c++;
            newCharacters.add(character);
            if(c % 3 == 0 && c != characterList.size()){
                newCharacters.add('.');
            }
        }

        Collections.reverse(newCharacters);

        StringBuilder builder = new StringBuilder();

        for (Character character : newCharacters) {
            builder.append(character);
        }

        return builder.toString();
    }

}
