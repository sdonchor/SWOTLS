package server;

import java.util.ArrayList;
import java.util.List;

/**
 * https://pl.wikipedia.org/wiki/System_ko%C5%82owy#Tabele_Bergera_jako_graf
 */
public class BergsCircle {
    public static List<MatchPair> getPairs(List<TournamentParticipant> competitors, int tournamentStage){
        BergsCircle circle = new BergsCircle(competitors);
        while(circle.getStage() != tournamentStage){
            circle.nextStage();
        }
        return circle.getPairs();
    }

    private ArrayList<TournamentParticipant> slots = new ArrayList<>();
    private int stage = 1;

    public BergsCircle(List<TournamentParticipant> competitors){
        slots.ensureCapacity(competitors.size());

        for(TournamentParticipant p : competitors){
            slots.add(p.getStartingPosition(), p);
        }

        if(slots.size()%2==1)
            slots.add(new TournamentParticipant(-1, slots.size()+1));
    }

    public List<MatchPair> getPairs(){
        List<MatchPair> pairs = new ArrayList<>();
        //Gracze znajdujący się na odpowiadających sobie wierchołkach (slotach) są ze sobą parowani - https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Round-robin-schedule-span-diagram.svg/1024px-Round-robin-schedule-span-diagram.svg.png
        for(int i = 0; i<slots.size()/2; i++){
            TournamentParticipant p1 = slots.get(i);
            TournamentParticipant p2 = slots.get(slots.size()-1-i);

            //Gracz o id -1 oznacza wolny los - jego przeciwnik pauzuje w tej kolejce (nie ma meczu na tym etapie).
            if(p1.getId()==-1 || p2.getId()==-1)
                continue;

            pairs.add( new MatchPair(p1, p2) );
        }
        return pairs;
    }

    public int getStage() {
        return stage;
    }

    /**
     * Ustawia koło tak aby wskazywało pary w następnym etapie.
     */
    public void nextStage(){
        //Pary następnych kolejek otrzyma się przesuwając numery wierzchołków (z wyjątkiem środkowego wierzchołka) zgodnie z ruchem wskazówek zegara.
        ArrayList<TournamentParticipant> circle = new ArrayList<>();
        circle.ensureCapacity(slots.size());
        //Jeden (ostatni) uczestnik jest nieruchomy na kole, a pozostali są przesuwani o jedną pozycję
        circle.add(slots.size()-1, slots.get(slots.size()-1));
        circle.add(0, slots.get(slots.size()-2));
        for(int i = 0; i<slots.size()-2; i++){
            circle.add(i+1, slots.get(i));
        }
        this.slots = circle;
        this.stage++;
    }
}