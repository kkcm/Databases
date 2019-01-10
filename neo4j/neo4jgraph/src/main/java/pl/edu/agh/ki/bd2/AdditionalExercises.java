package pl.edu.agh.ki.bd2;

public class AdditionalExercises {

    private  GraphDatabase graphDatabase;

    public AdditionalExercises(GraphDatabase graphDatabase){
        this.graphDatabase = graphDatabase;
    }

    public void runAllTests() {

        System.out.println("Exercise 4:");
        System.out.println(createNewActor("Adam Adamowicz"));
        System.out.println(createNewMovie("Rodzina Adamsow"));
        System.out.println(createNewRelationACTS_IN("Adam Adamowicz", "Rodzina Adamsow"));

        System.out.println("Exercise 5:");
        System.out.println(setActorProperties("Adam Adamowicz", "Warszawa, Polska", "29.02.1993", "Dużo by opowiadac..."));

        System.out.println("Exercise 6:");
        System.out.println(actorsWhoPlayedInNMoviesOrMore("6"));

        System.out.println("Exercise 7:");
        System.out.println(averageMoviesPlayedForActorsWhoPlayedInNMoviesOrMore("7"));

        System.out.println("Exercise 8:");
        System.out.println(actorsWhoPlayedInNMoviesOrMoreAndDirectedOneOrMore("5"));

        System.out.println("Exercise 9:");
        System.out.println(usersWhoRatedMovieWithNStarsOrMore("maheshksp", "3"));

        System.out.println("Exercise 10:");
        System.out.println(pathsBetweenActorsWithoutMovies("Emma Watson", "Tomasz Karolak"));

        System.out.println("Exercise 11:");
        compareTimeWithAndWithoutIndex("Emma Watson", "Tomasz Karolak");
    }

    private String createNewActor(final String newActorName) {
//        graphDatabase.runCypher("MATCH (a:Actor {name: '" + newActorName + "'})-[r:ACTS_IN]->(m:Movie {title: 'Rodzina Adamsow'}) DELETE r, a, m");
//        graphDatabase.runCypher("MATCH (a:Actor {name: '" + newActorName + "'}) DELETE a");

        return graphDatabase.runCypher("CREATE (a:Actor {name: '" + newActorName + "'}) RETURN a.name");
    }

    private String createNewMovie(final String newMovieTitle) {
        return graphDatabase.runCypher("CREATE (m:Movie {title: '" + newMovieTitle + "'}) RETURN m.title");
    }

    private String createNewRelationACTS_IN(final String actorName, final String movieTitle) {
        return graphDatabase.runCypher("MERGE (a:Actor {name: '" + actorName + "'})-[:ACTS_IN]->(m:Movie {title: '" + movieTitle + "'})");
    }

    private String setActorProperties(final String actorName, final String actorBirthplace, final String actorBirthday, final String actorBiography) {
        return graphDatabase.runCypher("MATCH (a:Actor {name: '" + actorName + "'}) " +
                "SET a.birthplace='" + actorBirthplace + "', a.birthday='" + actorBirthday + "', a.biography='" + actorBiography + "'  RETURN a");
    }

    private String actorsWhoPlayedInNMoviesOrMore(final String number) {
        return graphDatabase.runCypher("MATCH (a:Actor)-[:ACTS_IN]->(m:Movie) WITH a, collect(m) as movies " +
                "WHERE length(movies) >= " + number + " RETURN a.name");
    }

    private String averageMoviesPlayedForActorsWhoPlayedInNMoviesOrMore(final String number) {
        return graphDatabase.runCypher("MATCH (a:Actor)-[:ACTS_IN]->(m:Movie) WITH a, collect(m) as movies " +
                "WHERE length(movies) >= " + number + " WITH length(movies) as movies RETURN avg(movies)");
    }

    private String actorsWhoPlayedInNMoviesOrMoreAndDirectedOneOrMore(final String number) {
        return graphDatabase.runCypher("MATCH (md:Movie)<-[:DIRECTED]-(a:Actor)-[:ACTS_IN]->(m:Movie) WITH a, collect(m) as movies " +
                "WHERE length(movies) >= " + number + " RETURN a.name, length(movies) ORDER BY length(movies)");
    }

    private String usersWhoRatedMovieWithNStarsOrMore(final String userName, final String number) {
        return graphDatabase.runCypher("MATCH (u1:User {login: '" + userName + "'})-[:FRIEND]->(u2:User)-[r:RATED]->(m:Movie) " +
                "WHERE r.stars >= " + number + " RETURN u2.name, m.title, r.stars");
    }

    private String pathsBetweenActorsWithoutMovies(final String firstActorName, final String secondActorName) {
        return graphDatabase.runCypher("MATCH p=shortestPath((a1:Actor {name: '" + firstActorName + "'})-[*]-(a2:Actor {name: '" + secondActorName + "'})) " +
                "RETURN extract(a in filter(x in nodes(p) where (x:Actor)) | a.name) as path");
    }

    private String createIndexOnActorName(){
        return graphDatabase.runCypher("CREATE INDEX ON :Actor(name) ");
    }

    private String dropIndexFromActorName(){
        return graphDatabase.runCypher("DROP INDEX ON :Actor(name) ");
    }

    private void compareTimeWithAndWithoutIndex(final String firstActorName, final String secondActorName){

        long timeWithoutIndexActor;
        long timeWithIndexActor;

        long timeWithoutIndexPath;
        long timeWithIndexPath;


        timeWithoutIndexActor = measureQueryTime("PROFILE MATCH (a:Actor) WHERE a.name = '" + firstActorName + "' RETURN a.name");
        timeWithoutIndexPath = measureQueryTime("PROFILE MATCH p=shortestPath( (a:Actor {name: '" + firstActorName + "'})-[*]-(b:Actor {name: '" + secondActorName + "'} ))  RETURN extract(a in filter(x in nodes(p) where (x:Actor)) | a.name) as path");

        System.out.println(createIndexOnActorName());
        timeWithIndexActor = measureQueryTime("PROFILE MATCH (a:Actor) WHERE a.name = '" + firstActorName + "' RETURN a.name");
        timeWithIndexPath = measureQueryTime("PROFILE MATCH p=shortestPath( (a:Actor {name: '" + firstActorName + "'})-[*]-(b:Actor {name: '" + secondActorName + "'} ))  RETURN extract(a in filter(x in nodes(p) where (x:Actor)) | a.name) as path");
        System.out.println(dropIndexFromActorName());

        System.out.println("Time for searching actor without index: " + timeWithoutIndexActor);
        System.out.println("Time for searching actor with index: " + timeWithIndexActor);
        System.out.println("Time difference for searching actor with and without index: " + (timeWithoutIndexActor - timeWithIndexActor));

        System.out.println();

        System.out.println("Time for searching path without index: " + timeWithoutIndexPath);
        System.out.println("Time for searching path with index: " + timeWithIndexPath);
        System.out.println("Time difference for searching path with and without index: " + (timeWithoutIndexPath - timeWithIndexPath));

    }

    private long measureQueryTime(final String query){
        long startTime = System.nanoTime();
        System.out.println(graphDatabase.runCypher(query));
        long endTime = System.nanoTime();

        return endTime - startTime;
    }





}
