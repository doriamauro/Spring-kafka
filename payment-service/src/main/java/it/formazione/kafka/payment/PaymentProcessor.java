package it.formazione.kafka.payment;
import it.formazione.kafka.payment.db.PaymentRepository;
import it.formazione.kafka.payment.db.PaymentRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** Lab 10: database enforces one payment per orderId. */
@Service
public class PaymentProcessor {
  private final PaymentRepository payments;
  public PaymentProcessor(PaymentRepository payments){this.payments=payments;}

  @Transactional
  public boolean process(long orderId) {
    if(payments.existsByOrderId(orderId)){
      System.out.println("LAB10 duplicate order "+orderId+" -> skipped");
      return false;
    }
    PaymentRecord row=payments.saveAndFlush(new PaymentRecord(orderId));
    System.out.printf("LAB10 stored order=%d paymentRow=%d%n",orderId,row.getId());
    return true;
  }
}
