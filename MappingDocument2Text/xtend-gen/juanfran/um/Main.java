package juanfran.um;

import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class Main {
  private static final String INPUT_XMI_PATH = "input-files/Document_integration.xmi";

  private static final String OUTPUT_JS_PATH = "output-files/Mongoose_integration.js";

  public static void main(final String[] args) {
    final MappingDocument2Text mapping2Text = new MappingDocument2Text();
    mapping2Text.loadSchema(Main.INPUT_XMI_PATH);
    InputOutput.<String>println("Starting the mapping process from DocumentSchema to JavaScript file...");
    mapping2Text.mappingDocument2Text(Main.OUTPUT_JS_PATH);
    InputOutput.<String>println((("Mapping successful! JavaScript file has been created at \'" + Main.OUTPUT_JS_PATH) + "\'"));
  }
}
