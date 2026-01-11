import java.util.Random;

public class Dice {

    private static final Random random = new Random();

    public static int throwSticks() {
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += random.nextBoolean() ? 1 : 0;
        }
        return (sum == 0) ? 5 : sum;
    }

    public static void displayThrow(int move) {
        System.out.println("Sticks result: " + move);
    }
}
