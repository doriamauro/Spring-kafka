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
    System.out.printf("AVRO order=%d amount=%s partition=%d offset=%d%n",event.getOrderId(),event.getAmount(),record.partition(),record.offset());
  }
}
