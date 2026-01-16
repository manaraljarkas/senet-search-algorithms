public class Evaluation {

    public int evaluate(Board board, Player ComputerPlayer) {
        int score = 0;
        Player opponent = ComputerPlayer.opposite();

        for (int i = 1; i <= 30; i++) {
            int source = board.getPieceAt(i);

            if (source == ComputerPlayer.getValue()) {
                score += i * 2;

                for (int dice = 1; dice <= 5; dice++) {
                    int target = i + dice;
                    if (target > 30) continue;

                    int targetPiece = board.getPieceAt(target);
                    if (targetPiece == opponent.getValue()) {
                        score += 12;
                    }
                }

                for (int dice = 1; dice <= 5; dice++) {
                    int opponent_Position = i - dice;
                    if (opponent_Position < 1) continue;

                    int opponent_Piece = board.getPieceAt(opponent_Position);
                    if (opponent_Piece == opponent.getValue()) {
                        score -= 15;
                    }
                }

            if (i == 26 ) {
                score += 35;
            }

            if (i == 27 ) {
                score -= 50;
            }

        }
            score += 100 * board.getScore(ComputerPlayer);
            score -= 100 * board.getScore(opponent);
        }

        return score;
    }

}

