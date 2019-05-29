package math_encyclopedia.controllers;

import math_encyclopedia.model.Translation;
import math_encyclopedia.model.URL;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import math_encyclopedia.model.Article;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

@Controller
class ArticleController {
    @RequestMapping(value = "/{language}/{articleId}")
    public String main(@PathVariable String language, @PathVariable String articleId, ModelMap model) {
        try {
            Article article = Article.create(language, articleId);
            model.addAttribute("article", article);
            model.addAttribute("translations", article.getTranslations());
            return "article";
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
            return "pageNotFound";
        }
    }
}

@Controller
class ArticleListController {
    @RequestMapping(value = "/articles/{language}")
    public String main(@PathVariable String language, ModelMap model) {
        try {
            model.addAttribute("title", "Алфавитный указатель");
            model.addAttribute("items", Article.list(language));
            TreeSet<Translation> translations = new TreeSet<>();
            if (!language.equals("ru"))
                translations.add(new Translation("Русский", new URL("/articles/ru")));
            if (!language.equals("en"))
                translations.add(new Translation("English", new URL("/articles/en")));
            model.addAttribute("translations", translations);
            return "alphabetical";
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
            return "pageNotFound";
        }
    }
}

