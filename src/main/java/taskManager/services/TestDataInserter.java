package taskManager.services;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.servlet.ServletException;

import taskManager.utils.DatabaseUtils;

@Singleton
@Startup
public class TestDataInserter {
    
    @EJB
    private DatabaseUtils utils;
    
    public TestDataInserter() {
    }
    
    @PostConstruct
    public void init() throws ServletException {
        utils.addTestDataToDB();
    }
}
