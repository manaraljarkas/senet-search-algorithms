import java.util.List;
import java.util.Map;

public class ExpectminmaxSolver {

    private int maxDepth;
    private boolean debug;
    private int visitedNodes;
    private Evaluation evaluation;
    private Player computerPlayer;
    private int lastMovedPiece = -1;

    public ExpectminmaxSolver(int depth, boolean debug, Player computerPlayer) {
        this.maxDepth = depth;
        this.debug = debug;
        this.visitedNodes = 0;
        this.evaluation = new Evaluation();
        this.computerPlayer = computerPlayer;
    }

    /* ==================================================
       اختيار أفضل حركة للكمبيوتر بعد رمية معروفة
       ================================================== */
    public Move findBestMove(Board board, int dice) {
        return findBestMove(board, dice, -1);
    }

    public Move findBestMove(Board board, int dice, int lastMovedPiece) {

        this.lastMovedPiece = lastMovedPiece;
        visitedNodes = 0;

        Node root = new Node(board, null, null);

        double bestValue = Double.NEGATIVE_INFINITY;
        Move bestMove = null;

        // توسعة الجذر حسب الرمية الحالية
        List<Node> children = root.expand(dice);

        for (Node child : children) {
            double value = expectiminimax(
                    child,
                    maxDepth - 1,
                    false,                // بعد حركة الكمبيوتر → دور الخصم
                    computerPlayer
            );

            // ===== Heuristic إضافي مرتبط بآخر قطعة =====
            Move action = child.getAction();

            if (action.from == 26) {
                if (dice == 3 || dice == 4 || dice == 5) {
                    value += 350;
                } else {
                    value -= 20;
                }
            }
            if (action.to == 26) {
                value += 300;
            }
            if (board.getPieceAt(26) == computerPlayer.getValue()
                    && dice >= 3 && dice <= 5
                    && action.from != 26) {
                value -= 300;
            }

            if (lastMovedPiece != -1 && action.from == lastMovedPiece) {
                if (lastMovedPiece > 10) {
                    value += 80;
                } else if (lastMovedPiece >= 8) {
                    value += 5;
                }
            }
            if (action.from > 10) {
                value += 20;
            }

            if (value > bestValue) {
                bestValue = value;
                bestMove = action;
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

    /* ==================================================
       خوارزمية Expectiminimax (Node-based)
       ================================================== */
    private double expectiminimax(
            Node node,
            int depth,
            boolean isMaxPlayer,
            Player computerPlayer
    ) {

        visitedNodes++;

        // شرط الإيقاف (Leaf Node)
        if (depth == 0 || node.isFinal()) {
            double value = evaluation.evaluate(
                    node.getState(),
                    computerPlayer);
            if (debug) {
                System.out.println(
                        "LEAF | Depth: " + node.getDepth()
                                + " | Value: " + value
                                + " | Action: " + node.getAction());
            }
            return value;
        }

        double expectedValue = 0.0;

        // ==============================
        // Chance Node: رميات العصي
        // ==============================
        for (Map.Entry<Integer, Double> entry :
                StickProbability.getProbabilities().entrySet()) {

            int dice = entry.getKey();
            double probability = entry.getValue();

            List<Node> children = node.expand(dice);
            if (children.isEmpty()) continue;

            double bestValueForDice;

            if (isMaxPlayer) {
                bestValueForDice = Double.NEGATIVE_INFINITY;
                for (Node child : children) {
                    bestValueForDice = Math.max(
                            bestValueForDice,
                            expectiminimax(child, depth - 1, false, computerPlayer)
                    );
                }
            } else {
                bestValueForDice = Double.POSITIVE_INFINITY;
                for (Node child : children) {
                    bestValueForDice = Math.min(
                            bestValueForDice,
                            expectiminimax(child, depth - 1, true, computerPlayer));
                }
            }

            if (debug) {
                System.out.println(
                        "CHANCE | Dice: " + dice
                                + " | Prob: " + probability
                                + " | ReturnedValue: " + bestValueForDice);
            }
            expectedValue += probability * bestValueForDice;
        }
        return expectedValue;
    }
}
