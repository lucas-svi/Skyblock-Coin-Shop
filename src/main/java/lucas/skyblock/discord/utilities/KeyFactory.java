package lucas.skyblock.discord.utilities;

import java.util.concurrent.ThreadLocalRandom;

public class KeyFactory {

    private static final String PREFIX = "sb-";
    private static final char[] alphabet = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};

    public static String generateKey(){
        StringBuilder random = new StringBuilder();

        for (int j = 0; j < 15; j++) {
            random.append(alphabet[ThreadLocalRandom.current().nextInt(alphabet.length)]);
        }

        return PREFIX + random.toString();
    }

}