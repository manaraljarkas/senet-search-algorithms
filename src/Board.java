import java.util.*;

public class Board {

    private List<Integer> cells;
    private Player currentPlayer;

    private int whiteOut;
    private int blackOut;

    private boolean whitePendingExit;
    private boolean blackPendingExit;

    private boolean whiteNeedsCheck28;
    private boolean blackNeedsCheck28;
    private boolean whiteNeedsCheck29;
    private boolean blackNeedsCheck29;

    public Board() {
        cells = new ArrayList<>(Collections.nCopies(30, 0));
        currentPlayer = Player.WHITE;
        initPieces();
    }

    private void initPieces() {
        for (int i = 0; i < 14; i++) {
            cells.set(i, (i % 2 == 0) ? 1 : 2);
        }
    }

    /* ====== GETTERS / SETTERS ====== */

    public int getPieceAt(int square) {
        return cells.get(square - 1);
    }

    public void setPieceAt(int square, int value) {
        cells.set(square - 1, value);
    }

    public void removePieceAt(int square) {
        cells.set(square - 1, 0);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer.opposite();
    }

    public boolean isPendingExit(Player p) {
        return p == Player.WHITE ? whitePendingExit : blackPendingExit;
    }

    public void setPendingExit(Player p, boolean value) {
        if (p == Player.WHITE) whitePendingExit = value;
        else blackPendingExit = value;
    }

    public boolean needsCheck28(Player p) {
        return p == Player.WHITE ? whiteNeedsCheck28 : blackNeedsCheck28;
    }

    public void setNeedsCheck28(Player p, boolean value) {
        if (p == Player.WHITE) whiteNeedsCheck28 = value;
        else blackNeedsCheck28 = value;
    }

    public boolean needsCheck29(Player p) {
        return p == Player.WHITE ? whiteNeedsCheck29 : blackNeedsCheck29;
    }

    public void setNeedsCheck29(Player p, boolean value) {
        if (p == Player.WHITE) whiteNeedsCheck29 = value;
        else blackNeedsCheck29 = value;
    }

    public void pieceOut(Player p) {
        if (p == Player.WHITE) whiteOut++;
        else blackOut++;
    }

    public int getScore(Player p) {
        return p == Player.WHITE ? whiteOut : blackOut;
    }

    /* ====== GAME END ====== */

    public boolean isFinal() {
        return whiteOut == 7 || blackOut == 7;
    }

    /* ====== COPY ====== */

    public Board deepCopy() {
        Board b = new Board();
        b.cells = new ArrayList<>(cells);
        b.currentPlayer = currentPlayer;
        b.whiteOut = whiteOut;
        b.blackOut = blackOut;
        b.whitePendingExit = whitePendingExit;
        b.blackPendingExit = blackPendingExit;
        b.whiteNeedsCheck28 = whiteNeedsCheck28;
        b.blackNeedsCheck28 = blackNeedsCheck28;
        b.whiteNeedsCheck29 = whiteNeedsCheck29;
        b.blackNeedsCheck29 = blackNeedsCheck29;
        return b;
    }

    /* ====== PRINT ====== */

    public void print() {
        System.out.println("\n======= SENET =======");
        printRow(1, 10, false);
        printRow(11, 20, true);
        printRow(21, 30, false);
        System.out.println("Score → W: " + whiteOut + " | B: " + blackOut);
        System.out.println("Turn: " + currentPlayer);
        System.out.println("=====================\n");
    }

    private void printRow(int s, int e, boolean rev) {
        if (!rev)
            for (int i = s; i <= e; i++) printCell(i);
        else
            for (int i = e; i >= s; i--) printCell(i);
        System.out.println();
    }

    private void printCell(int i) {
        int v = getPieceAt(i);
        if (v == 1) System.out.print(" W ");
        else if (v == 2) System.out.print(" B ");
        else System.out.printf(" %2d ", i);
    }
}
