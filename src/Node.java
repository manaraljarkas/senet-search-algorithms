import java.util.ArrayList;
import java.util.List;

public class Node {
    private Board state;
    private Node parent;
    private Move action;
    private int cost;
    private int depth;
    public Node(Board state, Node parent, Move action, int cost) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.cost = cost;
        this.depth = parent == null ? 0 : parent.depth + 1;
    }
        public List<Node> getNextStates(int dice) {
        List<Node> children = new ArrayList<>();

        for (Move move : state.getPossibleActions(dice)) {
            Board newState = state.deepCopy();
            newState.applyMove(move);

            children.add(new Node(newState, this, move, cost + 1));
        }
        return children;
    }

    public boolean isFinal() {
        return state.isFinal();
    }

}
