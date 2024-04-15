package co.gatedaccess.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;


public class CodeGenerator {

    private final CodeType type;

    public CodeGenerator(CodeType type) {
        this.type = type;
    }

    /**
     * Returns a numeric string with a specified length
     * @return code
     */
    public String getCode(){
        Environment environment = new StandardEnvironment();
        final int CODE_LENGTH;
        switch (type){
            case guard -> {
                CODE_LENGTH = Integer.parseInt(environment.getRequiredProperty("security-guard.otp.length"));
                return UUID.randomUUID().toString().replace("-", "").substring(0, CODE_LENGTH); // Adjust length as needed
            }
            case visitor -> {
                CODE_LENGTH = Integer.parseInt(environment.getRequiredProperty("visitor.access-code.length"));
                StringBuilder sb = new StringBuilder();
                SecureRandom random = new SecureRandom();
                for (int i = 0; i < CODE_LENGTH; i++) {
                    sb.append(random.nextInt(10)); // Append a random digit (0-9)
                }
                return sb.toString();
            }
            case community -> {
                CODE_LENGTH = Integer.parseInt(environment.getRequiredProperty("community.invite-code.length"));
                return UUID.randomUUID().toString().replace("-", "").substring(0, CODE_LENGTH); // Adjust length as needed
            }
            default -> {
                return "";
            }
        }
    }
}
