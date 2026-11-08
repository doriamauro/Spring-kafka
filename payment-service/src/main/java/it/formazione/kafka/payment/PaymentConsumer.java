package it.formazione.kafka.payment;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class PaymentConsumer {
  @KafkaListener(topics="orders-string")
  public void receive(ConsumerRecord<String,String> record){
    System.out.printf("Payment received key=%s value=%s partition=%d offset=%d%n",record.key(),record.value(),record.partition(),record.offset());
  }
}
