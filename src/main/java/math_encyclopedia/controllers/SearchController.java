package math_encyclopedia.controllers;

import math_encyclopedia.model.Model;
import math_encyclopedia.model.SearchResult;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;
import java.util.List;

@Controller
public class SearchController {
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String main(ModelMap model, @RequestParam String query)
    {
        model.addAttribute("query", query);

        if (query.isEmpty())
            return "search";

        ResultSet results = Model.instance().select(
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                        " PREFIX text: <http://jena.apache.org/text#>" +
                        " SELECT ?fragment ?title ?url" +
                        "   WHERE {" +
                        "       (?s ?sc ?fragment) text:query (rdfs:isDefinedBy '" + query + "' \"highlight:s:<em class='highlight'> | e:</em>\") ." +
                        "       ?s rdfs:label ?title ." +
                        "       ?s rdfs:comment ?url ." +
                        "   }"
        );

        List<SearchResult> ret = new LinkedList<>();
        while (results.hasNext()) {
            QuerySolution res = results.next();
            ret.add(new SearchResult(res.get("title").toString(), res.get("url").toString(), res.get("fragment").toString()));
        }
        if (!ret.isEmpty())
            model.addAttribute("searchResults", ret);

        return "search";
    }
}
