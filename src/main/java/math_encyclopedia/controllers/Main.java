package math_encyclopedia.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.jena.query.*;

@Controller
public class Main {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String main(ModelMap model)
    {
        return "main";
    }

}
