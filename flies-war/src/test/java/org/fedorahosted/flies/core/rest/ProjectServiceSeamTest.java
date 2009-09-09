package org.fedorahosted.flies.core.rest;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.fedorahosted.flies.rest.MediaTypes;
import org.fedorahosted.flies.rest.client.ProjectResource;
import org.fedorahosted.flies.rest.dto.Project;
import org.fedorahosted.flies.rest.dto.ProjectRef;
import org.fedorahosted.flies.rest.dto.ProjectRefs;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.ResourceRequestEnvironment;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

@Test(groups={"seam-tests"})
public class ProjectServiceSeamTest extends SeamTest {

	ResourceRequestEnvironment sharedEnvironment;
	ClientRequestFactory clientRequestFactory;
	ProjectResource projectService;
	
	@BeforeClass
	public void prepareSharedEnvironment() throws Exception {
		sharedEnvironment = new ResourceRequestEnvironment(this) {
			@Override
			public Map<String, Object> getDefaultHeaders() {
				return new HashMap<String, Object>() {
					{
						put("X-Auth-Token", "bob");
					}
				};
			}
		};
		
		ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
		RegisterBuiltin.register(instance);

		clientRequestFactory = 
			new ClientRequestFactory(
					new SeamMockClientExecutor(this), new URI("/restv1/"));

		projectService = clientRequestFactory.createProxy(ProjectResource.class);
	
	}

	public void retrieveListOfProjectsAsJson() throws Exception {
		new ResourceRequest(sharedEnvironment, Method.GET, "/restv1/projects") {
			@Override
			protected void prepareRequest(EnhancedMockHttpServletRequest request) {
				request.addHeader("Accept", MediaType.APPLICATION_JSON);
			}

			@Override
			protected void onResponse(EnhancedMockHttpServletResponse response) {
				assertThat( response.getStatus(), is(200));
			}
		};
	}
	public void retrieveListOfProjectsAsXml() throws Exception {
		new ResourceRequest(sharedEnvironment, Method.GET, "/restv1/projects") {
			@Override
			protected void prepareRequest(EnhancedMockHttpServletRequest request) {
				request.addHeader("Accept", MediaTypes.APPLICATION_FLIES_PROJECTS_XML);
			}

			@Override
			protected void onResponse(EnhancedMockHttpServletResponse response) {
				assertThat( response.getStatus(), is(200));
				
				// check that the content we get back is XML
				byte [] xmlVal = response.getContentAsByteArray();
				JAXBContext context;
				ProjectRefs projectRefs = null;
				try {
					context = JAXBContext.newInstance(ProjectRefs.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					projectRefs = (ProjectRefs) unmarshaller.unmarshal(new ByteArrayInputStream(xmlVal));
					
				} catch (JAXBException e) {
					fail("Failed to initialize Jaxb", e);
				}
				
				assertThat( projectRefs, notNullValue() );
				
				assertThat( projectRefs.getProjects().size(), is(1) );
				
				ProjectRef projectRef = projectRefs.getProjects().get(0);
				assertThat( projectRef.getName(), is("Sample Project"));
			}

		}.run();
	}
	
	public void retrieveProjectRefsUsingClientFramework() throws Exception{

		ClientResponse<ProjectRefs> response = projectService.getProjects();
		
		assertThat( response.getStatus(), is(200) );
		assertThat( response.getEntity(), notNullValue() );
		assertThat( response.getEntity().getProjects().size(), is(1) );

	}
	
	public void retrieveNonExistingProject(){

		ClientResponse<Project> response = projectService.getProject("invalid-project");
		assertThat( response.getStatus(), is(404) );
	}

	public void retrieveExistingProject(){

		ClientResponse<Project> response = projectService.getProject("sample-project");
		assertThat( response.getStatus(), lessThan(400) );
	}
	
}