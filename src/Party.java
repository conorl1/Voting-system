import java.util.*;

/**
 * Represents a political party.
 */
public class Party {
    private final String name;
    private final Set<Candidate> candidates;
    private final Double politicalLeaning;
    private final Double popularity;
    private final List<Double> agePopularityModifiers;
    private final List<Double> genderPopularityModifiers;
    private Map<Voter,Double> votes;

    /**
     * Creates a new Party object.
     *
     * @param name the name of the Party
     * @param politicalLeaning Double ranging from -1 to 1, representing the political leaning of this Party
     * @param popularity Double ranging from 0 to 1, representing the overall popularity of this Party
     * @param agePopularityModifiers List of Doubles ranging from -1 to 1, representing the relative popularity of this party with each age group
     * @param genderPopularityModifiers List of Doubles ranging from -1 to 1, representing the relative popularity of this party with each gender
     */
    Party(String name, Double politicalLeaning, Double popularity, List<Double> agePopularityModifiers, List<Double> genderPopularityModifiers) {
        this.name = name;
        this.politicalLeaning = politicalLeaning;
        this.popularity = popularity;
        candidates = new HashSet<>();
        this.agePopularityModifiers = agePopularityModifiers;
        this.genderPopularityModifiers = genderPopularityModifiers;
        votes = new HashMap<>();
    }

    /**
     * Adds a Set of Candidates to this Party.
     *
     * @param candidates Set of Candidates
     */
    public void addCandidates(Set<Candidate> candidates) {
        this.candidates.addAll(candidates);
    }

    /**
     * Adds a Candidate to this Party.
     *
     * @param candidate a Candidate
     */
    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
    }

    /**
     * Removes the input Candidate from this Party.
     *
     * @param candidate a Candidate
     */
    public void removeCandidate(Candidate candidate) {
        candidates.remove(candidate);
    }

    /**
     * Returns the name of this Party
     *
     * @return the name of this Party.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a Set of the Candidates in this Party.
     *
     * @return the Set of Candidates in this Party
     */
    public Set<Candidate> getCandidates() {
        return candidates;
    }

    /**
     * Returns this Party's political leaning.
     *
     * @return the Double representing the political leaning of this Party.
     */
    public Double getPoliticalLeaning() {
        return politicalLeaning;
    }

    /**
     * Returns this Party's popularity score given the Voter's age group and gender.
     *
     * @param ageGroup an Integer representing a Voter's age group
     * @param gender an Integer representing a Voter's gender
     * @return a Double representing the popularity score
     */
    public Double getPopularity(Integer ageGroup, Integer gender) {
        return popularity + agePopularityModifiers.get(ageGroup) + genderPopularityModifiers.get(gender);
    }

    /**
     * Returns the vote Map of this Party.
     *
     * @return the Map of Voters and Doubles representing the votes cast to this Party
     */
    public Map<Voter, Double> getVotes() {
        return votes;
    }

    /**
     * Calculates and returns the total number of votes for this Party.
     *
     * @return a Double representing the total votes for this Party
     */
    public Double getVoteCount() {
        Double numberOfVotes = 0.0;

        for (Double voteFraction : votes.values()) {
            numberOfVotes += voteFraction;
        }

        return  numberOfVotes;
    }

    /**
     * Adds a given fraction of a vote to this Party.
     *
     * @param voter a Voter voting for this Party
     * @param fraction the fraction of the voter's vote being cast for this Party
     */
    public void addVote(Voter voter, Double fraction) {
        votes.put(voter, fraction);
    }

}
