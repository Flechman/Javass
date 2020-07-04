package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayablePlayer implements Player {

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        Scanner keyb = new Scanner(System.in);
        System.out.println(state.trick().toString());
        CardSet playableCard = state.trick().playableCards(hand);
        System.out.println("vos cartes possibles: " +playableCard);
        System.out.println("Quel est l'index de la carte que vous voulez jouer ?");
        Integer a = keyb.nextInt();
        while (a<0 || a>playableCard.size()){
            System.out.println("Quel est l'index de la carte que vous voulez jouer ?");  
            a = keyb.nextInt();
        }
        Card c = playableCard.get(a);
        return c;
    }



}
