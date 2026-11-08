package it.formazione.kafka.order;
import it.formazione.kafka.order.event.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class OrderProducer {
  private final KafkaTemplate<String,OrderEvent> template;
  public OrderProducer(KafkaTemplate<String,OrderEvent> template){this.template=template;}
  public void publish(OrderEvent event){
    template.send("orders-json",Long.toString(event.orderId()),event)
      .whenComplete((result,error)->{
        if(error!=null) System.err.println("Publish failed: "+error.getMessage());
        else System.out.printf("JSON event partition=%d offset=%d%n",result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
      });
  }
}
