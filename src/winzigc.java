import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class winzigc {

    public static void main(String[] args) {
        Lexer lexer;
        Parser parser;

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
                    lexer = new Lexer();
                    ArrayList<String> lot = lexer.getListOfTokens(program);
                    for (String token : lot) {
                        System.out.println(token);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "-ast":
                try {
                    String program = readFile(args[1]);
                    lexer = new Lexer();
                    ArrayList<String> lot = lexer.getListOfTokens(program);
                    parser = new Parser();
                    ArrayList<String> ast = parser.getAbstractSyntaxTree(lot);
                    for (String node : ast) {
                        System.out.println(node);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "-help":
                System.out.println("called -help");
                break;
            default:
                System.out.println("Invalid command. Run with flag -help for help");
        }
    }

    private static String readFile(String path) throws IOException {
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
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
