package math_encyclopedia.model;

import com.google.common.collect.TreeMultimap;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.util.*;

import static math_encyclopedia.model.Model.makeAbsoluteUri;
import static math_encyclopedia.model.Model.unmakeAbsoluteUri;

class TranslationQuery {
    String language;
    String query;
    TranslationQuery(String language, String query) {
        this.language = language;
        this.query = query;
    }
}

class RussianArticle extends Article {
    RussianArticle(String url) throws Exception {
        super(url);
    }
    RussianArticle(String url, String ontologyId, String title) throws Exception {
        super(url, ontologyId, title);
    }
    static String LanguageCodeStatic() { return "ru"; }
    String LanguageCode() { return LanguageCodeStatic(); }
    static String OntologyClassStatic() { return makeAbsoluteUri("Статья_рус"); }
    String OntologyClass() { return OntologyClassStatic(); }
    List<TranslationQuery> TranslationsQueries() {
        return List.of(
            new TranslationQuery(
            "English",
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?url ?title" +
                    " WHERE {" +
                        "<" + ontologyId + "> <" + makeAbsoluteUri("Имеет_перевод") + "> ?eng_article . " +
                        "?eng_article rdfs:label ?title . " +
                        "?eng_article rdfs:comment ?url " +
                    "}")
        );
    }

    String MscQuery() {
        return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                " SELECT ?msc" +
                " WHERE {" +
                    "<" + ontologyId + "> <" + makeAbsoluteUri("Имеет_перевод") + "> ?articleEn . " +
                    "?articleEn <" + makeAbsoluteUri("Относится_к_разделу") + "> ?msc . " +
                "}";
    }

    TreeSet<URL> ReferencedIn() {
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?title ?url" +
                    " WHERE {" +
                        "?term <" + makeAbsoluteUri("Содержится_в_названии_рус") + "> <" + ontologyId + "> . " +
                        "?annotation <" + makeAbsoluteUri("Ссылается_на_термин") + "> ?term . " +
                        "?annotation <" + makeAbsoluteUri("Содержится_в_тексте_аннотация") + "> ?article . " +
                        "?article rdfs:label ?title . " +
                        "?article rdfs:comment ?url . " +
                    "}"
        );
        TreeSet<URL> ret = new TreeSet<>();
        if (res == null)
            return ret;
        while (res.hasNext()) {
            QuerySolution queryRes = res.next();
            ret.add(new URL(queryRes.getLiteral("url").getString(), queryRes.getLiteral("title").getString()));
        }
        return ret;
    }
}

class EnglishArticle extends Article {
    EnglishArticle(String url) throws Exception {
        super(url);
    }

    EnglishArticle(String url, String ontologyId, String title) throws Exception {
        super(url, ontologyId, title);
    }

    static String LanguageCodeStatic() {
        return "en";
    }

    String LanguageCode() {
        return LanguageCodeStatic();
    }

    static String OntologyClassStatic() {
        return makeAbsoluteUri("Статья_англ");
    }

    String OntologyClass() {
        return OntologyClassStatic();
    }

    List<TranslationQuery> TranslationsQueries() {
        return List.of(
                new TranslationQuery(
                        "Русский",
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                                " SELECT ?url ?title" +
                                " WHERE {" +
                                "?rus_article <" + makeAbsoluteUri("Имеет_перевод") + "> <" + ontologyId + "> . " +
                                "?rus_article rdfs:label ?title . " +
                                "?rus_article rdfs:comment ?url . " +
                                "}")
        );
    }

    String MscQuery() {
        return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                " SELECT ?msc" +
                " WHERE {" +
                "<" + ontologyId + "> <" + makeAbsoluteUri("Относится_к_разделу") + "> ?msc . " +
                "}";
    }

    TreeSet<URL> ReferencedIn() {
        return new TreeSet<>();
    }
}

public abstract class Article implements Translatable, Comparable {
    String url;
    String ontologyId;
    String title;
    String body;
    TreeSet<Translation> translationsWithUrls;
    Set<URL> seeAlso;
    TreeSet<MSC> msc;
    TreeSet<URL> referencedIn;

    abstract String LanguageCode();
    abstract List<TranslationQuery> TranslationsQueries();
    abstract String OntologyClass();

    private TreeSet<Translation> Translations() throws Exception {
        TreeSet<Translation> ret = new TreeSet<>();
        List<TranslationQuery> queries = TranslationsQueries();
        for (TranslationQuery query : queries) {
            ResultSet res = Model.instance().select(query.query);
            if (res == null)
                continue;
            TreeSet<Translation> translations = new TreeSet<>();
            while (res.hasNext()) {
                QuerySolution queryRes = res.next();
                if (!queryRes.contains("url"))
                    continue;
                String title = queryRes.contains("title") ? queryRes.getLiteral("title").getString() : null;
                String lang = (res.hasNext() || !translations.isEmpty()) && title != null ? query.language + " (" + title + ")" : query.language;
                translations.add(new Translation(lang, new URL(queryRes.getLiteral("url").getString(), title)));
            }
            ret.addAll(translations);
        }
        return ret;
    }

    private Set<URL> SeeAlso() {
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?title ?url" +
                    " WHERE {" +
                        "<" + ontologyId + "> rdfs:seeAlso ?seeAlso . " +
                        "?seeAlso rdfs:label ?title . " +
                        "?seeAlso rdfs:comment ?url . " +
                    "}"
        );
        Set<URL> ret = new TreeSet<>();
        if (res == null)
            return ret;
        while (res.hasNext()) {
            QuerySolution queryRes = res.next();
            ret.add(new URL(queryRes.getLiteral("url").getString(), queryRes.getLiteral("title").getString()));
        }
        return ret;
    }

    abstract String MscQuery();

    private TreeSet<MSC> Msc() throws Exception {
        ResultSet res = Model.instance().select(MscQuery());
        TreeSet<MSC> ret = new TreeSet<>();
        if (res == null)
            return ret;
        while (res.hasNext()) {
            QuerySolution queryRes = res.next();
            ret.add(MSC.create(LanguageCode(), unmakeAbsoluteUri(queryRes.getResource("msc").getURI())));
        }
        return ret;
    }

    abstract TreeSet<URL> ReferencedIn();

    private String Body() {
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?body" +
                    " WHERE {" +
                        "<" + ontologyId + "> rdfs:isDefinedBy ?body . " +
                    "}"
        );
        return res.next().getLiteral("body").getString();
    }

    Article(String url, String ontologyId, String title) {
        this.url = url;
        this.ontologyId = ontologyId;
        this.title = title;
    }

    Article(String url) throws Exception {
        this.url = url;
        ResultSet res = Model.instance().select(
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                " SELECT ?article ?title" +
                " WHERE {" +
                    "?article a <" + OntologyClass() + "> . " +
                    "?article rdfs:comment \"" + url + "\" . " +
                    "?article rdfs:label ?title . " +
                    "?article rdfs:isDefinedBy ?body . " +
                "}"
        );
        if (res == null || !res.hasNext())
            throw new Exception("Article with title \"" + title + "\" doesn't exist");
        QuerySolution queryResult = res.next();
        ontologyId = queryResult.getResource("article").getURI();
        title = queryResult.getLiteral("title").getString();
        if (res.hasNext())
            throw new Exception("Found more than one article with title \"" + title + "\"");
    }

    private static String makeUrl(String language, String articleId) {
        return "/" + language + "/" + articleId;
    }

    public static Article create(String language, String articleId) throws Exception {
        switch (language) {
            case "ru": return new RussianArticle(makeUrl(language, articleId));
            case "en": return new EnglishArticle(makeUrl(language, articleId));
            default: throw new Exception("Unknown language");
        }
    }

    public static Map<Character, Collection<Article>> list(String language) throws Exception {
        String ontoClass;
        switch (language) {
            case "ru": ontoClass = RussianArticle.OntologyClassStatic(); break;
            case "en": ontoClass = EnglishArticle.OntologyClassStatic(); break;
            default: throw new Exception("Unknown language");
        }
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?url ?article ?title" +
                    " WHERE {" +
                        "?article a <" + ontoClass + "> . " +
                        "?article rdfs:comment ?url . " +
                        "?article rdfs:label ?title . " +
                    "}");
        TreeMultimap<Character, Article> ret = TreeMultimap.create();
        if (res == null)
            return ret.asMap();
        while (res.hasNext()) {
            QuerySolution article = res.next();
            String url = article.getLiteral("url").getString();
            String ontologyId = article.getResource("article").getURI();
            String title = article.getLiteral("title").getString();
            if (title.isEmpty())
                continue;
            switch (language) {
                case "ru":
                    ret.put(title.charAt(0), new RussianArticle(url, ontologyId, title));
                    break;
                case "en":
                    ret.put(title.charAt(0), new EnglishArticle(url, ontologyId, title));
                    break;
            }
        }
        return ret.asMap();
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        if (body == null)
            body = Body();
        return body;
    }

    public Set<URL> getSeeAlso() {
        if (seeAlso == null)
            seeAlso = SeeAlso();
        return seeAlso;
    }

    public TreeSet<MSC> getMsc() throws Exception {
        if (msc == null)
            msc = Msc();
        return msc;
    }

    public TreeSet<URL> getReferencedIn() throws Exception {
        if (referencedIn == null)
            referencedIn = ReferencedIn();
        return referencedIn;
    }

    public TreeSet<Translation> getTranslations() throws Exception {
        if (translationsWithUrls == null)
            translationsWithUrls = Translations();
        return translationsWithUrls;
    }

    public URL getUrl() {
        return new URL(url, getTitle());
    }

    @Override
    public int compareTo(Object o) {
        Article other = (Article) o;
        int res = 0;
        if (title != null && other.title != null)
            res = title.compareTo(other.title);
        if (res == 0 && url != null && other.url != null)
            res = url.compareTo(other.url);
        return res;
    }
}
