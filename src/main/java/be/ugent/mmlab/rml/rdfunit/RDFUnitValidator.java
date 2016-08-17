package be.ugent.mmlab.rml.rdfunit;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.writer.RdfStreamWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.results.TestExecutionWriter;
import org.aksw.rdfunit.sources.SchemaService;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12/18/14 12:51 PM
 */
public class RDFUnitValidator {
    
    // Log
    private static final Logger log = LogManager.getLogger(RDFUnitValidator.class);
    
    private final String dataFolder = ".rdfunit/";
    private final String serializationFormat = "TURTLE";
    private final TestCaseExecutionType testCaseExecutionType = TestCaseExecutionType.extendedTestCaseResult;
    private final RDFUnitConfiguration configuration;

    private final TestSuite testSuite;

    public RDFUnitValidator(String datasetURI, String rdfDataFile) {

        RDFUnitUtils.fillSchemaServiceFromLOV();
        SchemaService.addSchemaDecl("dbpedia-owl", "http://dbpedia.org/ontology/", "http://mappings.dbpedia.org/server/ontology/dbpedia.owl");


        configuration = new RDFUnitConfiguration(datasetURI, dataFolder);

        // Set the source
       // try {
        File file = new File(rdfDataFile);
        log.error("file.getAbsolutePath() " + file.getAbsolutePath());
        configuration.setCustomDereferenceURI(file.getAbsolutePath());
            //configuration.setCustomTextSource(rdfData, serializationFormat);
       // } catch (UndefinedSerializationException e) {
       //     throw new IllegalArgumentException("Unsupported format"); // should never be thrown
       // }

        configuration.setTestCaseExecutionType(testCaseExecutionType);

        // Identify all namespaces in the data and try to identify them
        configuration.setAutoSchemataFromQEF(configuration.getTestSource().getExecutionFactory(), true);


        // Initialize RDFUnit
        RDFUnit rdfUnit = new RDFUnit();
        try {
            rdfUnit.init();
        } catch (RdfReaderException e) {
            throw new RuntimeException("Cannot initialize RDFUnit");
        }
        // Generate TestSuite for current dataset
        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(
                configuration.isAutoTestsEnabled(),
                configuration.isTestCacheEnabled(),
                configuration.isManualTestsEnabled());
        testSuite = testGeneratorExecutor.generateTestSuite(
                configuration.getTestFolder(),
                configuration.getTestSource(),
                rdfUnit.getAutoGenerators());

    }

    public String validate() {
        final boolean enableRDFUnitLogging = false;
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(enableRDFUnitLogging);
        testExecutorMonitor.setExecutionType(configuration.getTestCaseExecutionType());

        final TestExecutor testExecutor = 
                TestExecutorFactory.createTestExecutor(configuration.getTestCaseExecutionType());
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        final TestSource testSource = configuration.getTestSource();

        testExecutor.execute(testSource, testSuite);

        TestExecution testExecution = testExecutorMonitor.getTestExecution();

        //OutputStream to get the results as string
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            Model model = ModelFactory.createDefaultModel();
            TestExecutionWriter.create(testExecution).write(model);

            new RdfStreamWriter(os, serializationFormat).write(model);
            return os.toString();
        } catch (RdfWriterException e) {
            return null;
        }


    }
}
