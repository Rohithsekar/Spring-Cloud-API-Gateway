package rohi.gateway.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class JwtAuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

//    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=
//            new JwtGrantedAuthoritiesConverter();
//
//    @Value("${jwt.auth.converter.principal-attribute}")
//    private String principalAttribute;
//
//    @Value("${jwt.auth.converter.resource-id}")
//    private String resourceId;

//    @Override
//    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
//        Collection<GrantedAuthority>  authorities = Stream.concat(
//                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
//                extractResourceRoles(jwt).stream()
//        ).collect(Collectors.toSet());
//
//        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
//    }

//    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt){
//        Map<String, Object> resourceAccess;
//        Map<String, Object> resource;
//        Collection<String> resourceRoles;
//
//        if(jwt.getClaim("resource_access") == null){
//            return Set.of();
//        }
//
//        resourceAccess = jwt.getClaim("resource_access");
//        if(resourceAccess.get(resourceId)== null){
//            return Set.of();
//        }
//
//        resource = (Map<String, Object>) resourceAccess.get(resourceId);
//        resourceRoles = (Collection<String>) resource.get("roles");
//
//
//        return resourceRoles.stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                .collect(Collectors.toSet());
//    }
//
//    private String getPrincipalClaimName(Jwt jwt){
//        String claimName = JwtClaimNames.SUB;
//        if(principalAttribute!= null){
//            claimName = principalAttribute;
//        }
//
//        return jwt.getClaim(claimName);
//    }

    /*
    To parse this part of jwtToken:
    "realm_access": {
    "roles": [
      "default-roles-rohi_realm",
      "offline_access",
      "ADMIN",
      "uma_authorization"
    ]

    we need the below method:
  }
     */

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        System.out.println("jwt is " + source);
        Map<String, Object> realmAccess = (Map<String, Object>)source.getClaims().get("realm_access");
        if(realmAccess == null || realmAccess.isEmpty()){
            return new HashSet<>();
        }

        Collection<GrantedAuthority> returnValue =  ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        System.out.println("Granted authorities are " + Arrays.toString(returnValue.toArray()));
        return returnValue;
    }
}


