package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Class which represents a simulated player using the MCTS algorithm
 */
public final class MctsPlayer implements Player {


    private PlayerId mctsPlayerId;
    private SplittableRandom rng;
    private int iterations;

    //A constant useful to compute the comparison between nodes
    private final static int VALUE_C = 40;

    /**
     * Constructor of MctsPlayer
     * @param ownId (PlayerId) : the identity of the MctsPlayer
     * @param rngSeed (long) : the seed for the random simulated games
     * @param iterations (int) : the number of iterations 
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        if(iterations<9) {
            throw new IllegalArgumentException("nombre d'itérations insuffisant");
        }
        this.iterations = iterations;
        mctsPlayerId = ownId;
        this.rng = new SplittableRandom(rngSeed);
    }

    /**
     * Override of the method cardToPlay of interface Player
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        Node root = new Node(state, hand, mctsPlayerId);
        //Apply the MCTS algorithm to the root
        for(int i=0;i<iterations;++i) {
            explorer(root);
        }

        int index = root.findBestChild(0);
        Card c = root.nodeTurnState.trick().playableCards(hand).get(index);
        return c;
    }

    /**
     * Generates a simulated random turn with the hand of cards of the MctsPlayer and all the other unplayed cards
     * @param mctsHand (CardSet) : the hand of cards of the MctsPlayer
     * @param state (TurnState) : the state of the game (to be able to simulate a turn from this state of the game)
     * @return (Score) : the score obtained at the end of the simulated turn
     */
    private Score randomTurn(CardSet mctsHand, TurnState state) {

        TurnState turn = state;
        CardSet mctsCopyHand = mctsHand;

        //While it is not the end of the simulated turn, cards continue to be played by the players and tricks continue to be 
        //collected. Note that the card placed are the unplayed cards of the turn.
        while(!turn.isTerminal()) {
            if(turn.nextPlayer() == mctsPlayerId) {
                CardSet playableMctsHand = turn.trick().playableCards(mctsCopyHand);
                Card cardToPlay = playableMctsHand.get(rng.nextInt(playableMctsHand.size()));
                turn = turn.withNewCardPlayedAndTrickCollected(cardToPlay);
                mctsCopyHand = mctsCopyHand.remove(cardToPlay);
            }
            else {
                CardSet playableUnplayedCards = turn.unplayedCards().difference(mctsCopyHand);
                playableUnplayedCards = turn.trick().playableCards(playableUnplayedCards);
                turn = turn.withNewCardPlayedAndTrickCollected(playableUnplayedCards.get(rng.nextInt(playableUnplayedCards.size())));
            }
        }
        return turn.score();
    }

    /**
     * Creates a new child to the given parent node  and updates the score of all the node contained 
     * in the given list
     * @param parents (Node): the node that we want to add a child to
     * @param path (List<Node>): the path of nodes to go from the root of the tree to the child that is going to be 
     * created
     */
    private void addNode(Node parent, List<Node> path) {
        TurnState state = parent.nodeTurnState;
        CardSet mctsCopyHand = parent.mctsHand;
        if(state.isTerminal()) {
            Score score = state.score();
            updateScore(score, path);
            return;
        }
        if(state.nextPlayer() == mctsPlayerId) {
            Card c = state.trick().playableCards(parent.cardSetofInexistantNodes).get(0);
            state = state.withNewCardPlayedAndTrickCollected(c);
            mctsCopyHand= mctsCopyHand.remove(c);
            parent.cardSetofInexistantNodes = parent.cardSetofInexistantNodes.remove(c);
        }
        else {
            Card c = state.trick().playableCards(parent.cardSetofInexistantNodes).get(0);
            state = state.withNewCardPlayedAndTrickCollected(c);
            parent.cardSetofInexistantNodes = parent.cardSetofInexistantNodes.remove(c);
        }
        Node newChild = new Node(state, mctsCopyHand, mctsPlayerId);
        Score scoreRandomTurn = randomTurn(mctsCopyHand, newChild.nodeTurnState);
        newChild.scoreForNode = scoreRandomTurn.turnPoints(parent.nodeTurnState.nextPlayer().team());
        newChild.finishedTurns += 1;
        putChildInArray(parent, newChild);
        updateScore(scoreRandomTurn, path);
    }

    /**
     * Put the given child in the array of childs of the given parent
     * @param parent (Node) : the parent node
     * @param child (Node) : the child node to add
     */
    private void putChildInArray(Node parent, Node child) {
        int index = parent.childsOfNode.length-(parent.cardSetofInexistantNodes.size()+1);
        parent.childsOfNode[index] = child;
    }

    /**
     * Updates of the score and finished turns count of all the nodes in the given list
     * @param path(List<Node>): the path of nodes to go from the root of the tree to the child that is going to be 
     * created
     * @param score (Score): the score obtained with the random turn of the child
     */
    private void updateScore(Score score, List<Node> path) {
        for(int i=0;i<path.size();++i) {
            if(i==0) {
                path.get(i).scoreForNode += score.turnPoints(mctsPlayerId.team().other());
                path.get(i).finishedTurns += 1;
            }
            else {
                path.get(i).scoreForNode += score.turnPoints(path.get(i-1).nodeTurnState.nextPlayer().team());
                path.get(i).finishedTurns += 1;
            }
        }
    }

    /**
     * Finds the node to explore and add a child (if possible) to this node
     * @param root (Node): the root of the tree
     */
    private void explorer(Node root) {
        List<Node> path = new ArrayList<Node>();
        Node nodeToExplore = root;
        while(nodeToExplore.cardSetofInexistantNodes.isEmpty() && !nodeToExplore.nodeTurnState.isTerminal()) {
            path.add(nodeToExplore);
            nodeToExplore = nodeToExplore.childsOfNode[nodeToExplore.findBestChild(VALUE_C)];
        }
        path.add(nodeToExplore);
        addNode(nodeToExplore, path);
    }

    /**
     * Represent a node of the tree that the MctsPlayer builds
     */
    private static class Node {

        private TurnState nodeTurnState;
        private Node[] childsOfNode;
        private CardSet cardSetofInexistantNodes;
        private int scoreForNode;
        private int finishedTurns;
        private CardSet mctsHand;

        /**
         * Constructor of Node
         * @param turnState (TurnState): the turnstate of the node
         * @param mctsHand (CardSet): the hand of the MctsPlayer when the node is created
         * @param mctsId (PlayerId): the Id of the MctsPlayer
         */
        private Node(TurnState state, CardSet mctsHand, PlayerId mctsPlayerId) {
            nodeTurnState = state;
            if(nodeTurnState.nextPlayer()==mctsPlayerId) {
                if(!state.isTerminal()) {
                    cardSetofInexistantNodes = nodeTurnState.trick().playableCards(mctsHand);
                }
                else {
                    cardSetofInexistantNodes = CardSet.EMPTY;
                }
            }
            else {
                if(!state.isTerminal()) {
                    cardSetofInexistantNodes = nodeTurnState.trick().playableCards(nodeTurnState.unplayedCards().difference(mctsHand));
                }
                else {
                    cardSetofInexistantNodes = CardSet.EMPTY;
                }
            }
            scoreForNode = 0;
            finishedTurns = 0;
            childsOfNode = new Node[cardSetofInexistantNodes.size()];
            this.mctsHand =mctsHand;
        }

        /**
         * Spare method to calculate a parameter (the "value V") useful to compare nodes with each other
         * @param parent (Node) : the parent node
         * @param constantC (double) : the useful constant in MctsPlayer
         * @return (double) : the value V for this node
         */
        private double calculateV(Node parent, double constantC) {
            if(finishedTurns > 0) {
                return ((((double)scoreForNode)/((double)finishedTurns)) + constantC*Math.sqrt(2*Math.log(((double)parent.finishedTurns))/((double)finishedTurns)));
            }
            else {return Double.POSITIVE_INFINITY;}
        }

        /**
         * Calculate the value "V" of all the child of the node and return the index the one with the biggest value
         * @param c (double): A parameter used in the formula to calculate the value "V" 
         * @return (int): the index of the best child 
         */
        private int findBestChild(double constantC) {
            double highestV = 0;
            int index = 0;
            for(int i=0;i<childsOfNode.length;++i) {
                if(childsOfNode[i] != null) {
                    double tempV = childsOfNode[i].calculateV(this, constantC);
                    if(tempV > highestV) {
                        highestV = tempV;
                        index = i;
                    }
                }
            }
            return index;
        }
        
    }
}
