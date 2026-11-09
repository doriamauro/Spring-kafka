package it.formazione.kafka.order;
import it.formazione.kafka.order.db.OutboxRepository;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/orders")
public class OrderController {
 private final OrderProducer directProducer;
 private final OrderCreationService orders;
 private final OutboxRepository outbox;
 public OrderController(OrderProducer directProducer,OrderCreationService orders,OutboxRepository outbox){
  this.directProducer=directProducer;this.orders=orders;this.outbox=outbox;
 }
 /** Legacy path: direct, not atomic with DB. Used in labs 01-10. */
 @PostMapping("/{id}/publish") @ResponseStatus(HttpStatus.ACCEPTED)
 public String publish(@PathVariable("id") long id){
   directProducer.publish(id);return "Direct Kafka publish accepted (NOT transactional outbox)";
 }
 /** New path: order + outbox row atomically committed in H2. */
 @PostMapping("/{id}/create") @ResponseStatus(HttpStatus.CREATED)
 public Map<String,Object> create(@PathVariable("id") long id){
   orders.create(id);return Map.of("orderId",id,"outbox","pending");
 }
 @GetMapping("/{id}/outbox")
 public Object state(@PathVariable("id") long id){
   return outbox.findByOrderIdOrderByIdAsc(id).stream()
    .map(row->Map.of("rowId",row.getId(),"orderId",row.getOrderId(),"published",row.isPublished())).toList();
 }
}
