# Lab 01 — KafkaTemplate

Crea il topic: `docker compose -f docker/compose.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic orders-string --partitions 3 --replication-factor 1 --bootstrap-server kafka:29092`.

Avvia Order Service e invia `POST http://localhost:8080/orders/123/publish`.

Osserva: la risposta HTTP 202 conferma l'accettazione della richiesta, NON l'avvenuta pubblicazione Kafka. Il risultato del CompletableFuture contiene partition e offset.
