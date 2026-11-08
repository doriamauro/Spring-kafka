package it.formazione.kafka.payment;

import java.io.File;
import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AvroSchemaCompatibilityTest {
  @Test
  void v2ReaderCanReadV1Writer() throws Exception {
    Schema writer=new Schema.Parser().parse(new File("../schemas/OrderEvent-v1.avsc"));
    Schema reader=new Schema.Parser().parse(new File("../schemas/OrderEvent-v2.avsc"));
    assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE,
      SchemaCompatibility.checkReaderWriterCompatibility(reader,writer).getType());
  }

  @Test
  void v1ReaderCanReadV2Writer() throws Exception {
    Schema reader=new Schema.Parser().parse(new File("../schemas/OrderEvent-v1.avsc"));
    Schema writer=new Schema.Parser().parse(new File("../schemas/OrderEvent-v2.avsc"));
    assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE,
      SchemaCompatibility.checkReaderWriterCompatibility(reader,writer).getType());
  }
}
