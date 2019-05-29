package math_encyclopedia.model;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static math_encyclopedia.model.Model.makeAbsoluteUri;
import static math_encyclopedia.model.Model.unmakeAbsoluteUri;

class RussianMSC extends MSC {
    RussianMSC(String id) throws Exception {
        super(id);
    }

    @Override
    String language() {
        return "ru";
    }

    TreeSet<URL> FillArticles() throws Exception {
        TreeSet<URL> ret = new TreeSet<>();
        ResultSet res = Model.instance().select(
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                        " SELECT ?title ?url" +
                        " WHERE {" +
                        "?articleEn <" + makeAbsoluteUri("Относится_к_разделу") + "> <" + makeAbsoluteUri(id) + "> . " +
                        "?articleRu <" + makeAbsoluteUri("Имеет_перевод") + "> ?articleEn . " +
                        "?articleRu rdfs:label ?title . " +
                        "?articleRu rdfs:comment ?url . " +
                        "}");
        while (res.hasNext()) {
            QuerySolution article = res.next();
            ret.add(new URL(article.getLiteral("url").toString(), article.getLiteral("title").toString()));
        }
        return ret;
    }
}

class EnglishMSC extends MSC {
    EnglishMSC(String id) throws Exception {
        super(id);
    }

    @Override
    String language() {
        return "en";
    }

    TreeSet<URL> FillArticles() throws Exception {
        TreeSet<URL> ret = new TreeSet<>();
        ResultSet res = Model.instance().select(
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                        " SELECT ?title ?url" +
                        " WHERE {" +
                        "?article <" + makeAbsoluteUri("Относится_к_разделу") + "> <" + makeAbsoluteUri(id) + "> . " +
                        "?article rdfs:label ?title . " +
                        "?article rdfs:comment ?url . " +
                        "}");
        while (res.hasNext()) {
            QuerySolution article = res.next();
            ret.add(new URL(article.getLiteral("url").toString(), article.getLiteral("title").toString()));
        }
        return ret;
    }
}

public abstract class MSC implements Comparable, Translatable {
    String id;
    private String title;
    private TreeSet<MSC> children;
    private TreeSet<MSC> related;
    private TreeSet<URL> articles;

    static String OntologyClassStatic() { return makeAbsoluteUri("Раздел_MSC"); }

    abstract String language();

    private TreeSet<MSC> FillChildren() throws Exception {
        TreeSet<MSC> ret = new TreeSet<>();
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?child" +
                    " WHERE {" +
                        "?child <" + makeAbsoluteUri("Подраздел_MSC") + "> <" + makeAbsoluteUri(id) + "> . " +
                    "}");
        while (res.hasNext()) {
            QuerySolution child = res.next();
            ret.add(MSC.create(language(), unmakeAbsoluteUri(child.getResource("child").toString())));
        }
        return ret;
    }

    private TreeSet<MSC> FillRelated() throws Exception {
        TreeSet<MSC> ret = new TreeSet<>();
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?related" +
                    " WHERE {" +
                        "?related <" + makeAbsoluteUri("Связан_с_разделом_MSC_MSC") + "> <" + makeAbsoluteUri(id) + "> . " +
                    "}");
        while (res.hasNext()) {
            QuerySolution related = res.next();
            ret.add(MSC.create(language(), unmakeAbsoluteUri(related.getResource("related").toString())));
        }
        return ret;
    }

    abstract TreeSet<URL> FillArticles() throws Exception;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TreeSet<MSC> getChildren() throws Exception {
        if (children == null)
            children = FillChildren();
        return children;
    }

    public TreeSet<MSC> getRelated() throws Exception {
        if (related == null)
            related = FillRelated();
        return related;
    }

    public TreeSet<URL> getArticles() throws Exception {
        if (articles == null)
            articles = FillArticles();
        return articles;
    }

    private URL getUrl(String language) {
        return new URL("/msc/" + getId() + "/" + language, getTitle());
    }

    public URL getUrl() {
        return getUrl(language());
    }

    public MSC(String id) throws Exception {
        this.id = id;
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?title" +
                    " WHERE {" +
                        "<" + makeAbsoluteUri(id) + "> rdfs:comment ?title . " +
                    "}");
        if (res == null || !res.hasNext())
            throw new Exception("Unknown MSC category: " + id);
        title = res.next().getLiteral("title").getString();
    }

    @Override
    public int compareTo(Object o) {
        MSC other = (MSC) o;
        return title.compareTo(other.title);
    }

    static private Map<String, MSC> cache = new TreeMap<>();
    static public MSC create(String language) throws Exception {
        if (!cache.containsKey(language))
            cache.put(language, create(language, "MSC2000"));
        return cache.get(language);
    }

    static public MSC create(String language, String id) throws Exception {
        switch (language) {
            case "en": return new EnglishMSC(id);
            case "ru": return new RussianMSC(id);
            default: throw new Exception("Unknown language: " + language);
        }
    }

    @Override
    public TreeSet<Translation> getTranslations() throws Exception {
        TreeSet<Translation> ret = new TreeSet<>();
        if (getArticles().isEmpty())
            return ret;
        if (!(this instanceof RussianMSC))
            ret.add(new Translation("Русский", getUrl("ru")));
        if (!(this instanceof EnglishMSC))
            ret.add(new Translation("English", getUrl("en")));
        return ret;
    }
}
