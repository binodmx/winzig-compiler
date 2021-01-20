package winzigc;

public enum TokenName {
    IDENTIFIER,                 // [a-zA-Z_]([a-zA-Z]|[0-9_])*
    INTEGER,                    // [0-9]+
    WHITESPACE,                 // ( |⇥|↲)+
    CHAR,                       // 'symbol'
    STRING,                     // "symbol*"
    COMMENT,                    // #symbol↲ or {symbol*}
    NEWLINE,                    // \n
    PROGRAM,                    // program
    VAR,                        // var
    CONST,                      // const
    TYPE,                       // type
    FUNCTION,                   // function
    RETURN,                     // return
    BEGIN,                      // begin
    END,                        // end
    SWAP,                       // :=:
    ASSIGN,                     // :=
    OUTPUT,                     // output
    IF,                         // if
    THEN,                       // then
    ELSE,                       // else
    WHILE,                      // while
    DO,                         // do
    CASE,                       // case
    OF,                         // of
    DOUBLEDOTS,                 // ..
    OTHERWISE,                  // otherwise
    REPEAT,                     // repeat
    FOR,                        // for
    UNTIL,                      // until
    LOOP,                       // loop
    POOL,                       // pool
    EXIT,                       // exit
    LESSTHANOREQUALTO,          // <=
    NOTEQUAL,                   // <>
    LESSTHAN,                   // <
    GREATERTHANOREQUALTO,       // >=
    GREATERTHAN,                // >
    EQUAL,                      // =
    MOD,                        // mod
    AND,                        // and
    OR,                         // or
    NOT,                        // not
    READ,                       // read
    SUCC,                       // succ
    PRED,                       // pred
    CHR,                        // chr
    ORD,                        // ord
    EOF,                        // eof
    COLON,                      // :
    SEMICOLON,                  // ;
    SINGLEDOT,                  // .
    COMMA,                      // ,
    OPENINGBRACKET,             // (
    CLOSINGBRACKET,             // )
    PLUS,                       // +
    MINUS,                      // -
    MULTIPLY,                   // *
    DIVIDE,                     // /
    EOP,
    UNKNOWN
}