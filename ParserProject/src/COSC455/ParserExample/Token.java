package COSC455.ParserExample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All the Tokens/Terminals Used by the parser. The purpose of the enum type
 * here is to eliminate the need for direct string comparisons which is generally
 * slow, as being difficult to maintain. (We want Java's "static type checking"
 * to do as much work for us as it can!)
 *
 * !!!!!!!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!!!!
 * -----------------------------------------------------------------------------
 * IN MOST REAL CASES, THERE WILL BE ONLY ONE LEXEME PER Token. !!!
 * -----------------------------------------------------------------------------
 *
 * !!!!!!!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!!!!
 *
 * The fact that several lexemes exist per token in this example is because this
 * is to parse simple English sentences where most of the token types have many
 * words (lexemes) that could fit. This is generally NOT the case in most
 * programming languages!!!
 */
public enum Token {
    
    // THESE ARE NOT USED IN THE GRAMMAR, BUT MIGHT BE USEFUL...  :)
    EOF, // End of file
    OTHER, // Could be "ID" in a "real programming language"
    IDENTIFIER("sum", "x", "y", "n", "count", "abs", "i"),
    ASSIGN_OP(":="),
    IF_STMT("if"),
    ENDIF_STMT("fi"),
    THEN_STMT("then"),
    WHILE_STMT("while"),
    DO_STMT("do"),
    ENDWHILE_STMT("od"),
    READING("read"),
    WRITING("write"),
    OPEN_P("("),
    CLOSE_P(")"),
    ADDITION_OP("+"),
    SUBTRACTION_OP("-"),
    MULTIPLICATION_OP("*"),
    DIVISION_OP("/"),
    LESS_OP("<"),
    GREAT_OP(">"),
    LESSEQ_OP("<="),
    GREATEQ_OP(">="),
    EQUALTO_OP("="),
    NOTEQUAL_OP("!="),
    //COND1("true", "false"),

    NUMBER("0","1","2","3","4","5","6","7","8","9"); // A sequence of digits.
    

    /**
     * A list of all lexemes for each token.
     */
    private final List<String> lexemeList;

    Token(final String... tokenStrings) {
        lexemeList = new ArrayList<>(tokenStrings.length);
        lexemeList.addAll(Arrays.asList(tokenStrings));
    }

    /**
     * Get a Token object from the Lexeme string.
     *
     * @param string The String (lexeme) to convert to a Token
     * @return A Token object based on the input String (lexeme)
     */
    public static Token fromLexeme(final String string) {
        // Just to be safe...
        final var lexeme = string.trim();

        // An empty string/lexeme should mean no more tokens to process.
        if (lexeme.isEmpty()) {
            return EOF;
        }

        // Regex for one or more digits optionally followed by . and more digits.
        // (doesn't handle "-", "+" etc., only digits)
        if (lexeme.matches("\\d+(?:\\.\\d+)?")) {
            return NUMBER;
        }

        // Search through ALL lexemes looking for a match with early bailout.
        for (var token : Token.values()) {
            if (token.lexemeList.contains(lexeme)) {
                // early bailout from for loop.
                return token;
            }
        }

        // NOTE: Other could represent an ID, for example.
        return OTHER;
    }
}
