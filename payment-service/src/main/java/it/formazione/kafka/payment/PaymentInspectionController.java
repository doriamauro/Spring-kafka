package it.formazione.kafka.payment;
import it.formazione.kafka.payment.db.PaymentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
@RestController @RequestMapping("/lab/payments")
public class PaymentInspectionController {
  private final PaymentRepository payments;
  public PaymentInspectionController(PaymentRepository payments){this.payments=payments;}
  @GetMapping("/{orderId}")
  public Map<String,Object> inspect(@PathVariable("orderId") long orderId){
    List<Long> rows=payments.findByOrderIdOrderByIdAsc(orderId).stream().map(p->p.getId()).toList();
    return Map.of("orderId",orderId,"rowCount",rows.size(),"paymentRowIds",rows);
  }
}
