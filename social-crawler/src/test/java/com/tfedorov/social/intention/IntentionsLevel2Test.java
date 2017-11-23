/**
 * 
 */
package com.tfedorov.social.intention;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tfedorov.social.intention.dao.Intention2LevelDao;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class IntentionsLevel2Test {
	
	@Mock
	private Intention2LevelDao daoMock;

	@InjectMocks
	private Intention2LevelServiceImpl service = new Intention2LevelServiceImpl();

	private static long idPurchase = 1l;
	@Test
	public void testLevel2() {

		List<Purchase> intentList = new ArrayList<Purchase>();
		
		intentList.add(createPurchase("Coupon","coupon*"));
		intentList.add(createPurchase("Sale","roll back"));
		intentList.add(createPurchase("Sale","sale*"));
		intentList.add(createPurchase("Price","good deal*","#dealer*"));
		
		Mockito.when(daoMock.getLevel2Lexicons()).thenReturn(intentList);
		service.setIntention2LevelDaoImpl(daoMock);
		service.reload();
		
		//coupon test
		boolean intentString = service.isIntention("Give me one coupon");
		Assert.assertTrue(intentString);
		intentString = service.isIntention("Give me one couponomania ");
		Assert.assertTrue(intentString);
		intentString = service.isIntention("Upper Case cOupOn ");
		Assert.assertTrue(intentString);
		
		//false test
		intentString = service.isIntention("There are no word");
		Assert.assertFalse(intentString);
		
		//roll back test
		intentString = service.isIntention("Jeans roll back ");
		Assert.assertTrue(intentString);
		intentString = service.isIntention("I wnt roll suchi in back rest  ");
		Assert.assertFalse(intentString);
		
		//sale test
		intentString = service.isIntention("You salemother f. ");
		Assert.assertTrue(intentString);
		
		//good deal test
		intentString = service.isIntention("in borshc good deal ");
		Assert.assertTrue(intentString);	
		intentString = service.isIntention("in borshc good dealer ");
		Assert.assertFalse(intentString);	
	}
	
	private Purchase createPurchase(String category, String primTest) {
		return createPurchase(category,primTest,"");
	}
	
	private Purchase createPurchase(String category, String primTest, String secondTest) {
		Purchase purchase = new Purchase();
		purchase.setId(idPurchase++);
		purchase.setCategoryLevel1(category);
		purchase.setPrimaryTest(primTest);
		purchase.setSecondaryTest(secondTest);
		return purchase;
	}
}
