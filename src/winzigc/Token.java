package winzigc;

public class Token {
    TokenName tokenName;
    String value;

    public Token(TokenName tokenName, String value) {
        this.tokenName = tokenName;
        this.value = value;
    }
}
