package rohi.gateway.api_gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @RequestMapping("/resilient")
    public Mono<String> orderFallBack() {
        return Mono.just("An error occurred. Please try after some time or contact support team!!!");
    }

    @RequestMapping("/paymentfallback")
    public Mono<String> paymentFallBack() {
        return Mono.just("An error occurred in the payment service. Please try after some time or contact support team!!!");
    }

}
