import java.util.*;

import static java.lang.Math.floor;
import static java.lang.Math.max;

/**
 * Representation of a region.
 */
public class Region {
    private String name;
    private Set<Constituency> constituencies;
    private Set<Party> parties;
    private Map<Party, Double> partyPopularities;
    private Integer seatsAvailable;

    /**
     * Creates a new Region object.
     *
     * @param name the name of the Region
     * @param seatsAvailable the number of seats available in the Region, should be equal to double the total number of seats available in all the Constituencies in the Region
     * @param constituencies Set of all the Constituencies in the Region
     * @param partyPopularities Map of Parties to Doubles representing the relative popularity of each Party in the Region
     */
    Region(String name, Integer seatsAvailable, Set<Constituency> constituencies, Map<Party, Double> partyPopularities) {
        this.name = name;
        this.seatsAvailable = seatsAvailable;
        this.constituencies = constituencies;
        this.parties = partyPopularities.keySet();
        this.partyPopularities = partyPopularities;
    }

    /**
     * Add the Party that has the least number of votes to the Set removedParties and
     * transfer their Voters' vote fractions to those Voter's next choice Party.
     *
     * @param electedParties the Set of Parties that have already won seats in this Region
     * @param removedParties the Set of Parties in this Region that have been eliminated
     * @return a Boolean representing whether a Party has been eliminated
     */
    private Boolean eliminateLastPlace(Set<Party> electedParties, Set<Party> removedParties) {
        Double lowestVoteCount = Double.MAX_VALUE;
        Party lastPlaceParty = null;

        for (Party party : parties) {
            if (!electedParties.contains(party) && !removedParties.contains(party)) {
                Double partyVotes = party.getVoteCount();

                if (partyVotes < lowestVoteCount) {
                    lowestVoteCount = partyVotes;
                    lastPlaceParty = party;
                }

            }

        }

        if (lastPlaceParty == null) {
            return false;
        }

        removedParties.add(lastPlaceParty);
        Map<Voter, Double> lastPlaceVotes = lastPlaceParty.getVotes();

        for (Voter voter : lastPlaceVotes.keySet()) {
            Double currentVoteAmount = lastPlaceVotes.get(voter);
            List<Candidate> voterRanking = voter.getRanking();

            for (Candidate candidate : voterRanking) {
                Party chosenParty = candidate.getParty();

                if (!removedParties.contains(chosenParty)) {
                    chosenParty.addVote(voter, currentVoteAmount);
                    lastPlaceParty.addVote(voter, 0.0);
                    break;
                }

            }

        }

        return true;

    }

    /**
     * Assign a seat to the Candidate in the given Party with the most votes who has not yet been given a seat.
     *
     * @param party a Party
     * @param electedCandidates List of Candidates who have won seats
     */
    private void assignMostVotedRemainingOfParty(Party party, List<Candidate> electedCandidates) {
        Double highestVotes = 0.0;
        Candidate mostVoted = null;

        for (Candidate partyCandidate : party.getCandidates()) {
            if (!electedCandidates.contains(partyCandidate)) {
                Double candidateVotes = partyCandidate.getVoteCount();

                if (candidateVotes > highestVotes) {
                    highestVotes = candidateVotes;
                    mostVoted = partyCandidate;
                }

            }

        }

        electedCandidates.add(mostVoted);
    }

    /**
     * Calculate the total number of seats that have been assigned to any Party.
     *
     * @param seats a Map of Parties to Integers representing the number of seats each Party should be assigned
     * @return the Integer total number of seats
     */
    private Integer totalAssignedSeats(Map<Party, Integer> seats) {
        Integer total = 0;

        for (Integer seatCount : seats.values()) {
            total += seatCount;
        }

        return total;
    }

    /**
     * Run an election in this Region.
     *
     * @return a List of the Candidates that have been elected in this Region
     */
    public List<Candidate> election() {
        Set<Voter> allVoters = new HashSet<>();
        List<Candidate> electedCandidates = new ArrayList<>();
        Map<Party, Integer> partySeats = new HashMap<>();
        Set<Party> electedParties = new HashSet<>();
        Set<Party> removedParties = new HashSet<>();

        // Get all the Voters in the Region
        for (Constituency constituency : constituencies) {
            allVoters.addAll(constituency.getVoters());
        }

        Integer population = allVoters.size();

        // Add a vote to each Voter's preferred Party
        for (Voter voter : allVoters) {
            Candidate candidate = voter.getFavourite();

            if (candidate != null) {
                candidate.getParty().addVote(voter, 1.0);
            }

        }

        // Determine what the seat distribution should be in the region based on the proportion of votes for each party
        while (totalAssignedSeats(partySeats) < seatsAvailable) {
            partySeats = new HashMap<>();

            for (Party party : parties) {
                Integer seats = (int) floor(party.getVoteCount() / population * seatsAvailable);
                partySeats.put(party, seats);
                electedParties.add(party);
            }

            Integer seatsAssigned = 0;

            for (Integer seatCount : partySeats.values()) {
                seatsAssigned += seatCount;
            }

            if (seatsAssigned < seatsAvailable) {
                Boolean eliminated = eliminateLastPlace(electedParties, removedParties);

                if (!eliminated) {
                    Set<Party> assignedExtra = new HashSet<>();

                    for (int i = 0; i < seatsAvailable - seatsAssigned; i++) {
                        Double highestRemainder = 0.0;
                        Party extraSeatParty = null;

                        for (Party party : electedParties) {

                            if (!assignedExtra.contains(party)) {
                                Double remainder = (party.getVoteCount() / population * seatsAvailable) - partySeats.get(party);

                                if (remainder > highestRemainder) {
                                    highestRemainder = remainder;
                                    extraSeatParty = party;
                                }

                            }

                        }

                        partySeats.put(extraSeatParty, partySeats.get(extraSeatParty) + 1);
                        assignedExtra.add(extraSeatParty);
                    }

                    break;
                }

            }

        }

        for (Party party : partySeats.keySet()) {
            System.out.println(party.getName() + " " + partySeats.get(party));
        }
        System.out.println("\n");

        // Get the winner of each individual constituency's election and assign them seats
        for (Constituency constituency : constituencies) {
            List<Candidate> elected = constituency.election();
            electedCandidates.addAll(elected);

            for (Candidate candidate : elected) {
                Party party = candidate.getParty();
                partySeats.put(party, max(partySeats.getOrDefault(party, 0) - 1, 0));
            }

        }

        Integer seatsLeftToAssign = totalAssignedSeats(partySeats);

        // If more seats have been assigned in the individual Constituencies to any Parties than they should have been
        // based on the seat distribution then remove seats from the other parties until the correct number of seats
        // remains, starting with the parties with the largest number of seats left to assign to a Candidate, then
        // cycling through all the parties
        if (seatsLeftToAssign > seatsAvailable / 2) {
            Set<Party> seatRemovedParties = new HashSet<>();

            for (int i = 0; i < seatsLeftToAssign - (seatsAvailable / 2); i++) {
                Set<Party> partiesWithSeatsLeft = new HashSet<>();

                for (Party party : parties) {

                    if (partySeats.get(party) > 0) {
                        partiesWithSeatsLeft.add(party);
                    }

                }

                if (partiesWithSeatsLeft.size() < seatRemovedParties.size()) {
                    seatRemovedParties = new HashSet<>();
                }

                Integer mostSeats = 0;
                List<Party> mostSeatsParties = new ArrayList<>();

                for (Party party : partiesWithSeatsLeft) {
                    Integer seatCount = partySeats.get(party);

                    if (!seatRemovedParties.contains(party)) {
                        if (seatCount > mostSeats) {
                            mostSeats = seatCount;
                            mostSeatsParties = new ArrayList<>();
                            mostSeatsParties.add(party);
                        } else if (seatCount.equals(mostSeats)) {
                            mostSeatsParties.add(party);
                        }

                    }

                }

                Party mostSeatsParty = null;
                Double leastPartyVotes = Double.MAX_VALUE;

                for (Party party : mostSeatsParties) {
                    Double partyVotes = party.getVoteCount();

                    if (partyVotes < leastPartyVotes) {
                        leastPartyVotes = partyVotes;
                        mostSeatsParty = party;
                    }

                }

                partySeats.put(mostSeatsParty, mostSeats - 1);
                seatRemovedParties.add(mostSeatsParty);
            }

        }

        // Assign each party's remaining seats to Candidates
        for (Party party : partySeats.keySet()) {

            for (int i = 0; i < partySeats.get(party); i++) {
                assignMostVotedRemainingOfParty(party, electedCandidates);
            }

        }

        return electedCandidates;
    }

}
