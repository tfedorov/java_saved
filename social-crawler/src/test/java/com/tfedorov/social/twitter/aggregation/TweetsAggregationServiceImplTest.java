/**
 *
 */
package com.tfedorov.social.twitter.aggregation;


import com.tfedorov.social.processing.AbstractChainProcessingHandler;
import com.tfedorov.social.processing.AbstractConditionalProcessingHandler;
import com.tfedorov.social.processing.ProcessingHandler;
import com.tfedorov.social.processing.TerminalHandler;
import com.tfedorov.social.processing.UsefulTweetsCounterHandler;
import com.tfedorov.social.topic.processing.TopicTaskExecutionHandler;
import com.tfedorov.social.twitter.processing.GeneralProcessingContext;
import com.tfedorov.social.twitter.processing.concarrency.ConcurrencyProtectionHandler;
import com.tfedorov.social.twitter.processing.filtering.BlackListFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.IndustryFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.LanguageFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.NormalizationFilterHandler;
import com.tfedorov.social.twitter.processing.filtering.TrendFilterHandler;
import com.tfedorov.social.twitter.processing.intention.IntentDetectionHandler;
import com.tfedorov.social.twitter.processing.intention.IntentionProcessingHandler;
import com.tfedorov.social.twitter.processing.mention.MentionsHandler;
import com.tfedorov.social.twitter.processing.retweet.TopRetweetsHandler;
import com.tfedorov.social.twitter.processing.sentiments.SentimentHandler;
import com.tfedorov.social.twitter.processing.terms.TweetTermsHandler;
import com.tfedorov.social.twitter.processing.trace.DumpTweetHandler;
import com.tfedorov.social.twitter.processing.tweet.FindKeywordsHandler;
import com.tfedorov.social.word.Word;
import com.tfedorov.social.word.dao.WordsDao;
import com.tfedorov.social.word.processing.WordsInfo;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import twitter4j.Status;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author tfedorov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TweetsAggregationServiceImplTest {

  private static String[] stopWordsArray = {"a", "about", "above", "across", "after", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "an", "and", "another", "any", "anybody", "anyone", "anything", "anywhere", "are", "area", "areas", "aren't", "around", "as", "ask", "asked", "asking", "asks", "at", "away", "b", "back", "backed", "backing", "backs", "be", "became", "because", "become", "becomes", "been", "before", "began", "behind", "being", "beings", "below", "best", "better", "between", "big", "both", "but", "by", "c", "came", "can", "cannot", "can't", "case", "cases", "certain", "certainly", "clear", "clearly", "come", "could", "couldn't", "d", "did", "didn't", "differ", "different", "differently", "do", "does", "doesn't", "doing", "done", "don't", "down", "downed", "downing", "downs", "during", "e", "each", "early", "either", "end", "ended", "ending", "ends", "enough", "even", "evenly", "ever", "every", "everybody", "everyone", "everything", "everywhere", "f", "face", "faces", "fact", "facts", "far", "felt", "few", "find", "finds", "first", "for", "four", "from", "full", "fully", "further", "furthered", "furthering", "furthers", "g", "gave", "general", "generally", "get", "gets", "give", "given", "gives", "go", "going", "good", "goods", "got", "great", "greater", "greatest", "group", "grouped", "grouping", "groups", "h", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "her", "here", "here's", "hers", "herself", "he's", "high", "higher", "highest", "him", "himself", "his", "how", "however", "how's", "i", "i'd", "if", "i'll", "i'm", "important", "in", "interest", "interested", "interesting", "interests", "into", "is", "isn't", "it", "its", "it's", "itself", "i've", "j", "just", "k", "keep", "keeps", "kind", "knew", "know", "known", "knows", "l", "large", "largely", "last", "later", "latest", "least", "less", "let", "lets", "let's", "like", "likely", "long", "longer", "longest", "m", "made", "make", "making", "man", "many", "may", "me", "member", "members", "men", "might", "more", "most", "mostly", "mr", "mrs", "much", "must", "mustn't", "my", "myself", "n", "necessary", "need", "needed", "needing", "needs", "never", "new", "newer", "newest", "next", "no", "nobody", "non", "noone", "nor", "not", "nothing", "now", "nowhere", "number", "numbers", "o", "of", "off", "often", "old", "older", "oldest", "on", "once", "one", "only", "open", "opened", "opening", "opens", "or", "order", "ordered", "ordering", "orders", "other", "others", "ought", "our", "ours", "ourselves", "out", "over", "own", "p", "part", "parted", "parting", "parts", "per", "perhaps", "place", "places", "point", "pointed", "pointing", "points", "possible", "present", "presented", "presenting", "presents", "problem", "problems", "put", "puts", "q", "quite", "r", "rather", "really", "right", "room", "rooms", "s", "said", "same", "saw", "say", "says", "second", "seconds", "see", "seem", "seemed", "seeming", "seems", "sees", "several", "shall", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "show", "showed", "showing", "shows", "side", "sides", "since", "small", "smaller", "smallest", "so", "some", "somebody", "someone", "something", "somewhere", "state", "states", "still", "such", "sure", "t", "take", "taken", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "therefore", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "thing", "things", "think", "thinks", "this", "those", "though", "thought", "thoughts", "three", "through", "thus", "to", "today", "together", "too", "took", "toward", "turn", "turned", "turning", "turns", "two", "u", "under", "until", "up", "upon", "us", "use", "used", "uses", "v", "very", "w", "want", "wanted", "wanting", "wants", "was", "wasn't", "way", "ways", "we", "we'd", "well", "we'll", "wells", "went", "were", "we're", "weren't", "we've", "what", "what's", "when", "when's", "where", "where's", "whether", "which", "while", "who", "whole", "whom", "who's", "whose", "why", "why's", "will", "with", "within", "without", "won't", "work", "worked", "working", "works", "would", "wouldn't", "x", "y", "year", "years", "yes", "yet", "you", "you'd", "you'll", "young", "younger", "youngest", "your", "you're", "yours", "yourself", "yourselves", "you've", "z", "able", "ain't", "could've", "dear", "else", "how'd", "how'll", "mightn't", "might've", "must've", "neither", "should've", "that'll", "tis", "'tis", "twas", "'twas", "what'd", "when'd", "when'll", "where'd", "where'll", "who'd", "who'll", "why'd", "why'll", "would've", "lol", "a", "a través", "algunos", "aún", "cada", "capaz", "casi", "como", "cómo", "con", "cualquier", "cuando", "de", "debe", "deber", "debería", "decir", "dejar", "demasiado", "desde", "después", "dice", "dijo", "donde", "dónde", "él", "ella", "ellos", "en", "entonces", "entre", "era", "eran", "eres", "es", "eso", "esta", "está", "estamos", "están", "estos", "haber", "había", "habían", "hace", "hacer", "han", "hará", "haría", "hay", "he", "hecho", "hizo", "la", "las", "les", "lo", "los", "más", "me", "mi", "mientras", "mínimo", "ni", "no", "nosotros", "nuestro", "nunca", "o", "obtener", "otro", "para", "podía", "podrá", "podría", "podría haber", "podría haberlo hecho", "por", "Por qué", "por qué", "porque", "porqué", "propio", "puede", "punto", "que", "qué", "querido", "quién", "quiere", "se", "ser", "serías", "si", "sido", "sin embargo", "sino", "sobre", "sólo", "sólo", "son", "soy", "su", "su", "su", "suya", "tal vez no", "también", "tan", "tener", "tengo", "tenía", "tiene", "tiene", "tienes", "todos", "un", "usted", "Vamos a", "Vas", "voluntad", "voy", "y", "yo", "большинство", "будем", "будете", "бы", "былa", "были", "было", "быть", "в", "ваш", "вероятно", "воля", "все", "вы", "где", "для", "должен", "должно", "другой", "его", "ее", "если", "есть", "еще", "затем", "и", "из", "или", "имеет", "их", "к", "каждый", "как", "когда", "когда-либо", "которого", "который", "кто", "любой", "меня", "мне", "мог", "может", "мощь", "моя", "мы", "на", "Надо", "наименее", "нам", "нас", "наши", "не", "некоторые", "нельзя", "нет", "ни", "но", "о", "однако", "он", "она", "они", "от", "по", "после", "потому что", "почему", "почти", "с", "скорее", "слишком", "собственный", "среди", "так", "также", "там", "тебе", "то время как", "только", "У", "хотел", "час", "часто", "чем", "через", "что", "эти", "это", "я"};
  private static String[] stopWordsSetValue = {"need", "я", "he'd", "said", "higher", "ваш", "parts", "быть", "who'll", "с", "until", "у", "over", "began", "she", "podrá", "something", "should've", "right", "opened", "these", "else", "asked", "должен", "once", "tiene", "как", "number", "he", "theirs", "shows", "tienes", "few", "further", "нам", "he's", "нас", "opening", "herself", "somebody", "each", "big", "go", "she's", "когда-либо", "before", "made", "nosotros", "parted", "чем", "interested", "needing", "de", "she'd", "could", "side", "do", "наши", "interesting", "ellos", "man", "sido", "f", "member", "g", "d", "may", "e", "b", "noone", "если", "c", "needs", "must've", "a", "n", "o", "would've", "l", "m", "won't", "j", "podría", "backing", "k", "h", "i", "yes", "w", "к", "v", "u", "new", "и", "debe", "t", "s", "о", "what", "r", "mínimo", "newer", "q", "p", "nothing", "в", "en", "having", "es", "z", "y", "esta", "yet", "x", "here's", "han", "dijo", "anywhere", "least", "также", "you'd", "took", "capaz", "by", "dear", "enough", "long", "same", "hizo", "has", "podía", "who", "backs", "couldn't", "есть", "would", "wanting", "facts", "any", "нет", "everybody", "querido", "how'll", "had", "be", "también", "think", "get", "likely", "far", "much", "and", "i'd", "differently", "better", "often", "hay", "against", "doing", "areas", "seeming", "orders", "i'm", "make", "large", "thing", "room", "does", "shan't", "ser", "vas", "today", "through", "possible", "area", "большинство", "generally", "showing", "men", "all", "members", "las", "keeps", "sides", "más", "debería", "todos", "at", "she'll", "as", "still", "habían", "где", "neither", "therefore", "'twas", "cada", "never", "which", "great", "cuando", "see", "i'll", "am", "anyone", "take", "тебе", "an", "there", "off", "why", "еще", "nobody", "they", "usted", "you've", "no", "of", "given", "собственный", "почему", "asks", "among", "youngest", "anybody", "on", "only", "says", "her", "то время как", "everyone", "скорее", "fully", "that's", "itself", "thoughts", "soy", "tengo", "or", "done", "son", "pointed", "them", "then", "должно", "will", "ought", "small", "upon", "lo", "different", "where'd", "puede", "donde", "thought", "most", "across", "clear", "ella", "él", "через", "furthers", "where's", "rather", "me", "потому что", "quiere", "mi", "aren't", "mr", "how'd", "smallest", "cualquier", "beings", "don't", "it's", "algunos", "tener", "my", "differ", "слишком", "aún", "per", "thinks", "how's", "cómo", "ni", "within", "pointing", "furthered", "where'll", "меня", "you're", "last", "second", "vamos a", "finds", "being", "newest", "him", "since", "where", "every", "a través", "среди", "almost", "more", "his", "we'd", "grouped", "имеет", "when", "someone", "certainly", "younger", "everywhere", "sobre", "asking", "isn't", "such", "tal vez no", "hers", "here", "eres", "presents", "whole", "la", "this", "его", "becomes", "так", "goods", "way", "там", "другой", "from", "están", "demasiado", "smaller", "while", "was", "наименее", "dejar", "ain't", "able", "if", "seemed", "below", "between", "less", "или", "час", "those", "is", "it", "ourselves", "gives", "important", "your", "gets", "into", "problem", "in", "know", "two", "away", "felt", "necessary", "things", "themselves", "also", "lets", "greater", "por", "they'll", "knew", "ours", "its", "obtener", "voluntad", "yourselves", "turning", "showed", "моя", "eran", "although", "eso", "interest", "haría", "después", "year", "along", "points", "place", "alone", "lol", "turn", "все", "going", "nowhere", "ends", "how", "under", "became", "downed", "always", "mientras", "los", "own", "sin embargo", "we", "ways", "decir", "face", "nunca", "give", "i've", "next", "use", "states", "почти", "mrs", "deber", "when's", "numbers", "older", "worked", "best", "mostly", "como", "could've", "estamos", "when'd", "we'll", "later", "back", "come", "us", "un", "young", "cannot", "seem", "para", "works", "up", "downing", "gave", "either", "fact", "presenting", "seconds", "кто", "doesn't", "they'd", "их", "down", "part", "dónde", "keep", "con", "to", "mightn't", "faces", "both", "become", "you'll", "good", "ended", "somewhere", "must", "didn't", "parting", "after", "otro", "that'll", "who's", "нельзя", "su", "ordering", "sees", "taken", "presented", "what's", "sólo", "может", "however", "который", "по", "whose", "so", "who'd", "behind", "hace", "se", "places", "si", "desde", "what'd", "that", "than", "whom", "several", "case", "got", "early", "oldest", "от", "casi", "он", "can", "about", "era", "podría haberlo hecho", "well", "longest", "часто", "каждый", "above", "que", "porque", "four", "но", "too", "estos", "yours", "они", "furthering", "ни", "thus", "не", "она", "надо", "you", "хотел", "на", "general", "needed", "мы", "anything", "tenía", "ordered", "high", "certain", "latest", "our", "very", "out", "when'll", "for", "everything", "propio", "tis", "whether", "однако", "went", "open", "are", "grouping", "quién", "can't", "shouldn't", "está", "yourself", "working", "groups", "rooms", "others", "problems", "we're", "again", "did", "wasn't", "like", "without", "why'd", "non", "shall", "many", "not", "present", "после", "he'll", "nor", "haven't", "now", "backed", "вы", "say", "myself", "saw", "years", "ask", "some", "tan", "why's", "might", "put", "qué", "некоторые", "которого", "por qué", "wanted", "kind", "porqué", "they've", "seems", "había", "бы", "want", "end", "just", "les", "cases", "let", "evenly", "воля", "already", "should", "suya", "wouldn't", "point", "really", "'tis", "mustn't", "когда", "clearly", "serías", "but", "old", "hadn't", "hecho", "show", "used", "together", "though", "been", "hasn't", "were", "turned", "toward", "из", "puts", "there's", "что", "three", "longer", "might've", "будем", "sure", "work", "будете", "we've", "sino", "wants", "himself", "knows", "why'll", "только", "even", "known", "perhaps", "ever", "wells", "other", "hará", "interests", "have", "highest", "вероятно", "hacer", "one", "для", "state", "nuestro", "haber", "turns", "ее", "let's", "because", "another", "dice", "эти", "order", "мне", "full", "during", "мощь", "entre", "making", "they're", "это", "weren't", "find", "voy", "with", "затем", "punto", "greatest", "entonces", "opens", "came", "the", "былa", "ending", "around", "мог", "любой", "quite", "были", "largely", "yo", "podría haber", "twas", "uses", "downs", "group", "their", "было", "first"};
  private static List<String> stopWordsDaoStandartResult;
  private static Set<String> stopWordsStandartRes;
  static{
    stopWordsDaoStandartResult = Arrays.asList(stopWordsArray);
    stopWordsStandartRes = new HashSet<String>(Arrays.asList(stopWordsSetValue));
  }
  private static String[] blackWordsArray = {"blow job", "fuck", "shit"};
  private static String[] blackWordsSetValue = {"blowjob","blow job", "fuck", "shit"};

  private static List<String> blackWordsDaoStandartResult;
  private static Set<String> blackWordsStandartSet;
  static{
    blackWordsDaoStandartResult = Arrays.asList(blackWordsArray);
    blackWordsStandartSet = new HashSet<String>(Arrays.asList(blackWordsSetValue));
  }

  @Mock
  private Status statusMock;

  @Mock
  private WordsDao wordsDao;
  @Mock
  private ProcessingHandler<GeneralProcessingContext> terminalHandlerMock;
  @Mock
  private ProcessingHandler<GeneralProcessingContext> normalizationFilterHandler;

  @InjectMocks
  private TweetsAggregationServiceImpl aggregationService = new TweetsAggregationServiceImpl();


  @Before
  public void setUp() {
   setPrivateMethByReflect("wordsDao", WordsDao.class, wordsDao);

  }

  @Test
  public void testInitWords() {
    Mockito.when(wordsDao.selectWordStrings(Word.WORD_TYPE.stop_words)).thenReturn(stopWordsDaoStandartResult);
    Mockito.when(wordsDao.selectWordStrings(Word.WORD_TYPE.black_words)).thenReturn(blackWordsDaoStandartResult);
    aggregationService.initWords();

    verify(wordsDao).selectWordStrings(Word.WORD_TYPE.stop_words);
    verify(wordsDao).selectWordStrings(Word.WORD_TYPE.black_words);
    verifyNoMoreInteractions(wordsDao);

    WordsInfo wordsInfoField = (WordsInfo)getPrivateField("wordsInfo");
    Assert.assertNotNull(wordsInfoField);
    Assert.assertEquals(stopWordsStandartRes, wordsInfoField.getStopWords());
    Assert.assertEquals(blackWordsStandartSet, wordsInfoField.getBlackWords());
  }

  @Test
  public void testInitBlackWordsModifier() {
    Mockito.when(wordsDao.selectWordStrings(Word.WORD_TYPE.stop_words)).thenReturn(stopWordsDaoStandartResult);

    String[] blackWordsUpperArray = { "Vagina job", "fuck all", "dIck job"};
    Mockito.when(wordsDao.selectWordStrings(Word.WORD_TYPE.black_words)).thenReturn(Arrays.asList(blackWordsUpperArray));
    aggregationService.initWords();

    verify(wordsDao).selectWordStrings(Word.WORD_TYPE.stop_words);
    verify(wordsDao).selectWordStrings(Word.WORD_TYPE.black_words);
    verifyNoMoreInteractions(wordsDao);

    WordsInfo wordsInfoField = (WordsInfo)getPrivateField("wordsInfo");
    Assert.assertNotNull(wordsInfoField);

    Assert.assertTrue(wordsInfoField.getBlackWords().contains("vagina job"));
    Assert.assertTrue(wordsInfoField.getBlackWords().contains("vaginajob"));
    Assert.assertTrue(wordsInfoField.getBlackWords().contains("dick job"));
    Assert.assertTrue(wordsInfoField.getBlackWords().contains("dickjob"));
    Assert.assertTrue(wordsInfoField.getBlackWords().contains("fuck all"));
    Assert.assertTrue(wordsInfoField.getBlackWords().contains("fuckall"));
    Assert.assertEquals(blackWordsUpperArray.length*2, wordsInfoField.getBlackWords().size());

  }
  @Test
  public void testInitWordsCaseSensitive() {
    String[] stopWordsUpperArray = { "aBoUt", "Above", "ACROSS"};
    Mockito.when(wordsDao.selectWordStrings(Word.WORD_TYPE.stop_words)).thenReturn(Arrays.asList(stopWordsUpperArray));

    String[] blackWordsUpperArray = {"neEd", "Я", "SAID"};
    Mockito.when(wordsDao.selectWordStrings(Word.WORD_TYPE.black_words)).thenReturn(Arrays.asList(blackWordsUpperArray));
    aggregationService.initWords();

    verify(wordsDao).selectWordStrings(Word.WORD_TYPE.stop_words);
    verify(wordsDao).selectWordStrings(Word.WORD_TYPE.black_words);
    verifyNoMoreInteractions(wordsDao);

    WordsInfo wordsInfoField = (WordsInfo)getPrivateField("wordsInfo");
    Assert.assertNotNull(wordsInfoField);

    String[] stopWordsUpperExpectedArray = {  "about", "above", "across"};
    Assert.assertEquals( new HashSet<String>(Arrays.asList(stopWordsUpperExpectedArray)), wordsInfoField.getStopWords());
    Assert.assertEquals( stopWordsUpperArray.length, wordsInfoField.getStopWords().size());

    String[] blackWordsUpperExpectedArray = {"need", "я", "said"};
    Assert.assertEquals(
      new HashSet<String>(Arrays.asList(blackWordsUpperExpectedArray)), wordsInfoField.getBlackWords());
  }

  private void setPrivateMethByReflect(String methodName, Class methodClass, Object classValue) {
    final Field fieldForChanging = ReflectionUtils.findField(TweetsAggregationServiceImpl.class, methodName );
    fieldForChanging.setAccessible(true);
    //SetValue
    ReflectionUtils.setField(fieldForChanging, aggregationService , classValue);
    fieldForChanging.setAccessible(false);
  }

  private Object getPrivateField(String methodName) {
    Field findField = ReflectionUtils.findField(TweetsAggregationServiceImpl.class,methodName);
    findField.setAccessible(true);
    Object field = ReflectionUtils.getField(findField, aggregationService);
    findField.setAccessible(false);
    return field;
  }

  @Test
  public void testProcessStatusBlackListFilterHandler() {

    setPrivateMethByReflect("wordsInfo", WordsInfo.class, new WordsInfo(stopWordsStandartRes, blackWordsStandartSet));
    ProcessingHandler<GeneralProcessingContext> blackListHandler = new BlackListFilterHandler(terminalHandlerMock, normalizationFilterHandler);
    setPrivateMethByReflect("startHandler", ProcessingHandler.class, blackListHandler);

    String[] tweetBlackWrdsArray = {"Monica make blow job now.", "Monica want fuck", "What is the shit for Monica", "Shit Monica", "Monica like blOwJoB"};
    for (String tweetText : tweetBlackWrdsArray) {
      Mockito.when(statusMock.getText()).thenReturn(tweetText);
      aggregationService.processStatus(statusMock);
    }
    verify(terminalHandlerMock, Mockito.times(tweetBlackWrdsArray.length)).process(Matchers.any(GeneralProcessingContext.class));
    // dumpTweetHendlerMock should not process
    verifyNoMoreInteractions(terminalHandlerMock, normalizationFilterHandler);

  }

  @Test
  public void testProcessStatusBlackListFilterHandlerFail() {

    setPrivateMethByReflect("wordsInfo", WordsInfo.class, new WordsInfo(stopWordsStandartRes, blackWordsStandartSet));
    ProcessingHandler<GeneralProcessingContext> blackListHandler = new BlackListFilterHandler(terminalHandlerMock, normalizationFilterHandler);
    setPrivateMethByReflect("startHandler", ProcessingHandler.class, blackListHandler);

    String[] tweetNoBlackWrdsArray = {"Monica decent woman.", "Monica want furtherck", "Хочу, чтоб все создатели Java, требующей постоянных обновлений, и те, кто использует ее в приложениях, горели в аду на сковородках"};
    for (String tweetText : tweetNoBlackWrdsArray) {
      Mockito.when(statusMock.getText()).thenReturn(tweetText);
      aggregationService.processStatus(statusMock);
    }
    verify(normalizationFilterHandler, Mockito.times(tweetNoBlackWrdsArray.length)).process(Matchers.any(GeneralProcessingContext.class));
    // dumpTweetHendlerMock should not process
    verifyNoMoreInteractions(terminalHandlerMock, normalizationFilterHandler);

  }

  @Test
  public void testBuildProcessingChain() {

    aggregationService.buildProcessingChain();
    Assert.assertEquals(DumpTweetHandler.class, aggregationService.getStartHandler().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, aggregationService.getStartHandler().getClass().getSuperclass());

    DumpTweetHandler startHandler = (DumpTweetHandler)aggregationService.getStartHandler();
    Assert.assertEquals(BlackListFilterHandler.class, startHandler.getSuccessor().getClass());
    Assert.assertEquals(AbstractConditionalProcessingHandler.class, startHandler.getSuccessor().getClass().getSuperclass());

    BlackListFilterHandler handler2 = (BlackListFilterHandler)startHandler.getSuccessor();
    Assert.assertEquals(TerminalHandler.class, handler2.getSuccessorTrue().getClass());
    Assert.assertEquals(LanguageFilterHandler.class, handler2.getSuccessorFalse().getClass());
    Assert.assertEquals(AbstractConditionalProcessingHandler.class, handler2.getSuccessorFalse().getClass().getSuperclass());

    LanguageFilterHandler handler2_1 = (LanguageFilterHandler)handler2.getSuccessorFalse();
    Assert.assertEquals(NormalizationFilterHandler.class, handler2_1.getSuccessorTrue().getClass());
    Assert.assertEquals(TerminalHandler.class, handler2_1.getSuccessorFalse().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler2_1.getSuccessorTrue().getClass().getSuperclass());

    NormalizationFilterHandler handler3 = (NormalizationFilterHandler)handler2_1.getSuccessorTrue();
    Assert.assertEquals(SentimentHandler.class, handler3.getSuccessor().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler3.getSuccessor().getClass().getSuperclass());

    SentimentHandler handler4 = (SentimentHandler)handler3.getSuccessor();
    Assert.assertEquals(IntentDetectionHandler.class, handler4.getSuccessor().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler4.getSuccessor().getClass().getSuperclass());

    IntentDetectionHandler handler5 = (IntentDetectionHandler)handler4.getSuccessor();
    Assert.assertEquals(TopicTaskExecutionHandler.class, handler5.getSuccessor().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler5.getSuccessor().getClass().getSuperclass());

    TopicTaskExecutionHandler topicTaskExecutionHandler = (TopicTaskExecutionHandler)handler5.getSuccessor();

    Assert.assertEquals(ConcurrencyProtectionHandler.class, topicTaskExecutionHandler.getRepeatedSuccessor().getClass());

    ConcurrencyProtectionHandler concurrencyProtectionHandler = (ConcurrencyProtectionHandler)topicTaskExecutionHandler.getRepeatedSuccessor();

    Assert.assertEquals(TrendFilterHandler.class, concurrencyProtectionHandler.getSuccessor().getClass());

    TrendFilterHandler trendHandler = (TrendFilterHandler) concurrencyProtectionHandler.getSuccessor();

    Assert.assertEquals(IndustryFilterHandler.class, trendHandler.getSuccessorFalse().getClass());

    IndustryFilterHandler industryFilterHandler = (IndustryFilterHandler)trendHandler.getSuccessorFalse();

    Assert.assertEquals(FindKeywordsHandler.class, industryFilterHandler.getSuccessorFalse().getClass());

    Assert.assertEquals(UsefulTweetsCounterHandler.class, topicTaskExecutionHandler.getSuccessor().getClass());

    UsefulTweetsCounterHandler handlerAfterIteration = (UsefulTweetsCounterHandler)topicTaskExecutionHandler.getSuccessor();
    Assert.assertEquals(TerminalHandler.class, handlerAfterIteration.getSuccessor().getClass());

    FindKeywordsHandler handler8 = (FindKeywordsHandler) industryFilterHandler.getSuccessorFalse();
    Assert.assertEquals(MentionsHandler.class, handler8.getSuccessor().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler8.getSuccessor().getClass().getSuperclass());

    MentionsHandler handler9 = (MentionsHandler)handler8.getSuccessor();
    Assert.assertEquals(TopRetweetsHandler.class, handler9.getSuccessor().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler9.getSuccessor().getClass().getSuperclass());

//    TopRetweetsHandler handler10 = (TopRetweetsHandler)handler9.getSuccessor();
//    Assert.assertEquals(LatesTweetsHandler.class, handler10.getSuccessor().getClass());
//    Assert.assertEquals(AbstractChainProcessingHandler.class, handler10.getSuccessor().getClass().getSuperclass());

    TopRetweetsHandler handler11 = (TopRetweetsHandler)handler9.getSuccessor();
    Assert.assertEquals(TweetTermsHandler.class, handler11.getSuccessor().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler11.getSuccessor().getClass().getSuperclass().getSuperclass());

    TweetTermsHandler handler12 = (TweetTermsHandler)handler11.getSuccessor();
    Assert.assertEquals(IntentionProcessingHandler.class, handler12.getSuccessor().getClass());
    Assert.assertEquals(AbstractChainProcessingHandler.class, handler12.getSuccessor().getClass().getSuperclass());

    IntentionProcessingHandler lastHandler = (IntentionProcessingHandler)handler12.getSuccessor();
    Assert.assertEquals(TerminalHandler.class, lastHandler.getSuccessor().getClass());

  }

}
