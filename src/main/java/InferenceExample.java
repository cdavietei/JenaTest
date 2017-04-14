import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class InferenceExample
{
   /**
    * This program takes 4 parameters an input file name
    * an output file name an input file format a reasoning
    * level {RDFS, OWL-DL}
    */
   public static void main(String[] args)
   {
	   //validate the program arguments
    //   if(args.length != 4)
    //   {
    //      System.err.println("Usage: java InferenceExample "
    //         + "<input file> <input format> <output file> "
    //         + "<none|rdfs|owl>");
    //      return;
    //   }

      String inputFileName = "data/mammals.ttl";
      String inputFileFormat = "Turtle";
      String outputFileName = "data/mammals.owl";
      String reasoningLevel = "owl";

      //create an input stream for the input file
      FileInputStream inputStream = null;
      PrintWriter writer = null;
      try
      {
         inputStream = new FileInputStream(inputFileName);
      } catch (FileNotFoundException e) {
         System.err.println("'" + inputFileName
            + "' is an invalid input file.");
         return;
      }

      //create an output print writer for the results
      try
      {
         writer = new PrintWriter(outputFileName);
      } catch (FileNotFoundException e) {
         System.err.println("'" + outputFileName
            + "' is an invalid output file.");
         return;
      }

      //create the appropriate jena model
      OntModel model = null;
      if("none".equals(reasoningLevel.toLowerCase()))
      {
         /*
          * "none" is jena model with OWL_DL
          * ontologies loaded and no inference enabled
          */
         model = ModelFactory.createOntologyModel(
            OntModelSpec.OWL_DL_MEM);
      }
      else if("rdfs".equals(reasoningLevel.toLowerCase()))
      {
         /*
          * "rdfs" is jena model with OWL_DL
          * ontologies loaded and RDFS inference enabled
          */
         model = ModelFactory.createOntologyModel(
            OntModelSpec.OWL_DL_MEM_RDFS_INF);
      }
      else if("owl".equals(reasoningLevel.toLowerCase()))
      {
         /*
          * "owl" is jena model with OWL_DL ontologies
          * wrapped around a pellet-based inference model
          */
         Reasoner reasoner =
            PelletReasonerFactory.theInstance().create();
         Model infModel = ModelFactory.createInfModel(
            reasoner, ModelFactory.createDefaultModel());
         model = ModelFactory.createOntologyModel(
            OntModelSpec.OWL_DL_MEM, infModel);
      }
      else
      {
         //invalid inference setting
         System.err.println("Invalid inference setting, "
            + "choose one of <none|rdfs|owl>.");
         return;
      }

      //load the facts into the model
      model.read(inputStream, null, inputFileFormat);

      //validate the file
      ValidityReport validityReport = model.validate();
      if(validityReport != null && !validityReport.isValid())
      {
         Iterator i = validityReport.getReports();
         while(i.hasNext())
         {
            System.err.println(
               ((ValidityReport.Report)i.next()).getDescription());
         }
         return;
      }

      //Iterate over the individuals, print statements about them
      ExtendedIterator iIndividuals = model.listIndividuals();
      while(iIndividuals.hasNext())
      {
         Individual i = (Individual)iIndividuals.next();
         printIndividual(i, writer);
      }
      iIndividuals.close();

      writer.close();
      model.close();
   }

   /**
    * Print information about the individual
    * @param i The individual to output
    * @param writer The writer to which to output
    */
   public static void printIndividual(
      Individual i, PrintWriter writer)
   {
      //print the local name of the individual (to keep it terse)
      writer.println("Individual: " + i.getLocalName());

      //print the statements about this individual
      StmtIterator iProperties = i.listProperties();
      while(iProperties.hasNext())
      {
         Statement s = (Statement)iProperties.next();
         writer.println("  " + s.getPredicate().getLocalName()
            + " : " + s.getObject().toString());
      }
      iProperties.close();
      writer.println();
   }
}
