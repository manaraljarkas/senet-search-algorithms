import java.util.Random;

public class Dice {
    private static final Random random = new Random();
    
//    Throws sticks (like a 4-sided die)
    public static int throwSticks() {
        return random.nextInt(4) + 1;
    }
    

    public static int throwSticksDetailed() {
        return throwSticks();
    }
    

    public static void displayThrow(int move) {
        System.out.println("Sticks thrown: " + move + " → Move " + move + " squares");
    }
}
