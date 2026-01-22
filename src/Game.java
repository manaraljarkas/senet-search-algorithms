import java.util.List;
import java.util.Scanner;

public class Game {

    private Board board;
    private Scanner scanner;

    private Player humanColor;
    private Player computerColor;
    private ExpectminmaxSolver solver;
    private int searchDepth;
    private boolean debugMode;
    private int dice;

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
        System.out.print("Enter search depth: ");
        searchDepth = scanner.nextInt();

        System.out.print("Enable algorithm debug? (1 = yes, 0 = no): ");
        debugMode = scanner.nextInt() == 1;

        solver = new ExpectminmaxSolver(searchDepth, debugMode, computerColor);

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
         dice = Dice.throwSticks();
        Dice.displayThrow(dice);

        // 🔍 فحص خاص للمواضع 28 و 29 بعد رمي العصي (إذا لم يحصل على العصي المطلوبة)
        checkSpecialPositionsAfterRoll(current, dice);

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
            selected = getComputerMove(moves, dice);
        }

        // ✅ تطبيق الحركة (مع قيمة النرد للتحقق من القواعد الخاصة)
        MoveRules.apply(board, selected, dice);

        System.out.println("Applied move: " + selected + "\n");

        // 🔍 فحص خاص: إذا كان الحجر لا يزال على 28 أو 29 بعد الحركة وكان لديه فرصة للخروج
        checkSpecialPositionsAfterMove(current, dice);
    }

    private int lastMovedPiece = -1; // تتبع آخر قطعة حركها الكمبيوتر

    private Move getComputerMove(List<Move> moves, int dice) {
        System.out.println("Computer is thinking...");

        // أولًا: حاول تحريك آخر قطعة تم تحريكها إذا كانت الحركة ممكنة
        Move lastPieceMove = null;
        for (Move m : moves) {
            if (m.from == lastMovedPiece) {
                lastPieceMove = m;
                break;
            }
        }

        int pieceToPrioritize = (lastPieceMove != null && lastMovedPiece != -1) ? lastMovedPiece : -1;
        Move bestMove = solver.findBestMove(board, dice, pieceToPrioritize);
        
        lastMovedPiece = bestMove.to;
        return bestMove;
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

    private void checkSpecialPositionsAfterRoll(Player current, int dice) {
        int pv = current.getValue();
        
        // فحص الموضع 28: إذا كان يجب تحريكه في هذا الدور (من الدور السابق)
        if (board.mustMoveBack28(current) && board.getPieceAt(28) == pv) {
            System.out.println("Stone on position 28 didn't exit when it had the chance. Moving back...");
            MoveRules.sendBackFromSpecialPosition(board, current, 28);
            board.print(); // طباعة اللوحة بعد تحريك الحجر
            board.setMustMoveBack28(current, false);
            board.setNeedsCheck28(current, false);
        }
        
        // فحص الموضع 29: إذا كان يجب تحريكه في هذا الدور (من الدور السابق)
        if (board.mustMoveBack29(current) && board.getPieceAt(29) == pv) {
            System.out.println("Stone on position 29 didn't exit when it had the chance. Moving back...");
            MoveRules.sendBackFromSpecialPosition(board, current, 29);
            board.print(); // طباعة اللوحة بعد تحريك الحجر
            board.setMustMoveBack29(current, false);
            board.setNeedsCheck29(current, false);
        }
        
        // فحص الموضع 28: يحتاج إلى 3 عصي
        // إذا لم يحصل على 3 عصي، ارجع الحجر فورًا
        if (board.needsCheck28(current) && board.getPieceAt(28) == pv) {
            if (dice != 3) {
                // إذا لم يحصل على 3 عصي، ارجع الحجر إلى 15 أو أقرب موضع متاح للخلف
                System.out.println("Stone on position 28 didn't get 3 sticks. Moving back...");
                MoveRules.sendBackFromSpecialPosition(board, current, 28);
                board.print(); // طباعة اللوحة بعد تحريك الحجر
                // إزالة العلامة (فرصة واحدة فقط)
                board.setNeedsCheck28(current, false);
            }
            // إذا حصل على 3 عصي، لا نزيل العلامة بعد - سنتحقق بعد الحركة
        }
        
        // فحص الموضع 29: يحتاج إلى 2 عصي
        // إذا لم يحصل على 2 عصي، ارجع الحجر فورًا
        if (board.needsCheck29(current) && board.getPieceAt(29) == pv) {
            if (dice != 2) {
                // إذا لم يحصل على 2 عصي، ارجع الحجر إلى 15 أو أقرب موضع متاح للخلف
                System.out.println("Stone on position 29 didn't get 2 sticks. Moving back...");
                MoveRules.sendBackFromSpecialPosition(board, current, 29);
                board.print(); // طباعة اللوحة بعد تحريك الحجر
                // إزالة العلامة (فرصة واحدة فقط)
                board.setNeedsCheck29(current, false);
            }
            // إذا حصل على 2 عصي، لا نزيل العلامة بعد - سنتحقق بعد الحركة
        }
    }

    private void checkSpecialPositionsAfterMove(Player current, int dice) {
        int pv = current.getValue();
        
        // فحص الموضع 28: إذا حصل على 3 عصي ولكن لم يخرج
        if (board.needsCheck28(current) && board.getPieceAt(28) == pv && dice == 3) {
            // الحجر لا يزال على 28 رغم وجود خيار الخروج - سيتم تحريكه في الدور القادم
            board.setMustMoveBack28(current, true);
            board.setNeedsCheck28(current, false);
        }
        
        // فحص الموضع 29: إذا حصل على 2 عصي ولكن لم يخرج
        if (board.needsCheck29(current) && board.getPieceAt(29) == pv && dice == 2) {
            // الحجر لا يزال على 29 رغم وجود خيار الخروج - سيتم تحريكه في الدور القادم
            board.setMustMoveBack29(current, true);
            board.setNeedsCheck29(current, false);
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
