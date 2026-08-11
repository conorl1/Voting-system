import java.util.HashMap;
import java.util.Map;

/**
 * Represents a candidate in an election.
 */
public class Candidate {
    private final String name;
    private Party party;
    private Map<Voter, Double> votes;

    /**
     * Creates a new Candidate object, a member of the Party specified.
     *
     * @param name the name of the Candidate
     * @param party the Party the Candidate is a member of
     */
    Candidate(String name, Party party) {
        this.name = name;
        this.party = party;
        party.addCandidate(this);
        votes = new HashMap<>();
    }

    /**
     * Adds a fractional vote to this Candidate.
     *
     * @param voter The Voter who is voting for this Candidate
     * @param fraction The fraction of the Voter's vote being given to this Candidate
     */
    public void addVote(Voter voter, Double fraction) {
        votes.put(voter, fraction);
    }

    /**
     * Returns the Map representing the votes of each Voter for this Candidate.
     *
     * @return The votes Map
     */
    public Map<Voter, Double> getVotes() {
        return this.votes;
    }

    /**
     * Calculates and returns the total number of votes for this Candidate.
     *
     * @return The total votes for this Candidate
     */
    public Double getVoteCount() {
        Double numberOfVotes = 0.0;

        for (Double voteFraction : votes.values()) {
            numberOfVotes += voteFraction;
        }

        return  numberOfVotes;
    }

    /**
     * Returns this Candidate's name.
     *
     * @return The name of this Candidate
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Party this candidate belongs to
     *
     * @return The Party this candidate is a member of
     */
    public Party getParty() {
        return party;
    }

    /**
     * Removes this Candidate from the Party it is a member of and adds it to a new one.
     *
     * @param newParty The new Party the Candidate is changing to
     */
    public void changeParty(Party newParty) {
        party.removeCandidate(this);
        newParty.addCandidate(this);
        party = newParty;
    }

}
