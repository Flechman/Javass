package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Represents a card of the game 
 */
public final class Card {

    //Packed representation of the card
    private int pcknum;

    /**
     * Constructor of Card
     * @param (int) pcknum the packed representation of the card
     */
    private Card(int pcknum) {
        this.pcknum = pcknum;
    }

    /**
     *Represents the color of the card
     */
    public enum Color {
        SPADE("\u2660"), //♠
        HEART("\u2665"), //♥
        DIAMOND("\u2666"), //♦
        CLUB("\u2663"); //♣

        //The string which is the name of the color
        private String symbol;

        /**
         * @param (String): colorName the symbol of the color
         */
        private Color(String colorName) {
            this.symbol = colorName;
        }

        //List containing every values of the enumerated type Color (in order of declaration)
        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));

        //The number of values of Color
        public static final int COUNT = 4;


        /**
         * Override of the method toString of the class Object 
         * @return the symbol associated to the color
         */
        @Override
        public String toString() {
            return symbol;
        }
    }

    /**
     * Represents the rank of the card
     */
    public enum Rank {
        SIX("6", 0),
        SEVEN("7", 1),
        EIGHT("8", 2),
        NINE("9", 7),
        TEN("10", 3),
        JACK("J", 8),
        QUEEN("Q", 4),
        KING("K", 5),
        ACE("A", 6);

        //The string which is the name of the rank of the card
        private String rankName;

        //The rank of the card if it is a trump
        private int trumpRank;
        
        /**
         * Constructor of enum Rank
         * @param rankName (String) : the name of the rank
         * @param trumpRank (int) : the rank of the card if it is a trump
         */
        private Rank(String rankName, int trumpRank) {
            this.rankName = rankName;
            this.trumpRank = trumpRank;
        }

        //List containing every values of the enumerated type Rank (in order of declaration)
        public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));

        //The number of values of Rank
        public static final int COUNT = 9;

        /**
         * Get the position associated to the rank of the trump card (between 0 and 8)
         * @return (int): this position
         */
        public int trumpOrdinal() {
            return trumpRank;
        }

        /**
         * Override of the method toString of the class Object 
         * @return (String): a compact representation of every card name of a certain color
         */
        @Override
        public String toString() {
            return rankName;
        }
    }

    /**
     * Returns a card when its color and rank are given
     * @param c (Color): the color of the card
     * @param r (Rank): the rank of the card
     * @return (Card): the card of given color and given rank
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * Returns the card which its packed representation is the parameter packed
     * @param packed the packed representation of the card
     * @return the card which corresponds to the integer packed
     */
    public static Card ofPacked(int packed) {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        return new Card(packed);
    }

    /**
     * Returns the packed representation of the card
     * @return (int) the packed representation of the card
     */
    public int packed() {
        return pcknum;
    }

    /**
     * Returns the color of the card which can be determined with the packed representation of the card
     * @return the color of the card
     */
    public Color color() {
        return PackedCard.color(pcknum);
    }

    /**
     * Returns the rank of the card which can be determined with the packed representation of the card
     * @return the rank of the card
     */
    public Rank rank() {
        return PackedCard.rank(pcknum);
    }

    /**
     * Compares the rank of the card to which we apply the method with the rank of the other card taken into parameter
     * @param trump (Color) : here to know if the cards are trumps or not, it effects the comparison
     * @param that (Card) : the card to compare to
     * @return (boolean) : true if the card to which we apply the method is superior than the other one, and false otherwise
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.packed(), that.packed());
    }

    /**
     * Can determine the value of the card
     * @param trump (Color) : plays a role in determining the the value of the card
     * @return (int) : the value of the card, whether it is a trump or not
     */
    public int points(Color trump) {
        return PackedCard.points(trump, pcknum);
    }

    /**
     * Override of the method equals of Object
     */
    @Override
    public boolean equals(Object thatO) {
        if(thatO==null) {
            return false;
        }
        else {
            if(thatO instanceof Card) {
                return (this.pcknum == ((Card)thatO).pcknum);
            }
            else {return false;}
        }
    }  

    /**
     * Override of the method hashCode of Object
     */
    @Override
    public int hashCode() {
        return pcknum;
    }

    /**
     * Override of the method toString of Object
     */
    @Override
    public String toString() {
        return PackedCard.toString(pcknum);
    }

}

