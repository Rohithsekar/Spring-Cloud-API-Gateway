package rohi.gateway.api_gateway;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
Both spring-cloud-starter-circuitbreaker-reactor-resilience4j and spring-cloud-starter-netflix-hystrix dependencies need
to be present in your project in order to achieve circuit-breaker and fallback meachanism.

The reason for this is the conditional auto-configuration performed by Spring. The
GatewayCircuitBreakerAutoConfiguration class, which is responsible for configuring the Spring Cloud CircuitBreaker
filter, uses the @ConditionalOnClass annotation to check for the presence of certain classes. In this case, it checks
for the presence of DispatcherHandler (a class from Spring WebFlux),
ReactiveResilience4JAutoConfiguration (from spring-cloud-starter-circuitbreaker-reactor-resilience4j), and
HystrixCircuitBreakerAutoConfiguration (from spring-cloud-starter-netflix-hystrix).

Since the @ConditionalOnClass annotation specifies an "and" relationship between the classes, both dependencies need to
be present for the auto-configuration to take place. Including both dependencies in your pom.xml file ensures that the
required classes are available for the auto-configuration to work properly.

To summarize, adding both spring-cloud-starter-circuitbreaker-reactor-resilience4j and
spring-cloud-starter-netflix-hystrix dependencies to your project resolves the issue by providing the necessary classes
for the auto-configuration of the Spring Cloud CircuitBreaker filter.
 */

@RestController
public class FallbackController {

    public static final String GATEWAY_SERVICE = "GATEWAY-SERVICE";


    @RequestMapping("/orderFallBack")
//    @CircuitBreaker(name = GATEWAY_SERVICE, fallbackMethod = "orderServiceFallBack")
    public String orderServiceFallBack(){
        return "Order service is taking too long to respond or is down. Please try again later";
    }

    @RequestMapping("/paymentFallBack")
//    @CircuitBreaker(name = GATEWAY_SERVICE, fallbackMethod = "paymentServiceFallBack")
    public String paymentServiceFallBack(){
        return "Payment service is taking too long to respond or is down.Please try again later";
    }


}
