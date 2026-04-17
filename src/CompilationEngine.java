import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CompilationEngine {
    public static boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isIdentifier(String str) {
        return str.matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    public static ArrayList<String> tokens = new ArrayList<>(Tokenizer.Tokenz);
    static StringBuilder result = new StringBuilder();
    public static ArrayList<String> symbols = new ArrayList<>(Arrays.asList(
            "{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*",
            "/", "&", "|", "<", ">", "=", "~"));
    public static ArrayList<String> ops = new ArrayList<>(Arrays.asList(
            "+", "-", "*", "/", "&", "|", "<", ">", "=", "~"));
    public static ArrayList<String> keywords = new ArrayList<>(Arrays.asList(
            "constructor", "function", "method", "field", "static",
            "var", "char", "boolean", "void", "true", "false",
            "null", "this", "let", "do", "if", "else", "while", "return"));

    public static boolean checkString(String check) {
        return check.startsWith("\"") && check.endsWith("\"");
    }

    public static void compileStatement() {
        while (!tokens.isEmpty()) {
            String token = tokens.get(0);
            if (token.equals("while")) {
                result.append("<whileStatement>\n");
                result.append("<keyword>while</keyword>\n");
                tokens.remove(0);
                compileWhile();
                result.append("</whileStatement>\n");
            } else if (token.equals("let")) {
                result.append("<statement>\n");
                result.append("<letStatement>\n");
                result.append("<keyword>let</keyword>\n");
                tokens.remove(0);
                compileLet();
                result.append("</letStatement>\n");
                result.append("</statement>\n");
            } else if (token.equals("if")) {
                result.append("<ifStatement>\n");
                result.append("<keyword>if</keyword>\n");
                tokens.remove(0);
                compileIf();
                result.append("</ifStatement>\n");
            } else {
                tokens.remove(0);
            }
        }
    }

    public static void compileWhile() {
        while (!tokens.isEmpty()) {
            String token = tokens.get(0);
            int tokenType = typeval(token);

            if (token.equals("{")) {
                result.append(typecheck(token));
                tokens.remove(0);
                compileStatement();
            } else if (token.equals("}")) {
                result.append(typecheck(token));
                tokens.remove(0);
                break;
            } else if (tokenType == 3) {
                if (token.equals("(")) {
                    result.append(typecheck(token));
                    tokens.remove(0);
                    compileExpression();
                } else {
                    result.append(typecheck(token));
                    tokens.remove(0);
                }
            } else if (tokenType == 1 || tokenType == 4 || tokenType == 5) {
                result.append(typecheck(token));
                tokens.remove(0);
            } else {
                result.append(typecheck(token));
                tokens.remove(0);
            }
        }
    }

    public static void compileIf() {
        while (!tokens.isEmpty()) {
            String token = tokens.get(0);
            int tokenType = typeval(token);

            if (token.equals("{")) {
                result.append(typecheck(token));
                tokens.remove(0);
                compileStatement();
            } else if (token.equals("}")) {
                result.append(typecheck(token));
                tokens.remove(0);
                break;
            } else if (token.equals(")")) {
                result.append(typecheck(token));
                tokens.remove(0);
            } else if (tokenType == 2) {
                if (token.equals("(")) {
                    result.append(typecheck(token));
                    tokens.remove(0);
                    compileExpression();
                } else {
                    result.append(typecheck(token));
                    tokens.remove(0);
                }
            } else if (tokenType == 1 || tokenType == 4 || tokenType == 5) {
                result.append(typecheck(token));
                tokens.remove(0);
            } else {
                result.append(typecheck(token));
                tokens.remove(0);
            }
        }
    }

    public static void compileLet() {
        while (!tokens.isEmpty()) {
            String token = tokens.get(0);
            int tokenType = typeval(token);

            if (tokenType == 5) {
                result.append(typecheck(token));
                tokens.remove(0);
            } else if (tokenType == 3) { // symbol
                if (token.equals("=")) {
                    result.append(typecheck(token));
                    tokens.remove(0);
                    compileExpression();
                } else {
                    result.append(typecheck(token));
                    tokens.remove(0);
                }
            } else {
                result.append(typecheck(token));
                tokens.remove(0);
            }
        }
    }

    public static void compileExpression() {
        result.append("<expression>\n");
        result.append(compileTerm());

    }

    public static StringBuilder compileTerm() {
        StringBuilder termResult = new StringBuilder();
        termResult.append("<term>\n");

        while (!tokens.isEmpty()) {
            String token = tokens.get(0);
            int tokenType = typeval(token);

            if (tokenType == 1 ){
                termResult.append("<term>\n").append(typecheck(token)).append("</term>\n");
                tokens.remove(token);
            }
            else if(tokenType==4) {
                termResult.append(typecheck(token)).append("</term>\n");
                tokens.remove(token);
            }
            else if(tokenType==5){
                termResult.append(typecheck(token)).append("</term>\n");
                tokens.remove(token);
            }
            else if (tokenType == 3) {
                if(Objects.equals(tokens.get(0), ";")||Objects.equals(tokens.get(0),")")){
                    termResult.append("</expression>\n").append(typecheck(tokens.get(0)));
                    tokens.remove(0);
                    return termResult;
                }
                termResult.append(typecheck(token));
                tokens.remove(0);
            } else {
                break;
            }
        }

        termResult.append("</term>\n");
        return termResult;
    }

    public static int typeval(String token) {
        if (isNumeric(token)) {
            return 1;
        } else if (keywords.contains(token)) {
            return 2;
        } else if (symbols.contains(token)) {
            return 3;
        } else if (checkString(token)) {
            return 4;
        } else if (isIdentifier(token)) {
            return 5;
        }
        return 0;
    }

    public static String typecheck(String token) {
        if (isNumeric(token)) {
            return "<integerConstant>" + token + "</integerConstant>\n";
        } else if (keywords.contains(token)) {
            return "<keyword>" + token + "</keyword>\n";
        } else if (symbols.contains(token)) {
            return "<symbol>" + token + "</symbol>\n";
        } else if (checkString(token)) {
            return "<stringConstant>" + token.substring(1, token.length() - 1) + "</stringConstant>\n";
        } else {
            return "<identifier>" + token + "</identifier>\n";
        }
    }

    public static void main(String[] args) {
        String codefile = "C:\\Users\\GS Adithya Krishna\\Documents\\java programmes\\Compiler part1\\src\\codefile.txt";
        String tokenfile = "C:\\Users\\GS Adithya Krishna\\Documents\\java programmes\\Compiler part1\\src\\Parseout.txt";

        try (BufferedReader ip = new BufferedReader(new FileReader(codefile));
             BufferedWriter np = new BufferedWriter(new FileWriter(tokenfile))) {
            String line;
            while ((line = ip.readLine()) != null) {
                 Tokenizer.lineTokenizer(line);
            }
            System.out.println(Tokenizer.Tokenz);

            tokens = new ArrayList<>(Tokenizer.Tokenz);
            compileStatement();
            np.write(result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
