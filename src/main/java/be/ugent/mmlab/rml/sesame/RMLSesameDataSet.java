/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.sesame;

import be.ugent.mmlab.rml.extractor.RMLUnValidatedMappingExtractor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 *
 * @author andimou
 */
public class RMLSesameDataSet extends SesameDataSet {

    // useful -local- constants
    static RDFFormat NTRIPLES = RDFFormat.NTRIPLES;
    static RDFFormat N3 = RDFFormat.N3;
    static RDFFormat RDFXML = RDFFormat.RDFXML;

    static RDFFormat Turtle = RDFFormat.TURTLE;
    static RDFFormat TURTLE = RDFFormat.TURTLE;
    static String RDFTYPE = RDF.TYPE.toString();

    // Log
    private static final Logger log = LogManager.getLogger(RMLSesameDataSet.class);
    //private Repository currentRepository = null;
    
    //static RDFFormat TURTLE = RDFFormat.TURTLE;
    
    public RMLSesameDataSet() {
		this(false);
	}
    
    public RMLSesameDataSet(boolean inferencing) {
        try {
            if (inferencing) {
                log.debug("inference enabled");
                currentRepository = new SailRepository(
                        new ForwardChainingRDFSInferencer(new MemoryStore()));
            } else {
                log.debug("inference disabled");
                currentRepository = new SailRepository(new MemoryStore());
            }
            currentRepository.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public RMLSesameDataSet(String pathToDir, boolean inferencing) {
        File f = new File(pathToDir);
        try {
            if (inferencing) {
                currentRepository = new SailRepository(
                        new ForwardChainingRDFSInferencer(new NativeStore(f)));
            } else {
                currentRepository = new SailRepository(new NativeStore(f));
            }
            currentRepository.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

	public RMLSesameDataSet(String sesameServer, String repositoryID) {
		currentRepository = new HTTPRepository(sesameServer, repositoryID);
		try {
			currentRepository.initialize();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

    /**
     * Load data in specified graph (use default graph if contexts is null)
     *
     * @param filePath
     * @param format
     * @param contexts
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Override
    
    public void loadDataFromFile(String filePath, RDFFormat format,
            Resource... contexts) throws RepositoryException, IOException, RDFParseException {

        RepositoryConnection con = null;
        try {
            con = currentRepository.getConnection();
            // upload a file
            File f = new File(filePath);
            try{
            con.add(f, null, RDFFormat.TURTLE);
            }
            catch(Exception e){
                log.error( 
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Syntax error: " 
                    + e );
            }
        } finally {
            try {
                con.close();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void add(Resource s, URI p, Value o, Resource... contexts) {

        try {
            RepositoryConnection con = currentRepository.getConnection();
            try {
                ValueFactory myFactory = con.getValueFactory();
                Statement st = myFactory.createStatement((Resource) s, p,
                        (Value) o);
                if(s == null || o == null)
                    log.debug(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "Added triple without checking if it's BNode (" + s.stringValue()
                        + ", " + p.stringValue() + ", " + o.stringValue() + ").");
                con.add(st, contexts);
                con.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            // handle exception
        }
    }
    
    @Override
    public List<Statement> tuplePattern(Resource s, URI p, Value o,
            Resource... contexts) {
        log.debug("");
        try {
            RepositoryConnection con = currentRepository.getConnection();
            try {
                RepositoryResult<Statement> repres = con.getStatements(s, p, o, true, contexts);

                log.debug(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "\n subject "
                        + s
                        + "\n predicate "
                        + p
                        + "\n object "
                        + o);
                ArrayList<Statement> reslist = new ArrayList<Statement>();
                while (repres.hasNext()) {
                    reslist.add(repres.next());
                }
                return reslist;
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public String printRDF(RDFFormat outform) {
        try {
            RepositoryConnection con = currentRepository.getConnection();
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                RDFWriter w = Rio.createWriter(outform, out);

                con.export(w);
                String result = new String(out.toByteArray(), "UTF-8");
                log.info("write result " + result);
                return result;
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
        
    public void skolemization(RMLSesameDataSet rmlMappingGraph) {
        TupleQueryResult result = null;
        try {
            RepositoryConnection con = currentRepository.getConnection();
            String queryString = "SELECT ?p WHERE { ?x ?p ?y } ";
            TupleQuery tupleQuery;
            tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            result = tupleQuery.evaluate();

        } catch (RepositoryException ex) {
            java.util.logging.Logger.getLogger(RMLUnValidatedMappingExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedQueryException ex) {
            java.util.logging.Logger.getLogger(RMLUnValidatedMappingExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (QueryEvaluationException ex) {
            java.util.logging.Logger.getLogger(RMLUnValidatedMappingExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while (result.hasNext()) {  // iterate over the result
                BindingSet bindingSet = result.next();
                Value valueOfX = bindingSet.getValue("x");
                Value valueOfY = bindingSet.getValue("y");
                log.debug(//Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        "\n valueOfX "
                        + valueOfX.stringValue()
                        + "\n valueOfY"
                        + valueOfY.stringValue());
            }
        } catch (QueryEvaluationException ex) {
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
