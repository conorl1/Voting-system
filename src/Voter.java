import java.util.List;
import java.util.Set;

/**
 * Class representing a Voter
 */
public class Voter {
    private final List<Candidate> candidateRanking;
    private Integer currentRankingPlace;

    /**
     * Creates a new Voter object.
     *
     * @param ranking This Voter's preference order of the Candidates in their Constituency
     */
    Voter(List<Candidate> ranking) {
        this.candidateRanking = ranking;
        currentRankingPlace = 0;
    }

    /**
     * Returns the Voter's Candidate ranking.
     *
     * @return This Voter's order of preference of the Candidates in their Constituency
     */
    public List<Candidate> getRanking() {
        return this.candidateRanking;
    }

    /**
     * Returns the first Candidate in this Voter's Candidate ranking
     *
     * @return This Voter's favourite Candidate
     */
    public Candidate getFavourite() {
        if (candidateRanking.isEmpty()) {
            return null;
        }

        return candidateRanking.getFirst();
    }

}
