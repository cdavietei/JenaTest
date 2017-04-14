//package net.semweb.ch2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;


public class HelloSemanticWeb {

    static String defaultNameSpace = "http://semwebprogramming.org/2009/ont/chp2:#";

    private Model _friends = null;

    public static void main(String[] args) throws IOException {

        HelloSemanticWeb hello = new HelloSemanticWeb();

        //Load my FOAF friends
        System.out.println("Load my FOAF Friends");
        hello.populateFOAFFriends();

        // Say Hello to myself
        System.out.println("\nSay Hello to Myself");
        hello.mySelf(hello._friends);

        // Say Hello to my FOAF Friends
        System.out.println("\nSay Hello to my FOAF Friends");
        hello.myFriends(hello._friends);

    }

    private void populateFOAFFriends(){
        _friends = ModelFactory.createOntologyModel();
        InputStream inFoafInstance = FileManager.get().open("data/FOAFFriends.rdf");
        _friends.read(inFoafInstance,defaultNameSpace);
        //inFoafInstance.close();

    }

    private void mySelf(Model model){
        //Hello to Me - focused search
        runQuery(" select DISTINCT ?name where{ people:me foaf:name ?name  }", model);  //add the query string

    }

    private void myFriends(Model model){
        //Hello to just my friends - navigation
        //runQuery(" select DISTINCT ?name ?emailAddr ?fullName where{ ?name ab:city \"Hempstead\" . ?name ab:email ?emailAddr . ?name ab:fullName ?fullName . } ", model);  //add the query string
        runQuery(" select DISTINCT ?name where{  people:me foaf:knows ?friend . ?friend foaf:name ?name . } ", model);  //add the query string

    }

    private void runQuery(String queryRequest, Model model){

        StringBuffer queryStr = new StringBuffer();
        // Establish Prefixes
        //Set default Name space first
        queryStr.append("PREFIX people" + ": <" + defaultNameSpace + "> ");
        queryStr.append("PREFIX rdfs" + ": <" + "http://www.w3.org/2000/01/rdf-schema#" + "> ");
        queryStr.append("PREFIX rdf" + ": <" + "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "> ");
        queryStr.append("PREFIX foaf" + ": <" + "http://xmlns.com/foaf/0.1/" + "> ");
        queryStr.append("PREFIX ab" + ": <http://learningsparql.com/ns/addressbook#" + "> ");

        //Now add query
        queryStr.append(queryRequest);
        Query query = QueryFactory.create(queryStr.toString());
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
	    ResultSet response = qexec.execSelect();

	    while( response.hasNext()){
                QuerySolution soln = response.nextSolution();
                RDFNode name = soln.get("?name");
                RDFNode nick = soln.get("?nick");
                RDFNode phone = soln.get("?phone");
                RDFNode homepage = soln.get("?homepage");
                RDFNode email = soln.get("?emailAddr");
                RDFNode fullName = soln.get("?fullName");
                if( name != null ){
		    System.out.println( " ... " + name.toString() );
		}
		else
                    System.out.println("No Friends found!");


	    }

        } finally { qexec.close();}
    }

}
