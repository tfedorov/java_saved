package com.tfedorov.social.stemming.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class StemmingDAOImplIntegrationTest {
	private static DriverManagerDataSource dataSource;

	private StemmingDAOImpl stemmingDAOImpl = new StemmingDAOImpl();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	    dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
	    dataSource.setUsername("dmp");
	    dataSource.setPassword("dmp01");

	    System.out.println("datasource initiated...");
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLoadMapByTopStemmedList() throws Exception {
		stemmingDAOImpl.setDataSource(dataSource);
		List<String> stemmedWordsList = new ArrayList<String>();
		stemmedWordsList.add("run");
		stemmedWordsList.add("millionair");
		stemmedWordsList.add("instead");
		stemmedWordsList.add("tweet");
		
		List<StemmingBean> list = stemmingDAOImpl.loadMapByTopStemmedList(stemmedWordsList, "en");
		System.out.println(list.get(0));
		
	}

}
