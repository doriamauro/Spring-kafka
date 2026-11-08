# Lab 05 — JSON

Crea topic `orders-json` con 3 partition. Pubblica con `POST http://localhost:8080/orders/123/publish`.

Le classi Java dei due microservizi hanno package distinti ma contratto JSON uguale. Il producer disabilita i Java type headers; il consumer imposta il proprio default type.

**Importante:** Schema Registry non entra in gioco per questa serializzazione JSON semplice. In caso di cambio schema, la compatibilità va gestita a livello applicativo.
