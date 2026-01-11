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
    }
    
    public Board(boolean empty) {
        cells = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            cells.add(0);
        currentPlayer = Player.WHITE;
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

                // Cannot move beyond square 30
                if (to > 30) {
                    continue;
                }
                
                // Cannot move backwards
                if (to < from) {
                    continue;
                }
                
                // Check if path is valid (can't jump over blocking squares 26, 28, 29)
                if (!canReachSquare(from, to, dice)) {
                    continue;
                }
                
                // Check destination square
                int target = cells.get(to - 1);
                
                // Cannot move to square occupied by own piece (except square 30 where piece is removed)
                if (to != 30 && target == currentPlayer.getValue()) {
                    continue;
                }
                
                // Square 30 is valid - piece will be removed
                if (to == 30) {
                    moves.add(new Move(from, to));
                    continue;
                }
                
                // Square 27 is valid destination (will be handled in applyMove)
                if (to == 27) {
                    moves.add(new Move(from, to));
                    continue;
                }
                
                // Square 15: valid, but if occupied will be handled in applyMove
                if (to == 15) {
                    moves.add(new Move(from, to));
                    continue;
                }
                
                // Other squares: must be empty or have opponent's piece
                if (target == 0 || target != currentPlayer.getValue()) {
                    moves.add(new Move(from, to));
                }
            }
        }
        return moves;
    }
    public Board deepCopy() {
        Board copy = new Board(true);
        copy.cells = new ArrayList<>(this.cells);
        copy.currentPlayer = this.currentPlayer;
        return copy;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    
    public int getPieceAt(int square) {
        if (square < 1 || square > 30) {
            return 0;
        }
        return cells.get(square - 1);
    }
    
    public boolean isSpecialSquare(int square) {
        return square == 15 || square == 26 || square == 27 || square == 28 || square == 29 || square == 30;
    }
    
    public boolean isBlockingSquare(int square) {
        return square == 26 || square == 28 || square == 29;
    }
    
    /**
     * Find first empty square before the given square (for rebirth logic)
     */
    public int findFirstEmptyBefore(int square) {
        for (int i = square - 1; i >= 1; i--) {
            if (cells.get(i - 1) == 0) {
                return i;
            }
        }
        return -1; // No empty square found
    }
    
    /**
     * Check if a move can reach destination without jumping over blocking squares
     */
    private boolean canReachSquare(int from, int to, int dice) {
        // Check if the path crosses any blocking squares (26, 28, 29) without landing on them
        for (int i = from + 1; i < to; i++) {
            if (isBlockingSquare(i)) {
                // Cannot jump over blocking squares - must land exactly on them
                return false;
            }
        }
        // Check if we're landing exactly on a blocking square
        if (isBlockingSquare(to)) {
            // Must land exactly on blocking square with the exact dice value
            return (to - from) == dice;
        }
        return true;
    }
    
    private void removePiece(int square) {
        if (square >= 1 && square <= 30) {
            cells.set(square - 1, 0);
        }
    }
    public void applyMove(Move move) {
        int from = move.from;
        int to = move.to;
    
        int movingPiece = cells.get(from - 1);
        
        // Validate move - piece must belong to current player
        if (movingPiece != currentPlayer.getValue()) {
            return;
        }
        
        // Handle square 30 - piece is removed from board (winning condition)
        if (to == 30) {
            cells.set(from - 1, 0); // Remove piece from board
            currentPlayer = currentPlayer.opposite(); // Switch turn
            return;
        }
        
        // Handle square 27 (Water) - must return to square 15 (or first empty before 15)
        if (to == 27) {
            cells.set(from - 1, 0); // Clear original position
            
            int rebirthSquare = 15;
            if (cells.get(rebirthSquare - 1) != 0) {
                // Square 15 is occupied, find first empty square before it
                int emptySquare = findFirstEmptyBefore(rebirthSquare);
                if (emptySquare != -1) {
                    rebirthSquare = emptySquare;
                } else {
                    // No empty square found - shouldn't happen in normal gameplay
                    // Place at square 15 anyway (swap with existing piece)
                    int existingPiece = cells.get(rebirthSquare - 1);
                    cells.set(rebirthSquare - 1, movingPiece);
                    // Put existing piece back at from position
                    if (existingPiece != 0 && existingPiece != movingPiece) {
                        cells.set(from - 1, existingPiece);
                    }
                    currentPlayer = currentPlayer.opposite();
                    return;
                }
            }
            
            // Place piece at rebirth square (15 or first empty before it)
            int targetAtRebirth = cells.get(rebirthSquare - 1);
            cells.set(rebirthSquare - 1, movingPiece);
            
            // If rebirth square had an opponent's piece, swap it back to original position
            if (targetAtRebirth != 0 && targetAtRebirth != movingPiece) {
                cells.set(from - 1, targetAtRebirth);
            }
            
            currentPlayer = currentPlayer.opposite();
            return;
        }
        
        // Handle square 15 (Rebirth) - if occupied, move to first empty square before it
        if (to == 15) {
            cells.set(from - 1, 0); // Clear original position
            
            int actualDestination = 15;
            if (cells.get(actualDestination - 1) != 0) {
                // Square 15 is occupied, find first empty square before it
                int emptySquare = findFirstEmptyBefore(actualDestination);
                if (emptySquare != -1) {
                    actualDestination = emptySquare;
                } else {
                    // No empty square found - swap with existing piece at 15
                    int existingPiece = cells.get(actualDestination - 1);
                    cells.set(actualDestination - 1, movingPiece);
                    if (existingPiece != 0 && existingPiece != movingPiece) {
                        cells.set(from - 1, existingPiece);
                    }
                    currentPlayer = currentPlayer.opposite();
                    return;
                }
            }
            
            // Place piece at actual destination
            int targetAtDest = cells.get(actualDestination - 1);
            cells.set(actualDestination - 1, movingPiece);
            
            // If destination had an opponent's piece, swap it back to original position
            if (targetAtDest != 0 && targetAtDest != movingPiece) {
                cells.set(from - 1, targetAtDest);
            }
            
            currentPlayer = currentPlayer.opposite();
            return;
        }
        
        // Handle regular moves
        int target = cells.get(to - 1);
        
        // Clear original position
        cells.set(from - 1, 0);
        
        // If destination has opponent's piece, swap positions
        if (target != 0 && target != movingPiece) {
            cells.set(from - 1, target);
        }
        
        // Place piece at destination
        cells.set(to - 1, movingPiece);
        
        // Switch turn
        currentPlayer = currentPlayer.opposite();
    }
    private String getSpecialSymbol(int index) {
        switch (index) {
            case 15: return "S";   // start back
            case 26: return "R";   //roadblock
            case 27: return "P";   // pool
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
    
    public void printWithMoves(int dice, List<Move> moves) {
        print();
        System.out.println("Dice result: " + dice);
        if (moves.isEmpty()) {
            System.out.println("No available moves. Turn skipped.");
        } else {
            System.out.println("Available moves:");
            for (int i = 0; i < moves.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + moves.get(i));
            }
        }
        System.out.println();
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
