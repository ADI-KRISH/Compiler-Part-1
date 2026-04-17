import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

public class Tokenizer {
    public static ArrayList<String> Tokenz=new ArrayList<>();
    public static ArrayList<String> keyword = new ArrayList<>(Arrays.asList(
            "constructor", "function", "method", "field", "static",
            "var", "char", "boolean", "void", "true", "false",
            "null", "this", "let", "do", "if", "else", "while", "return"));

    public static ArrayList<Character> symbols = new ArrayList<>(Arrays.asList(
            '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*',
            '/', '&', '|', '<', '>', '=', '~'));

    public static boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String handleIntconstant(String a) {
        if (isNumeric(a)) {
            return "<Integer Constant> " + a + " <Integer Constant>";
        }
        return a;
    }

    public static boolean checkString(
            String check) {
        return check.startsWith("\"") && check.endsWith("\"");
    }

    public static String handleStringConstant(String stringconstant) {
        if (checkString(stringconstant)) {
            return "<String Constant> " + stringconstant + " <String Constant>";
        }
        return stringconstant;
    }

    public static String lineTokenizer(String line) {
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile("\\b\\w+\\b|\\S");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String token = matcher.group();
            if (keyword.contains(token)) {
                Tokenz.add(token);
                result.append("<Keyword> ").append(token).append(" <Keyword> \n");
            } else if (symbols.contains(token.charAt(0))) {
                Tokenz.add(token);
                result.append("<Symbols> ").append(token).append(" <Symbols> \n");
            } else if (isNumeric(token)) {
                Tokenz.add(token);
                result.append(handleIntconstant(token)).append("\n");
            } else if (checkString(token)) {
                Tokenz.add(token);
                result.append(handleStringConstant(token)).append("\n");
            } else {
                Tokenz.add(token);
                result.append("<Identifier> ").append(token).append(" <Identifier> \n");
            }
        }
        return result.toString();
    }

    public static void main(String args[]) {
        String codefile = "code file (input file) path ";
        String tokenfile = "output file path";

        try (BufferedReader ip = new BufferedReader(new FileReader(codefile));
             BufferedWriter np = new BufferedWriter(new FileWriter(tokenfile))) {

            String line;
            while ((line = ip.readLine()) != null) {
                String tokenizedLine = lineTokenizer(line);
                np.write(tokenizedLine);
            }
            System.out.println(Tokenz);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
