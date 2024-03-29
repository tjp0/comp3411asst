/* z3459006 COMP3411 assignment 3. See agent.java for overview */
package ninetactoe;

public abstract class Strategy {
    protected int states_evaluated = 0;
    public abstract int getBestMove(Board b);
    public int getStatesEvaluated() {
        return states_evaluated;
    }
}
