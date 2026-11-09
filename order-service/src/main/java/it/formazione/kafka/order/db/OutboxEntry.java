package it.formazione.kafka.order.db;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="order_outbox")
public class OutboxEntry {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private long orderId;
 @Column(nullable=false) private boolean published;
 @Column(nullable=false) private Instant createdAt;
 protected OutboxEntry(){}
 public OutboxEntry(long orderId){this.orderId=orderId;this.createdAt=Instant.now();this.published=false;}
 public Long getId(){return id;}
 public long getOrderId(){return orderId;}
 public boolean isPublished(){return published;}
 public void markPublished(){this.published=true;}
}
