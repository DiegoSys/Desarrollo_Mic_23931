package ec.edu.espe.plantillaEspe.config.security;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;



@Service
public class UserInfoService {
    private final RestTemplate restTemplate;

    @Autowired
    public UserInfoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${spring.user}")
    private String userInfo;

    public Map<String, Object> getUserInfo(String accessToken) {
        String url = "https://srvcas.espe.edu.ec/oauth2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        System.out.println("irl:" + userInfo);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map body = response.getBody();
            //return ((Number) body.get("PIDM")).longValue();
            System.out.println("respuesta : "+ response.getBody());
        } else {
            throw new RuntimeException("Failed to get PIDM from user information API");
        }


        return  response.getBody();
    }

}
