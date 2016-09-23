/*
 * Copyright Curtin University, 2015.
 */
package au.edu.curtin.lims.controller;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import au.edu.curtin.lims.db.ITestRepository;
import au.edu.curtin.lims.domain.Test;
import au.edu.curtin.lims.form.TestForm;
import au.edu.curtin.lims.viewmodel.TestViewModel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Test")
public class TestController extends BaseController {
    
    @Inject
    private ITestRepository testRepository;
    
    //@Inject
    //private IInstrumentRepository instrumentRepository;

    //private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentController.class);

    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    public ModelAndView index(
            Locale locale) {
        ModelAndView modelAndView = this.getModelAndView("index", locale);
        List<Test> testnames = testRepository.findAll();
        modelAndView.addObject("testnames", TestViewModel.createList(testnames));    
        return modelAndView;
    }
    
    @RequestMapping(value = "/Create", method = RequestMethod.GET)
    public ModelAndView create(
            Locale locale) {
        ModelAndView modelAndView = this.getModelAndView("create", locale);
        modelAndView.addObject("testForm", new TestForm());
        return modelAndView;
    }
    
    @RequestMapping(value = "/Create", method = RequestMethod.POST)
    public String create(
            TestForm testForm,
            Model model) {
        Test test = new Test();
        
        // TODO: handle case where username is already taken
        test.setTestname(testForm.getTestname());
        //person.setPassword(new BCryptPasswordEncoder().encode(personForm.getPassword()));
        this.testRepository.save(test);
        return "redirect:/Test"; 
        // TODO: add person.getId() here 
    }
    

}
