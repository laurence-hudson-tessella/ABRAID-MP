package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import java.util.List;

/**
 * Controller for the Atlas Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AtlasController extends AbstractController {

    @Autowired
    public AtlasController() {
    }

    /**
     * Gets all countries in the database and adds them to the model map.
     * Return the view to display.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showPage() {
        return "atlas";
    }
}
