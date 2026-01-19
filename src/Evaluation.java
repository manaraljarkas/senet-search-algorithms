public class Evaluation {

    public int evaluate(Board board, Player computerPlayer) {

        int score = 0;
        Player opponent = computerPlayer.opposite();

        for (int i = 1; i <= 30; i++) {
            int piece = board.getPieceAt(i);

            if (piece == computerPlayer.getValue()) {

                boolean threatened = false;
                for (int d = 1; d <= 5; d++) {
                    int enemyPos = i - d;
                    if (enemyPos >= 1 && board.getPieceAt(enemyPos) == opponent.getValue()) {
                        threatened = true;
                        switch (d) {
                            case 1: score -= 40; break;
                            case 2: score -= 70; break;
                            case 3: score -= 45; break;
                            case 4: score -= 25; break;
                            case 5: score -= 15; break;
                        }
                    }
                }

                if (i >= 10) {
                    if (!threatened) {
                        score += (i - 10) * 4;
                    } else {
                        score -= 20;
                    }
                } else {
                    score += i;
                }

                if (i >= 26 && i <= 30) {
                    score += 40;
                }

                if (i == 26) {
                    score += 80;
                }

                if (i == 27) {
                    score -= 100;
                }


                if (i == 28) {
                    score += 30;
                }
                if (i == 29) {
                    score += 50;
                }
                if (i == 30) {
                    score += 60;
                }


                if (i >= 21 && i <= 25) {
                    score += (i - 20) * 3;
                }

                for (int dice = 1; dice <= 5; dice++) {
                    int target = i + dice;
                    if (target > 30) continue;

                    if (board.getPieceAt(target) == opponent.getValue()) {
                        score += 15;
                    }
                }

                if (i < 30 && board.getPieceAt(i + 1) == opponent.getValue()) {
                    score -= 15;
                }

            }

            if (piece == opponent.getValue()) {
                score -= i * 2;
                if (i == 26) score -= 50; //
                if (i >= 28 && i <= 30) score -= 30;
            }
        }

        score += 200 * board.getScore(computerPlayer);
        score -= 200 * board.getScore(opponent);

        return score;
    }
}
