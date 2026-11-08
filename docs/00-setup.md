# Lab 00 — Ambiente

1. Avvia Kafka e Schema Registry con `docker compose -f docker/compose.yaml up -d`.
2. Verifica `http://localhost:8081/subjects` (lista JSON).
3. Compila con `mvn clean verify`.
4. Le app girano su Windows, il broker è raggiungibile su `localhost:9092`.

I Docker listener INTERNAL/EXTERNAL separano rete Docker e host. Il Registry usa `kafka:29092`.
