package co.gatedaccess.web;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class SecureInterceptor implements HandlerInterceptor {
    private static final Logger log = Logger.getLogger(SecureInterceptor.class.getSimpleName());


    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        String idToken = request.getHeader("Authorization");

        if (idToken != null) {
            try {
                idToken = idToken.split(" ")[1]; //Remove Bearer prefix

                // Running on dev, skip token decode
                if (request.getRequestURL().toString().contains("localhost:8080")) {
                    System.out.println("Bearer is: " + idToken);
                    request.setAttribute("user", idToken);
                    System.out.println("token is: " + idToken);
                    return true;
                }


                ApiFuture<FirebaseToken> tokenAsync = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken);
                FirebaseToken firebaseToken = tokenAsync.get(5, TimeUnit.SECONDS);
                request.setAttribute("user", firebaseToken.getUid());
                return true;
            } catch (Exception e) {
                log.info(e.getLocalizedMessage());
            }
        }

        response.setContentType("application/json");
        response.sendError(400, "Unauthorized user");
        return false;
    }
}
