import java.util.List;
import java.util.Scanner;

public class Game {
    private Board board;
    private Scanner scanner;
    
    public Game() {
        this.board = new Board();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        System.out.println("========================================");
        System.out.println("    Welcome to SENET Game!");
        System.out.println("    White vs Black");
        System.out.println("========================================\n");
        
        while (!board.isFinal()) {
            playTurn();
        }
        
        // Game ended - announce winner
        announceWinner();
        scanner.close();
    }
    
    private void playTurn() {
        // Display board
        board.print();
        
        Player currentPlayer = board.getCurrentPlayer();
        System.out.println(">>> " + currentPlayer + " player's turn");
        
        // Throw sticks
        int dice = Dice.throwSticksDetailed();
        Dice.displayThrow(dice);
        System.out.println();
        
        // Get possible moves
        List<Move> possibleMoves = board.getPossibleActions(dice);
        
        // Display available moves
        if (possibleMoves.isEmpty()) {
            System.out.println("No available moves for " + currentPlayer + " player.");
            System.out.println("Turn skipped.\n");
            // Switch player (move generation already checks, but we need to manually switch)
            board.setCurrentPlayer(currentPlayer.opposite());
            return;
        }
        
        System.out.println("Available moves:");
        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            System.out.println("  " + (i + 1) + ". Move piece from square " + move.from + " to square " + move.to);
        }
        System.out.println();
        
        // Get player input
        Move selectedMove = getPlayerMove(possibleMoves);
        
        // Apply move
        board.applyMove(selectedMove);
        
        System.out.println("Move applied: " + selectedMove);
        System.out.println();
    }
    
    private Move getPlayerMove(List<Move> possibleMoves) {
        while (true) {
            System.out.print("Select your move (enter number 1-" + possibleMoves.size() + "): ");
            
            if (!scanner.hasNextInt()) {
                scanner.next(); // consume invalid input
                System.out.println("Invalid input! Please enter a number.\n");
                continue;
            }
            
            int choice = scanner.nextInt();
            
            if (choice < 1 || choice > possibleMoves.size()) {
                System.out.println("Invalid choice! Please select a number between 1 and " + possibleMoves.size() + ".\n");
                continue;
            }
            
            return possibleMoves.get(choice - 1);
        }
    }
    
    private void announceWinner() {
        board.print();
        
        // Check who won - the player whose pieces are all removed wins
        boolean whiteExists = false;
        boolean blackExists = false;
        
        for (int i = 0; i < 30; i++) {
            int piece = board.getPieceAt(i + 1);
            if (piece == Player.WHITE.getValue()) {
                whiteExists = true;
            } else if (piece == Player.BLACK.getValue()) {
                blackExists = true;
            }
        }
        
        if (!whiteExists) {
            System.out.println("========================================");
            System.out.println("    WHITE PLAYER WINS!");
            System.out.println("    (All WHITE pieces removed from board)");
            System.out.println("========================================");
        } else if (!blackExists) {
            System.out.println("========================================");
            System.out.println("    BLACK PLAYER WINS!");
            System.out.println("    (All BLACK pieces removed from board)");
            System.out.println("========================================");
        } else {
            // This shouldn't happen, but handle it anyway
            System.out.println("Game ended in a draw or unexpected state.");
        }
    }
}
