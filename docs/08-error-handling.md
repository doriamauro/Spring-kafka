# Lab 08 — Retry, DLT e ErrorHandlingDeserializer

Crea `orders-avro.DLT` con 3 partition (come `orders-avro`). Il PaymentConsumer simula due errori: orderId=500 (retry due volte + DLT), amount negativo (nessun retry + DLT).

`DefaultErrorHandler` gestisce i retry e `DeadLetterPublishingRecoverer` invia i record definitivamente falliti nel DLT.

Per un record Avro corrotto, il delegate `KafkaAvroDeserializer` fallisce prima di `@KafkaListener`. `ErrorHandlingDeserializer` conserva l'informazione sull'errore affinché il container invochi l'error handler. Il recoverer ripubblica i byte originali; il `DelegatingByTypeSerializer` usa `ByteArraySerializer` per `byte[]` e `KafkaAvroSerializer` per SpecificRecord validi.

**Attenzione:** DLT e commit non sono automaticamente atomici; non inviare pagamenti reali senza idempotenza e verifica del provider.

### Prova errori
Pubblica ID 500 tramite l'endpoint esistente. Esamina `orders-avro.DLT` con un consumer Avro (non la CLI String).

### Verifica recovery
Se un errore è permanente, non riprovare indiscriminatamente. Guasti temporanei di Schema Registry meritano una strategia diversa dai dati invalidi.
