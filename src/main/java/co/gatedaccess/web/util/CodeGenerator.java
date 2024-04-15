package co.gatedaccess.web.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import java.security.SecureRandom;
import java.util.UUID;

public class CodeGenerator {

    private final CodeType type;

    Environment environment = new AnnotationConfigApplicationContext().getEnvironment();

    public CodeGenerator(CodeType type) {
        this.type = type;
    }

    /**
     * Returns a numeric string with a specified length
     *
     * @return code
     */
    public String getCode() {
        final int CODE_LENGTH;
        switch (type) {
            case guard -> {
                CODE_LENGTH = environment.getRequiredProperty("security-guard.otp.length", Integer.class);
                return UUID.randomUUID().toString().replace("-", "").substring(0, CODE_LENGTH); // Adjust length as needed
            }
            case visitor -> {
                CODE_LENGTH = environment.getRequiredProperty("visitor.access-code.length", Integer.class);
                StringBuilder sb = new StringBuilder();
                SecureRandom random = new SecureRandom();
                for (int i = 0; i < CODE_LENGTH; i++) {
                    sb.append(random.nextInt(10)); // Append a random digit (0-9)
                }
                return sb.toString();
            }
            case community -> {
                CODE_LENGTH = environment.getRequiredProperty("community.invite-code.length", Integer.class);
                return UUID.randomUUID().toString().replace("-", "").substring(0, CODE_LENGTH); // Adjust length as needed
            }
            default -> {
                return "";
            }
        }
    }
}
