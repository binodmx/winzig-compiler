import java.util.HashMap;

public class CodeGenerator {
    private Parser parser;
    private HashMap<String, Integer> declarationTable;
    private AbstractSyntaxTree ast;
    String file;

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
        System.out.println(file);
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
            newLine = newLine + arg + " ";
        }
        return file + "\n" + newLine;
    }

    private String open() {
        return "";
    }

    private String close(String file) {
        this.file = file;
        return file;
    }

    private void traverseNode(Node node) {
        String x;
        String y;
        switch (node.getData()) {
            case "program":
                node.getLeftChild().getRightChild().setCodeL(open());
                node.getLeftChild().getRightChild().setNextL(1);
                node.getLeftChild().getRightChild().setTopL(0);
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(close(gen(node.getLeftChild().getRightChild().getCodeR(), "stop")));
                break;
            case "<integer>":
            case "<char>":
            case "<string>":
                node.setCodeR(gen(node.getCodeL(), "lit", node.getLeftChild().getData()));
                node.setNextR(node.getNextL() + 1);
                node.setTopR(node.getTopL() + 1);
                break;
            case "if":
                switch (node.getN()) {
                    case 2:
                        node.getLeftChild().setCodeL(node.getCodeL());
                        node.getLeftChild().setNextL(node.getNextL());
                        node.getLeftChild().setTopL(node.getTopL());
                        traverseNode(node.getLeftChild());
                        node.getLeftChild().getRightChild().setCodeL(gen(node.getLeftChild().getCodeR(), "iffalse", Integer.toString(node.getLeftChild().getRightChild().getNextR())));
                        node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR() + 1);
                        node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR() - 1);
                        traverseNode(node.getLeftChild().getRightChild());
                        node.setCodeR(node.getLeftChild().getRightChild().getCodeR());
                        node.setNextR(node.getLeftChild().getRightChild().getNextR());
                        node.setTopR(node.getLeftChild().getRightChild().getTopR());
                        break;
                    case 3:
                        node.getLeftChild().setCodeL(node.getCodeL());
                        node.getLeftChild().setNextL(node.getNextL());
                        node.getLeftChild().setTopL(node.getTopL());
                        traverseNode(node.getLeftChild());
                        node.getLeftChild().getRightChild().setCodeL(gen(node.getLeftChild().getCodeR(), "iffalse", Integer.toString(node.getLeftChild().getRightChild().getNextR() + 1)));
                        node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR() + 1);
                        node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR() - 1);
                        traverseNode(node.getLeftChild().getRightChild());
                        node.getLeftChild().getRightChild().getRightChild().setCodeL(gen(node.getLeftChild().getRightChild().getCodeR(), "goto", Integer.toString(node.getLeftChild().getRightChild().getRightChild().getNextR())));
                        node.getLeftChild().getRightChild().getRightChild().setNextL(node.getLeftChild().getRightChild().getNextR() + 1);
                        node.getLeftChild().getRightChild().getRightChild().setTopL(node.getLeftChild().getRightChild().getTopR());
                        traverseNode(node.getLeftChild().getRightChild().getRightChild());
                        node.setCodeR(node.getLeftChild().getRightChild().getRightChild().getCodeR());
                        node.setNextR(node.getLeftChild().getRightChild().getRightChild().getNextR());
                        node.setTopR(node.getLeftChild().getRightChild().getRightChild().getTopR());
                        break;
                }
                break;
            case "while":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(gen(node.getLeftChild().getCodeR(), "iffalse", Integer.toString(node.getLeftChild().getRightChild().getNextR() + 1)));
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR() + 1);
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR() - 1);
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getRightChild().getCodeR(), "goto", Integer.toString(node.getNextL())));
                node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getRightChild().getTopR());
                break;
            case "<null>":
                node.setCodeR(gen(node.getCodeL(), "lit"));
                node.setNextR(node.getNextL() + 1);
                node.setTopR(node.getTopL() + 1);
                break;
            case "integer":
            case "string":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.setCodeR(node.getLeftChild().getCodeR());
                node.setNextR(node.getLeftChild().getNextR());
                node.setTopR(node.getLeftChild().getTopR());
                break;
            case "assign":
                node.getLeftChild().getRightChild().setCodeL(node.getCodeL());
                node.getLeftChild().getRightChild().setNextL(node.getNextL());
                node.getLeftChild().getRightChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild().getRightChild());
                x = node.getLeftChild().getLeftChild().getData();
                if (lookup(x) == 0) {
                    enter(x, node.getLeftChild().getRightChild().getTopR());
                    node.setCodeR(node.getLeftChild().getRightChild().getCodeR());
                    node.setNextR(node.getLeftChild().getRightChild().getNextR());
                    node.setTopR(node.getLeftChild().getRightChild().getTopR());
                } else {
                    node.setCodeR(gen(node.getLeftChild().getRightChild().getCodeR(), "save", Integer.toString(lookup(x))));
                    node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                    node.setTopR(node.getLeftChild().getRightChild().getTopR() - 1);
                }
                break;
            case "swap":
                x = node.getLeftChild().getLeftChild().getData();
                y = node.getLeftChild().getRightChild().getLeftChild().getData();
                node.setCodeR(gen(gen(gen(gen(node.getCodeL(), "load", Integer.toString(lookup(x))), "load", Integer.toString(lookup(y))), "save", Integer.toString(lookup(x))), "save", Integer.toString(lookup(y))));
                node.setNextR(node.getNextL() + 4);
                node.setTopR(node.getTopL());
                break;
            case "<=":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(gen(node.getLeftChild().getRightChild().getCodeR(), "greaterthan"), "not"));
                node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getRightChild().getTopR() - 1);
                break;
            case "<":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getRightChild().getCodeR(), "lessthan"));
                node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getRightChild().getTopR() - 1);
                break;
            case ">=":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(gen(node.getLeftChild().getRightChild().getCodeR(), "lessthan"), "not"));
                node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getRightChild().getTopR() - 1);
                break;
            case ">":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getRightChild().getCodeR(), "greaterthan"));
                node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getRightChild().getTopR() - 1);
                break;
            case "=":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getRightChild().getCodeR(), "equal"));
                node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getRightChild().getTopR() - 1);
                break;
            case "<>":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopR());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(gen(node.getLeftChild().getRightChild().getCodeR(), "equal"), "not"));
                node.setNextR(node.getLeftChild().getRightChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getRightChild().getTopR() - 1);
                break;
            case "+":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopL());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getCodeR(), "add"));
                node.setNextR(node.getLeftChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getTopR() - 1);
                break;
            case "-":
                switch (node.getN()) {
                    case 1:
                        node.getLeftChild().setCodeL(node.getCodeL());
                        node.getLeftChild().setNextL(node.getNextL());
                        node.getLeftChild().setTopL(node.getTopL());
                        traverseNode(node.getLeftChild());
                        node.setCodeR(gen(node.getLeftChild().getCodeR(), "negate"));
                        node.setNextR(node.getLeftChild().getNextR() + 1);
                        node.setTopR(node.getLeftChild().getTopR());
                        break;
                    case 2:
                        node.getLeftChild().setCodeL(node.getCodeL());
                        node.getLeftChild().setNextL(node.getNextL());
                        node.getLeftChild().setTopL(node.getTopL());
                        traverseNode(node.getLeftChild());
                        node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                        node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                        node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopL());
                        traverseNode(node.getLeftChild().getRightChild());
                        node.setCodeR(gen(node.getLeftChild().getCodeR(), "subtract"));
                        node.setNextR(node.getLeftChild().getNextR() + 1);
                        node.setTopR(node.getLeftChild().getTopR() - 1);
                        break;
                }
                break;
            case "or":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopL());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getCodeR(), "or"));
                node.setNextR(node.getLeftChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getTopR() - 1);
                break;
            case "*":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopL());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getCodeR(), "multiply"));
                node.setNextR(node.getLeftChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getTopR() - 1);
                break;
            case "/":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopL());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getCodeR(), "divide"));
                node.setNextR(node.getLeftChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getTopR() - 1);
                break;
            case "and":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopL());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getCodeR(), "and"));
                node.setNextR(node.getLeftChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getTopR() - 1);
                break;
            case "mod":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.getLeftChild().getRightChild().setCodeL(node.getLeftChild().getCodeR());
                node.getLeftChild().getRightChild().setNextL(node.getLeftChild().getNextR());
                node.getLeftChild().getRightChild().setTopL(node.getLeftChild().getTopL());
                traverseNode(node.getLeftChild().getRightChild());
                node.setCodeR(gen(node.getLeftChild().getCodeR(), "mod"));
                node.setNextR(node.getLeftChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getTopR() - 1);
                break;
            case "not":
                node.getLeftChild().setCodeL(node.getCodeL());
                node.getLeftChild().setNextL(node.getNextL());
                node.getLeftChild().setTopL(node.getTopL());
                traverseNode(node.getLeftChild());
                node.setCodeR(gen(node.getLeftChild().getCodeR(), "not"));
                node.setNextR(node.getLeftChild().getNextR() + 1);
                node.setTopR(node.getLeftChild().getTopR());
                break;
            case "<identifier>":
                node.setCodeR(gen(node.getCodeL(), "load", Integer.toString(lookup(node.getLeftChild().getData()))));
                node.setNextR(node.getNextL() + 1);
                node.setTopR(node.getTopL() + 1);
                break;
            default:

        }
    }

}
