package juanfran.um;

@SuppressWarnings("all")
public class Main {
  private static final String INPUT_XMI_PATH = "input-files/USchema_7.xmi";

  private static final String OUTPUT_XMI_PATH = "output-files/Document_7.xmi";

  public static void main(final String[] args) {
    final MappingUschema2Document mapping = new MappingUschema2Document();
    mapping.loadSchema(Main.INPUT_XMI_PATH);
    mapping.executeMapping();
    mapping.saveSchema(Main.OUTPUT_XMI_PATH);
  }
}
