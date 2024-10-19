package ru.gavrilovegor519.tasks_user.keycloak;

import jakarta.ws.rs.core.Response;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakAdminClientService {
    @Value("${keycloak.auth-server-url}")
    private String serverURL;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientID;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final KeycloakProvider kcProvider;

    public KeycloakAdminClientService(KeycloakProvider kcProvider) {
        this.kcProvider = kcProvider;
    }

    public Response createKeycloakUser(String email, String password) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(email);
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(email);
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        return usersResource.create(kcUser);
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public JsonNode refreshToken(String refreshToken) {
        String url = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";
        return Unirest.post(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", clientID)
                .field("client_secret", clientSecret)
                .field("refresh_token", refreshToken)
                .field("grant_type", "refresh_token")
                .asJson().getBody();
    }
}

