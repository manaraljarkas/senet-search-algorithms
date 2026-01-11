import java.util.*;

public class Node {

    private Board state;
    private Node parent;
    private Move action;
    private int depth;

    public Node(Board state, Node parent, Move action) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
    }

    public List<Node> expand(int dice) {
        List<Node> children = new ArrayList<>();
        for (Move m : MoveRules.generateMoves(state, dice)) {
            Board copy = state.deepCopy();
            MoveRules.apply(copy, m);
            children.add(new Node(copy, this, m));
        }
        return children;
    }

    public boolean isFinal() {
        return state.isFinal();
    }

    public Board getState() {
        return state;
    }
}
