package it.formazione.kafka.order.db;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StoredOrderRepository extends JpaRepository<StoredOrder,Long>{}
