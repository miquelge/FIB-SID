/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jenatest;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Property;
import java.io.FileNotFoundException;


/**
 *
 * @author Ignasi Gómez-Sebastià
 */
public class Main {



    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws FileNotFoundException
    {
        
        String JENA = "./";
        String File = "PizzaPatatitas.owl";
        String NamingContext = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
        
        System.out.println("----------------Starting program -------------");

        JenaTester tester = new JenaTester(JENA,File,NamingContext);
        
        System.out.println("Load the Ontology");
        tester.loadOntology();   
        System.out.println("------------------");    
        
        tester.addInstancesCustom(
                "http://www.co-ode.org/ontologies/pizza/pizza.owl#CoberturaDeDistribuidos",
                "CoberturaDeDistribuidos"
        );
        tester.addInstancesCustom(
                "http://www.co-ode.org/ontologies/pizza/pizza.owl#CoberturaDeQuesoSistemas",
                "CoberturaDeQuesoSistemas"
        );
        tester.addInstancesCustom(
                "http://www.co-ode.org/ontologies/pizza/pizza.owl#BaseInteligente",
                "BaseInteligente"
        );
        System.out.println("Intances created");
        
        System.out.println("Check intance creation");
        //tester.getIndividuals();
        
        
        
        /*
        System.out.println("Get the different individuals");
        tester.getIndividuals();
        System.out.println("------------------");
        
        System.out.println("Grouping individuals by class");
        tester.getIndividualsByClass();
        System.out.println("------------------");
        
        
        System.out.println("Grouping properties by class");
        tester.getPropertiesByClass();       
        System.out.println("------------------");
        
        System.out.println("Run a test Data property");
        tester.runSparqlQueryDataProperty();
        System.out.println("------------------");
        
        System.out.println("Run a test Object property");
        tester.runSparqlQueryObjectProperty();
        System.out.println("------------------");
        
        System.out.println("Run and modify");
        tester.runSparqlQueryModify();
        System.out.println("------------------");

        System.out.println("Re-Run to check modification");
        tester.runSparqlQueryModify();
        System.out.println("------------------");
        */
        
        tester.releaseOntology();
        
        System.out.println("--------- Program terminated --------------------");
     
    }

}
