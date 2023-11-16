package rohi.gateway.api_gateway.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig  {

    private JwtAuthConverter jwtAuthConverter;

    @Autowired
    public SecurityConfig(JwtAuthConverter jwtAuthConverter){
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity security){

        security.authorizeExchange(exchanges -> exchanges.pathMatchers(HttpMethod.GET).permitAll()
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .pathMatchers("/order/**", "/payment/**").hasRole("USER"))
                .oauth2ResourceServer(oauth -> oauth.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));

        security.csrf(csrf-> csrf.disable());
        return security.build();
    }

    /**
     * The purpose of returning a Mono here is to indicate that the conversion operation might be asynchronous.
     * Mono in this context signifies that the authentication process might involve asynchronous operations.
     * For instance, fetching user details, roles, or permissions could be asynchronous operations,
     * especially when dealing with remote databases or external services. By returning a Mono, you're
     * allowing these operations to be executed asynchronously without blocking threads, ensuring efficient
     * use of system resources, especially in a reactive environment.
     *
     * The ReactiveJwtAuthenticationConverterAdapter is used to adapt the synchronous JwtAuthenticationConverter
     * (which works with blocking code) into a reactive-friendly version that returns a
     * Mono<AbstractAuthenticationToken>.The adapter ReactiveJwtAuthenticationConverterAdapter allows the
     * synchronous logic to be integrated into the reactive flow seamlessly. It transforms the synchronous
     * result into a Mono to fit into the reactive programming model.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor(){

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtAuthConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }


}