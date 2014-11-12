/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.sesame;

import java.io.File;
import java.io.IOException;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
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
    private static Log log = LogFactory.getLog(RMLSesameDataSet.class);
    //private Repository currentRepository = null;
    
    //static RDFFormat TURTLE = RDFFormat.TURTLE;
    
    public RMLSesameDataSet() {
		this(false);
	}
    
    public RMLSesameDataSet(boolean inferencing) {
		try {
			if (inferencing) {
				currentRepository = new SailRepository(
						new ForwardChainingRDFSInferencer(new MemoryStore()));
			} else {
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
}
