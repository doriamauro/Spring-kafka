# Lab 02 — @KafkaListener

Avvia Payment Service in un terminale separato. Pubblica più ordini dal producer e osserva key, value, partition e offset.

Prova a riavviare il consumer: con lo stesso group-id riprende dagli offset committed. Con un group-id nuovo e auto-offset-reset=earliest rilegge i record ancora conservati.

Consumer group = identità logica della funzione applicativa; ogni partition è assegnata a un consumer alla volta all'interno dello stesso gruppo.
