//package rohi.gateway.api_gateway;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.ratelimiter.RateLimiter;
//import io.github.resilience4j.retry.Retry;
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import io.github.resilience4j.retry.RetryConfig;
//import io.github.resilience4j.retry.RetryRegistry;
//import io.github.resilience4j.ratelimiter.RateLimiterConfig;
//import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//@Configuration
//public class ResilienceConfig {
//
//
//
//    @Bean
//    public RateLimiter rateLimiter() {
//        RateLimiterConfig config = RateLimiterConfig.custom()
//                .limitForPeriod(10)
//                .limitRefreshPeriod(Duration.ofSeconds(1))
//                .timeoutDuration(Duration.ZERO)
//                .build();
//        return RateLimiter.of("customRateLimiter", config);
//    }
//
//
//    @Bean
//    public Retry retry() {
//        RetryConfig config = RetryConfig.custom()
//                .maxAttempts(3)
//                .waitDuration(Duration.ofSeconds(10))
//                .build();
//        return Retry.of("customRetry", config);
//    }
//
//    @Bean
//    public CircuitBreaker circuitBreaker() {
//        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
//                .slidingWindowSize(10)
//                .permittedNumberOfCallsInHalfOpenState(2)
//                .waitDurationInOpenState(Duration.ofSeconds(5))
//                .failureRateThreshold(50)
//                .build();
//        return CircuitBreaker.of("customCircuitBreaker", config);
//    }
//}
