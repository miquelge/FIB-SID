
package RiverBasin;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 *
 * @author Miquelge
 */
public class WWTPJENA {
    
    String jenaPath;
    String ontologyFile;
    String namingContext;
    OntModel model;
    OntDocumentManager documentManager;
    
    public WWTPJENA(String jenaPath, String ontologyFile, String namingContext) {
        this.jenaPath = jenaPath;
        this.ontologyFile = ontologyFile;
        this.namingContext =  namingContext;
    }
    
    public void loadOntology () {
        System.out.println("Loading Ontology");
        model = ModelFactory.createOntologyModel(
                OntModelSpec.OWL_DL_MEM_TRANS_INF
        );
        documentManager = model.getDocumentManager();
        documentManager.addAltEntry(
                namingContext,
                "file:" + this.jenaPath + this.ontologyFile
        );
        model.read(namingContext);
        System.out.println("Ontology loaded");   
    }

    public void releaseOntology() throws FileNotFoundException {
        System.out.println("Releasing Ontology");
        if (!model.isClosed()) {
            model.write(new FileOutputStream("./ModifiedOntology.owl", true));
            model.close();
        }
        System.out.println("Ontology released");
    }

    public void getIndividuals() {
        System.out.println("The Ontology has the following individuals:");
        int j = 1;
        for (Iterator i = model.listIndividuals(); i.hasNext(); ) {
            System.out.println("  " + j + ".-");
            System.out.println(i.next());
            j++;
        }
    }

    public void getIndividualsByClass() {
        System.out.println("The Ontology has the following classes and individuals:");
        Iterator<OntClass> classesIt = model.listNamedClasses();
        while (classesIt.hasNext()) {
            OntClass actual = classesIt.next();
            System.out.println("    Individuals of the class: " + actual.getURI());
            OntClass pizzaClass = model.getOntClass(actual.getURI());
            int j = 0;
            for (Iterator i = model.listIndividuals(pizzaClass); i.hasNext(); ) {
                System.out.println("        " + j + ".-");
                System.out.println("        " + i.next());
                j++;
            }
        }

    }
    
    public void getPropertiesByClass() {
        System.out.println("The Ontology has the following classes and properties:");
        Iterator<OntClass> classesIt = model.listNamedClasses();
        while (classesIt.hasNext()) {
            OntClass actual = classesIt.next();
            System.out.println( "Properties of the class " + actual.getURI() + ":");
            OntClass ontClass = model.getOntClass(actual.getURI() );
            Iterator<OntProperty> classProperties = ontClass.listDeclaredProperties();
            while (classProperties.hasNext()) {
                OntProperty property = classProperties.next();
                System.out.println("    · Name :" + property.getLocalName() );
                System.out.println("        · Domain :" + property.getDomain() );
                System.out.println("        · Range :" + property.getRange());
                System.out.println("        · Inverse :" + property.hasInverse() );
                System.out.println("        · IsData :" + property.isDatatypeProperty() );
                System.out.println("        · IsFunctional :" + property.isFunctionalProperty() );
                System.out.println("        · IsObject :" + property.isObjectProperty() );
                System.out.println("        · IsSymetric :" + property.isSymmetricProperty() );
                System.out.println("        · IsTransitive :" + property.isTransitiveProperty() );                

            }

                    
        }

    }
    
    public void addInstance(String classUri, String className) {
        System.out.println("Adding " + className + " instance");
        OntClass ontologyClass = model.getOntClass(classUri);
        System.out.println(ontologyClass);
        Individual instance = ontologyClass.createIndividual(className);
        System.out.println(className + " added");
    }

    public void displayIndustriesWithPollutantGenerated() {
        System.out.println("Getting Industries:");
        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                            + "PREFIX ontology: <" + namingContext + "#> "
                            + "SELECT ?Industry ?Quantity "
                            + "where {"
                            + " ?Industry a ?y. "
                            + " ?y rdfs:subClassOf ontology:Industry. "
                            + " ?Industry ontology:pollutantGenerated ?Pollutant. "
                            + " ?Pollutant ontology:hasValue ?Quantity "
                            + "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        
        for (Iterator iter = results; iter.hasNext();) {
            ResultBinding res = (ResultBinding)iter.next();
            Object Industry = res.get("Industry");
            Object PollutantGenerated = res.get("Quantity");
            System.out.println("Industry: "+ Industry + "(Pollutant generated: " + PollutantGenerated + ")") ;
        }
        qe.close();
        System.out.println("Done getting Industries");
    }
    
    public void displayTreatmentPlantsWithPollutantTreated() {
        System.out.println("Getting Treatment Plants:");
        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                            + "PREFIX ontology: <" + namingContext + "#> "
                            + "SELECT ?Plant ?Quantity "
                            + "where {"
                            + " ?Plant a ?y. "
                            + " ?y rdfs:subClassOf ontology:TreatmentPlant. "
                            + " ?Plant ontology:pollutantTreated ?Pollutant. "
                            + " ?Pollutant ontology:hasValue ?Quantity "
                            + "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        
        for (Iterator iter = results; iter.hasNext();) {
            ResultBinding res = (ResultBinding)iter.next();
            Object Industry = res.get("Plant");
            Object PollutantTreated = res.get("Quantity") ;
            System.out.println("Treatment Plant: "+ Industry + "(Pollutant Treated: " + PollutantTreated + ")") ;
        }
        qe.close();
        System.out.println("Done getting Treatment Plants");
    }
    
}
