package ec.edu.espe.plantillaEspe.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private final OAuth2ResourceServerProperties.Opaquetoken opaqueTokenProps;
    private final RestTemplateBuilder builder;


    CustomOpaqueTokenIntrospector(RestTemplateBuilder builder, OAuth2ResourceServerProperties resourceServerProps) {
        this.opaqueTokenProps = resourceServerProps.getOpaquetoken();
        this.builder = builder;
    }


    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        RestTemplate restTemplate = new RestTemplate();
        UserInfoService userInfoService = new UserInfoService(restTemplate);
        RestOperations restOperations = builder
                .defaultHeader("Authorization", "Bearer " + token)
                .setConnectTimeout(Duration.ofSeconds(60))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
        OAuth2AuthenticatedPrincipal principal;
        principal = new NimbusOpaqueTokenIntrospector(opaqueTokenProps.getIntrospectionUri(), restOperations)
                .introspect(token);
        Map<String, Object> roles = userInfoService.getUserInfo(token);
        System.out.println(roles.get("groups"));


        Collection<GrantedAuthority> authorities1 = convertRoles(roles.get("groups").toString());
        for (GrantedAuthority authority : authorities1) {
            System.out.println(authority.getAuthority());
        }

        Set<GrantedAuthority> authorities = new HashSet<>(principal.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        for (GrantedAuthority authority : authorities) {
            System.out.println(authority.getAuthority());
        }


        return new OAuth2IntrospectionAuthenticatedPrincipal(principal.getAttributes(), authorities1);
    }

    public Collection<GrantedAuthority> convertRoles(String roles) {
        // Separar la cadena en roles individuales
        String[] rolesArray = roles.split(",");

        // Crear una colección para almacenar los GrantedAuthority
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Convertir cada rol a SimpleGrantedAuthority y agregarlo a la colección
        for (String role : rolesArray) {
            authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.trim())));
        }

        return authorities;
    }

}
