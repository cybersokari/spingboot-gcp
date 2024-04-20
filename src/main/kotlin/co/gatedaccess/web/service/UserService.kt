package co.gatedaccess.web.service;

import co.gatedaccess.web.http.response.TokenBody;
import co.gatedaccess.web.data.model.Member;
import co.gatedaccess.web.data.repo.MemberRepo;
import co.gatedaccess.web.util.CodeGenerator;
import co.gatedaccess.web.util.CodeType;
import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.auth.openidconnect.IdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    MemberRepo memberRepo;

    @Autowired
    private CodeGenerator codeGenerator;

    @Value("${google.client.id}")
    String GOOGLE_CLIENT_ID;

    public ResponseEntity<?> getCustomTokenForClientLogin(String token, String provider) {

        try {
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            if (provider.equalsIgnoreCase("google")) {

                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                        // Specify the CLIENT_ID of the app that accesses the backend:
                        .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                        // Or, if multiple clients access the backend:
                        //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                        .build();

                GoogleIdToken googleIdToken = verifier.verify(token);
                if (googleIdToken != null) {
                    GoogleIdToken.Payload payload = googleIdToken.getPayload();

                    // Print user identifier
                    String googleUserId = payload.getSubject();

                    // Get profile information from payload
                    String email = payload.getEmail();
                    //boolean emailVerified = payload.getEmailVerified();
                    String name = (String) payload.get("name");
                    String photoUrl = (String) payload.get("picture");
                    //String locale = (String) payload.get("locale");
                    String familyName = (String) payload.get("family_name");
                    String givenName = (String) payload.get("given_name");

                    String userId;
                    if (memberRepo.existsMemberByEmail(email)) {
                        userId = memberRepo.findMemberById(email).getId();
                    } else {
                        Member member = new Member.Builder()
                                .withPhotoUrl(photoUrl)
                                .withFirstName(givenName)
                                .withLastName(familyName)
                                .withGoogleUserId(googleUserId)
                                .withEmailVerifiedAt(new Date())
                                .withEmail(email).build();
                        userId = memberRepo.save(member).getId();
                    }
                    String customFirebaseToken = FirebaseAuth.getInstance().createCustomToken(userId);
                    return ResponseEntity.ok(new TokenBody(customFirebaseToken));

                } else {
                    return ResponseEntity.badRequest().body("Invalid google token");
                }

            } else {

                IdTokenVerifier verifier = new IdTokenVerifier.Builder()
                        .setIssuer("https://appleid.apple.com")
                        .setAudience(Collections.singletonList("your_client_id")) // Replace with your client ID
                        .setIssuers(Collections.singletonList("https://appleid.apple.com"))
                        .build();

                IdToken idToken = IdToken.parse(jsonFactory, token);
                verifier.verify(idToken);

                //TODO: implement Apple Auth
                return ResponseEntity.badRequest().body(String.format("The %s provider is not supported at this time", provider));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }

    }

    public ResponseEntity<Optional<String>> getCommunityInviteCode(String userId){
        Member member = memberRepo.findMemberById(userId);

        if (member != null && member.getCommunity() != null){

            while (member.getInviteCode() == null){
                //Recursively try to update users invite code if the update fails due to possible duplicate
                try {
                    member.setInviteCode(codeGenerator.getCode(CodeType.community));
                    member = memberRepo.save(member);
                }catch (DuplicateKeyException ignore){}
            }
            return ResponseEntity.ok(Optional.of(member.getInviteCode()));
        }
        return ResponseEntity.badRequest().build();
    }

}
