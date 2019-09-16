package math_encyclopedia.model;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.query.*;
import org.apache.jena.query.text.EntityDefinition;
import org.apache.jena.query.text.TextDatasetFactory;
import org.apache.jena.query.text.TextIndexConfig;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.InputStream;
import java.util.logging.Logger;

public class Model {
    private static Model inst = null;
    public static Model instance() {
        if (inst == null)
            inst = new Model();
        return inst;
    }

    private static final String modelFileName = "MathOnt.rdf";
    private static final String baseUri = "http://www.mathEnc.ru";
    private Dataset dataset;
    private org.apache.jena.rdf.model.Model model;

    public static String makeAbsoluteUri(String relativeUri) {
        return baseUri + "#" + relativeUri;
    }

    public static String unmakeAbsoluteUri(String absoluteUri) {
        return absoluteUri.replace(baseUri + "#", "");
    }

    private Model() {
        EntityDefinition entDef = new EntityDefinition("uri", "fictitious", ResourceFactory.createProperty(makeAbsoluteUri("something.not.existing")));
        entDef.set("isDefinedBy", RDFS.isDefinedBy.asNode());
        entDef.set("label", RDFS.label.asNode());
        Directory dir = new RAMDirectory();
        TextIndexConfig conf = new TextIndexConfig(entDef);
        conf.setValueStored(true);
        conf.setAnalyzer(new RussianAnalyzer());
        dataset = TextDatasetFactory.createLucene(DatasetFactory.createGeneral(), dir, conf);

        InputStream in = FileManager.get().open(modelFileName);
        if (in == null) {
            throw new IllegalArgumentException("File \"" + modelFileName + "\" not found");
        }

        dataset.begin(ReadWrite.WRITE);
        try {
            model = dataset.getDefaultModel();
            RDFDataMgr.read(model, in, Lang.RDFXML);
            dataset.commit();
        } finally {
            dataset.end();
        }
    }

    public ResultSet select(String query) {
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            results = ResultSetFactory.copyResults(results) ;
            return results;
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
            return null;
        }
    }

    public org.apache.jena.rdf.model.Model getModel() {
        return model;
    }
}
