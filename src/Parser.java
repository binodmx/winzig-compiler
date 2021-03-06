import java.util.Arrays;
import java.util.Stack;

public class Parser {
    private final Stack<Node> stack;
    private final Lexer lexer;
    private Token nextToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.stack = new Stack<>();
    }

    public void printAbstractSyntaxTree() {
        this.getAbstractSyntaxTree().print();
    }

    public AbstractSyntaxTree getAbstractSyntaxTree() {
        nextToken = this.lexer.getNextToken();
        Winzig();
        return new AbstractSyntaxTree(stack.pop());
    }

    private void read(TokenName tokenName) {
        if (nextToken.tokenName != tokenName) {
            System.out.println("Error: expected " + tokenName + ". but found " + nextToken.tokenName + ".");
            throw new Error();
        } else {
            nextToken = lexer.getNextToken();
        }
    }

    private void buildTree(String x, int n) {
        Node p = null;
        for (int i = 0; i < n; i++) {
            Node c = stack.pop();
            c.setRightChild(p);
            p = c;
        }
        stack.push(new Node(x, p, null, n));
    }

    private void Winzig() {
        read(TokenName.PROGRAM);
        Name();
        read(TokenName.COLON);
        Consts();
        Types();
        Dclns();
        SubProgs();
        Body();
        Name();
        read(TokenName.SINGLEDOT);
        buildTree("program", 7);
    }

    private void Consts() {
        if (nextToken.tokenName == TokenName.CONST) {
            read(TokenName.CONST);
            Const();
            int n = 1;
            while (nextToken.tokenName == TokenName.COMMA) {
                read(TokenName.COMMA);
                Const();
                n++;
            }
            read(TokenName.SEMICOLON);
            buildTree("consts", n);
        } else {
            buildTree("consts", 0);
        }
    }

    private void Const() {
        Name();
        read(TokenName.EQUAL);
        ConstValue();
        buildTree("const", 2);
    }

    private void ConstValue() {
        switch (nextToken.tokenName) {
            case INTEGER:
                buildTree(nextToken.value, 0);
                buildTree("<integer>", 1);
                nextToken = this.lexer.getNextToken();
                break;
            case CHAR:
                buildTree(nextToken.value, 0);
                buildTree("<char>", 1);
                nextToken = this.lexer.getNextToken();
                break;
            case IDENTIFIER:
                Name();
                break;
            default:
                System.out.println("Error: unexpected token " + nextToken.value);
                throw new Error();
        }
    }

    private void Types() {
        if (nextToken.tokenName == TokenName.TYPE) {
            read(TokenName.TYPE);
            Type();
            read(TokenName.SEMICOLON);
            int n = 1;
            while (nextToken.tokenName == TokenName.IDENTIFIER) {
                Type();
                read(TokenName.SEMICOLON);
                n++;
            }
            buildTree("types", n);
        } else {
            buildTree("types", 0);
        }
    }

    private void Type() {
        Name();
        read(TokenName.EQUAL);
        LitList();
        buildTree("type", 2);
    }

    private void LitList() {
        read(TokenName.OPENINGBRACKET);
        Name();
        int n = 1;
        while (nextToken.tokenName == TokenName.COMMA) {
            read(TokenName.COMMA);
            Name();
            n++;
        }
        buildTree("lit", n);
        read(TokenName.CLOSINGBRACKET);
    }

    private void SubProgs() {
        int n = 0;
        while (nextToken.tokenName == TokenName.FUNCTION) {
            Fcn();
            n++;
        }
        buildTree("subprogs", n);
    }

    private void Fcn() {
        read(TokenName.FUNCTION);
        Name();
        read(TokenName.OPENINGBRACKET);
        Params();
        read(TokenName.CLOSINGBRACKET);
        read(TokenName.COLON);
        Name();
        read(TokenName.SEMICOLON);
        Consts();
        Types();
        Dclns();
        Body();
        Name();
        read(TokenName.SEMICOLON);
        buildTree("fcn", 8);
    }

    private void Params() {
        Dcln();
        int n = 1;
        while (nextToken.tokenName == TokenName.SEMICOLON) {
            read(TokenName.SEMICOLON);
            Dcln();
            n++;
        }
        buildTree("params", n);
    }

    private void Dclns() {
        if (nextToken.tokenName == TokenName.VAR) {
            read(TokenName.VAR);
            Dcln();
            read(TokenName.SEMICOLON);
            int n = 1;
            while (nextToken.tokenName == TokenName.IDENTIFIER) {
                Dcln();
                read(TokenName.SEMICOLON);
                n++;
            }
            buildTree("dclns", n);
        } else {
            buildTree("dclns", 0);
        }
    }

    private void Dcln() {
        Name();
        int n = 1;
        while (nextToken.tokenName == TokenName.COMMA) {
            read(TokenName.COMMA);
            Name();
            n++;
        }
        read(TokenName.COLON);
        Name();
        buildTree("var", n + 1);
    }

    private void Body() {
        read(TokenName.BEGIN);
        Statement();
        int n = 1;
        while (nextToken.tokenName == TokenName.SEMICOLON) {
            read(TokenName.SEMICOLON);
            Statement();
            n++;
        }
        read(TokenName.END);
        buildTree("block", n);
    }

    private void Statement() {
        int n;
        switch (nextToken.tokenName) {
            case IDENTIFIER:
                Assignment();
                break;
            case OUTPUT:
                read(TokenName.OUTPUT);
                read(TokenName.OPENINGBRACKET);
                OutExp();
                n = 1;
                while (nextToken.tokenName == TokenName.COMMA) {
                    read(TokenName.COMMA);
                    OutExp();
                    n++;
                }
                read(TokenName.CLOSINGBRACKET);
                buildTree("output", n);
                break;
            case IF:
                read(TokenName.IF);
                Expression();
                read(TokenName.THEN);
                Statement();
                n = 0;
                if (nextToken.tokenName == TokenName.ELSE) {
                    read(TokenName.ELSE);
                    Statement();
                    n++;
                }
                buildTree("if", n + 2);
                break;
            case WHILE:
                read(TokenName.WHILE);
                Expression();
                read(TokenName.DO);
                Statement();
                buildTree("while", 2);
                break;
            case REPEAT:
                read(TokenName.REPEAT);
                Statement();
                n = 1;
                while (nextToken.tokenName == TokenName.SEMICOLON) {
                    read(TokenName.SEMICOLON);
                    Statement();
                    n++;
                }
                read(TokenName.UNTIL);
                Expression();
                buildTree("repeat", n + 1);
                break;
            case FOR:
                read(TokenName.FOR);
                read(TokenName.OPENINGBRACKET);
                ForStat();
                read(TokenName.SEMICOLON);
                ForExp();
                read(TokenName.SEMICOLON);
                ForStat();
                read(TokenName.CLOSINGBRACKET);
                Statement();
                buildTree("for", 4);
                break;
            case LOOP:
                read(TokenName.LOOP);
                Statement();
                n = 1;
                while (nextToken.tokenName == TokenName.SEMICOLON) {
                    read(TokenName.SEMICOLON);
                    Statement();
                    n++;
                }
                read(TokenName.POOL);
                buildTree("loop", n);
                break;
            case CASE:
                read(TokenName.CASE);
                Expression();
                read(TokenName.OF);
                n = Caseclauses() + OtherwiseClause();
                read(TokenName.END);
                buildTree("case", n + 1);
                break;
            case READ:
                read(TokenName.READ);
                read(TokenName.OPENINGBRACKET);
                Name();
                n = 1;
                while (nextToken.tokenName == TokenName.COMMA) {
                    read(TokenName.COMMA);
                    Name();
                    n++;
                }
                read(TokenName.CLOSINGBRACKET);
                buildTree("read", n);
                break;
            case EXIT:
                read(TokenName.EXIT);
                buildTree("exit", 0);
                break;
            case RETURN:
                read(TokenName.RETURN);
                Expression();
                buildTree("return", 1);
                break;
            case BEGIN:
                Body();
                break;
            default:
                buildTree("<null>", 0);
        }
    }

    private void OutExp() {
        if (nextToken.tokenName == TokenName.STRING) {
            StringNode();
            buildTree("string", 1);
        } else {
            Expression();
            buildTree("integer", 1);
        }
    }

    private void StringNode() {
        buildTree(nextToken.value, 0);
        buildTree("<string>", 1);
        nextToken = this.lexer.getNextToken();
    }

    private int Caseclauses() {
        Caseclause();
        read(TokenName.SEMICOLON);
        int n = 1;
        while (Arrays.asList(TokenName.INTEGER, TokenName.CHAR, TokenName.IDENTIFIER).contains(nextToken.tokenName)) {
            Caseclause();
            read(TokenName.SEMICOLON);
            n++;
        }
        return n;
    }

    private void Caseclause() {
        CaseExpression();
        int n = 1;
        while (nextToken.tokenName == TokenName.COMMA) {
            read(TokenName.COMMA);
            CaseExpression();
            n++;
        }
        read(TokenName.COLON);
        Statement();
        buildTree("case_clause", n + 1);
    }

    private void CaseExpression() {
        ConstValue();
        if (nextToken.tokenName == TokenName.DOUBLEDOTS) {
            read(TokenName.DOUBLEDOTS);
            ConstValue();
            buildTree("..", 2);
        }
    }

    private int OtherwiseClause() {
        if (nextToken.tokenName == TokenName.OTHERWISE) {
            read(TokenName.OTHERWISE);
            Statement();
            buildTree("otherwise", 1);
            return 1;
        }
        return 0;
    }

    private void Assignment() {
        Name();
        switch (nextToken.tokenName) {
            case ASSIGN:
                read(TokenName.ASSIGN);
                Expression();
                buildTree("assign", 2);
                break;
            case SWAP:
                read(TokenName.SWAP);
                Name();
                buildTree("swap", 2);
                break;
            default:
                System.out.println("Error: unexpected token " + nextToken.value);
                throw new Error();
        }
    }

    private void ForStat() {
        if (nextToken.tokenName == TokenName.IDENTIFIER) {
            Assignment();
        } else {
            buildTree("<null>", 0);
        }
    }

    private void ForExp() {
        if (Arrays.asList(
                TokenName.MINUS,
                TokenName.PLUS,
                TokenName.NOT,
                TokenName.EOF,
                TokenName.IDENTIFIER,
                TokenName.INTEGER,
                TokenName.CHAR,
                TokenName.OPENINGBRACKET,
                TokenName.SUCC,
                TokenName.PRED,
                TokenName.CHR,
                TokenName.ORD
        ).contains(nextToken.tokenName)) {
            Expression();
        } else {
            buildTree("true", 0);
        }
    }

    private void Expression() {
        Term();
        if (Arrays.asList(
                TokenName.LESSTHANOREQUALTO,
                TokenName.LESSTHAN,
                TokenName.GREATERTHANOREQUALTO,
                TokenName.GREATERTHAN,
                TokenName.EQUAL,
                TokenName.NOTEQUAL
        ).contains(nextToken.tokenName)) {
            switch (nextToken.tokenName) {
                case LESSTHANOREQUALTO:
                    read(TokenName.LESSTHANOREQUALTO);
                    Term();
                    buildTree("<=", 2);
                    break;
                case LESSTHAN:
                    read(TokenName.LESSTHAN);
                    Term();
                    buildTree("<", 2);
                    break;
                case GREATERTHANOREQUALTO:
                    read(TokenName.GREATERTHANOREQUALTO);
                    Term();
                    buildTree(">=", 2);
                    break;
                case GREATERTHAN:
                    read(TokenName.GREATERTHAN);
                    Term();
                    buildTree(">", 2);
                    break;
                case EQUAL:
                    read(TokenName.EQUAL);
                    Term();
                    buildTree("=", 2);
                    break;
                case NOTEQUAL:
                    read(TokenName.NOTEQUAL);
                    Term();
                    buildTree("<>", 2);
                    break;
            }
        }
    }

    private void Term() {
        Factor();
        while (Arrays.asList(TokenName.PLUS, TokenName.MINUS, TokenName.OR).contains(nextToken.tokenName)) {
            switch (nextToken.tokenName) {
                case PLUS:
                    read(TokenName.PLUS);
                    Factor();
                    buildTree("+", 2);
                    break;
                case MINUS:
                    read(TokenName.MINUS);
                    Factor();
                    buildTree("-", 2);
                    break;
                case OR:
                    read(TokenName.OR);
                    Factor();
                    buildTree("or", 2);
                    break;
            }
        }
    }

    private void Factor() {
        Primary();
        while (Arrays.asList(
                TokenName.MULTIPLY,
                TokenName.DIVIDE,
                TokenName.AND,
                TokenName.MOD
        ).contains(nextToken.tokenName)) {
            switch (nextToken.tokenName) {
                case MULTIPLY:
                    read(TokenName.MULTIPLY);
                    Primary();
                    buildTree("*", 2);
                    break;
                case DIVIDE:
                    read(TokenName.DIVIDE);
                    Primary();
                    buildTree("/", 2);
                    break;
                case AND:
                    read(TokenName.AND);
                    Primary();
                    buildTree("and", 2);
                    break;
                case MOD:
                    read(TokenName.MOD);
                    Primary();
                    buildTree("mod", 2);
                    break;
            }
        }
    }

    private void Primary() {
        switch (nextToken.tokenName) {
            case MINUS:
                read(TokenName.MINUS);
                Primary();
                buildTree("-", 1);
                break;
            case PLUS:
                read(TokenName.PLUS);
                Primary();
                break;
            case NOT:
                read(TokenName.NOT);
                Primary();
                buildTree("not", 1);
                break;
            case EOF:
                read(TokenName.EOF);
                buildTree("eof", 0);
                break;
            case IDENTIFIER:
                Name();
                if (nextToken.tokenName == TokenName.OPENINGBRACKET) {
                    read(TokenName.OPENINGBRACKET);
                    Expression();
                    int n = 1;
                    while (nextToken.tokenName == TokenName.COMMA) {
                        read(TokenName.COMMA);
                        Expression();
                        n++;
                    }
                    read(TokenName.CLOSINGBRACKET);
                    buildTree("call", n + 1);
                }
                break;
            case INTEGER:
                buildTree(nextToken.value, 0);
                buildTree("<integer>", 1);
                nextToken = this.lexer.getNextToken();
                break;
            case CHAR:
                buildTree(nextToken.value, 0);
                buildTree("<char>", 1);
                nextToken = this.lexer.getNextToken();
                break;
            case OPENINGBRACKET:
                read(TokenName.OPENINGBRACKET);
                Expression();
                read(TokenName.CLOSINGBRACKET);
                break;
            case SUCC:
                read(TokenName.SUCC);
                read(TokenName.OPENINGBRACKET);
                Expression();
                read(TokenName.CLOSINGBRACKET);
                buildTree("succ", 1);
                break;
            case PRED:
                read(TokenName.PRED);
                read(TokenName.OPENINGBRACKET);
                Expression();
                read(TokenName.CLOSINGBRACKET);
                buildTree("pred", 1);
                break;
            case CHR:
                read(TokenName.CHR);
                read(TokenName.OPENINGBRACKET);
                Expression();
                read(TokenName.CLOSINGBRACKET);
                buildTree("chr", 1);
                break;
            default:
                System.out.println("Error: unexpected token " + nextToken.value);
                throw new Error();
        }
    }

    private void Name() {
        if (nextToken.tokenName == TokenName.IDENTIFIER) {
            buildTree(nextToken.value, 0);
            buildTree("<identifier>", 1);
            nextToken = this.lexer.getNextToken();
        } else {
            System.out.println("Error: expected " + TokenName.IDENTIFIER + ". but found " + nextToken.tokenName + ".");
            throw new Error();
        }
    }

}
