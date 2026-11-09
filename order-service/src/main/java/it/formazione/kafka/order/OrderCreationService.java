package it.formazione.kafka.order;
import it.formazione.kafka.order.db.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** Transactional outbox: order and publish intent are stored atomically in ONE database. */
@Service
public class OrderCreationService {
 private final StoredOrderRepository orders;
 private final OutboxRepository outbox;
 public OrderCreationService(StoredOrderRepository orders,OutboxRepository outbox){
  this.orders=orders;this.outbox=outbox;
 }
 @Transactional
 public void create(long id){
   if(orders.existsById(id)) throw new IllegalArgumentException("Order already exists: "+id);
   orders.save(new StoredOrder(id));
   outbox.save(new OutboxEntry(id));
 }
}
