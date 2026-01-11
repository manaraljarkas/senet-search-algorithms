public class Move {
    
    public int from; // 1..30
    public int to;   // 1..30

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "(" + from + " -> " + to + ")";
    }
}
