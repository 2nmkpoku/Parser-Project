/*
COURSE: COSC455xxx
Assignment: Program 1
Name: Mkpoku, Nnamdi
Name: Olaniyi, Joyce (2nd Student, if applicable)
*/
package COSC455.ParserExample;
//  ************** NOTE: REQUIRES JAVA 11 OR ABOVE! ******************

import java.util.logging.Logger;
import static COSC455.ParserExample.Token.*;

/*
 * GRAMMAR FOR PROCESSING SIMPLE SENTENCES:
 *
 * <SENTENCE> ::= <NP> <VP> <NP> <PP> <SENTENCE_TAIL>
 * <SENTENCE_TAIL> ::= <CONJ> <SENTENCE> | <EOS>
 *
 * <NP> ::= <ART> <ADJ_LIST> <NOUN>
 * <ADJ_LIST> ::= <ADJ> <ADJ_TAIL> | <<EMPTY>>
 * <ADJ_TAIL> ::= <COMMA> <ADJ> <ADJ_TAIL> | <<EMPTY>>
 *
 * <VP> ::= <ADV> <VERB> | <VERB>
 * <PP> ::= <PREP> <NP> | <<EMPTY>>
 *
 * // *** Terminal Productions (Actual terminals omitted, but they are just the
 * valid words in the language). ***
 *
 * <COMMA> ::= ','
 * <EOS> ::= '.' | '!'
 *
 * <ADJ> ::= ...adjective list...
 * <ADV> ::= ...adverb list...
 * <ART> ::= ...article list...
 * <CONJ> ::= ...conjunction list...
 * <NOUN> ::= ...noun list...
 * <PREP> ::= ...preposition list...
 * <VERB> ::= ...verb list....
 */

/**
 * The Syntax Analyzer.
 *
 * ************** NOTE: REQUIRES JAVA 11 OR ABOVE! ******************
 */
public class Parser1 {

    // The lexer which will provide the tokens
    private final LexicalAnalyzer lexer;

    // the actual "code generator"
    private final CodeGenerator codeGenerator;

    /**
     * The constructor initializes the terminal literals in their vectors.
     *
     * @param lexer The Lexer Object
     */
    public Parser1(LexicalAnalyzer lexer, CodeGenerator codeGenerator) {
        this.lexer = lexer;
        this.codeGenerator = codeGenerator;

        // Change this to automatically prompt to see the Open WebGraphViz dialog or
        // not.
        MAIN.PROMPT_FOR_GRAPHVIZ = true;
    }

    /*
     * Since the "Compiler" portion of the code knows nothing about the start rule,
     * the "analyze" method
     * must invoke the start rule.
     *
     * Begin analyzing...
     */
    void analyze() {
        try {
            // Generate header for our output
            var startNode = codeGenerator.writeHeader("PARSE TREE");

            // THIS IS OUR START RULE
            BEGIN_PARSING(startNode);

            // generate footer for our output
            codeGenerator.writeFooter();

        } catch (ParseException ex) {
            final String msg = String.format("%s\n", ex.getMessage());
            Logger.getAnonymousLogger().severe(msg);
        }
    }

    /**
     * The start rule for the grammar.
     *
     * @param parentNode The parent node for the parse tree
     * @throws ParseException If there is a syntax error
     */
    void BEGIN_PARSING(final TreeNode parentNode) throws ParseException {
        // TODO: Change if necessary!
        PROGRAM(parentNode);

    }

    void PROGRAM(final TreeNode fromNode) throws ParseException {
        final var nodeName = codeGenerator.addNonTerminalToParseTree(fromNode);

        STMT_LIST(nodeName);
        //EMPTY(nodeName);
    }

    // <stmt_list  <stmt> <stmt_list> | <empty>
    void STMT_LIST(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(IDENTIFIER) || lexer.isCurrentToken(READING) || lexer.isCurrentToken(WRITING) || lexer.isCurrentToken(IF_STMT) || lexer.isCurrentToken(WHILE_STMT))  {
            
            STMT(treeNode);
            STMT_LIST(treeNode);
        } else {
            EMPTY(treeNode);
        }

    }

    
    // stmt id := expr | read id | write expr
    void STMT(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(IDENTIFIER)) {
            ID(treeNode);
            ASSIGN(treeNode);
            EXPR(treeNode);
        } else if (lexer.isCurrentToken(READING)) {
            READ(treeNode);
            ID(treeNode);
        } else if (lexer.isCurrentToken(WRITING)) {
            WRITE(treeNode);
            EXPR(treeNode);
        } else if (lexer.isCurrentToken(IF_STMT)) {
            IF(treeNode);
            CONDITIONAL(treeNode);
            THEN(treeNode);
            STMT_LIST(treeNode);
            FI(treeNode);
        }else {
            WHILE(treeNode);
            CONDITIONAL(treeNode);
            DO(treeNode);
            STMT_LIST(treeNode);
            OD(treeNode);
        }

    }

    

    //private void COND(TreeNode treeNode) {
    //}

   
    void EXPR(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        TERM(treeNode);
        TERM_TAIL(treeNode);
    }

    
    // term_tail add_opp term term_tail | empty
    void TERM_TAIL(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(ADDITION_OP)) {
            ADD_OP(treeNode);
            TERM(treeNode);
            TERM_TAIL(treeNode);
        } else {
            EMPTY(treeNode);
        }
    }

    
    // term -> factor factor_tail
    void TERM(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        FACTOR(treeNode);
        FACTOR_TAIL(treeNode);

    }

    
    // FACTOR_TAIL -> MULTI_OP FACTOR FACTOR_TAIL | EMPTY
    void FACTOR_TAIL(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(MULTIPLICATION_OP)) {
            MULT_OP(treeNode);
            FACTOR(treeNode);
            FACTOR_TAIL(treeNode);
        } else {
            EMPTY(treeNode);
        }
    }

    // factor-> ( expr )| id | number
    void FACTOR(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(OPEN_P)) {
            OPEN_PS(treeNode);
            EXPR(treeNode);
            CLOSE_PS(treeNode);
        } else if (lexer.isCurrentToken(IDENTIFIER)) {
            ID(treeNode);
        } else {
            NUMBERS(treeNode);
        }
    }

    // add_op +|-
    void ADD_OP(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(ADDITION_OP)) {
            PLUS(treeNode);
        } else
            MINUS(treeNode);
    }

    // mult_op *|/
    void MULT_OP(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(MULTIPLICATION_OP)) {
            MULT(treeNode);
        } else
            DIV(treeNode);
    }

    void CONDITIONAL(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        
            EXPR(treeNode);
            RELATION(treeNode);
            EXPR(treeNode);
        
    }

    void RELATION(final TreeNode fromNode) throws ParseException {
        final var treeNode = codeGenerator.addNonTerminalToParseTree(fromNode);

        if (lexer.isCurrentToken(LESS_OP)) {
            LESS_THAN(treeNode);
        }

        else if (lexer.isCurrentToken(GREAT_OP)) {
            GREAT_THAN(treeNode);
        }

        else if (lexer.isCurrentToken(LESSEQ_OP)) {
            LESS_EQUAL(treeNode);
        }

        else if (lexer.isCurrentToken(GREATEQ_OP)) {
            GREAT_EQUAL(treeNode);
        }

        else if (lexer.isCurrentToken(EQUALTO_OP)) {
            EQUAL_TO(treeNode);
        }

        else {
            NOTEQUAL_TO(treeNode);
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////
    // For the sake of completeness, each terminal-token has its own method,
    // though they all do the same thing here. In a "REAL" program, each terminal
    // would likely have unique code associated with it.
    /////////////////////////////////////////////////////////////////////////////////////
    void EMPTY(final TreeNode fromNode) throws ParseException {
        codeGenerator.addEmptyToTree(fromNode);
    }

    // <EOS>
    

    void ID(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(IDENTIFIER)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an Identifier", fromNode);
        }
    }
    
    void ASSIGN(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(ASSIGN_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an Assignment", fromNode);
        }
    }
    
    void READ(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(READING)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Read expr", fromNode);
        }
    }

    void WRITE(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(WRITING)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Write expr", fromNode);
        }
    }

    void IF(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(IF_STMT)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an if statement", fromNode);
        }
    }

    void FI(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(ENDIF_STMT)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an endif statement", fromNode);
        }
    }

    void THEN(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(THEN_STMT)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an then statement", fromNode);
        }
    }

    void WHILE(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(WHILE_STMT)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an while statement", fromNode);
        }
    }

    void DO(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(DO_STMT)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an do statement", fromNode);
        }
    }

    void OD(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(ENDWHILE_STMT)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is an endwhile statement", fromNode);
        }
    }

    // Open ()
    void OPEN_PS(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(OPEN_P)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a (", fromNode);
        }
    }

    void CLOSE_PS(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(CLOSE_P)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a )", fromNode);
        }
    }

    // <NUMBER>
    void NUMBERS(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(NUMBER)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Number", fromNode);
        }
    }

    // Addition
    void PLUS(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(ADDITION_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("an Addtion operator", fromNode);
        }
    }

    // Subtraction
    void MINUS(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(SUBTRACTION_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Subtraction operator", fromNode);
        }
    }

    // Multiplication
    void MULT(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(MULTIPLICATION_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Multiplication operator", fromNode);
        }
    }

    // Division
    void DIV(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(DIVISION_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Division operator", fromNode);
        }
    }

    /*void COND(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(COND1)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("this is a condition", fromNode);
        }
    }
    */ 

    void LESS_THAN(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(LESS_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Less than operator", fromNode);
        }
    }

    void GREAT_THAN(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(GREAT_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Greater than operator", fromNode);
        }
    }

    void LESS_EQUAL(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(LESSEQ_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Less than equal to operator", fromNode);
        }
    }

    void GREAT_EQUAL(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(GREATEQ_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a Greater than equal to operator", fromNode);
        }
    }

    void EQUAL_TO(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(EQUALTO_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("an equal to operator", fromNode);
        }
    }

    void NOTEQUAL_TO(final TreeNode fromNode) throws ParseException {
        if (lexer.isCurrentToken(NOTEQUAL_OP)) {
            addTerminalAndAdvanceToken(fromNode);
        } else {
            raiseException("a not equal to operator", fromNode);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Terminal:
    // Test its type and continue if we really have a terminal node, syntax error if
    // fails.
    void addTerminalAndAdvanceToken(final TreeNode fromNode) throws ParseException {
        final var currentTerminal = lexer.getCurrentToken();

        String nodeLabel = String.format("<%s>", currentTerminal);
        final var terminalNode = codeGenerator.addNonTerminalToParseTree(fromNode, nodeLabel);

        codeGenerator.addTerminalToTree(terminalNode, lexer.getCurrentLexeme());
        lexer.advanceToken();
    }

    // Handle all the errors in one place for cleaner parser code.
    private void raiseException(String expected, TreeNode fromNode) throws ParseException {
        final var template = "SYNTAX ERROR: '%s' was expected but '%s' was found.";
        final var err = String.format(template, expected, lexer.getCurrentLexeme());
        codeGenerator.syntaxError(err, fromNode);
    }
}
