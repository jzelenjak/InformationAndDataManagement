import java.sql.*;
import java.util.Properties;

/**
 * Helpful resources that were used:
 * An example: https://www.javaguides.net/2020/02/java-jdbc-postgresql-select-example.html
 * https://jdbc.postgresql.org/documentation/head/index.html
 * https://jdbc.postgresql.org/documentation/80/connect.html
 *
 * Driver: https://mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
 * TO run: `java -cp "postgresql-42.3.3.jar" src/SQL.java`
 */
public class SQL {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        getTitlesForYear(1990);
        //getCharacterNames("Matrix");
        //getTitlesWithKeyword("thriller");
        //getAllRoles("DiCaprio");
    }


    /**
     * Return all titles for a specific year
     * @param year to select the titles
     */
    private static void getTitlesForYear(int year) {
        String url = "jdbc:postgresql://localhost:5432/imdb_100k";
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","");  // put your postgres user password here
        // I Establish a Connection
        try(Connection conn = DriverManager.getConnection(url, props)) {
            // II Create a prepared statement using connection object
            PreparedStatement st = conn.prepareStatement("SELECT title FROM title_100k WHERE production_year = ?");
            st.setInt(1,year);
            System.out.println(st);

            // III Execute or update the query
            ResultSet rs = st.executeQuery();

            // IV Process the ResultSet object
            while(rs.next()) System.out.println(rs.getString("title"));
        } catch (SQLException e) {
            printSQLException(e);
        }
    }


    /**
     * Return all character names played in titles that contain a specific string
     * @param str to be matched in the title
     */
    private static void getCharacterNames(String str) {
        String url = "jdbc:postgresql://localhost:5432/imdb_100k?user=postgres&password=";  // put your postgres user password here
        // I Establish a Connection
        try(Connection conn = DriverManager.getConnection(url)){
            // II Create a prepared statement using connection object
            PreparedStatement st = conn.prepareStatement(
                    "SELECT DISTINCT ch.name FROM char_name_100k AS ch JOIN cast_info_100k AS c ON ch.id=c.person_role_id JOIN title_100k AS t ON t.id=c.movie_id WHERE t.title LIKE ?"
            );
            st.setString(1, String.format("%%%s%%", str));
            System.out.println(st);

            // III Execute or update the query
            ResultSet rs = st.executeQuery();

            // IV Process the ResultSet object
            while(rs.next()) System.out.println(rs.getString("name"));

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Return all titles that are associated with a specific keyword
     * @param keyword to be matched
     */
    private static void getTitlesWithKeyword(String keyword) {
        String url = "jdbc:postgresql://localhost:5432/imdb_100k?user=postgres&password=";  // put your postgres user password here
        // I Establish a Connection
        try(Connection conn = DriverManager.getConnection(url)) {
            // II Create a prepared statement using connection object
            PreparedStatement st = conn.prepareStatement(
                    "SELECT t.title FROM keyword_100k AS k JOIN movie_keyword_100k AS mk ON k.id=mk.keyword_id JOIN title_100k AS t ON t.id=mk.movie_id WHERE k.keyword LIKE ?"
            );
            st.setString(1, String.format("%%%s%%", keyword));
            System.out.println(st);

            // III Execute or update the query
            ResultSet rs = st.executeQuery();

            // IV Process the ResultSet object
            while(rs.next()) System.out.println(rs.getString("title"));
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Given a name, return all roles it is associated with
     * @param name to be matched
     */
    private static void getAllRoles(String name) {
        String url = "jdbc:postgresql://localhost:5432/imdb_100k?user=postgres&password=";  // put your postgres user password here
        // I Establish a Connection
        try(Connection conn = DriverManager.getConnection(url)) {
            // II Create a prepared statement using connection object
            PreparedStatement st = conn.prepareStatement("SELECT DISTINCT ch.name\n" +
                    "FROM (SELECT * FROM person_100k WHERE name LIKE ?) AS p JOIN cast_info_100k AS c ON c.person_id=p.id\n" +
                    "JOIN char_name_100k AS ch ON ch.id=c.person_role_id");
            st.setString(1, String.format("%%%s%%", name));

            // III Execute or update the query
            ResultSet rs = st.executeQuery();

            // IV Process the ResultSet object
            while(rs.next()) System.out.println(rs.getString("name"));
        } catch(SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Print SQLException in a more understandable way
     * @param ex SQLException to be printed
     */
    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    /*You can further experiment with creating statements for Inserting new rows to existing relations or for Creating new tables.*/

    /*
     * The -cp, or CLASSPATH, is used as an option to the Java command.
     * It is a parameter in the JVM or Java compiler that specifies the location of classes and packages which are defined by the user.
     * The CLASSPATH parameter can either be set via the command-line or through an environment variable.
     * The -cp parameter specifies a list of directories, JAR archives and ZIP archives to search for class files.
     */
}
