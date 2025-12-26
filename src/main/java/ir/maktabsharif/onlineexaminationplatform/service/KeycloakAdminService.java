package ir.maktabsharif.onlineexaminationplatform.service;

import ir.maktabsharif.onlineexaminationplatform.dto.RegisterReq;
import ir.maktabsharif.onlineexaminationplatform.exception.DoubleUsernameException;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;

    private final String serverUrl = "http://localhost:9090";
    private final String realm = "OEP";
    private final String clientId = "admin-cli";
    private final String adminUsername = "admin";
    private final String adminPassword = "admin";

    @Autowired
    public KeycloakAdminService(@Lazy UserService userService) {
        this.userService = userService;
    }

    private String getAdminToken() {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        serverUrl + "/realms/master/protocol/openid-connect/token",
                        new HttpEntity<>(form, headers),
                        Map.class
                );

        return (String) response.getBody().get("access_token");
    }
    public void updateClientRole(String keycloakUserId,String oldRole,String newRole) {

        String token = getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<List> clients =
                restTemplate.exchange(
                        serverUrl + "/admin/realms/" + realm + "/clients?clientId=spring-backend",
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        List.class
                );

        Map client = (Map) clients.getBody().get(0);
        String clientUuid = (String) client.get("id");

        ResponseEntity<Map> roleResp =
                restTemplate.exchange(
                        serverUrl + "/admin/realms/" + realm + "/clients/" + clientUuid + "/roles/" + newRole,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        Map.class
                );
        ResponseEntity<Map> oldRoleResp =
                restTemplate.exchange(
                        serverUrl + "/admin/realms/" + realm + "/clients/" + clientUuid + "/roles/" + oldRole,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        Map.class
                );

        Map role = roleResp.getBody();
        Map oldRoleRespBody = oldRoleResp.getBody();

        restTemplate.exchange(
                serverUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId
                        + "/role-mappings/clients/" + clientUuid,
                HttpMethod.DELETE,
                new HttpEntity<>(List.of(oldRoleRespBody), headers),
                Void.class
        );

        restTemplate.exchange(
                serverUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId
                        + "/role-mappings/clients/" + clientUuid,
                HttpMethod.POST,
                new HttpEntity<>(List.of(role), headers),
                Void.class
        );
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createUser(RegisterReq dto) {
        try {
            userService.findByUsername(dto.username());
        }catch (UsernameNotFoundException e) {

            String token = getAdminToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> user = new HashMap<>();
            user.put("username", dto.username());
            user.put("email", dto.email());
            user.put("enabled", true);
            user.put("emailVerified", true);

            user.put("credentials", List.of(
                    Map.of(
                            "type", "password",
                            "value", dto.password(),
                            "temporary", false
                    )
            ));

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(user, headers);

            ResponseEntity<Void> response =
                    restTemplate.postForEntity(
                            serverUrl + "/admin/realms/" + realm + "/users",
                            entity,
                            Void.class
                    );

            String location = response.getHeaders().getLocation().toString();
            return location.substring(location.lastIndexOf("/") + 1);
        }
        throw new DoubleUsernameException();
    }

    public void deleteUser(String keycloakId) {

        String token = getAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        restTemplate.exchange(
                serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
    }


}