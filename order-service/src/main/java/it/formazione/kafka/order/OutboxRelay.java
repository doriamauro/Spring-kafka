package it.formazione.kafka.order;
import it.formazione.kafka.contract.OrderEvent;
import it.formazione.kafka.order.db.OutboxEntry;
import it.formazione.kafka.order.db.OutboxRepository;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * Teaching-only single-instance poller.
 * May publish the same event twice after ack-before-mark crash: consumer must be idempotent.
 */
@Component
public class OutboxRelay {
 private final OutboxRepository outbox;
 private final OutboxMarkService marker;
 private final KafkaTemplate<String,OrderEvent> kafka;
 private final boolean crashAfterAck;
 public OutboxRelay(OutboxRepository outbox,OutboxMarkService marker,
                   KafkaTemplate<String,OrderEvent> kafka,
                   @Value("${lab.outbox.crash-after-ack:false}") boolean crashAfterAck){
  this.outbox=outbox;this.marker=marker;this.kafka=kafka;this.crashAfterAck=crashAfterAck;
 }
 @Scheduled(fixedDelayString="${lab.outbox.poll-delay-ms:3000}",initialDelay=3000)
 public void publishPending(){
   for(OutboxEntry entry : outbox.findTop20ByPublishedFalseOrderByIdAsc()){
     try{
       long id=entry.getOrderId();
       OrderEvent event=OrderEvent.newBuilder().setOrderId(id).setCustomerId(42L)
         .setAmount(79.90).setCurrency("EUR").setTimestamp(Instant.now().toString()).build();
       var result=kafka.send("orders-avro",Long.toString(id),event).get(10,TimeUnit.SECONDS);
       System.out.printf("OUTBOX Kafka acknowledged order=%d partition=%d offset=%d%n",
         id,result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
       if(crashAfterAck && id==911L){
         System.err.println("LAB11 deliberate crash AFTER Kafka ack, BEFORE outbox mark");
         System.err.flush();Runtime.getRuntime().halt(92);
       }
       marker.markPublished(entry.getId());
     }catch(Exception e){
       System.err.println("OUTBOX still pending id="+entry.getId()+" cause="+e.getMessage());
       break; // next scheduled poll tries again
     }
   }
 }
}
