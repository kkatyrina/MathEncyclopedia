package math_encyclopedia.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import math_encyclopedia.model.Article;

import java.util.logging.Logger;

@Controller
public class ArticleController {
    @RequestMapping(value = "/{language}/{articleId}")
    public String main(@PathVariable String language, @PathVariable String articleId, ModelMap model) {
        try {
            model.addAttribute("article", Article.create(language, articleId));
            return "article";
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
            return "articleNotFound";
        }
    }
}
