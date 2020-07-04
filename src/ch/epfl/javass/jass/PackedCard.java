package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Class which contains methods to manipulate the cards of the game
 */
public final class PackedCard {

    //Represents an invalid packed card
    public static final int INVALID =0b111111;
    
    public static final int SIZE_RANK = 4, SIZE_COLOR = 2, UNUSED_BITS_LENGTH = 26;
    public static final int START_RANK = 0, START_COLOR = 4, STARTING_BIT_UNUSED = 6;

    /**
     * Constructor of the class
     */
    private PackedCard() {}

    /**
     * Checks if the packed representation of a card is valid
     * @param pkCard (int) : the packed representation of the card
     * @return (boolean) : true if it is valid, and false otherwise
     */
    public static boolean isValid(int pkCard) {
        if ((Bits32.extract(pkCard, START_RANK, SIZE_RANK)) < Card.Rank.COUNT && (Bits32.extract(pkCard, STARTING_BIT_UNUSED, UNUSED_BITS_LENGTH))==0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gives the card which is associated to the color c and the rank r
     * @param c (Card.Color) : the color of the card
     * @param r (Card.Rank) : the rank of the card
     * @return (int) : the card of color c and rank r
     */
    public static int pack(Card.Color c, Card.Rank r) {

        int color= c.ordinal();
        int rank= r.ordinal();

        int packedCard = Bits32.pack(rank, SIZE_RANK, color, SIZE_COLOR);

        return packedCard;
    }

    /**
     * Gives the color of the specified card
     * @param pkCard : the packed representation of the card
     * @return (Card.Color) : the color which corresponds to the specified card
     */
    public static Card.Color color(int pkCard) {
        //assert isValid(pkCard);
        int color = Bits32.extract(pkCard, START_COLOR, SIZE_COLOR);
        return Card.Color.ALL.get(color);
    }

    /**
     * Gives the rank of the specified card
     * @param pkCard (int) : the packed representation of the card
     * @return (Card.Rank) : the rank which corresponds to the specified card
     */
    public static Card.Rank rank(int pkCard) {
        //assert isValid(pkCard);
       int rank = Bits32.extract(pkCard, START_RANK, SIZE_RANK);
               return Card.Rank.ALL.get(rank);
    }

    /**
     * Compares the rank of the card pkCardL with the rank of the card pkCardR
     * @param trump (Card.Color) : the color which is the trump
     * @param pkCardL (int) : the packed representation of the first  card
     * @param pkCardR (int) : the packed representation of the second  card
     * @return (boolean) : true if the rank of the first card is greater than the rank of the second card and false otherwise,
     * always taking into consideration the impact of trump
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {

        if(color(pkCardL).equals(color(pkCardR))) {
            if(color(pkCardL).equals(trump)) {
                if(rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                if(rank(pkCardL).ordinal() > rank(pkCardR).ordinal()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        else {
            if(color(pkCardL).equals(trump)) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Can determine the value of the card
     * @param trump (Card.Color) : plays a role in determining the the value of the card
     * @param pkCard (int) : the specified card with its packed representation
     * @return (int) : the value of the card, whether it is a trump or not
     */
    public static int points(Card.Color trump, int pkCard) {

        int rank=Bits32.extract(pkCard, START_RANK, SIZE_RANK);

        if (trump.ordinal()== Bits32.extract(pkCard, START_COLOR, SIZE_COLOR)) {
            switch (rank) {
            case 3:
                rank= 9; //just an index to know that it's a trump nine
                break;
            case 5:
                rank= 10; //just an index to know that it's a trump jack
                break;
            }
        }

        switch (rank) {
        case 4:
            return 10;
        case 5:
            return 2;
        case 6:
            return 3;
        case 7:
            return 4;
        case 8:
            return 11;
        case 9:
            return 14;
        case 10:
            return 20;  
        default:   
            return 0;

        }
    }

    /**
     * Returns a representation of the card given as a string with first the symbol of the color followed by the
     * initial of the rank
     * @param pkCard (int) : the packed representation of the card
     * @return (String) : a representation of the the card as explained above
     */
    public static String toString(int pkCard) {
        //assert isValid(pkCard);
        return color(pkCard).toString()+rank(pkCard).toString();
    }
}
