package it.formazione.kafka.payment;
import it.formazione.kafka.payment.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class PaymentConsumer {
  @KafkaListener(topics="orders-json")
  public void receive(ConsumerRecord<String,OrderEvent> record){
    OrderEvent event=record.value();
    System.out.printf("JSON order=%d amount=%s partition=%d offset=%d%n",event.orderId(),event.amount(),record.partition(),record.offset());
  }
}
