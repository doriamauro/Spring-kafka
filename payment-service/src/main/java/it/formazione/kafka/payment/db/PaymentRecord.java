package it.formazione.kafka.payment.db;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="payment_record")
public class PaymentRecord {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private long orderId;
  @Column(nullable=false) private Instant createdAt;
  protected PaymentRecord() {}
  public PaymentRecord(long orderId) {this.orderId=orderId;this.createdAt=Instant.now();}
  public Long getId(){return id;}
  public long getOrderId(){return orderId;}
  public Instant getCreatedAt(){return createdAt;}
}
