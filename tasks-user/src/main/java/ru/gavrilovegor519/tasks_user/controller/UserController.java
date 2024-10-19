package ru.gavrilovegor519.tasks_user.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import kong.unirest.core.JsonNode;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gavrilovegor519.tasks_user.dto.input.LoginDto;
import ru.gavrilovegor519.tasks_user.dto.input.RefreshDto;
import ru.gavrilovegor519.tasks_user.dto.input.RegDto;
import ru.gavrilovegor519.tasks_user.keycloak.KeycloakAdminClientService;
import ru.gavrilovegor519.tasks_user.keycloak.KeycloakProvider;

@RestController
@RequestMapping("/user")
public class UserController {
    private final KeycloakAdminClientService kcAdminClient;

    private final KeycloakProvider kcProvider;

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(UserController.class);

    public UserController(KeycloakAdminClientService kcAdminClient, KeycloakProvider kcProvider) {
        this.kcProvider = kcProvider;
        this.kcAdminClient = kcAdminClient;
    }

    @PostMapping(value = "/reg")
    public ResponseEntity<String> createUser(@RequestBody @Valid RegDto user) {
        try (Response createdResponse = kcAdminClient.createKeycloakUser(user.getEmail(), user.getPassword())) {
            return ResponseEntity.status(createdResponse.getStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createdResponse.readEntity(String.class));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody @Valid LoginDto loginRequest) {
        AccessTokenResponse accessTokenResponse = null;
        try (Keycloak keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(
                loginRequest.getEmail(), loginRequest.getPassword()).build()) {
            accessTokenResponse = keycloak.tokenManager().getAccessToken();
            return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse);
        } catch (BadRequestException ex) {
            LOG.warn("invalid account. User probably hasn't verified email.", ex);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);
        }
    }

    @PostMapping("/refresh")
    public JsonNode refresh(@RequestBody @Valid RefreshDto refreshDto) {
        return kcAdminClient.refreshToken(refreshDto.getRefreshToken());
    }

}
