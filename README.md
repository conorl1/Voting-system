# Voting-system
A simulator of a modified version of the single transferrable vote or proportional ranked choice voting system.

Implemented in Java.

Voting is simulated across multiple Constituencies across multiple Regions, with half of seats in each Region coming from the individual constituencies and half coming from regional seats to increase the proportionality across the Region. The ideal seat distribution in a Region is determined by the total number of votes for the Parties that the Candidates voted for in the Constituencies are members of.

Main.java contains testing code with some example Parties. It also includes code to generate Regions containing random Constituencies with randomly generated Voters.

Voting can be simulated across multiple elections to get average results.

Main.java can be modified to run with different data. For example, change the popularity scores of the Parties, add different Parties, change the number of Constituencies in each Region, or change the number of seats in each constituency.
