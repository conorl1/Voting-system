import java.util.*;

/**
 * Represents a Constituency
 */
public class Constituency {
    private final String name;
    private final Set<Voter> voters;
    private final Set<Candidate> candidates;
    private final Integer seatsAvailable;
    private final Double threshold;
    private final Integer totalVotes;

    /**
     * Creates a new Constituency object.
     *
     * @param name the name of this Constituency
     * @param voters the Set of Voters in this Constituency
     * @param candidates the Set of Candidates in this Constituency
     * @param seatsAvailable Integer representing the number of seats available in this Constituency
     */
    Constituency(String name, Set<Voter> voters, Set<Candidate> candidates, Integer seatsAvailable) {
        this.name = name;
        this.voters = voters;
        this.candidates = candidates;
        this.seatsAvailable = seatsAvailable;
        threshold = 1.0 / (seatsAvailable + 1);
        totalVotes = voters.size();
    }

    /**
     * Returns the Set containing the Voters in this Constituency.
     *
     * @return the Set of Voters in this Constituency
     */
    public Set<Voter> getVoters() {
        return voters;
    }

    /**
     * Get all the candidates whose vote total is currently over the threshold needed to win a seat
     *
     * @return A Set of Candidates which are the candidates over the seat threshold
     */
    private Set<Candidate> candidatesOverVoteThreshold() {
        Set<Candidate> overThreshold = new HashSet<>();

        for (Candidate candidate : candidates) {
//            System.out.println(candidate.getName() + " " + candidate.getVoteCount().toString());

            if (candidate.getVoteCount() > totalVotes * threshold) {
                overThreshold.add(candidate);
            }

        }

        return overThreshold;
    }

    /**
     * For each elected candidate with surplus votes,
     * transfer a fraction of the candidate's voter's votes to the voter's next choice
     *
     * @param electedCandidates the List of Candidates that have already won seats in this Constituency
     * @param removedCandidates the Set of Candidates in this Constituency that have been eliminated
     */
    private void transferFractionalVoteSurplus(List<Candidate> electedCandidates, Set<Candidate> removedCandidates) {
        Double transferableVoteCount = 0.0;

        for (Candidate candidate : electedCandidates) {
            Map<Voter, Double> candidateVotes = candidate.getVotes();
            Map<Voter, Double> transferableVotes = new HashMap<>();

            for (Voter voter : candidateVotes.keySet()) {
                List<Candidate> voterRanking = voter.getRanking();
                Boolean voterHasNextCandidate = false;

                for (Candidate chosenCandidate : voterRanking) {

                    if (!electedCandidates.contains(chosenCandidate) && !removedCandidates.contains(chosenCandidate)) {
                        voterHasNextCandidate = true;
                        break;
                    }

                }

                if (voterHasNextCandidate) {
                    Double voteAmount = candidateVotes.get(voter);
                    transferableVotes.put(voter, voteAmount);
                    transferableVoteCount += voteAmount;
                }

            }

            Double neededVotes = threshold * totalVotes;
            Double transferVoteCount = candidate.getVoteCount() - neededVotes;
            Double keepVoteCount = transferableVoteCount - transferVoteCount;

            Double keepFraction = keepVoteCount / transferableVoteCount;
            Double transferFraction = transferVoteCount / transferableVoteCount;

            for (Voter voter : transferableVotes.keySet()) {
                Double currentVoteAmount = transferableVotes.get(voter);
                List<Candidate> voterRanking = voter.getRanking();
                candidate.addVote(voter, currentVoteAmount * keepFraction);

                for (Candidate chosenCandidate : voterRanking) {

                    if (!electedCandidates.contains(chosenCandidate) && !removedCandidates.contains(chosenCandidate)) {
                        chosenCandidate.addVote(voter, currentVoteAmount * transferFraction);
                        break;
                    }

                }

            }

        }

    }

    /**
     * Add the Candidate that has the least number of votes to the Set removedCandidates and
     * transfer their Voters' vote fractions to those Voter's next choice Candidate.
     *
     * @param electedCandidates the List of Candidates that have already won seats in this Constituency
     * @param removedCandidates the Set of Candidates in this Constituency that have been eliminated
     */
    private void eliminateLastPlace(List<Candidate> electedCandidates, Set<Candidate> removedCandidates) {
        Double lowestVoteCount = Double.MAX_VALUE;
        Candidate lastPlaceCandidate = null;

        for (Candidate candidate : candidates) {
            if (!electedCandidates.contains(candidate) && !removedCandidates.contains(candidate)) {
                Double candidateVotes = candidate.getVoteCount();

                if (candidateVotes < lowestVoteCount) {
                    lowestVoteCount = candidateVotes;
                    lastPlaceCandidate = candidate;
                }

            }

        }

        removedCandidates.add(lastPlaceCandidate);
        Map<Voter, Double> lastPlaceVotes = lastPlaceCandidate.getVotes();

        for (Voter voter : lastPlaceVotes.keySet()) {
            Double currentVoteAmount = lastPlaceVotes.get(voter);
            List<Candidate> voterRanking = voter.getRanking();

            for (Candidate chosenCandidate : voterRanking) {

                if (!electedCandidates.contains(chosenCandidate) && !removedCandidates.contains(chosenCandidate)) {
                    chosenCandidate.addVote(voter, currentVoteAmount);
                    lastPlaceCandidate.addVote(voter, 0.0);
                    break;
                }

            }

        }

    }

    /**
     * Run an election in the Constituency.
     *
     * @return the List of Candidates that have been elected.
     */
    public List<Candidate> election() {
        List<Candidate> electedCandidates = new ArrayList<>();
        Set<Candidate> removedCandidates = new HashSet<>();

        // Get each Voter's first pick of Candidate to vote for and assign that Candidate a vote fraction of 1.0
        for (Voter voter : voters) {
            Candidate candidate = voter.getFavourite();

            if (candidate != null) {
                candidate.addVote(voter, 1.0);
            }

        }

        // Transfer votes until all the seats have been filled
        while (electedCandidates.size() < seatsAvailable) {
            Set<Candidate> overThresholdCandidates = candidatesOverVoteThreshold();

            if (!overThresholdCandidates.isEmpty()) {
                electedCandidates.addAll(overThresholdCandidates);
                transferFractionalVoteSurplus(electedCandidates, removedCandidates);
            } else {
                eliminateLastPlace(electedCandidates, removedCandidates);
            }

            if (removedCandidates.size() + 1 == candidates.size()) {
                break;
            }

        }

        // If any seats have not been filled assign them to the remaining candidates with the most votes
        while (electedCandidates.size() < seatsAvailable) {
            Candidate mostPopular = null;
            Double mostVotes = 0.0;

            for (Candidate candidate : candidates) {
                Double voteCount = candidate.getVoteCount();

                if (voteCount > mostVotes) {
                    mostPopular = candidate;
                    mostVotes = voteCount;
                }

            }

            electedCandidates.add(mostPopular);
        }

        return electedCandidates;
    }

}
