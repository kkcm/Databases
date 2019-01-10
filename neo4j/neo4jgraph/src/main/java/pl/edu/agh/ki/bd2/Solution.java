package pl.edu.agh.ki.bd2;

public class Solution {

    private  GraphDatabase graphDatabase;

    public Solution(GraphDatabase graphDatabase){
        this.graphDatabase = graphDatabase;
    }

    public void databaseStatistics() {
        System.out.println(graphDatabase.runCypher("CALL db.labels()"));
        System.out.println(graphDatabase.runCypher("CALL db.relationshipTypes()"));
    }

    public void runAllTests() {
        System.out.println(findActorByName("Emma Watson"));
        System.out.println(findMovieByTitleLike("Star Wars"));
        System.out.println(findRatedMoviesForUser("maheshksp"));
        System.out.println(findCommonMoviesForActors("Emma Watson", "Daniel Radcliffe"));
        System.out.println(findMovieRecommendationForUser("emileifrem"));
    }

    private String findActorByName(final String actorName) {
        return graphDatabase.runCypher("MATCH (a:Actor {name: '" + actorName + "'}) RETURN a.name");

    }

    private String findMovieByTitleLike(final String movieName) {
        return graphDatabase.runCypher("MATCH (m:Movie) WHERE m.title CONTAINS '" + movieName + "' RETURN m.title");
    }

    private String findRatedMoviesForUser(final String userLogin) {
        return graphDatabase.runCypher("MATCH (u:User {login: '" + userLogin + "'})-[RATED]->(m:Movie) RETURN m.title");

    }

    private String findCommonMoviesForActors(String actorOne, String actorTwo) {
        return graphDatabase.runCypher("MATCH (a1:Actor {name: '" + actorOne + "'})-[:ACTS_IN]->(m:Movie)" +
                "<-[:ACTS_IN]-(a2:Actor {name: '" + actorTwo + "'}) RETURN m.title");
    }

    private String findMovieRecommendationForUser(final String userLogin) {
        return graphDatabase.runCypher("MATCH (u1:User {login: '" + userLogin + "'})<-[FRIEND]-(u2:User)" +
        "-[RATED]->(recommended:Movie) RETURN recommended.title");
    }

}
