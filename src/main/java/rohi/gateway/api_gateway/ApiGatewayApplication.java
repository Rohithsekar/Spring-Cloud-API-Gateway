package rohi.gateway.api_gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


import java.time.LocalDateTime;

/**
 * Spring Cloud Gateway is built on Spring Boot, Spring WebFlux, and Project Reactor. As a consequence,
 * many of the familiar synchronous libraries (Spring Data and Spring Security, for example)
 * and patterns you know may not apply when you use Spring Cloud Gateway. If you are unfamiliar
 * with these projects, we suggest you begin by reading their documentation to familiarize
 * yourself with some new concepts before working with Spring Cloud Gateway.
 *
 * Spring Cloud Gateway requires the Netty runtime provided by Spring Boot and Spring Webflux.
 * It does not work in a traditional Servlet Container or when built as a WAR.
 *
 * The Spring webflux dependency comes as part of the spring-cloud-starter-gateway dependency
 *
 *
 */

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	/** define a circuit breaker when you know there will be no response from the downstream microservice
	 * the RouteLocatorBuilder bean includes a fluent API*/

	@Bean
	public RouteLocator routeConfig(RouteLocatorBuilder routeLocatorBuilder) {

		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/order/**")
						.filters( f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("orderCircuitBreaker")
										.setFallbackUri("forward:/fallback/resilient")))
						.uri("http://localhost:9089")
				)
				.route(r -> r.path("/payment/**")
						.filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config
								.setName("paymentCircuitBreaker")
								.setFallbackUri("forward:/fallback/paymentfallback")))
						.uri("http://localhost:9090")
				)
				.route(r -> r.path("/admin/**")
						.filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("adminCircuitBreaker")
										.setFallbackUri("forward:/fallback/resilient")))
						.uri("http://localhost:9094")
				)
				.build();
	}




	/**
	 * One of the imperative architectural concerns is to protect APIs and service endpoints from
	 * harmful effects, such as denial of service, cascading failure. or overuse of resources.
	 * Rate limiting is a technique to control the rate by which an API or a service is consumed.
	 * In a distributed system, no better option exists than to centralize configuring and managing
	 * the rate at which consumers can interact with APIs. Only those requests within a defined rate
	 * would make it to the API. Anymore would raise an HTTP “Many requests” error.
	 *
	 *
	 * RequestRateLimiter is one of the many gateway filters offered by Spring Cloud Gateway.
	 * The implementation determines whether a request is allowed to proceed or has exceeded
	 * its limit. The implementation lets you (optionally) plug in a key to manage limiting
	 * the number of requests to different services. While it is customizable to implement how
	 * to resolve a key, the gateway comes with one that uses a user’s Principal name.
	 * A secured gateway is needed to resolve a user’s principal name, but you have the option
	 * to implement the KeyResolver interface to instead resolve a different key from the
	 * ServerWebExchange. You can point to a custom KeyResolver bean
	 * (for example, named customKeyResolver) in the configuration by using a
	 * SPEL #{@customKeyResolver} expression. The following listing shows the KeyResolver interface:
	 *
	 * public interface KeyResolver {
	 *     Mono<String> resolve(ServerWebExchange exchange);
	 * }
	 * The gateway would deny requests if no key was resolved. To let the gateway accept a missing
	 * resolved key, you can set the following property:
	 *
	 * spring.cloud.gateway.filter.request-rate-limiter.deny-empty-key=false
	 * You can also specify a status code that the gateway should report when it could not figure
	 * out a key by setting the following property:
	 *
	 * spring.cloud.gateway.filter.request-rate-limiter.empty-key-status-code=
	 *
	 * Consider a blueprint architecture in which a gateway controls limiting API consumptions by
	 * using Redis. The provided Redis implementation uses the Token Bucket algorithm.
	 * To use it, you need to include the spring-boot-starter-data-redis Spring Boot starter dependency
	 * in the gateway application. Basically, the token bucket algorithm uses balance tokens as a
	 * means to maintain an accumulating budget of utilization. The algorithm assumes tokens will
	 * be added to a bucket at a certain rate while calls to an API consume from these tokens.
	 * One API invocation may perform many operations to compose a response in order to fulfill
	 * a request (think of GraphQL-based APIs). In such cases, the algorithm helps to recognize
	 * that one invocation may cost an API more than one token.
	 *
	 * The provided Redis implementation lets you define the request rate at which users can make
	 * calls within a certain time period. It also makes it possible to accommodate sporadic
	 * demands while constrained by the defined consumption rate. For example, a configuration
	 * can define a replenish rate of 500 requests per second by setting the
	 * redis-rate-limiter.replenishRate=500 property and a
	 * burst capacity of 1000 request per second by setting the
	 * redis-rate-limiter.burstCapacity=1000 property.
	 * Doing so limits consumption to 500 requests every second.
	 * If a burst in the number of requests occurs, only 1,000 requests are allowed.
	 * However, because 1,000 requests are a quota of 2 seconds, the gateway would not route
	 * requests for the next second. The configuration also lets you define how many tokens a
	 * request would cost by setting the property redis-rate-limiter.requestedTokens property.
	 * Typically, it is set to 1.
	 *
	 * To use a gateway with a request limiting feature, it needs to be configured with
	 * the RequestRateLimiter gateway filter. The configuration can specify arguments
	 * to define a replenish rate, burst capacity, and the number of tokens that a request costs.
	 * The example below illustrates how to configure a gateway with these arguments:
	 *
	 * spring:
	 *   cloud:
	 *     gateway:
	 *       routes:
	 *         - id: route1
	 *           uri: http://localhost:8081
	 *           predicates:
	 *             - Path=/backend
	 *           filters:
	 *           - name: RequestRateLimiter
	 *             args:
	 *               redis-rate-limiter.replenishRate: 500
	 *               redis-rate-limiter.burstCapacity: 1000
	 *               redis-rate-limiter.requestedTokens: 1
	 *
	 * Spring cloud gateway provides the flexibility to define your own custom rate
	 * limiter implementation. It offers a RateLimiter interface to implement and
	 * define a bean. The rate limiter bean can be configured by using a SPEL expression,
	 * as in the case of a custom key resolver. For instance, you can define a custom rate
	 * limiter bean named customRateLimiter and a custom key resolver named customKeyResolver
	 * and configure a route like this:
	 *
	 * @Bean
	 * public KeyResolver customKeyResolver {
	 * 	return exchange -> ....  // returns a Mono of String
	 * }
	 * spring:
	 *   cloud:
	 *     gateway:
	 *       routes:
	 *         - id: route1
	 *           uri: http://localhost:8081
	 *           predicates:
	 *             - Path=/backend
	 *           filters:
	 *           - name: RequestRateLimiter
	 *             args:
	 *               rate-limiter: "#{customRateLimiter}"
	 *               key-resolver: "#{customKeyResolver}"
	 */




	}