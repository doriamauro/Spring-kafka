# Lab 06 — Avro e Schema Registry

Crea topic `orders-avro` (3 partition). Avvia Registry e servizi, poi chiama `POST http://localhost:8080/orders/123/publish`.

Gli oggetti `OrderEvent` sono generati dallo schema in `src/main/avro` tramite `mvn generate-sources`. Il producer usa KafkaAvroSerializer e registra il subject `orders-avro-value`.

Verifica `http://localhost:8081/subjects`, poi `http://localhost:8081/subjects/orders-avro-value/versions/latest`.

La libreria Avro non è un container. Lo Schema Registry è un servizio distinto e usa Kafka per memorizzare i metadati degli schemi.

Per questo laboratorio l'importo è `double` per semplicità didattica. In pagamenti reali usare Avro decimal oppure importi interi in unità minime.
