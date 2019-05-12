package math_encyclopedia.model;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeJava;

class RussianArticle extends Article {
    RussianArticle(String articleId) throws Exception {
        super(articleId);
    }
    String Language() { return "ru"; }
    String OntologyClass() { return Model.makeAbsoluteUri("Статья_рус"); }
    Map<String, String> Translations() throws Exception {
        ResultSet res = Model.instance().select(
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                " SELECT ?url" +
                " WHERE {" +
                    "<" + ontologyId + "> <" + Model.makeAbsoluteUri("Имеет_перевод") + "> ?eng_article . " +
                    "?eng_article rdfs:comment ?url " +
                "}"
        );

        QuerySolution queryRes;
        if (res != null && res.hasNext()) {
            queryRes = res.next();
            if (res.hasNext())
                throw new Exception("Found more than one translation for article with name \"" + name + "\"");
            if (queryRes.contains("url"))
                return Map.of("English", queryRes.getLiteral("url").getString());
        }
        return Map.of();
    }
}

class EnglishArticle extends Article {
    EnglishArticle(String articleId) throws Exception {
        super(articleId);
    }
    String Language() { return "en"; }
    String OntologyClass() { return Model.makeAbsoluteUri("Статья_англ"); }
    Map<String, String> Translations() throws Exception {
        ResultSet res = Model.instance().select(
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                " SELECT ?url" +
                " WHERE {" +
                    "?rus_article <" + Model.makeAbsoluteUri("Имеет_перевод") + "> <" + ontologyId + "> . " +
                    "?rus_article rdfs:comment ?url " +
                "}"
        );

        QuerySolution queryRes;
        if (res != null && res.hasNext()) {
            queryRes = res.next();
            if (res.hasNext())
                throw new Exception("Found more than one translation for article with name \"" + name + "\"");
            if (queryRes.contains("url"))
                return Map.of("Русский", queryRes.getLiteral("url").getString());
        }
        return Map.of();
    }
}

public abstract class Article {
    String articleId;
    String ontologyId;
    String name;
    String body;
    Map<String, String> translationsWithUrls;

    abstract String Language();
    abstract String OntologyClass();
    private String URL() { return "/" + Language() + "/" + articleId; }
    abstract Map<String, String> Translations() throws Exception;

    Article(String articleId) throws Exception {
        this.articleId = articleId;
        ResultSet res = Model.instance().select(
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                " SELECT ?article ?name ?body" +
                " WHERE {" +
                    "?article a <" + OntologyClass() + "> . " +
                    "?article rdfs:comment \"" + URL() + "\" . " +
                    "?article rdfs:label ?name . " +
                    "?article rdfs:isDefinedBy ?body " +
                "}"
        );
        if (res == null || !res.hasNext())
            throw new Exception("Article with name \"" + name + "\" doesn't exist");
        QuerySolution queryResult = res.next();
        ontologyId = queryResult.getResource("article").getURI();
        name = queryResult.getLiteral("name").getString();
        body = unescapeJava(queryResult.getLiteral("body").getString());
        if (res.hasNext())
            throw new Exception("Found more than one article with name \"" + name + "\"");
        translationsWithUrls = Translations();
    }

    public static Article create(String language, String articleId) throws Exception {
        switch (language) {
            case "ru": return new RussianArticle(articleId);
            case "en": return new EnglishArticle(articleId);
            default: throw new Exception("Unknown language");
        }
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getTranslations() {
        return translationsWithUrls;
    }
}
