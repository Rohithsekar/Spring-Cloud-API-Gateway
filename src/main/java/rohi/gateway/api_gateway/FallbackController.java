package rohi.gateway.api_gateway;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
