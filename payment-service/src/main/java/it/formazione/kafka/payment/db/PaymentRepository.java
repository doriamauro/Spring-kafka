package it.formazione.kafka.payment.db;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentRepository extends JpaRepository<PaymentRecord,Long> {
  long countByOrderId(long orderId);
  boolean existsByOrderId(long orderId);
  List<PaymentRecord> findByOrderIdOrderByIdAsc(long orderId);
}
