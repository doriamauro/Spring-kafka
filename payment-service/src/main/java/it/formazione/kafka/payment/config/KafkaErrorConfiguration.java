package it.formazione.kafka.payment.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfiguration {

  @Bean
  public ProducerFactory<String,Object> dltProducerFactory() {
    Map<String,Object> config=new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
    config.put(ProducerConfig.ACKS_CONFIG,"all");
    config.put("schema.registry.url","http://localhost:8081");
    Map<Class<?>,Serializer<?>> delegates=new LinkedHashMap<>();
    delegates.put(byte[].class,new ByteArraySerializer());
    delegates.put(SpecificRecord.class,new KafkaAvroSerializer());
    return new DefaultKafkaProducerFactory<>(config,new StringSerializer(),
      new DelegatingByTypeSerializer(delegates,true));
  }

  @Bean
  public KafkaTemplate<String,Object> dltKafkaTemplate(ProducerFactory<String,Object> dltProducerFactory) {
    return new KafkaTemplate<>(dltProducerFactory);
  }

  @Bean
  public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String,Object> dltKafkaTemplate) {
    DeadLetterPublishingRecoverer recoverer=new DeadLetterPublishingRecoverer(
      dltKafkaTemplate,(record,exception)->new TopicPartition("orders-avro.DLT",record.partition()));
    DefaultErrorHandler handler=new DefaultErrorHandler(recoverer,new FixedBackOff(1000L,2L));
    handler.addNotRetryableExceptions(IllegalArgumentException.class);
    return handler;
  }
}
