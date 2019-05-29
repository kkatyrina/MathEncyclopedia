package math_encyclopedia.controllers;

import math_encyclopedia.model.MSC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
class MscListController {
    @RequestMapping(value = "/msc/{language}")
    public String main(@PathVariable String language, ModelMap model) {
        try {
            MSC root = MSC.create(language);
            model.addAttribute("root", root);
            model.addAttribute("translations", root.getTranslations());
            return "mscList";
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
            return "pageNotFound";
        }
    }
}


@Controller
class MscController {
    @RequestMapping(value = "/msc/{mscId}/{language}")
    public String main(@PathVariable String mscId, @PathVariable String language, ModelMap model) {
        try {
            MSC msc = MSC.create(language, mscId);
            model.addAttribute("msc", msc);
            model.addAttribute("translations", msc.getTranslations());
            return "msc";
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
            return "pageNotFound";
        }
    }
}