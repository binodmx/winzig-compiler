import java.util.ArrayList;
import java.util.Arrays;

public class Lexer {
    String sourceCode;
    int lexemeBegin = 0;
    int lexemeCurrent = 0;
    ArrayList<Character> letters = new ArrayList<>(Arrays.asList(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'T', 'S', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '_'
    ));
    ArrayList<Character> numbers = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
    ArrayList<Character> whitespaces = new ArrayList<>(Arrays.asList(' ', '\f', '\r', '\t'));

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Token getNextToken() {
        return this.getNextScreenedToken();
    }

    private Token getNextScreenedToken() {
        Token token = this.getNextScannedToken();
        while (Arrays.asList(TokenName.COMMENT, TokenName.WHITESPACE, TokenName.NEWLINE).contains(token.tokenName)) {
            token = this.getNextScannedToken();
        }
        return token;
    }

    private Token getNextScannedToken() {
        if (lexemeCurrent >= this.sourceCode.length()) {
            return new Token(TokenName.EOP, null);
        }

        lexemeBegin = lexemeCurrent;

        String identifier = this.findIdentifier();
        if (identifier != null) {
            switch (identifier) {
                case "and": return new Token(TokenName.AND, identifier);
                case "begin": return new Token(TokenName.BEGIN, identifier);
                case "case": return new Token(TokenName.CASE, identifier);
                case "chr": return new Token(TokenName.CHR, identifier);
                case "const": return new Token(TokenName.CONST, identifier);
                case "do": return new Token(TokenName.DO, identifier);
                case "else": return new Token(TokenName.ELSE, identifier);
                case "end": return new Token(TokenName.END, identifier);
                case "eof": return new Token(TokenName.EOF, identifier);
                case "exit": return new Token(TokenName.EXIT, identifier);
                case "for": return new Token(TokenName.FOR, identifier);
                case "function": return new Token(TokenName.FUNCTION, identifier);
                case "if": return new Token(TokenName.IF, identifier);
                case "loop": return new Token(TokenName.LOOP, identifier);
                case "mod": return new Token(TokenName.MOD, identifier);
                case "not": return new Token(TokenName.NOT, identifier);
                case "of": return new Token(TokenName.OF, identifier);
                case "or": return new Token(TokenName.OR, identifier);
                case "ord": return new Token(TokenName.ORD, identifier);
                case "otherwise": return new Token(TokenName.OTHERWISE, identifier);
                case "output": return new Token(TokenName.OUTPUT, identifier);
                case "pool": return new Token(TokenName.POOL, identifier);
                case "pred": return new Token(TokenName.PRED, identifier);
                case "program": return new Token(TokenName.PROGRAM, identifier);
                case "read": return new Token(TokenName.READ, identifier);
                case "repeat": return new Token(TokenName.REPEAT, identifier);
                case "return": return new Token(TokenName.RETURN, identifier);
                case "succ": return new Token(TokenName.SUCC, identifier);
                case "then": return new Token(TokenName.THEN, identifier);
                case "type": return new Token(TokenName.TYPE, identifier);
                case "until": return new Token(TokenName.UNTIL, identifier);
                case "var": return new Token(TokenName.VAR, identifier);
                case "while": return new Token(TokenName.WHILE, identifier);
                default: return new Token(TokenName.IDENTIFIER, identifier);
            }
        }

        String integer = this.findInteger();
        if (integer != null) {
            return new Token(TokenName.INTEGER, integer);
        }

        String character = this.findChar();
        if (character != null) {
            return new Token(TokenName.CHR, character);
        }

        String string = this.findString();
        if (string != null) {
            return new Token(TokenName.STRING, string);
        }

        String commentType1 = this.findCommentType1();
        if (commentType1 != null) {
            return new Token(TokenName.COMMENT, commentType1);
        }

        String commentType2 = this.findCommentType2();
        if (commentType2 != null) {
            return new Token(TokenName.COMMENT, commentType2);
        }


        String whitespace = this.findWhitespace();
        if (whitespace != null) {
            return new Token(TokenName.WHITESPACE, whitespace);
        }

        if (lexemeCurrent + 3 < this.sourceCode.length()) {
            String length3operator = this.sourceCode.substring(lexemeBegin, lexemeCurrent + 3);
            if (length3operator.equals(":=:")) {
                lexemeCurrent = lexemeCurrent + 3;
                return new Token(TokenName.SWAP, length3operator);
            }
        }

        if (lexemeCurrent + 2 < this.sourceCode.length()) {
            String length2operator = this.sourceCode.substring(lexemeBegin, lexemeCurrent + 2);
            switch (length2operator) {
                case ":=":
                    lexemeCurrent = lexemeCurrent + 2;
                    return new Token(TokenName.ASSIGN, length2operator);
                case "..":
                    lexemeCurrent = lexemeCurrent + 2;
                    return new Token(TokenName.DOUBLEDOTS, length2operator);
                case "<=":
                    lexemeCurrent = lexemeCurrent + 2;
                    return new Token(TokenName.LESSTHANOREQUALTO, length2operator);
                case "<>":
                    lexemeCurrent = lexemeCurrent + 2;
                    return new Token(TokenName.NOTEQUAL, length2operator);
                case ">=":
                    lexemeCurrent = lexemeCurrent + 2;
                    return new Token(TokenName.GREATERTHANOREQUALTO, length2operator);
            }
        }

        String length1operator = this.sourceCode.substring(lexemeBegin, lexemeCurrent + 1);
        switch (length1operator) {
            case ",":
                lexemeCurrent++;
                return new Token(TokenName.COMMA, length1operator);
            case ";":
                lexemeCurrent++;
                return new Token(TokenName.SEMICOLON, length1operator);
            case ":":
                lexemeCurrent++;
                return new Token(TokenName.COLON, length1operator);
            case ".":
                lexemeCurrent++;
                return new Token(TokenName.SINGLEDOT, length1operator);
            case "(":
                lexemeCurrent++;
                return new Token(TokenName.OPENINGBRACKET, length1operator);
            case ")":
                lexemeCurrent++;
                return new Token(TokenName.CLOSINGBRACKET, length1operator);
            case "*":
                lexemeCurrent++;
                return new Token(TokenName.MULTIPLY, length1operator);
            case "/":
                lexemeCurrent++;
                return new Token(TokenName.DIVIDE, length1operator);
            case "-":
                lexemeCurrent++;
                return new Token(TokenName.MINUS, length1operator);
            case "+":
                lexemeCurrent++;
                return new Token(TokenName.PLUS, length1operator);
            case "<":
                lexemeCurrent++;
                return new Token(TokenName.LESSTHAN, length1operator);
            case "=":
                lexemeCurrent++;
                return new Token(TokenName.EQUAL, length1operator);
            case ">":
                lexemeCurrent++;
                return new Token(TokenName.GREATERTHAN, length1operator);
        }

        String newline = this.sourceCode.substring(lexemeBegin, lexemeCurrent + 1);
        if (newline.equals("\n")) {
            lexemeCurrent++;
            return new Token(TokenName.NEWLINE, "");
        }

        return new Token(TokenName.UNKNOWN, null);
    }

    private char getCurrentChar() {
        if (lexemeCurrent < this.sourceCode.length()) {
            return this.sourceCode.charAt(lexemeCurrent);
        }
        return '\0';
    }

    private String findIdentifier() {
        if (this.letters.contains(this.getCurrentChar())) {
            lexemeCurrent++;
            while (this.letters.contains(this.getCurrentChar()) || this.numbers.contains(this.getCurrentChar())) {
                lexemeCurrent++;
            }
            return this.sourceCode.substring(lexemeBegin, lexemeCurrent);
        }
        return null;
    }

    private String findInteger() {
        if (this.numbers.contains(this.getCurrentChar())) {
            lexemeCurrent++;
            while (this.numbers.contains(this.getCurrentChar())) {
                lexemeCurrent++;
            }
            return this.sourceCode.substring(lexemeBegin, lexemeCurrent);
        }
        return null;
    }

    private String findChar() {
        if (this.getCurrentChar() == '\'') {
            lexemeCurrent++;
            if (this.getCurrentChar() == '\'') {
                return this.sourceCode.substring(lexemeBegin + 1, lexemeCurrent++);
            }
            lexemeCurrent++;
            if (this.getCurrentChar() == '\'') {
                return this.sourceCode.substring(lexemeBegin + 1, lexemeCurrent++);
            }
        }
        return null;
    }

    private String findString() {
        if (this.getCurrentChar() == '"') {
            lexemeCurrent++;
            while (this.getCurrentChar() != '"') {
                lexemeCurrent++;
            }
            return this.sourceCode.substring(lexemeBegin + 1, lexemeCurrent++);
        }
        return null;
    }

    private String findCommentType1() {
        if (this.getCurrentChar() == '#') {
            lexemeCurrent++;
            while (this.getCurrentChar() != '\n') {
                lexemeCurrent++;
            }
            return this.sourceCode.substring(lexemeBegin + 1, lexemeCurrent++);
        }
        return null;
    }

    private String findCommentType2() {
        if (this.getCurrentChar() == '{') {
            lexemeCurrent++;
            while (this.getCurrentChar() != '}') {
                lexemeCurrent++;
            }
            return this.sourceCode.substring(lexemeBegin + 1, lexemeCurrent++);
        }
        return null;
    }

    private String findWhitespace() {
        if (this.whitespaces.contains(this.getCurrentChar())) {
            lexemeCurrent++;
            while (this.whitespaces.contains(this.getCurrentChar())) {
                lexemeCurrent++;
            }
            return "";
        }
        return null;
    }
}