package it.formazione.kafka.order.db;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="orders")
public class StoredOrder {
 @Id private Long id;
 @Column(nullable=false) private Instant createdAt;
 protected StoredOrder(){}
 public StoredOrder(long id){this.id=id;this.createdAt=Instant.now();}
 public Long getId(){return id;}
 public Instant getCreatedAt(){return createdAt;}
}
