public class Evaluation {

    public int evaluate(Board board, Player computerPlayer) {

        int score = 0;
        Player opponent = computerPlayer.opposite();
        var probability = StickProbability.getProbabilities();

        for (int i = 1; i <= 30; i++) {
            int piece = board.getPieceAt(i);

            if (piece == computerPlayer.getValue()) {

                boolean threatened = false;
                double expected_Threat = 0.0;
                for (int d = 1; d <= 5; d++) {
                    int opponent_position = i - d;
                    if (opponent_position >= 1 && board.getPieceAt(opponent_position) == opponent.getValue()) {
                        threatened = true;

                        double p = probability.getOrDefault(d, 0.0);
                        int base;
                        switch (d) {
                            case 1: base = 40; break;
                            case 2: base = 200; break;
                            case 3: base = 45; break;
                            case 4: base = 25; break;
                            case 5: base = 15; break;
                            default: base = 0; break;
                        }
                        double distance_Weight;
                        if (d == 2) {
                            distance_Weight = (i >= 10 && i < 20) ? 6.0 : 1.0;
                        } else {
                            distance_Weight = 1.0;
                        }
                        if (i < 20 && p >= 0.35) {
                            score -= 50;
                        }
                        expected_Threat += p * base * distance_Weight;
                    }
                    if (i + d >= 26 && i + d <= 30) {
                        score += 150;
                    }
                }

                int behind_2 = i - 2;
                if (behind_2 >= 1 &&  board.getPieceAt(behind_2) == opponent.getValue()) {

                    if (i >= 11 && i <= 20) {
                        double dice_2 = probability.getOrDefault(2, 0.0);

                        if (i < 20 && dice_2 >= 0.3) {
                            score -= 100;
                        }
                    }
                }
                if (i >= 21) {
                    score -= Math.round(expected_Threat * 0.5);
                } else {
                    score -= Math.round(expected_Threat * 3.0);
                }


                if (i >= 10) {
                    if (!threatened) {
                        score += (i - 10) * 10;
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
                if (i == 25) {
                    score += 60;
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
                if (i >= 28 && i <= 29) {
                    double p2 = probability.getOrDefault(2, 0.0);
                    score += Math.round(400 * p2);
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
        }score += 200 * board.getScore(computerPlayer);
        score -= 200 * board.getScore(opponent);

        return score;
    }
}