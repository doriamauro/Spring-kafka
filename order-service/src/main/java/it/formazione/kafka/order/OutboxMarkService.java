package it.formazione.kafka.order;
import it.formazione.kafka.order.db.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class OutboxMarkService {
 private final OutboxRepository outbox;
 public OutboxMarkService(OutboxRepository outbox){this.outbox=outbox;}
 @Transactional
 public void markPublished(Long id){
   var row=outbox.findById(id).orElseThrow();
   row.markPublished();
 }
}
