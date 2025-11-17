package validacoes;

// Caso fique mais fácil para do lado do android, pode colocar para retornar uma exception com a mensagem
public class StringValidator {
    public static boolean validateMinimunMaximum(String value, int min, int max) {
        return value.length() > min && value.length() < max;
    }

    public static boolean validateIfLessThan(String value, int min) {
        return value.length() > min;
    }

    public static boolean validateIfGreaterThan(String value, int max) {
        return value.length() < max;
    }

    public static boolean validateIfLengthEquals(String value, int length) {
        return value.length() == length;
    }
}
