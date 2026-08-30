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
     * @param politicalLeaningTolerance Double representing how closely a Party has to match the Voter's political leaning
     * @param popularityCare Double representing how much the Voter cares about the popularity of a Party
     * @param ageGroup Integer representing the age group the Voter is part of
     * @param gender Integer representing the voter's gender
     * @param partyPopularities a Map of Parties to Doubles representing the relative popularity of each Party in the Region
     * @param partyPopularityMultipliers a Map of Parties to Doubles representing multipliers of each Party's popularity, signifying how each Party's popularity in the constituency varies from the regional average
     * @return the next Candidate in the Voter's preference order
     */
    private static Candidate getClosestCandidate(Set<Candidate> candidates, Double politicalLeaning, Double politicalLeaningTolerance, Double popularityCare, Integer ageGroup, Integer gender, Map<Party, Double> partyPopularities, Map<Party, Double> partyPopularityMultipliers) {
        Double leaningNearness = politicalLeaningTolerance;
        Candidate closestCandidate = null;

        for (Candidate candidate : candidates) {
            Party party = candidate.getParty();
            double candidateCompatibility = abs(party.getPoliticalLeaning() - politicalLeaning);

            if (candidateCompatibility <= leaningNearness && (party.getPopularity(ageGroup, gender) + partyPopularities.get(party)) * partyPopularityMultipliers.get(party) >= popularityCare) {
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
     * @param politicalLeaningTolerance Double representing how closely a Party has to match the Voter's political leaning
     * @param popularityCare Double representing how much the Voter cares about the popularity of a Party
     * @param ageGroup Integer representing the age group the Voter is part of
     * @param gender Integer representing the voter's gender
     * @param partyPopularities a Map of Parties to Doubles representing the relative popularity of each Party in the Region
     * @param partyPopularityMultipliers a Map of Parties to Doubles representing multipliers of each Party's popularity, signifying how each Party's popularity in the constituency varies from the regional average
     * @return a List of some of the Constituency's Candidates in order of preference
     */
    private static List<Candidate> rankCandidates(Set<Candidate> candidates, Double politicalLeaning, Double politicalLeaningTolerance, Double popularityCare, Integer ageGroup, Integer gender, Map<Party, Double> partyPopularities, Map<Party, Double> partyPopularityMultipliers) {
        List<Candidate> ranking = new ArrayList<>();

        while (true) {
            Candidate nextCandidate = getClosestCandidate(candidates, politicalLeaning, politicalLeaningTolerance, popularityCare, ageGroup, gender, partyPopularities, partyPopularityMultipliers);

            if (nextCandidate == null) {
                break;
            }

            ranking.add(nextCandidate);
            candidates.remove(nextCandidate);
//            System.out.println(nextCandidate.getParty().getName());
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
            Double politicalLeaningTolerance = random() * 1.8;
            Double popularityCare = random() * 0.5;
            Integer ageGroup = (int) floor((random() * 5));
            Integer gender = (int) floor(random() * 2);
            List<Candidate> rankedCandidates = rankCandidates(new HashSet<>(candidates), politicalLeaning, politicalLeaningTolerance, popularityCare, ageGroup, gender, partyPopularities, partyPopularityMultipliers);

            if (!rankedCandidates.isEmpty()) {
                voters.add(new Voter(rankedCandidates));
            }
//            System.out.println("----------");
        }

        return voters;

    }

    /**
     * Creates the number of Constituencies input.
     *
     * @param constituencyCount an Integer number of Constituencies to generate
     * @param parties a Map of Parties to Doubles representing the relative popularity of each Party in the region
     * @param region the Region the Constituencies are being created for
     * @return a Set of the Constituencies created
     */
    private static Set<Constituency> generateConstituencies(Integer constituencyCount, Map<Party, Double> parties, Region region) {
        Set<Constituency> constituencies  = new HashSet<>();

        for (int i = 0; i < constituencyCount; i++) {
            String constituencyName = "Constituency" + i;
            Set<Candidate> candidates = new HashSet<>();
            Map<Party, Double> partyPopularityMultipliers = new HashMap<>();

            for (Party party : parties.keySet()) {
                candidates.add(new Candidate(party.getName() + constituencyName + "Candidate", party, region));
                partyPopularityMultipliers.put(party, random() * 1.5 + 0.5);
            }

            int votersCount = (int) (69724 + random() * 7338);
            Set<Voter> voters = generateVoters(votersCount, candidates, parties, partyPopularityMultipliers);
//            System.out.println("Voters: " + votersCount + ", Voted: " + voters.size());
            constituencies.add(new Constituency(constituencyName, voters, candidates, 1));
        }

        return constituencies;
    }

    /**
     * Generates a Set of Regions with the number of Constituencies input.
     *
     * @param parties a Map of Parties to Lists of Doubles representing the relative popularity of each Party in each region
     * @param constituencyCounts a List of Integers representing the number of Constituencies in each Region
     * @return a Set of Regions
     */
    private static Set<Region> generateRegions(Map<Party, List<Double>> parties, List<Integer> constituencyCounts) {
        Set<Region> regions = new HashSet<>();
        int i = 0;

        for (Integer count : constituencyCounts) {
            Map<Party, Double> regionParties = new HashMap<>();

            for (Party party : parties.keySet()) {
                regionParties.put(party, parties.get(party).get(i));
            }

            Region region = new Region("Region" + i, count * 2, regionParties.keySet());
            region.addConstituencies(generateConstituencies(count, regionParties, region));
            regions.add(region);
            i++;
        }

        return regions;
    }

    /**
     * Generates a Set of Regions and runs an election in each of them, returning the overall votes for each Party.
     *
     * @param parties a Map of Parties to Lists of Doubles representing the relative popularity of each Party in each region
     * @param constituencyCounts a List of Integers representing the number of Constituencies in each Region
     * @return a Map of Parties to Integers representing the number of Votes each Party received in the election
     */
    public static Map<Party, Integer> simulateElection(Map<Party, List<Double>> parties, List<Integer> constituencyCounts) {
        // Generate regions
        Set<Region> regions = generateRegions(parties, constituencyCounts);
        Map<Party, Integer> overallResults = new HashMap<>();

        // Run the election
        for (Region region : regions) {
//            System.out.println(region.getName());
            List<Candidate> elected = region.election();
            Map<Party, Integer> results = new HashMap<>();

            // Display aggregated counts of votes per party
            for (Candidate candidate : elected) {
                Party party = candidate.getParty();
                results.put(party, results.getOrDefault(party, 0) + 1);
                overallResults.put(party, overallResults.getOrDefault(party, 0) + 1);
            }

//            for (Party party : results.keySet()) {
//                System.out.println(party.getName() + " " + results.get(party));
//            }

//            System.out.println("----------");
        }

        // Display overall results
//        System.out.println("Overall results:");

//        for (Party party : overallResults.keySet()) {
//            System.out.println(party.getName() + ": " + overallResults.get(party));
//        }

        return overallResults;
    }

    /**
     * Main method for running code.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Set up test Parties
        Map<Party, List<Double>> parties = new HashMap<>();
        Party circleParty = new Party("Circle Party", 0.6, 0.1, Arrays.asList(0.2,0.08,0.02,-0.03,-0.07), Arrays.asList(0.0,0.03));
        Party lineParty = new Party("Line Party", -0.4, 0.2, Arrays.asList(-0.13,-0.08,-0.06,-0.02,0.18), Arrays.asList(-0.02,0.01));
        Party triangleParty = new Party("Triangle Party", -0.6, 0.25, Arrays.asList(-0.1,-0.05,-0.04,0.03,0.04), Arrays.asList(0.05,-0.02));
        Party squareParty = new Party("Square Party", 0.2, 0.26, Arrays.asList(0.0,0.06,0.03,-0.02,-0.08), Arrays.asList(-0.04,-0.04));
        Party pentagonParty = new Party("Pentagon Party", -0.2, 0.1, Arrays.asList(0.01,0.0,-0.01,0.03,-0.02), Arrays.asList(0.01,0.02));
        Party hexagonParty = new Party("Hexagon Party", -0.8, 0.02, Arrays.asList(0.0,0.01,0.01,0.01,-0.01), Arrays.asList(0.02,0.01));
        Party septagonParty = new Party("Septagon Party", 0.8, 0.01, Arrays.asList(0.01,0.01,0.01,0.01,0.0), Arrays.asList(0.01,0.01));
        Party octagonParty = new Party("Octagon Party", 0.0, 0.03, Arrays.asList(0.01,-0.01,-0.01,0.01,-0.01), Arrays.asList(0.0,0.0));
        Party nonagonParty = new Party("Nonagon Party", 0.4, 0.01, Arrays.asList(0.02,0.0,0.0,0.0,0.0), Arrays.asList(0.01,0.0));

        // Set up Map of Parties with relative regional popularities
        parties.put(circleParty,Arrays.asList(0.04,0.0,-0.02,-0.01,0.0,0.0,0.01,-0.02,-0.01,-0.03,-0.03));
        parties.put(lineParty,Arrays.asList(0.0,0.01,0.01,0.04,0.01,0.03,0.03,-0.05,-0.04,-0.1,-0.09));
        parties.put(triangleParty,Arrays.asList(-0.07,0.01,0.05,0.02,0.06,0.03,0.02,0.02,0.05,-0.06,0.02));
        parties.put(squareParty,Arrays.asList(0.06,-0.08,-0.09,-0.07,-0.05,-0.04,-0.03,0.07,0.05,-0.08,-0.07));
        parties.put(pentagonParty,Arrays.asList(0.01,0.08,0.05,0.02,-0.02,-0.02,-0.01,-0.03,0.0,0.0,-0.03));
        parties.put(hexagonParty,Arrays.asList(0.0,0.0,0.01,0.01,0.0,0.01,0.01,0.0,0.0,0.01,0.0));
        parties.put(septagonParty,Arrays.asList(0.0,0.0,0.01,0.01,0.0,0.01,0.0,0.01,-0.01,0.01,0.0));
        parties.put(octagonParty,Arrays.asList(-0.03,-0.03,-0.03,-0.03,-0.03,-0.03,-0.03,-0.03,-0.03,0.29,-0.03));
        parties.put(nonagonParty,Arrays.asList(-0.01,-0.01,-0.01,-0.01,-0.01,-0.01,-0.01,-0.01,-0.01,-0.01,0.23));

        List<Integer> constituencyCounts = Arrays.asList(37,46,29,31,24,29,27,37,14,29,16);

        // Run simulator repeatedly
        int runs = 10;
        Map<Party, Integer> resultsTotals = new HashMap<>();

        for (int i = 0; i < runs; i++) {
            System.out.println("Running simulation " + i);
            Map<Party, Integer> results = simulateElection(parties, constituencyCounts);

            for (Party party : results.keySet()) {
                resultsTotals.put(party, resultsTotals.getOrDefault(party, 0) + results.get(party));
                party.removeCandidates();
            }

        }

        // Display average results
        for (Party party : resultsTotals.keySet()) {
            int averageTotal = resultsTotals.get(party) / runs;
            System.out.println(party.getName() + ": " + averageTotal);
        }

    }

}