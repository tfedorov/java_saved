package com.tfedorov.social.intention;

import java.util.ArrayList;
import java.util.List;

import com.tfedorov.social.intention.dao.Intention2LevelDaoImpl;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tfedorov.social.intention.dao.IntentLexiconDaoImpl;

public class IntentionsIntegrationTest {

    private static Intention2LevelDaoImpl intention2LevelDaoImpl;
    private static IntentLexiconDaoImpl intentLexiconDao;

    private static DriverManagerDataSource dataSource;
    
    private boolean consoleOut = false;

    @BeforeClass
    public static void setUp() {
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
        dataSource.setUsername("dmp");
        dataSource.setPassword("dmp01");
        intention2LevelDaoImpl = new Intention2LevelDaoImpl();
        intention2LevelDaoImpl.setDataSource(dataSource);

        intentLexiconDao = new IntentLexiconDaoImpl();
        intentLexiconDao.setDataSource(dataSource);

        System.out.println("datasource initiated...");

    }
  
    @Test
    public void testLevel1() {
  
      Intention1LevelServiceImpl service = new Intention1LevelServiceImpl();
      service.setIntentLexiconDaoImpl(intentLexiconDao);
      service.reload();
  
      List<IntentionStringTestUtil> suiteList = new ArrayList<IntentionsIntegrationTest.IntentionStringTestUtil>();
      
      //No intention tweets
      suiteList.add(new IntentionStringTestUtil("Simple tweet without intention", "Simple tweet without intention", false, null));
      suiteList.add(new IntentionStringTestUtil("I @ will change a job", "I @ will change a job http://google.com", false, null));
      
      //table lexicon
      //'1', 'could change', '&3.3 I, &3.3 me, &3.3 my, &3.3 we, &3.3 our'
      suiteList.add(new IntentionStringTestUtil("I could change anotherword anotherword anotherword", "I could change anotherword anotherword anotherword", true, " <span class=\"intent_qualification\"> I </span> <span class=\"intent_term\"> could change </span> anotherword anotherword anotherword"));
      suiteList.add(new IntentionStringTestUtil("anotherword anotherword could change our anotherword", "anotherword anotherword could change our anotherword", true, "anotherword anotherword <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> our </span> anotherword"));
      suiteList.add(new IntentionStringTestUtil("anotherword anotherword could change anotherword our anotherword", "anotherword anotherword could change anotherword our anotherword", true, "anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword <span class=\"intent_qualification\"> our </span> anotherword"));
      suiteList.add(new IntentionStringTestUtil("anotherword anotherword could change anotherword anotherword our anotherword", "anotherword anotherword could change anotherword anotherword our anotherword", true, "anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword anotherword <span class=\"intent_qualification\"> our </span> anotherword"));
      suiteList.add(new IntentionStringTestUtil("anotherword anotherword could change anotherword anotherword anotherword our anotherword", "anotherword anotherword could change anotherword anotherword anotherword our anotherword", true, "anotherword anotherword <span class=\"intent_term\"> could change </span> anotherword anotherword anotherword <span class=\"intent_qualification\"> our </span> anotherword"));
      suiteList.add(new IntentionStringTestUtil("anotherword anotherword could change anotherword anotherword anotherword anotherword our anotherword", "anotherword anotherword could change anotherword anotherword anotherword anotherword our anotherword", false, null ));
      suiteList.add(new IntentionStringTestUtil("could change anotherword our anotherword", "could change our anotherword", true, " <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> our </span> anotherword"));
      
      suiteList.add(new IntentionStringTestUtil("I cou1ld change my car false", "I cou1ld change my car false", false, null));
      suiteList.add(new IntentionStringTestUtil("What could change false", "What could change false http://google.com", false, null));
      
      //table lexicon
      //'2', 'could change', '&3.3 you, &3.3 your'
      suiteList.add(new IntentionStringTestUtil("You could change my car true", "You could change my car true", true, "You <span class=\"intent_term\"> could change </span> <span class=\"intent_qualification\"> my </span> car true"));
      suiteList.add(new IntentionStringTestUtil("Your could change word fatum true", "Your could change word fatum true", true, "Your <span class=\"intent_term\"> could change </span> word fatum true"));
   
      //table lexicon
      //'19', 'if I', ''
      suiteList.add(new IntentionStringTestUtil("if I had a job", "if I had a job  http://google.com", true, " <span class=\"intent_term\"> if I </span> had a job  http://google.com"));
      suiteList.add(new IntentionStringTestUtil("if smb not I had a job", "if smo not I had a job", false, null));
      
      //table lexicon
      //'22', 'looking to', '&3.3 I, &3.3 me, &3.3 my, &3.3 we, &3.3 our'
      suiteList.add(new IntentionStringTestUtil("I looking to addidas you are", "I looking to addidas you are  http://google.com", true, " <span class=\"intent_qualification\"> I </span> <span class=\"intent_term\"> looking to </span> addidas you are  http://google.com"));
      suiteList.add(new IntentionStringTestUtil("I anotherword anotherword anotherword anotherword looking to anotherword anotherword anotherword", "I anotherword anotherword anotherword anotherword looking to anotherword anotherword anotherword", false, null));
      
      //Do checking
      for (IntentionStringTestUtil testSuite : suiteList) {
        IntentString intentString = service.isIntentionString(testSuite.getNormalizedText(), testSuite.getOriginalText());
        sysoutData(testSuite, intentString);
        Assert.assertEquals(testSuite.getIsIntention().booleanValue(), intentString.isIntention());
        Assert.assertEquals(testSuite.getResultHtml(), intentString.getResultHTML());
      }
  
    }

    private void sysoutData(IntentionStringTestUtil testSuite, IntentString intentString) {
      if (consoleOut) {
        System.out.println("Original text: " + testSuite.getOriginalText());
        System.out.println("Clear text: " + testSuite.getNormalizedText());
        System.out.println("Original text: " + testSuite.getOriginalText());
        System.out.println("THIS IS " + (intentString.isIntention() ? "" : "NOT") + " INTENTION FIRST LEVEL!");
      }
    }

    private class IntentionStringTestUtil{
      
      String normalizedText;
      String originalText;
      
      Boolean isIntention;
      String resultHtml;
      
      public IntentionStringTestUtil(String normalizedText, String originalText, Boolean isIntention, String resultHtml) {
        super();
        this.normalizedText = normalizedText;
        this.originalText = originalText;
        this.isIntention = isIntention;
        if(isIntention)
        this.resultHtml = resultHtml;
        else
          this.resultHtml = originalText;
      }

      /**
       * @return the normalizedText
       */
      public String getNormalizedText() {
        return normalizedText;
      }

      /**
       * @return the originalText
       */
      public String getOriginalText() {
        return originalText;
      }

      /**
       * @return the isIntention
       */
      public Boolean getIsIntention() {
        return isIntention;
      }

      /**
       * @return the resultHtml
       */
      public String getResultHtml() {
        return resultHtml;
      }

    
      
    }   
    
    @Test
    public void testLevel2() {

        // Pattern: "best deal*,#dealer*,Price"

        // 1. "I like my Job and I will make best deals with dealer!" - false
        // 2. "I like my Job and I will make best deals with dealing!" - true
        // 3. "I like my Job and I will make best deal with dealing!" - true
        // 4. "I like my Job and I will make best deal!" - true
        // 5. "I like my Job and I will make best deal with dealers!" - false

        String originalText = "I like my Job and I will make best deal with dealers!";

        Intention2LevelServiceImpl service = new Intention2LevelServiceImpl();
        service.setIntention2LevelDaoImpl(intention2LevelDaoImpl);
        service.reload();

        boolean intentString = service.isIntention(originalText);

        Assert.assertFalse(intentString);
        System.out.println("Original text: " + originalText);
        System.out.println("THIS IS " + (intentString ? "" : "NOT") + " INTENTION 2!");

    }

}
