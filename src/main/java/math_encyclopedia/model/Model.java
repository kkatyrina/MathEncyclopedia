package math_encyclopedia.model;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

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
    private org.apache.jena.rdf.model.Model model;

    public static String makeAbsoluteUri(String relativeUri) {
        return baseUri + "#" + relativeUri;
    }

    public static String unmakeAbsoluteUri(String absoluteUri) {
        return absoluteUri.replace(baseUri + "#", "");
    }

    private Model() {
        model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(modelFileName);
        if (in == null) {
            throw new IllegalArgumentException("File \"" + modelFileName + "\" not found");
        }
        model.read(in, null);
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
}
