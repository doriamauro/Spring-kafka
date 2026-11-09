package it.formazione.kafka.payment;
import it.formazione.kafka.payment.db.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PaymentProcessor.class)
class PaymentProcessorTest {
  @Autowired private PaymentProcessor processor;
  @Autowired private PaymentRepository repository;

  @Test void sameOrderTwiceCreatesOnlyOnePayment(){
    assertTrue(processor.process(99001L));
    assertFalse(processor.process(99001L));
    assertEquals(1,repository.countByOrderId(99001L));
  }
}
