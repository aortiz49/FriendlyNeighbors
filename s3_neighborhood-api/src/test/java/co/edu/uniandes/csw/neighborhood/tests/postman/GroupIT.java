/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.neighborhood.tests.postman;

import co.edu.uniandes.csw.neighborhood.dtos.ResidentProfileDTO;
import co.edu.uniandes.csw.neighborhood.mappers.BusinessLogicExceptionMapper;
import co.edu.uniandes.csw.neighborhood.resources.ResidentProfileResource;
import co.edu.uniandes.csw.postman.tests.PostmanTestBuilder;
import java.io.File;
import java.io.IOException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 * @author albayona
 */
@RunWith(Arquillian.class)
public class GroupIT {
    
    private static final String COLLECTION = "Group-Tests.postman_collection";

    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "s3_neighborhood-api.war")//War del modulo api
                // Dependencies are added
                .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .importRuntimeDependencies().resolve()
                        .withTransitivity().asFile())
                //Compiled packages from service added
                .addPackage(ResidentProfileResource.class.getPackage()) //Only looks at the packages
                .addPackage(ResidentProfileDTO.class.getPackage()) //Only looks at the packages
                .addPackage(BusinessLogicExceptionMapper.class.getPackage())
                // Data base configuration added
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                // Beans configuraration for dependencies injection 
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                // web.xml for servlets deployment
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/glassfish-resources.xml"));
    }

    @Test
    @RunAsClient
    public void postman() throws IOException {
        PostmanTestBuilder tp = new PostmanTestBuilder();
        try {
            tp.setTestWithoutLogin(COLLECTION, "Entorno-IT.postman_environment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        String desiredResult = "0";
        Assert.assertEquals("Requests error from: " + COLLECTION, desiredResult, tp.getRequests_failed());
        Assert.assertEquals("Assertions error from: " + COLLECTION, desiredResult, tp.getAssertions_failed());
        System.out.println(tp.toString());

    }
    
}
