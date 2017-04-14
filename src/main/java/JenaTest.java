import java.io.*;
import java.util.Iterator;

import org.mindswap.pellet.jena.*;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.*;
import org.apache.jena.reasoner.rulesys.*;
import org.apache.jena.util.*;


public class JenaTest {

    protected Model mModel;
    public static final String DEFAULT_NAME_SPACE = "http://hofstra.edu/#";

    public static void main(String[] args) {
        JenaTest jena = new JenaTest();

        System.out.println("Loading model\n");
        jena.populateModel("data/Books.ttl");

        System.out.println("Selecting all journals by John Maur");
        jena.selectAuthor("John Maur");

        System.out.println("Selecting all journals about Computers");
        jena.selectTopic("Computers");

    }

    public void populateModel(String filePath) {
        mModel = ModelFactory.createOntologyModel();
        InputStream modelInstance = FileManager.get().open(filePath);
        mModel.read(modelInstance, DEFAULT_NAME_SPACE, "TTL");
    }

    public void selectAuthor(String author) {
        String queryString = String.format("SELECT DISTINCT ?title  WHERE { " +
            "?article schema:author \"%s\" . "+
            "?article schema:name ?title .  }",
        author);

        runQuery(queryString, mModel);
    }

    public void selectTopic(String topic) {
        String queryString = String.format("SELECT DISTINCT ?title WHERE { " +
            "?article schema:genre \"%s\" . " +
            "?article schema:name ?title . }",
        topic);

        runQuery(queryString, mModel);
    }

    public void runQuery(String queryRequest, Model model){

        StringBuilder queryStr = new StringBuilder();
        // Establish Prefixes
        //Set default Name space first
        queryStr.append("PREFIX article: <"+ DEFAULT_NAME_SPACE +">");
        queryStr.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
        queryStr.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        queryStr.append("PREFIX schema: <http://schema.org/#> ");

        //Now add query
        queryStr.append(queryRequest);
        System.out.println(queryStr.toString());
        Query query = QueryFactory.create(queryStr.toString());

        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
	    ResultSet response = qexec.execSelect();

	    System.out.println(ResultSetFormatter.asText(response));

        } finally { qexec.close();}
    }

}
