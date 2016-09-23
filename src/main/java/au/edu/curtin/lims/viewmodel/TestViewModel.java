/*
 * Copyright Curtin University, 2015.
 */
package au.edu.curtin.lims.viewmodel;

import java.util.ArrayList;
import java.util.List;

import au.edu.curtin.lims.domain.Test;

public class TestViewModel extends BaseViewModel {
    public String testname;
        
    public List<String> testnames = new ArrayList<String>();
    
    public TestViewModel(Test test) {
        super(test.getTestId());
        
        this.testname = test.getTestname();        
    }
    
    
    public static List<TestViewModel> createList(List<Test> testnames) {
        List<TestViewModel> testViewModels = new ArrayList<TestViewModel>(); 
        
        for (Test test : testnames) {
            testViewModels.add(new TestViewModel(test));
        }
        
        return testViewModels;
    } 
}
