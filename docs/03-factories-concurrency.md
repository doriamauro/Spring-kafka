# Lab 03 — Factories e concurrency

Spring Boot autoconfigura ProducerFactory, KafkaTemplate, ConsumerFactory e ConcurrentKafkaListenerContainerFactory. La factory crea i listener container per `@KafkaListener`.

Esperimento: modifica `spring.kafka.listener.concurrency` da 1 a 3 in payment-service/application.yml e osserva thread/partition nei log. Per 3 partition sono utili al massimo 3 consumer nello stesso gruppo per questo topic.

Con 2 Pod Kubernetes e concurrency 3 avresti 6 consumer, ma con 3 partition soltanto 3 riceverebbero partition. Non equivale a sei elaborazioni parallele.

Configurazione manuale della factory: usa `ConcurrentKafkaListenerContainerFactoryConfigurer` se vuoi conservare tutte le customizzazioni di Boot; non sostituire i bean automatici senza motivo.
