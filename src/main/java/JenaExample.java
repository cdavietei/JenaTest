import java.io.*;
import java.util.Iterator;

import org.mindswap.pellet.jena.*;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.*;
import org.apache.jena.reasoner.rulesys.*;
import org.apache.jena.util.*;


public class JenaExample {


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
