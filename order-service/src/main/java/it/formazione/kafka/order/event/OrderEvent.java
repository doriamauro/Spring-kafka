package it.formazione.kafka.order.event;
import java.math.BigDecimal;
import java.time.Instant;
public record OrderEvent(long orderId,long customerId,BigDecimal amount,Instant timestamp){}
