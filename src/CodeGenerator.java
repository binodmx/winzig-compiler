import java.util.HashMap;

public class CodeGenerator {
    private Parser parser;
    private HashMap<String, Integer> declarationTable;
    private AbstractSyntaxTree ast;
    String code;

    public CodeGenerator(Parser parser) {
        this.parser = parser;
        declarationTable = new HashMap<String, Integer>();
    }

    public void generateCode() {
        ast = parser.getAbstractSyntaxTree();
        traverseNode(ast.root);
    }

    public void printCode() {
        generateCode();
        System.out.println(code);
    }

    private void enter(String name, int n) {
        declarationTable.put(name, n);
    }

    private int lookup(String name) {
        return declarationTable.getOrDefault(name, 0);
    }

    private String gen(String file, String... args) {
        String newLine = "";
        for (String arg : args) {
            newLine = newLine + " " + arg;
        }
        return file + "\n" + newLine;
    }

    private String open() {
        return "";
    }

    private void close() {

    }

    private void traverseNode(Node node) {
//        switch (node.getData()) {
//            case "program":
//
//        }
    }

}
