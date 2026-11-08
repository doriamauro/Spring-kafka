package it.formazione.kafka.order;
import it.formazione.kafka.order.event.OrderEvent;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/orders")
public class OrderController {
  private final OrderProducer producer;
  public OrderController(OrderProducer producer){this.producer=producer;}
  @PostMapping("/{id}/publish") @ResponseStatus(HttpStatus.ACCEPTED)
  public String publish(@PathVariable long id){
    producer.publish(new OrderEvent(id,42L,new BigDecimal("79.90"),Instant.now()));
    return "Publish request accepted (async)";
  }
}
