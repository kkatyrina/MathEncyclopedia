package math_encyclopedia.model;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.util.*;

import static math_encyclopedia.model.Model.makeAbsoluteUri;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

class RussianArticle extends Article {
    RussianArticle(String articleId) throws Exception {
        super(articleId);
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
}

class EnglishArticle extends Article {
    EnglishArticle(String articleId) throws Exception {
        super(articleId);
    }
    static String LanguageCodeStatic() { return "en"; }
    String LanguageCode() { return LanguageCodeStatic(); }
    static String OntologyClassStatic() { return makeAbsoluteUri("Статья_англ"); }
    String OntologyClass() { return OntologyClassStatic(); }
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

}

public abstract class Article implements Translatable {
    String articleId;
    String ontologyId;
    String name;
    String body;
    TreeSet<Translation> translationsWithUrls;
    Set<URL> seeAlso;
    Set<URL> msc;

    abstract String LanguageCode();
    abstract List<TranslationQuery> TranslationsQueries();
    abstract String OntologyClass();
    private String URL() { return "/" + LanguageCode() + "/" + articleId; }

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

    Set<URL> SeeAlso() {
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

    Set<URL> Msc() {
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?msc ?title" +
                    " WHERE {" +
                        "<" + ontologyId + "> " + makeAbsoluteUri("Относится_к_разделу") + "> ?msc . " +
                        "?msc rdfs:comment ?title . " +
                    "}"
        );
        Set<URL> ret = new TreeSet<>();
        while (res.hasNext()) {
            QuerySolution queryRes = res.next();
            ret.add(new URL(queryRes.getLiteral("url").getString(), queryRes.getLiteral("title").getString()));
        }
        return ret;
    }

    Article(String articleId) throws Exception {
        this.articleId = articleId;
        ResultSet res = Model.instance().select(
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                " SELECT ?article ?name ?body" +
                " WHERE {" +
                    "?article a <" + OntologyClass() + "> . " +
                    "?article rdfs:comment \"" + URL() + "\" . " +
                    "?article rdfs:label ?name . " +
                    "?article rdfs:isDefinedBy ?body . " +
                "}"
        );
        if (res == null || !res.hasNext())
            throw new Exception("Article with name \"" + name + "\" doesn't exist");
        QuerySolution queryResult = res.next();
        ontologyId = queryResult.getResource("article").getURI();
        name = queryResult.getLiteral("name").getString();
        body = unescapeHtml4(queryResult.getLiteral("body").getString());
        if (res.hasNext())
            throw new Exception("Found more than one article with name \"" + name + "\"");
        translationsWithUrls = Translations();
        seeAlso = SeeAlso();
    }

    public static Article create(String language, String articleId) throws Exception {
        switch (language) {
            case "ru": return new RussianArticle(articleId);
            case "en": return new EnglishArticle(articleId);
            default: throw new Exception("Unknown language");
        }
    }

    public static Map<Character, Collection<URL>> list(String language) throws Exception {
        String ontoClass;
        switch (language) {
            case "ru": ontoClass = RussianArticle.OntologyClassStatic(); break;
            case "en": ontoClass = EnglishArticle.OntologyClassStatic(); break;
            default: throw new Exception("Unknown language");
        }
        ResultSet res = Model.instance().select(
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    " SELECT ?url ?title" +
                    " WHERE {" +
                        "?article a <" + ontoClass + "> . " +
                        "?article rdfs:comment ?url . " +
                        "?article rdfs:label ?title . " +
                    "}");
        TreeMultimap<Character, URL> ret = TreeMultimap.create();
        if (res == null)
            return Map.of();
        while (res.hasNext()) {
            QuerySolution article = res.next();
            if (!article.contains("title") || !article.contains("url"))
                continue;
            String title = article.getLiteral("title").toString();
            if (title.isEmpty())
                continue;
            ret.put(title.charAt(0), new URL(article.getLiteral("url").toString(), title));
        }
        return ret.asMap();
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public Set<URL> getSeeAlso() {
        return seeAlso;
    }

    public TreeSet<Translation> getTranslations() {
        return translationsWithUrls;
    }
}
