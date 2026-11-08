# Lab 04 — Producer e consumer properties

Producer: `acks=all` richiede conferme in-sync, ma il broker singolo non offre ridondanza. `enable.idempotence` previene duplicazioni da retry del producer (non duplicazioni applicative). `linger.ms` e `batch.size` migliorano il batching a costo di possibile latenza.

Consumer: `max.poll.records` limita i record restituiti al singolo poll, NON il numero di thread. `max.poll.interval.ms` pone un vincolo al tempo tra poll. Con un listener record e concurrency=1 i record vengono elaborati sequenzialmente.

Calcola: 20 record x 2 secondi = circa 40 secondi prima del prossimo poll, salvo overhead. Gli acknowledgment non rendono atomici gli effetti HTTP esterni.
