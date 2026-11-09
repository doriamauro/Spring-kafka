package it.formazione.kafka.order;
import it.formazione.kafka.order.db.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest @Import(OrderCreationService.class)
class OrderCreationServiceTest {
 @Autowired OrderCreationService service;
 @Autowired StoredOrderRepository orders;
 @Autowired OutboxRepository outbox;
 @Test void orderAndOutboxWrittenTogether(){
   service.create(123456L);
   assertTrue(orders.existsById(123456L));
   assertEquals(1,outbox.findByOrderIdOrderByIdAsc(123456L).size());
   assertFalse(outbox.findByOrderIdOrderByIdAsc(123456L).get(0).isPublished());
 }
}
