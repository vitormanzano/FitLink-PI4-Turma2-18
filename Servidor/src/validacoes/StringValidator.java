package validacoes;

// Caso fique mais fácil para do lado do android, pode colocar para retornar uma exception com a mensagem
public class StringValidator {
    public static boolean ValidateMinimunMaximum(String value, int min, int max) {
        return value.length() > min && value.length() < max;
    }

    public static boolean ValidateIfLessThan(String value, int min) {
        return value.length() > min;
    }

    public static boolean ValidateIfGreaterThan(String value, int max) {
        return value.length() < max;
    }

    public static boolean ValidateIfLengthEquals(String value, int length) {
        return value.length() == length;
    }
}
