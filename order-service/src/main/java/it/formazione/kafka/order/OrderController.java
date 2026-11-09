package it.formazione.kafka.order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/orders")
public class OrderController {
 private final OrderProducer producer;
 public OrderController(OrderProducer producer){this.producer=producer;}
 @PostMapping("/{id}/publish") @ResponseStatus(HttpStatus.ACCEPTED)
 public String publish(@PathVariable("id") long id){producer.publish(id);return "Avro publish request accepted (async)";}
}
