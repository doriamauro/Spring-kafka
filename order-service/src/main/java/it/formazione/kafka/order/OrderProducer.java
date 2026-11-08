package it.formazione.kafka.order;
import it.formazione.kafka.contract.OrderEvent;
import java.time.Instant;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class OrderProducer {
 private final KafkaTemplate<String,OrderEvent> template;
 public OrderProducer(KafkaTemplate<String,OrderEvent> template){this.template=template;}
 public void publish(long id){
   OrderEvent event=OrderEvent.newBuilder().setOrderId(id).setCustomerId(42L).setAmount(79.90).setTimestamp(Instant.now().toString()).setCurrency("EUR").build();
   template.send("orders-avro",Long.toString(id),event).whenComplete((result,error)->{
     if(error!=null) System.err.println("AVRO publish error: "+error.getMessage());
     else System.out.printf("AVRO published partition=%d offset=%d%n",result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
   });
 }
}
