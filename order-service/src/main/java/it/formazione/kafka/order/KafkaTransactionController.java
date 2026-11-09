package it.formazione.kafka.order;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/lab/kafka-transactions")
public class KafkaTransactionController {
 private final KafkaTransactionDemo demo;
 public KafkaTransactionController(KafkaTransactionDemo demo){this.demo=demo;}
 @PostMapping("/{firstId}") @ResponseStatus(HttpStatus.ACCEPTED)
 public Map<String,Object> transaction(@PathVariable("firstId") long firstId,
   @RequestParam(name="fail",defaultValue="false") boolean fail){
   demo.sendPair(firstId,fail);
   return Map.of("transactionCommitted",true,"firstOrderId",firstId,"secondOrderId",firstId+1);
 }
}
