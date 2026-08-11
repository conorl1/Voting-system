import java.util.*;

import static java.lang.Math.*;

/**
 * Main class for testing.
 */
public class Main {

    /**
     * Get the Candidate closest to the Voter's political leaning, whose popularity is above the Voter's popularityCare value.
     *
     * @param candidates the Set of Candidate that can be chosen from
     * @param politicalLeaning Double ranging from -1 to 1 representing the political leaning of the Voter
     * @param popularityCare Double representing how much the Voter cares about the popularity of a Party
     * @param ageGroup Integer representing the age group the Voter is part of
     * @param gender Integer representing the voter's gender
     * @param partyPopularities a Map of Parties to Doubles representing the relative popularity of each Party in the Region
     * @param partyPopularityMultipliers a Map of Parties to Doubles representing multipliers of each Party's popularity, signifying how each Party's popularity in the constituency varies from the regional average
     * @return the next Candidate in the Voter's preference order
     */
    private static Candidate getClosestCandidate(Set<Candidate> candidates, Double politicalLeaning, Double popularityCare, Integer ageGroup, Integer gender, Map<Party, Double> partyPopularities, Map<Party, Double> partyPopularityMultipliers) {
        Double leaningNearness = Double.MAX_VALUE;
        Candidate closestCandidate = null;

        for (Candidate candidate : candidates) {
            Party party = candidate.getParty();
            Double candidateCompatibility = abs(party.getPoliticalLeaning() - politicalLeaning);

            if (candidateCompatibility < leaningNearness && (party.getPopularity(ageGroup, gender) + partyPopularities.get(party)) * partyPopularityMultipliers.get(party) >= popularityCare) {
                leaningNearness = candidateCompatibility;
                closestCandidate = candidate;
            }

        }

        return closestCandidate;

    }

    /**
     * Generates a ranking of Candidates in a Constituency for a Voter
     *
     * @param candidates the Set of Candidates that the Voter can vote for
     * @param politicalLeaning Double ranging from -1 to 1 representing the political leaning of the Voter
     * @param popularityCare Double representing how much the Voter cares about the popularity of a Party
     * @param ageGroup Integer representing the age group the Voter is part of
     * @param gender Integer representing the voter's gender
     * @param partyPopularities a Map of Parties to Doubles representing the relative popularity of each Party in the Region
     * @param partyPopularityMultipliers a Map of Parties to Doubles representing multipliers of each Party's popularity, signifying how each Party's popularity in the constituency varies from the regional average
     * @return a List of some of the Constituency's Candidates in order of preference
     */
    private static List<Candidate> rankCandidates(Set<Candidate> candidates, Double politicalLeaning, Double popularityCare, Integer ageGroup, Integer gender, Map<Party, Double> partyPopularities, Map<Party, Double> partyPopularityMultipliers) {
        List<Candidate> ranking = new ArrayList<>();

        while (true) {
            Candidate nextCandidate = getClosestCandidate(candidates, politicalLeaning, popularityCare, ageGroup, gender, partyPopularities, partyPopularityMultipliers);

            if (nextCandidate == null) {
                break;
            }

            ranking.add(nextCandidate);
            candidates.remove(nextCandidate);
//            System.out.println(nextCandidate.getParty().getName());

            if (random() < 0.25) {
                break;
            }

            popularityCare -= 0.01;

        }

        return ranking;

    }

    /**
     * Creates the number of Voters input.
     *
     * @param voterCount an Integer number of Voters to create
     * @param candidates the Set of Candidates that the Voters can vote for
     * @param partyPopularities a Map of Parties to Doubles representing the relative popularity of each Party in the Region
     * @param partyPopularityMultipliers a Map of Parties to Doubles representing multipliers of each Party's popularity, signifying how each Party's popularity in the constituency varies from the regional average
     * @return a Set of the Voters created
     */
    private static Set<Voter> generateVoters(Integer voterCount, Set<Candidate> candidates, Map<Party, Double> partyPopularities, Map<Party, Double> partyPopularityMultipliers) {
        Set<Voter> voters = new HashSet<>();

        for (int i = 0; i < voterCount; i++) {
            Double politicalLeaning = random() * 1.8 - 0.9;
            Double popularityCare = random() * 0.25;
            Integer ageGroup = (int) floor((random() * 5));
            Integer gender = (int) floor(random() * 2);
            voters.add(new Voter(rankCandidates(new HashSet<>(candidates), politicalLeaning, popularityCare, ageGroup, gender, partyPopularities, partyPopularityMultipliers)));
//            System.out.println("----------");
        }

        return voters;

    }

    /**
     * Creates the number of Constituencies input.
     *
     * @param constituencyCount an Integer number of Constituencies to generate.
     * @param parties a Map of Parties to Doubles representing the relative popularity of each Party in the region
     * @return a Set of the Constituencies created
     */
    private static Set<Constituency> generateConstituencies(Integer constituencyCount, Map<Party, Double> parties) {
        Set<Constituency> constituencies  = new HashSet<>();

        for (int i = 0; i < constituencyCount; i++) {
            String constituencyName = "Constituency" + i;
            Set<Candidate> candidates = new HashSet<>();
            Map<Party, Double> partyPopularityMultipliers = new HashMap<>();

            for (Party party : parties.keySet()) {
                candidates.add(new Candidate(party.getName() + constituencyName + "Candidate", party));
                partyPopularityMultipliers.put(party, random() * 1.5 + 0.5);
            }

            Set<Voter> voters = generateVoters((int) (69724 + random() * 7338), candidates, parties, partyPopularityMultipliers);
            constituencies.add(new Constituency(constituencyName, voters, candidates, 1));
        }

        return constituencies;
    }

    /**
     * main method for testing code.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Set up test Parties
        Map<Party, Double> parties = new HashMap<>();
        Party circleParty = new Party("Circle Party", 0.6, 0.13, Arrays.asList(0.2,0.06,0.02,-0.04,-0.11), Arrays.asList(-0.01,0.02));
        Party lineParty = new Party("Line Party", -0.4, 0.2, Arrays.asList(-0.11,-0.06,-0.04,-0.01,0.18), Arrays.asList(-0.03,0.01));
        Party triangleParty = new Party("Triangle Party", -0.6, 0.26, Arrays.asList(-0.11,-0.08,-0.01,0.03,0.02), Arrays.asList(0.03,-0.04));
        Party squareParty = new Party("Square Party", 0.2, 0.21, Arrays.asList(0.03,0.09,0.03,-0.01,-0.08), Arrays.asList(0.0,0.0));
        Party pentagonParty = new Party("Pentagon Party", -0.2, 0.11, Arrays.asList(0.0,-0.01,0.01,0.01,0.03), Arrays.asList(0.0,0.01));
        Party hexagonParty = new Party("Hexagon Party", -0.8, 0.03, Arrays.asList(0.0,-0.01,-0.01,0.0,-0.02), Arrays.asList(0.0,0.0));
        Party septagonParty = new Party("Septagon Party", 0.8, 0.02, Arrays.asList(0.0,0.0,0.0,0.01,-0.01), Arrays.asList(0.0,0.0));
        Party octagonParty = new Party("Octagon Party", 0.0, 0.03, Arrays.asList(-0.01,0.01,0.0,0.0,-0.02), Arrays.asList(0.0,0.0));
        Party nonagonParty = new Party("Nonagon Party", 0.4, 0.01, Arrays.asList(0.01,0.01,0.0,0.0,-0.01), Arrays.asList(0.0,0.0));

        // Set up Map of Parties with relative regional popularities
        parties.put(circleParty,0.0);
        parties.put(lineParty,0.05);
        parties.put(triangleParty,0.0);
        parties.put(squareParty,-0.08);
        parties.put(pentagonParty,0.08);
        parties.put(hexagonParty,0.0);
        parties.put(septagonParty,0.0);
        parties.put(octagonParty,-0.03);
        parties.put(nonagonParty,-0.01);

        // Create the Region for testing
        Region testRegion = new Region("Test region", 92, generateConstituencies(46, parties), parties);

        // Run the election
        List<Candidate> elected = testRegion.election();

        Map<Party, Integer> results = new HashMap<>();

        // Display aggregated counts of votes per party
        for (Candidate candidate : elected) {
            Party party = candidate.getParty();
            results.put(party, results.getOrDefault(party, 0) + 1);
        }

        for (Party party : results.keySet()) {
            System.out.println(party.getName() + " " + results.get(party));
        }

    }

}