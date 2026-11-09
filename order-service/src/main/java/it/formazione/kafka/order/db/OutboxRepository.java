package it.formazione.kafka.order.db;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OutboxRepository extends JpaRepository<OutboxEntry,Long> {
 List<OutboxEntry> findTop20ByPublishedFalseOrderByIdAsc();
 List<OutboxEntry> findByOrderIdOrderByIdAsc(long orderId);
}
