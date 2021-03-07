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
        String previousCode = "";
        traverse(ast.root);
        while (!previousCode.equals(code)) {
            previousCode = code;
            traverse(ast.root);
        }
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
            newLine = newLine + arg + " ";
        }
        return file + newLine + "\n";
    }

    private String open() {
        return "";
    }

    private String close(String file) {
        this.code = file;
        return file;
    }

    private void traverse(Node node) {
        String x, y;
        Node e1, e2, e3, e4, e5, e6, e7;
        switch (node.getData()) {
            case "program":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e3 = e2.getRightChild();
                e4 = e3.getRightChild();
                e5 = e4.getRightChild();
                e6 = e5.getRightChild();
                e7 = e6.getRightChild();
                e2.setCodeL(open());
                e2.setNextL(1);
                e2.getRightChild().setTopL(0);
                traverse(e2);
                e3.setCodeL(e2.getCodeR());
                e3.setNextL(e2.getNextR());
                e3.setTopL(e2.getTopR());
                traverse(e3);
                e4.setCodeL(e3.getCodeR());
                e4.setNextL(e3.getNextR());
                e4.setTopL(e3.getTopR());
                traverse(e4);
                e5.setCodeL(e4.getCodeR());
                e5.setNextL(e4.getNextR());
                e5.setTopL(e4.getTopR());
                traverse(e5);
                e6.setCodeL(e5.getCodeR());
                e6.setNextL(e5.getNextR());
                e6.setTopL(e5.getTopR());
                traverse(e6);
                node.setCodeR(close(gen(e6.getCodeR(), "stop")));
                break;
            case "<integer>":
            case "<char>":
            case "<string>":
                node.setCodeR(gen(node.getCodeL(), "lit", node.getLeftChild().getData()));
                node.setNextR(node.getNextL() + 1);
                node.setTopR(node.getTopL() + 1);
                break;
            case "var":
                node.setCodeR(node.getCodeL());
                node.setNextR(node.getNextL());
                node.setTopR(node.getTopL());
                break;
            case "if":
                switch (node.getN()) {
                    case 2:
                        e1 = node.getLeftChild();
                        e2 = e1.getRightChild();
                        e1.setCodeL(node.getCodeL());
                        e1.setNextL(node.getNextL());
                        e1.setTopL(node.getTopL());
                        traverse(e1);
                        e2.setCodeL(gen(e1.getCodeR(), "iffalse", Integer.toString(e2.getNextR())));
                        e2.setNextL(e1.getNextR() + 1);
                        e2.setTopL(e1.getTopR() - 1);
                        traverse(e2);
                        node.setCodeR(e2.getCodeR());
                        node.setNextR(e2.getNextR());
                        node.setTopR(e2.getTopR());
                        break;
                    case 3:
                        e1 = node.getLeftChild();
                        e2 = e1.getRightChild();
                        e3 = e2.getRightChild();
                        e1.setCodeL(node.getCodeL());
                        e1.setNextL(node.getNextL());
                        e1.setTopL(node.getTopL());
                        traverse(e1);
                        e2.setCodeL(gen(e1.getCodeR(), "iffalse", Integer.toString(e2.getNextR() + 1)));
                        e2.setNextL(e1.getNextR() + 1);
                        e2.setTopL(e1.getTopR() - 1);
                        traverse(e2);
                        e3.setCodeL(gen(e2.getCodeR(), "goto", Integer.toString(e3.getNextR())));
                        e3.setNextL(e2.getNextR() + 1);
                        e3.setTopL(e2.getTopR());
                        traverse(e3);
                        node.setCodeR(e3.getCodeR());
                        node.setNextR(e3.getNextR());
                        node.setTopR(e3.getTopR());
                        break;
                }
                break;
            case "while":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(gen(e1.getCodeR(), "iffalse", Integer.toString(e2.getNextR() + 1)));
                e2.setNextL(e1.getNextR() + 1);
                e2.setTopL(e1.getTopR() - 1);
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "goto", Integer.toString(node.getNextL())));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR());
                break;
            case "assign":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e2.setCodeL(node.getCodeL());
                e2.setNextL(node.getNextL());
                e2.setTopL(node.getTopL());
                traverse(e2);
                x = e1.getLeftChild().getData();
                if (lookup(x) == 0) {
                    enter(x, e2.getTopR());
                    node.setCodeR(e2.getCodeR());
                    node.setNextR(e2.getNextR());
                    node.setTopR(e2.getTopR());
                } else {
                    node.setCodeR(gen(e2.getCodeR(), "save", Integer.toString(lookup(x))));
                    node.setNextR(e2.getNextR() + 1);
                    node.setTopR(e2.getTopR() - 1);
                }
                break;
            case "swap":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                x = e1.getLeftChild().getData();
                y = e2.getLeftChild().getData();
                node.setCodeR(gen(gen(gen(gen(node.getCodeL(), "load", Integer.toString(lookup(x))), "load", Integer.toString(lookup(y))), "save", Integer.toString(lookup(x))), "save", Integer.toString(lookup(y))));
                node.setNextR(node.getNextL() + 4);
                node.setTopR(node.getTopL());
                break;
            case "<=":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(gen(e2.getCodeR(), "greaterthan"), "not"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "<":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "lessthan"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case ">=":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(gen(e2.getCodeR(), "lessthan"), "not"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case ">":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "greaterthan"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "=":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "equal"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "<>":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(gen(e2.getCodeR(), "equal"), "not"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "+":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "add"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "-":
                switch (node.getN()) {
                    case 1:
                        e1 = node.getLeftChild();
                        e1.setCodeL(node.getCodeL());
                        e1.setNextL(node.getNextL());
                        e1.setTopL(node.getTopL());
                        traverse(e1);
                        node.setCodeR(gen(e1.getCodeR(), "negate"));
                        node.setNextR(e1.getNextR() + 1);
                        node.setTopR(e1.getTopR());
                        break;
                    case 2:
                        e1 = node.getLeftChild();
                        e2 = e1.getRightChild();
                        e1.setCodeL(node.getCodeL());
                        e1.setNextL(node.getNextL());
                        e1.setTopL(node.getTopL());
                        traverse(e1);
                        e2.setCodeL(e1.getCodeR());
                        e2.setNextL(e1.getNextR());
                        e2.setTopL(e1.getTopR());
                        traverse(e2);
                        node.setCodeR(gen(e2.getCodeR(), "subtract"));
                        node.setNextR(e2.getNextR() + 1);
                        node.setTopR(e2.getTopR() - 1);
                        break;
                }
                break;
            case "or":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "or"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "*":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "multiply"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "/":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "divide"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "and":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "and"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "mod":
                e1 = node.getLeftChild();
                e2 = e1.getRightChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                e2.setCodeL(e1.getCodeR());
                e2.setNextL(e1.getNextR());
                e2.setTopL(e1.getTopR());
                traverse(e2);
                node.setCodeR(gen(e2.getCodeR(), "mod"));
                node.setNextR(e2.getNextR() + 1);
                node.setTopR(e2.getTopR() - 1);
                break;
            case "not":
                e1 = node.getLeftChild();
                e1.setCodeL(node.getCodeL());
                e1.setNextL(node.getNextL());
                e1.setTopL(node.getTopL());
                traverse(e1);
                node.setCodeR(gen(e1.getCodeR(), "not"));
                node.setNextR(e1.getNextR() + 1);
                node.setTopR(e1.getTopR());
                break;
            case "<identifier>":
                node.setCodeR(gen(node.getCodeL(), "load", Integer.toString(lookup(node.getLeftChild().getData()))));
                node.setNextR(node.getNextL() + 1);
                node.setTopR(node.getTopL() + 1);
                break;
            default:
                if (node.getN() == 0) {
                    node.setCodeR(node.getCodeL());
                    node.setNextR(node.getNextL());
                    node.setTopR(node.getTopL());
                } else {
                    Node currentChild = node.getLeftChild();
                    currentChild.setCodeL(node.getCodeL());
                    currentChild.setNextL(node.getNextL());
                    currentChild.setTopL(node.getTopL());
                    traverse(currentChild);
                    for (int i = 1; i < node.getN(); i++) {
                        currentChild.getRightChild().setCodeL(currentChild.getCodeR());
                        currentChild.getRightChild().setNextL(currentChild.getNextR());
                        currentChild.getRightChild().setTopL(currentChild.getTopR());
                        traverse(currentChild.getRightChild());
                        currentChild = currentChild.getRightChild();
                    }
                    node.setCodeR(currentChild.getCodeR());
                    node.setNextR(currentChild.getNextR());
                    node.setTopR(currentChild.getTopR());
                }
        }
    }
}
