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

import com.tfedorov.social.intention.dao.IntentLexiconDao;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class IntentionsLevel1Test {
  private long id = 1;
  @Mock
  private IntentLexiconDao daoMock;
  
  @InjectMocks
  private Intention1LevelServiceImpl service = new Intention1LevelServiceImpl();

  @Test
  public void testLevel1() {
     
    ArrayList<IntentLexicon> intentList = new ArrayList<IntentLexicon>();
    intentList.add(createIntentLexicon("could change","&3.3 you", "&3.3 your"));
    intentList.add(createIntentLexicon("give you","&3.3 I", "&3.3 me"));
    intentList.add(createIntentLexicon("I will"));
    intentList.add(createIntentLexicon("need","&3.3 you","&3.3 your"));
    Mockito.when(daoMock.getIntentLexicons()).thenReturn(intentList);
    service.setIntentLexiconDaoImpl(daoMock);
    service.reload();

    //Checkin no match condition
    IntentString result = service.isIntentionString("anotherword", "anotherword http://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals("anotherword http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword could", "anotherword could http://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals( "anotherword could http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword yo1u could change", "anotherword yo1u could change http://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals( "anotherword yo1u could change http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword you could chan1ge", "anotherword you could chan1ge http://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals( "anotherword you could chan1ge http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword could you change", "anotherword could you change http://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals( "anotherword could you change http://google.com",result.getResultHTML());  
    //"&3.3 you" checking
    result = service.isIntentionString("could change you anotherword", "could change you anotherword  http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( " <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> you </span> anotherword  http://google.com",result.getResultHTML());
    
    //2 qualificaton in phrase
    result = service.isIntentionString(" you could change you anotherword", " you could change you anotherword  http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( " <span class=\"intent_qualification\"> you </span> <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> you </span> anotherword  http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword anotherword could change you anotherword", "anotherword anotherword could change you anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword anotherword <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> you </span> anotherword http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword anotherword could change anotherword you anotherword", "anotherword anotherword could change anotherword you anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword <span class=\"intent_qualification\"> you </span> anotherword http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword anotherword could change anotherword anotherword you anotherword", "anotherword anotherword could change anotherword anotherword you anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword anotherword <span class=\"intent_qualification\"> you </span> anotherword http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword anotherword could change anotherword anotherword anotherword you anotherword", "anotherword anotherword could change anotherword anotherword anotherword you anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword anotherword anotherword <span class=\"intent_qualification\"> you </span> anotherword http://google.com",result.getResultHTML());
    /*
    result = service.isIntentionString("anotherword anotherword could change anotherword anotherword another-word you anotherword", "anotherword anotherword could change anotherword anotherword another-word you anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword anotherword<span class=\"intent_term\"> could change </span>anotherword anotherword another-word<span class=\"intent_qualification\"> you </span>anotherword http://google.com",result.getResultHTML());
    */
    //only 3 words after could change and before you
    result = service.isIntentionString("anotherword anotherword could change anotherword anotherword anotherword anotherword you anotherword", "anotherword anotherword could change anotherword anotherword anotherword anotherword you anotherword http://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals( "anotherword anotherword could change anotherword anotherword anotherword anotherword you anotherword http://google.com",result.getResultHTML());
      
    result = service.isIntentionString("you could change", "you could change http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( " <span class=\"intent_qualification\"> you </span> <span class=\"intent_term\"> could change </span> http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("anotherword you anotherword could change anotherword", "anotherword you anotherword could change anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword <span class=\"intent_qualification\"> you </span> anotherword <span class=\"intent_term\"> could change </span> anotherword http://google.com",result.getResultHTML());
 
    result = service.isIntentionString("anotherword you anotherword anotherword could change anotherword", "anotherword you anotherword anotherword could change anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword <span class=\"intent_qualification\"> you </span> anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword http://google.com",result.getResultHTML());
  
    result = service.isIntentionString("anotherword you anotherword anotherword anotherword could change anotherword", "anotherword you anotherword anotherword anotherword could change anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword <span class=\"intent_qualification\"> you </span> anotherword anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword http://google.com",result.getResultHTML());
  
    result = service.isIntentionString("anotherword you anotherword anotherword anotherword anotherword could change anotherword", "anotherword you anotherword anotherword anotherword anotherword could change anotherword http://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals( "anotherword you anotherword anotherword anotherword anotherword could change anotherword http://google.com",result.getResultHTML());
  
    //"&3.3 your" checking
    result = service.isIntentionString("anotherword your anotherword could change anotherword", "anotherword your anotherword could change anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword <span class=\"intent_qualification\"> your </span> anotherword <span class=\"intent_term\"> could change </span> anotherword http://google.com",result.getResultHTML());
 
    result = service.isIntentionString("anotherword your anotherword could change you anotherword", "anotherword your anotherword could change you anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword <span class=\"intent_qualification\"> your </span> anotherword <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> you </span> anotherword http://google.com",result.getResultHTML());
 
    result = service.isIntentionString("anotherword you could change anotherword could change you anotherword", "anotherword you could change anotherword could change you anotherword http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword <span class=\"intent_qualification\"> you </span> <span class=\"intent_term\"> could change </span> anotherword <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> you </span> anotherword http://google.com",result.getResultHTML());
 
    // "I will" check
    result = service.isIntentionString("anotherword i will", "anotherword i will http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "anotherword <span class=\"intent_term\"> I will </span> http://google.com",result.getResultHTML());
    
    result = service.isIntentionString("i anotherword will", "I anotherword willhttp://google.com");
    Assert.assertEquals(false, result.isIntention());
    Assert.assertEquals( "I anotherword willhttp://google.com",result.getResultHTML());
    
    
    //"give you" check
    result = service.isIntentionString("I give you", "I give you http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( " <span class=\"intent_qualification\"> I </span> <span class=\"intent_term\"> give you </span> http://google.com",result.getResultHTML());
 
    //"I will" + "give you"
    result = service.isIntentionString("I will give you", "I will give you http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( " <span class=\"intent_qualification\"> I </span> will <span class=\"intent_term\"> give you </span> http://google.com",result.getResultHTML());
 
    //"I will" + "give you"
    /*
    result = service.isIntentionString("I will anotherword anotherword anotherword me give you", "I will anotherword anotherword anotherword me give you http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( " <span class=\"intent_qualification\"> I </span> will anotherword anotherword anotherword <span class=\"intent_qualification\"> me </span> <span class=\"intent_term\"> give you </span> http://google.com",result.getResultHTML());
 	*/
    
    //"could change" + "give you"
    /*
    result = service.isIntentionString("I could change anotherword anotherword anotherword me give you", "I could change anotherword anotherword anotherword me give you http://google.com");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( " <span class=\"intent_qualification\"> I </span> could change anotherword anotherword anotherword <span class=\"intent_qualification\"> me </span> <span class=\"intent_term\"> give you </span> http://google.com",result.getResultHTML());
	*/
    result = service.isIntentionString("All you need is 69 Cents to Pre Order \"What About Love\" on iTunes!!(: #WHATABOUTLOVE", "All you need is 69 Cents to Pre Order \"What About Love\" on iTunes!!(: #WHATABOUTLOVE http://t.co/ueBnraZcb5 http://t.co/7nG17T7SDq");
    Assert.assertEquals(true, result.isIntention());
    Assert.assertEquals( "All <span class=\"intent_qualification\"> you </span> <span class=\"intent_term\"> need </span> is 69 Cents to Pre Order \"What About Love\" on iTunes!!(: #WHATABOUTLOVE http://t.co/ueBnraZcb5 http://t.co/7nG17T7SDq",result.getResultHTML());
 
     
  }

  @Test
  public void testLevelIntenttion() {
	  
	  //https://datamartllc.atlassian.net/browse/AXS-76
	    ArrayList<IntentLexicon> intentList = new ArrayList<IntentLexicon>();
	    /*intentList.add(createIntentLexicon("could change","&3.3 you", "&3.3 your"));
	    intentList.add(createIntentLexicon("give you","&3.3 I", "&3.3 me"));
	    intentList.add(createIntentLexicon("I will"));
	    intentList.add(createIntentLexicon("need","&3.3 you","&3.3 your"));*/
	    intentList.add(createIntentLexicon("need","&3.3 I","&3.3 me","&3.3 my","&3.3 we","&3.3 our"));
	    Mockito.when(daoMock.getIntentLexicons()).thenReturn(intentList);
	    service.setIntentLexiconDaoImpl(daoMock);
	    service.reload();	  
	    
	  //https://datamartllc.atlassian.net/browse/AXS-76
	    String fullMessageAXS76 = "Some people are funny. They spend money they don't have, to buy things they don't need, to impress people they don't even likes";
	    String normalisedMessageAXS76 = "some people are funny they spend money they don't have to buy things they don't need to impress people they don't even likes ";
	    IntentString result = service.isIntentionString(normalisedMessageAXS76, fullMessageAXS76);
	    Assert.assertEquals(true, result.isIntention());
	    Assert.assertEquals("Some people are funny. They spend money they don't have, to buy things they don't <span class=\"intent_term\"> need </span> , to impress people they don't even likes",result.getResultHTML());
  }
  private IntentLexicon createIntentLexicon(String searchTerm, String... qualificationStrArrr) {
    IntentLexicon changeLexicon = new IntentLexicon();
    changeLexicon.setId(id++);
    changeLexicon.setSearchTerm(searchTerm);
    List<Qualification> qualificationList = new ArrayList<Qualification>();
    for (String qualificationStr : qualificationStrArrr) {
      qualificationList.add(new Qualification(qualificationStr));
    }

    changeLexicon.setQualifications(qualificationList);
    return changeLexicon;
  }
}
