
package RiverBasin;

import java.io.FileNotFoundException;

/**
 *
 * @author Miquelge
 */
public class Main {

    
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Inici del programa");
        
        String JENA = "./";
        String File = "OriginalOntology.owl";
        String NamingContext = "http://www.semanticweb.org/salvarez/ontologies/OriginalOntology";
        
        WWTPJENA jena = new WWTPJENA(JENA, File, NamingContext);
        
        jena.loadOntology();
        
        //jena.getIndividuals();
        //jena.getIndividualsByClass();
        //jena.getPropertiesByClass();
        
        jena.addInstance(
                "http://www.semanticweb.org/salvarez/ontologies/OriginalOntology#Food",
                "Hacendoso"
        );
        
        jena.displayIndustriesWithPollutantGenerated();
        jena.displayTreatmentPlantsWithPollutantTreated();
        
        jena.releaseOntology();
        
        System.out.println("Fi del programa");
    }
    
}
