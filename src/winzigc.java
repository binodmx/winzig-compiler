import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class winzigc {

    public static void main(String[] args) {
        Lexer lexer;
//        Parser parser;

        String flag = "";
        try {
            flag = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid command. Run with flag -help for help");
        }
        switch (flag) {
            case "-lot":
                try {
                    String program = readFile(args[1]);
                    lexer = new Lexer(program);
                    Token token = lexer.getNextToken();
                    while (token.tokenName != TokenName.EOP && token.tokenName != TokenName.UNKNOWN) {
                        System.out.println(token.tokenName.toString() + ": " + token.value);
                        token = lexer.getNextToken();
                    }
                } catch (IOException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                break;
            case "-ast":
//                try {
//                    String program = readFile(args[1]);
//                    lexer = new Lexer(program);
//                    parser = new Parser();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case "-help":
                System.out.println("called -help");
                break;
            default:
                System.out.println("Invalid command. Run with flag -help for help");
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
}
