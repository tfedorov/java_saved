package com.tfedorov.social.normalization.stemming;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tfedorov.social.stemming.dao.StemmingDAOImpl;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class StemmingServiceImplIntegrationTest {

	private static DriverManagerDataSource dataSource;
	private static StemmingDAOImpl stemmingDAO = new StemmingDAOImpl();
	private StemmingServiceImpl stemmingServiceImpl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	    dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
	    dataSource.setUsername("dmp");
	    dataSource.setPassword("dmp01");

	    System.out.println("datasource initiated...");
	    stemmingDAO.setDataSource(dataSource);
	}

	@Test
	public void testLoadTopWordsListByStemmed() throws Exception {
		stemmingServiceImpl = new StemmingServiceImpl();
		stemmingServiceImpl.setStemmingDAO(stemmingDAO);
		
		List<String> stemmedWordsList = new ArrayList<String>();
		stemmedWordsList.add("run");
		stemmedWordsList.add("champion");
		stemmedWordsList.add("instead");
		stemmedWordsList.add("tweet");
		
		Map<String, String> result = stemmingServiceImpl.loadTopWordsListByStemmed(stemmedWordsList, "en");
		Assert.assertNotNull(result);
		Assert.assertEquals(stemmedWordsList.size(), result.size());
		Assert.assertEquals("run",result.get("run"));
		Assert.assertEquals("champions",result.get("champion"));
		Assert.assertEquals("instead",result.get("instead"));
		Assert.assertEquals("tweet",result.get("tweet"));
		
	}

}
