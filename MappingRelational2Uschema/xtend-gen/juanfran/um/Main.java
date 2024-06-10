package juanfran.um;

@SuppressWarnings("all")
public class Main {
  private static final String INPUT_XMI_PATH = "test-input-files/Relational_6-1_N.xmi";

  private static final String OUTPUT_XMI_PATH = "output-files/USchema_6-1_N.xmi";

  public static void main(final String[] args) {
    final MappingRelational2Uschema mapping = new MappingRelational2Uschema();
    mapping.loadSchema(Main.INPUT_XMI_PATH);
    mapping.executeMapping();
    mapping.saveSchema(Main.OUTPUT_XMI_PATH);
  }
}
