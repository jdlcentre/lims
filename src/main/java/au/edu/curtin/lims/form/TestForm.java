/*
 * Copyright Curtin University, 2015.
 */
package au.edu.curtin.lims.form;

public class TestForm {
    private Integer testId;

    private String testname;
       
    public TestForm() { }

    public Integer getTestId() {
        return this.testId;
    }
    
    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTestname() {
        return this.testname;
    }
    
    public void setTestname(String testname) {
        this.testname = testname;
    }
}