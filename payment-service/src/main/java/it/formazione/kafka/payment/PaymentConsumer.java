package it.formazione.kafka.payment;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class PaymentConsumer {
  @KafkaListener(topics="orders-string")
  public void receive(ConsumerRecord<String,String> record){
    System.out.printf("thread=%s key=%s value=%s partition=%d offset=%d%n",Thread.currentThread().getName(),record.key(),record.value(),record.partition(),record.offset());
  }
}
