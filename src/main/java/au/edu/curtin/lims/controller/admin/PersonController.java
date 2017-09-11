/*
 * Copyright Curtin University, 2015.
 */
package au.edu.curtin.lims.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import au.edu.curtin.lims.controller.BaseController;
import au.edu.curtin.lims.db.IAuthorityRepository;
import au.edu.curtin.lims.db.IPersonRepository;
import au.edu.curtin.lims.domain.Authority;
import au.edu.curtin.lims.domain.Person;
import au.edu.curtin.lims.form.PersonForm;
import au.edu.curtin.lims.viewmodel.PersonDetailViewModel;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Admin/Person")
@Secured("ROLE_ADMIN")
public class PersonController extends BaseController {
    @Inject
    private IAuthorityRepository authorityRepository;
    
    @Inject
    private IPersonRepository personRepository;
    
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    public ModelAndView index(
            Locale locale) {
        ModelAndView modelAndView = this.getModelAndView("index", locale);
        List<Person> people = personRepository.findAll();
        modelAndView.addObject("people", PersonDetailViewModel.createList(people));
        
        List<String> authorities = new ArrayList<String>();
        
        for (Authority authority : authorityRepository.findAll()) {
            authorities.add(authority.getAuthorityName());
        }
        
        modelAndView.addObject("authorities", authorities);
        return modelAndView;
    }
    
    @RequestMapping(value = "/{personId:[0-9]+}", method = RequestMethod.GET)
    public ModelAndView create(
            @PathVariable("personId") final int personId,
            Locale locale) {
        ModelAndView model = this.getModelAndView("detail", locale);
        Person person = personRepository.findById(personId);
        model.addObject("person", new PersonDetailViewModel(person));
        return model;
    }
    
    @RequestMapping(value = "/Create", method = RequestMethod.GET)
    public ModelAndView create(
            Locale locale) {
        ModelAndView modelAndView = this.getModelAndView("create", locale);
        modelAndView.addObject("personForm", new PersonForm());
        return modelAndView;
    }
    
    @RequestMapping(value = "/Create", method = RequestMethod.POST)
    public String create(
            PersonForm personForm,
            Model model) {
        Person person = new Person();
        
        // TODO: handle case where username is already taken
        person.setUsername(personForm.getUsername());
        person.setPassword(new BCryptPasswordEncoder().encode(personForm.getPassword()));
        this.personRepository.save(person);
        return "redirect:/Admin/Person/" + person.getPersonId(); 
    }
}
