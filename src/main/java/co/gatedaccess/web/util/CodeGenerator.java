package co.gatedaccess.web.util;

import java.security.SecureRandom;
import java.util.UUID;

public class CodeGenerator {
    private static final int CODE_LENGTH = 8; // Adjust length as needed

    public static String generateMemberInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, CODE_LENGTH); // Adjust length as needed
    }

    public static String generateVisitorAccessCode() {
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return sb.toString();
    }
}
