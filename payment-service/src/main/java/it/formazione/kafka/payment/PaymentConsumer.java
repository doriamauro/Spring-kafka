package it.formazione.kafka.payment;
import it.formazione.kafka.contract.OrderEvent;
import it.formazione.kafka.payment.db.PaymentRecord;
import it.formazione.kafka.payment.db.PaymentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class PaymentConsumer {
  private final PaymentRepository payments;
  private final boolean crashAfterSave;
  public PaymentConsumer(PaymentRepository payments,
      @Value("${lab.crash-after-save:false}") boolean crashAfterSave) {
    this.payments=payments;this.crashAfterSave=crashAfterSave;
  }
  @KafkaListener(topics="orders-avro")
  public void receive(ConsumerRecord<String,OrderEvent> record){
    OrderEvent event=record.value();
    if(event.getAmount()<0) throw new IllegalArgumentException("Negative amount");
    if(event.getOrderId()==500L) throw new IllegalStateException("Simulated provider error");
    PaymentRecord saved=payments.saveAndFlush(new PaymentRecord(event.getOrderId()));
    System.out.printf("LAB09 DB row=%d order=%d offset=%d%n",
      saved.getId(),event.getOrderId(),record.offset());
    if(crashAfterSave && event.getOrderId()==909L){
      System.err.println("LAB09 deliberate JVM crash after DB commit; Kafka offset uncommitted");
      System.err.flush();
      Runtime.getRuntime().halt(91);
    }
  }
}
