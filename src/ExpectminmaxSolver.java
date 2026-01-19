import java.util.List;
import java.util.Map;

public class ExpectminmaxSolver {

    private int maxDepth;
    private boolean debug;
    private int visitedNodes;
    private Evaluation evaluation;
    private Player computerPlayer;
    private int lastMovedPiece = -1; // لتخزين آخر قطعة تم تحريكها

    public ExpectminmaxSolver(int depth, boolean debug, Player computerPlayer) {
        this.maxDepth = depth;
        this.debug = debug;
        this.visitedNodes = 0;
        this.evaluation = new Evaluation();
        this.computerPlayer = computerPlayer;
    }

    /* =================================================
       اختيار أفضل حركة بعد الرمية الحالية
       ================================================= */
    public Move findBestMove(Board board, int dice) {
        return findBestMove(board, dice, -1);
    }

    public Move findBestMove(Board board, int dice, int lastMovedPiece) {
        this.lastMovedPiece = lastMovedPiece;
        visitedNodes = 0;

        Player current = board.getCurrentPlayer();
        boolean isMax = (current == computerPlayer);

        double bestValue = isMax ?
                Double.NEGATIVE_INFINITY :
                Double.POSITIVE_INFINITY;

        Move bestMove = null;

        List<Move> moves = MoveRules.generateMoves(board, dice);

        for (Move m : moves) {
            Board copy = board.deepCopy();
            MoveRules.apply(copy, m, dice);

            double value = expectminmax(copy, maxDepth - 1, !isMax);

            if (lastMovedPiece != -1 && m.from == lastMovedPiece) {
                if (lastMovedPiece > 10) {
                    value += 50;
                } else if (lastMovedPiece >= 8) {
                    value += 5;
                }
            }

            if (m.from > 10) {
                value += 20;
            }

            if (isMax && value > bestValue) {
                bestValue = value;
                bestMove = m;
            }

            if (!isMax && value < bestValue) {
                bestValue = value;
                bestMove = m;
            }
        }

        if (debug) {
            System.out.println("\n===== EXPECTIMINIMAX RESULT =====");
            System.out.println("Chosen Move: " + bestMove);
            System.out.println("Evaluation Value: " + bestValue);
            System.out.println("Visited Nodes: " + visitedNodes);
            System.out.println("================================\n");
        }

        return bestMove;
    }

    /* =================================================
       خوارزمية Expectiminimax
       ================================================= */
    private double expectminmax(Board board, int depth, boolean isMaxPlayer) {

        visitedNodes++;

        if (depth == 0 || board.isFinal()) {
            return evaluate(board);
        }

        double expectedValue = 0.0;

        // عقدة حظ: رمي العصي في الأدوار القادمة
        for (Map.Entry<Integer, Double> entry :
                StickProbability.getProbabilities().entrySet()) {

            int dice = entry.getKey();
            double probability = entry.getValue();

            List<Move> moves = MoveRules.generateMoves(board, dice);

            if (moves.isEmpty()) continue;

            double best;

            if (isMaxPlayer) {
                best = Double.NEGATIVE_INFINITY;
                for (Move m : moves) {
                    Board copy = board.deepCopy();
                    MoveRules.apply(copy, m, dice);
                    best = Math.max(best,
                            expectminmax(copy, depth - 1, false));
                }
            } else {
                best = Double.POSITIVE_INFINITY;
                for (Move m : moves) {
                    Board copy = board.deepCopy();
                    MoveRules.apply(copy, m, dice);
                    best = Math.min(best,
                            expectminmax(copy, depth - 1, true));
                }
            }

            if (debug) {
                System.out.println("Chance Node | Dice: " + dice +
                        " | Prob: " + probability +
                        " | Value: " + best);
            }

            expectedValue += probability * best;
        }

        return expectedValue;
    }

    /* =================================================
       تابع تقييم الحالة
       ================================================= */
    private double evaluate(Board board) {
       // return board.getScore(Player.WHITE)
         //- board.getScore(Player.BLACK);
        return evaluation.evaluate(board, computerPlayer);
    }
}