package it.formazione.kafka.payment;
import it.formazione.kafka.contract.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class PaymentConsumer {
  @KafkaListener(topics="orders-avro")
  public void receive(ConsumerRecord<String,OrderEvent> record) {
    OrderEvent event=record.value();
    if(event.getAmount()<0) throw new IllegalArgumentException("Invalid amount: "+event.getAmount());
    if(event.getOrderId()==500L) throw new IllegalStateException("Simulated temporary provider failure");
    System.out.printf("PAYMENT OK order=%d amount=%s currency=%s partition=%d offset=%d%n",
      event.getOrderId(),event.getAmount(),event.getCurrency(),record.partition(),record.offset());
  }
}
