import java.util.List;
import java.util.Scanner;

public class Game {

    private Board board;
    private Scanner scanner;

    private Player humanColor;
    private Player computerColor;

    public Game() {
        board = new Board();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("========================================");
        System.out.println("        WELCOME TO SENET");
        System.out.println("        WHITE vs BLACK");
        System.out.println("========================================\n");

        // اختيار اللون
        System.out.println("Choose your color:");
        System.out.println("1 - WHITE");
        System.out.println("2 - BLACK");

        int choice = scanner.nextInt();
        humanColor = (choice == 1) ? Player.WHITE : Player.BLACK;
        computerColor = humanColor.opposite();

        System.out.println("You play as: " + humanColor);
        System.out.println("Computer plays as: " + computerColor + "\n");

        while (!board.isFinal()) {
            playTurn();
        }

        announceWinner();
        scanner.close();
    }

    private void playTurn() {

        board.print();

        Player current = board.getCurrentPlayer();
        System.out.println(">>> Turn: " + current);

        // 🎲 رمي العصي
        int dice = Dice.throwSticks();
        Dice.displayThrow(dice);

        // 🎯 توليد الحركات (من MoveRules)
        List<Move> moves = MoveRules.generateMoves(board, dice);

        if (moves.isEmpty()) {
            System.out.println("No available moves. Turn skipped.\n");
            board.switchPlayer();
            return;
        }

        System.out.println("Available moves:");
        for (int i = 0; i < moves.size(); i++) {
            System.out.println((i + 1) + ". " + moves.get(i));
        }

        // Move selected = getPlayerMove(moves);

        Move selected;

        if (current == humanColor) {
            selected = getPlayerMove(moves);
        } else {
            selected = getComputerMove(moves);
        }

        // ✅ تطبيق الحركة
        MoveRules.apply(board, selected);

        System.out.println("Applied move: " + selected + "\n");
    }

    private Move getComputerMove(List<Move> moves) {
        System.out.println("Computer is thinking...");
        return moves.get(0); // اختيار أول حركة فقط (بدائي)
    }

    private Move getPlayerMove(List<Move> moves) {
        while (true) {
            System.out.print("Choose move (1-" + moves.size() + "): ");

            if (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Invalid input.");
                continue;
            }

            int choice = scanner.nextInt();
            if (choice < 1 || choice > moves.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            return moves.get(choice - 1);
        }
    }

    private void announceWinner() {
        board.print();

        if (board.getScore(Player.WHITE) == 7) {
            System.out.println("🏆 WHITE WINS!");
        } else if (board.getScore(Player.BLACK) == 7) {
            System.out.println("🏆 BLACK WINS!");
        } else {
            System.out.println("Game ended unexpectedly.");
        }
    }
}
