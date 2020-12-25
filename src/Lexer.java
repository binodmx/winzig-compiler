import java.util.ArrayList;
import java.util.Collections;

public class Lexer {

    public Lexer() { }

    public ArrayList<String> getListOfTokens(String program) {
        String[] splited = program.split("\\s+");
        ArrayList<String> lot = new ArrayList<>();
        Collections.addAll(lot, splited);
        return lot;
    }
}
