package it.formazione.kafka.order;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import it.formazione.kafka.contract.OrderEvent;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class KafkaTransactionDemo {
 private final DefaultKafkaProducerFactory<String,OrderEvent> factory;
 private final KafkaTemplate<String,OrderEvent> template;
 public KafkaTransactionDemo(@Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrap,
   @Value("${lab.schema-registry-url:http://localhost:8081}") String registry){
   Map<String,Object> props=new HashMap<>();
   props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrap);
   props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
   props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,KafkaAvroSerializer.class);
   props.put(ProducerConfig.ACKS_CONFIG,"all");
   props.put("schema.registry.url",registry);
   factory=new DefaultKafkaProducerFactory<>(props);
   factory.setTransactionIdPrefix("lab12-"+UUID.randomUUID()+"-");
   template=new KafkaTemplate<>(factory);
 }
 public void sendPair(long id,boolean fail){
   template.executeInTransaction(tx->{
     tx.send("orders-avro-tx",Long.toString(id),event(id));
     System.out.println("LAB12 first record enqueued in transaction");
     if(fail)throw new IllegalStateException("LAB12 intentional transaction rollback");
     tx.send("orders-avro-tx",Long.toString(id+1),event(id+1));
     return null;
   });
   System.out.println("LAB12 Kafka transaction COMMITTED");
 }
 private OrderEvent event(long id){
   return OrderEvent.newBuilder().setOrderId(id).setCustomerId(42L)
     .setAmount(79.90).setCurrency("EUR").setTimestamp(Instant.now().toString()).build();
 }
 @PreDestroy public void shutdown(){factory.destroy();}
}
