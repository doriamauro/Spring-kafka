package it.formazione.kafka.payment;
import it.formazione.kafka.contract.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class PaymentConsumer {
  private final PaymentProcessor processor;
  private final boolean crashAfterSave;
  public PaymentConsumer(PaymentProcessor processor,
      @Value("${lab.crash-after-save:false}") boolean crashAfterSave){
    this.processor=processor;this.crashAfterSave=crashAfterSave;
  }
  @KafkaListener(topics="orders-avro")
  public void receive(ConsumerRecord<String,OrderEvent> record){
    OrderEvent event=record.value();
    if(event.getAmount()<0) throw new IllegalArgumentException("Negative amount");
    if(event.getOrderId()==500L) throw new IllegalStateException("Simulated provider error");
    boolean created=processor.process(event.getOrderId());
    if(created && crashAfterSave && event.getOrderId()==909L){
      System.err.println("LAB10 deliberate crash after DB commit, before Kafka offset commit");
      System.err.flush();
      Runtime.getRuntime().halt(91);
    }
  }
}
