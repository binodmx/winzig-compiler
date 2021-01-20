import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class winzigc {
    public static void main(String[] args) {
        String program;
        Lexer lexer;
        Parser parser;
        String flag = "";
        try {
            flag = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid command. Run with flag -help for help.");
            System.exit(0);
        }
        switch (flag) {
            case "-lot":
                try {
                    program = readFile(args[1]);
                    lexer = new Lexer(program);
                    Token token = lexer.getNextToken();
                    while (token.tokenName != TokenName.EOP && token.tokenName != TokenName.UNKNOWN) {
                        System.out.println(token.tokenName.toString() + ": " + token.value);
                        token = lexer.getNextToken();
                    }
                } catch (IOException e) {
                    System.out.println("Invalid file.");
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid file name. Run with flag -help for help.");
                }
                break;
            case "-ast":
                try {
                    program = readFile(args[1]);
                    lexer = new Lexer(program);
                    parser = new Parser(lexer);
                    AbstractSyntaxTree ast = parser.getAbstractSyntaxTree();
                    ast.print();
                } catch (IOException e) {
                    System.out.println("Invalid file.");
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid file name. Run with flag -help for help.");
                }
                break;
            case "-help":
                System.out.println("Usage: java winzigc [stage] [path]\n");
                System.out.println("where stage includes:");
                System.out.println("    -lot: generate 'List of Tokens'");
                System.out.println("    -ast: generate 'Abstract Syntax Tree'\n");
                System.out.println("path is used to specify relative path to the winzig program.");
                break;
            default:
                System.out.println("Invalid command. Run with flag -help for help.");
        }
    }

    private static String readFile(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        StringBuilder stringBuilder = new StringBuilder();
        int val;
        while ((val = bufferedReader.read()) != -1) {
            char c = (char) val;
            stringBuilder.append(c);
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
//    public final String ANSI_RED = "\u001B[31m";
//    public final String ANSI_RESET = "\u001B[0m";
//    System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);
}
