import java.util.ArrayList;
import java.util.List;

public class Board {
     private List<Integer> cells; // 0 فاضي، 1 أبيض، 2 أسود
    private Player currentPlayer;
        public Board() {
        cells = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            cells.add(0);

        currentPlayer = Player.WHITE;
        initPieces();
    }public Board(boolean empty) {
        cells = new ArrayList<>();
        if (empty) {
            for (int i = 0; i < 30; i++)
                cells.add(0);
        }
    }
    
    private void initPieces() {
        for (int i = 1; i <= 14; i++) {
            if (i % 2 == 1)
                cells.set(i - 1, Player.WHITE.getValue());
            else
                cells.set(i - 1, Player.BLACK.getValue());
        }
    }
    public boolean isFinal() {
        boolean whiteExists = cells.contains(Player.WHITE.getValue());
        boolean blackExists = cells.contains(Player.BLACK.getValue());
        return !whiteExists || !blackExists;
    }
    public List<Move> getPossibleActions(int dice) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            if (cells.get(i) == currentPlayer.getValue()) {
                int from = i + 1;
                int to = from + dice;

                if (to <= 30) {
                    int target = cells.get(to - 1);
                    if (target == 0 || target != currentPlayer.getValue()) {
                        moves.add(new Move(from,to));
                    }
                }
            }
        }
        return moves;
    }
    public Board deepCopy() {
        Board copy = new Board(false); 
        copy.cells = new ArrayList<>(this.cells);
        copy.currentPlayer = this.currentPlayer;
        return copy;
    }
    public void applyMove(Move move) {

        int from = move.from;
        int to = move.to;
    
        int movingPiece = cells.get(from - 1);
        int target = cells.get(to - 1);
    
        // 1️⃣ تفريغ الخانة الأصلية
        cells.set(from - 1, 0);
    
        // 2️⃣ إذا في حجر خصم → تبديل
        if (target != 0 && target != movingPiece) {
            cells.set(from - 1, target);
        }
    
        // 3️⃣ وضع الحجر بالمكان الجديد
        cells.set(to - 1, movingPiece);
    
        
    
        // 5️⃣ تبديل الدور
        currentPlayer = currentPlayer.opposite();
    }
    private String getSpecialSymbol(int index) {
        switch (index) {
            case 15: return "S";   // start back
            case 26: return "R";   //roadblock
            case 27: return "W";   // water
            case 28: return "3";
            case 29: return "2";
            case 30: return "F";   // free
            default: return "";
        }
    }
    
    
    public void print() {

        System.out.println("\n========= SENET BOARD =========");
    
        printRow(1, 10, false);
        printRow(11, 20, true);
        printRow(21, 30, false);
    
        System.out.println("Current Player: " + currentPlayer);
        System.out.println("===============================\n");
    }
    private void printRow(int start, int end, boolean reverse) {

        if (!reverse) {
            for (int i = start; i <= end; i++)
                printCell(i);
        } else {
            for (int i = end; i >= start; i--)
                printCell(i);
        }
        System.out.println();
    }
    private void printCell(int index) {

        int value = cells.get(index - 1);
    
        if (value == Player.WHITE.getValue()) {
            System.out.print("  W ");
            return;
        }
        if (value == Player.BLACK.getValue()) {
            System.out.print("  B ");
            return;
        }
    
        String special = getSpecialSymbol(index);
        if (!special.isEmpty()) {
            System.out.printf(" %2s ", special);
            return;
        }
    
        System.out.printf(" %2d ", index);
    }
    
}
