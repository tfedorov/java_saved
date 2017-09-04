package com.game.service;


import junit.framework.Assert;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.game.dao.GameDao;
import com.game.exception.GameDataAccesException;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

public class PartyServiceIntegrationTest {
	
	private static final String TEST_USER = "testUser";

	private GameDao dao;
	
	private PartyThreadBalancerService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		/*
		dao = new GameDao(new Morphia(),new Mongo(), "cheatTest");
		
		dao.clearAll();
		service = new PartyService();
		service.setDao(dao);
		*/
	}

	@Test
	public void testRegisterCandidat() throws GameDataAccesException {
/*
		String registerCandidat1 = service.registerCandidat(TEST_USER);
		String registerCandidat2 = service.registerCandidat(TEST_USER);
		
		Assert.assertEquals(registerCandidat1, registerCandidat2);
		
		ObjectId id = dao.getNotCompletedGameWithUser(TEST_USER).getId();
		System.out.println(dao.get(id));
		
		
		System.out.println(dao.getGameJson(id.toString()));
		//System.out.println(registerCandidat);
*/

	}
	
	@After
	public void after() throws Exception {
		//dao.clearAll();
	}	

}
