package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Represents a trick of a round
 */
public class Trick {

    //Packed representation of the trick
    private final int pkTrick;

    public final static Trick INVALID = new Trick(PackedTrick.INVALID);


    /**
     * Constructor of Trick
     * @param pkTrick (int) : the packed representation of the trick
     */
    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    /**
     * Returns an empty trick, but with the given trump and the first player to start
     * @param trump (Color) : the trump of the round
     * @param firstPlayer (PlayerId) : the player to start
     * @return (Trick) : the empty trick
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Returns the trick corresponding to the packed value given in argument
     * @param packed (int) : the packed representation of the intended trick
     * @return (Trick) : the trick with "packed" as its packed representation
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * Getter for the packed representation of the trick
     * @return (int) : the packed trick
     */
    public int packed() {
        return pkTrick;
    }

    /**
     * Sets the next trick of the round to an empty trick, by saving the player that won the last trick along with the current trump and 
     * the index of the trick
     * @return (Trick) : the new reseted trick
     */
    public Trick nextEmpty() {
        if(!this.isFull())  {throw new IllegalStateException();}
        return new Trick(PackedTrick.nextEmpty(pkTrick));
    }

    /**
     * Checks if the trick has no cards yet
     * @return (boolean) : true if there are no cards in the trick and false otherwise
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(pkTrick);
    }

    /**
     * Checks if the trick is full or not
     * @return (boolean) : true iff the trick is full, false otherwise
     */
    public boolean isFull() {
        return PackedTrick.isFull(pkTrick);
    }

    /**
     * Checks if it is the last trick of the turn
     * @return (boolean) : true iff it is the last trick of the turn, false otherwise
     */
    public boolean isLast() {
        return PackedTrick.isLast(pkTrick);
    }

    /**
     * Returns the size of the trick
     * @return (int) : the number of cards contained in the trick
     */
    public int size() {
        return PackedTrick.size(pkTrick);
    }

    /**
     * Gets the trump of the trick
     * @return (Color) : the trump of the trick
     */
    public Color trump() {
        return PackedTrick.trump(pkTrick);
    }

    /**
     * Returns the index of the trick in the turn
     * @return (int) : the index of the trick
     */
    public int index() {
        return PackedTrick.index(pkTrick);
    }

    /**
     * Returns the player associated to the index of the card in the trick
     * @param index (int) : the index of the card
     * @return (PlayerId) : the player associated to the index of the card
     */
    public PlayerId player(int index) {
        Preconditions.checkIndex(index, PlayerId.COUNT);
        return PackedTrick.player(pkTrick, index);
    }

    /**
     * Returns the wanted card determined by its index in the trick
     * @param index (int) : the index of the card
     * @return (Card) : the wanted card 
     */
    public Card card(int index) {
        Preconditions.checkIndex(index, size());
        return Card.ofPacked(PackedTrick.card(pkTrick, index));
    }

    /**
     * Adds the specified card in the trick, and returns the updated trick
     * @param c (Card) : the specified card
     * @return (Trick) : the updated trick
     */
    public Trick withAddedCard(Card c) {
        if(this.isFull())  {throw new IllegalStateException();}
        return new Trick(PackedTrick.withAddedCard(pkTrick, c.packed()));
    }

    /**
     * Returns the base color of the trick, which is the color of the first card placed 
     * @return (Color) : the base color of the trick
     */
    public Color baseColor() {
        if(this.isEmpty())  {throw new IllegalStateException();}
        return PackedTrick.baseColor(pkTrick);
    }

    /**
     * Returns a set of cards which contains all the cards of a hand that can be played depending on the cards
     * in the trick
     * @param hand (CardSet) : the set of cards representing a hand
     * @return (CardSet) : all the cards of a hand that can be played depending on the current cards in the trick
     */
    public CardSet playableCards(CardSet hand) {
        if(this.isFull())  {throw new IllegalStateException();}
        return CardSet.ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
    }

    /**
     * Gets the value of the trick, taking into account the additional 5 points if it is the last trick of the round
     * @return (int) : the value of the trick
     */ 
    public int points() {
        return PackedTrick.points(pkTrick);
    }

    /**
     * Returns the player that is currently leading the trick
     * @return (PlayerId) : the player leading the trick
     */
    public PlayerId winningPlayer() {
        if(this.isEmpty())  {throw new IllegalStateException();}
        return PackedTrick.winningPlayer(pkTrick);
    }

    /**
     * Override of the method equals
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO==null) {
            return false;
        }
        else {
            if (thatO instanceof Trick) {
                return (this.pkTrick== ((Trick)thatO).pkTrick);
            }
            else {return false;}
        }  
    }

    /**
     * Override of the method hashCode
     */
    @Override
    public int hashCode() {
        return pkTrick;
    }

    /**
     * Override of the method toString
     */
    @Override
    public String toString() {
        return PackedTrick.toString(pkTrick);
    }
}
