package it.formazione.kafka.order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class OrderProducer {
  private final KafkaTemplate<String,String> template;
  public OrderProducer(KafkaTemplate<String,String> template){this.template=template;}
  public void publish(long id){
    template.send("orders-string",Long.toString(id),"ORDER_CREATED:"+id)
      .whenComplete((result,error)->{
        if(error!=null){System.err.println("Kafka publish failed: "+error.getMessage());}
        else{System.out.printf("Published partition=%d offset=%d%n",result.getRecordMetadata().partition(),result.getRecordMetadata().offset());}
      });
  }
}
