package com.tfedorov.social.normalization;

import java.util.HashSet;

import com.tfedorov.social.normalization.stemming.StemmingService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tfedorov.social.normalization.stemming.StemmingServiceImpl;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

public class TestNormalizationPerformance {

  private static StemmingService stemmingService = new StemmingServiceImpl();

  private String sentence =
      "Ukraine is a country in Eastern Europe. Ukraine borders the Russian Federation to the east and northeast, Belarus to the northwest, Poland, Slovakia and Hungary to the west, Romania and Moldova to the southwest, and the Black Sea and Sea of Azov to the south and southeast, respectively. It has an area of 603,628 km², making it the largest country entirely within Europe.";

  private String sentance2 =
      "Independence and expansion The American Revolution was the first successful colonial war of independence against a European power. Americans had developed a democratic system of local government and an ideology of \"republicanism\" that held government rested on the will of the people (not the king), which strongly opposed corruption and demanded civic virtue. They demanded their rights as Englishmen and rejected British efforts to impose taxes without the approval of colonial legislatures. The British insisted and the conflict escalated to full-scale war in 1775, the American Revolutionary War.[47] On June 14, 1775, the Continental Congress, convening in Philadelphia, established a Continental Army under the command of George Washington.[48] Proclaiming that \"all men are created equal\" and endowed with \"certain unalienable Rights\", the Congress adopted the Declaration of Independence, drafted largely by Thomas Jefferson, on July 4, 1776. That date is now celebrated annually as America's Independence Day. In 1777, the Articles of Confederation established a weak government that operated until 1789.[49] Declaration of Independence, by John Trumbull, picturing the Committee of Five presenting their draft to the Second Continental Congress in 1776. States that did not originally belong to the Union were initially organized as territories. Citizens were encouraged to settle there, and after sufficient time, the territories were admitted into the Union as full and equal states. After a naval victory followed by the British defeat at Yorktown by American forces assisted by the French,[50] the United States was independent. In the peace treaty of 1783 Britain recognized American sovereignty over most territory east of the Mississippi River. Nationalists calling for a much stronger federal government with powers of taxation led the constitutional convention in 1787. After intense debate in state conventions the United States Constitution was ratified in 1788. The first Senate, House of Representatives, and president—George Washington—took office in 1789. The Bill of Rights, forbidding federal restriction of personal freedoms and guaranteeing a range of legal protections, was adopted in 1791.[51] Attitudes toward slavery were shifting; nearly all states officially outlawed the international slave trade before the federal government criminalized it in 1808.[52] All the Northern states abolished slavery between 1780 and 1804, leaving the slave states of the South as defenders of the \"peculiar institution\". With cotton a highly profitable plantation crop after 1820, Southern whites increasingly decided slavery was a positive good for everyone, including the slaves.[53] The Second Great Awakening, beginning about 1800, converted millions to evangelical Protestantism. In the North it energized multiple social reform movements, including abolitionism.[54] Americans' eagerness to expand westward prompted a long series of Indian Wars.[55] The Louisiana Purchase of French-claimed territory under President Thomas Jefferson in 1803 almost doubled the nation's size.[56] The War of 1812, declared against Britain over various grievances and fought to a draw, strengthened U.S. nationalism.[57] A series of U.S. military incursions into Florida led Spain to cede it and other Gulf Coast territory in 1819.[58] President Andrew Jackson took office in 1829, and began a set of reforms which led to the era of Jacksonian democracy, which is considered to have lasted from 1830 to 1850. This included many reforms, such as wider male suffrage, and various adjustments to the power of the Federal government. This also led to the rise of the Second Party System, which refers to the dominant parties which existed from 1828 to 1854. The Trail of Tears in the 1830s exemplified the Indian removal policy that moved Indians to their own reservations with annual government subsidies. The United States annexed the Republic of Texas in 1845, amid a period when the concept of Manifest Destiny was becoming popular.[59] The 1846 Oregon Treaty with Britain led to U.S. control of the present-day American Northwest.[60] The U.S. victory in the Mexican-American War resulted in the 1848 cession of California and much of the present-day American Southwest.[61] The California Gold Rush of 1848–49 further spurred western migration.[62] New railways made relocation easier for settlers and increased conflicts with Native Americans.[63] Over a half-century, up to 40 million American bison, or buffalo, were slaughtered for skins and meat and to ease the railways' spread.[64] The loss of the buffalo, a primary resource for the plains Indians, was an existential blow to many native cultures.[64] In 1869, President Ulysses S. Grant's Peace policy reversed the previous costly policy of \"wars of extermination\" in order to civilize and give Indians eventual United State citizenship having incorporated Indians as wards of the state, led by a philanthropic Board of Indian Commissioners. [65] Slavery, civil war and industrialization Tensions between slave and free states mounted with arguments about the relationship between the state and federal governments, as well as violent conflicts over the spread of slavery into new states.[66] Abraham Lincoln, candidate of the largely antislavery Republican Party, was elected president in 1860.[67] Before he took office, seven slave states declared their secession—which the federal government maintained was illegal—and formed the Confederate States of America.[68] Battle of Gettysburg, Pennsylvania. The Civil War cemented the Union and spurred the steel industry and intercontinental railroad construction. Ellis Island, New York City. East Coast immigrants worked in factories, railroads, and mines, and created demand for industrialized agriculture. With the Confederate attack upon Fort Sumter, the Civil War began and four more slave states joined the Confederacy.[68] Lincoln's Emancipation Proclamation in 1863 declared slaves in the Confederacy to be free. Following the Union victory in 1865, three amendments to the U.S. Constitution ensured freedom for the nearly four million African Americans who had been slaves,[69] made them citizens, and gave them voting rights. The war and its resolution led to a substantial increase in federal power.[70] The war remains the deadliest conflict in American history, resulting in the deaths of 620,000 soldiers.[71] After the war, the assassination of Abraham Lincoln radicalized Republican Reconstruction policies aimed at reintegrating and rebuilding the Southern states while ensuring the rights of the newly freed slaves.[72] President Ulysses S. Grant implemented the Department of Justice and used the U.S. Military to enforce suffrage and civil rights for African Americans in the South destroying the Ku Klux Klan in 1871 under the Force Acts.[73] The resolution of the disputed 1876 presidential election by the Compromise of 1877 ended Reconstruction; Jim Crow laws soon disenfranchised many African Americans.[72] In the North, urbanization and an unprecedented influx of immigrants from Southern and Eastern Europe hastened the country's industrialization. The wave of immigration, lasting until 1924, provided labor and transformed American culture.[74] United States immigration policies were Eurocentric, which barred Asians from naturalization, and restricted their immigration beginning with the Chinese Exclusion Act in 1882.[75] National infrastructure development spurred economic growth. The end of the Civil War spurred greater settlement and development of the American Old West. This was due to a variety of social and technological developments, including the completion of the First Transcontinental Telegraph in 1861 and the First Transcontinental Railroad in 1869. The 1867 Alaska Purchase from Russia completed the country's mainland expansion. The Wounded Knee Massacre in 1890 was the last major armed conflict of the Indian Wars. In 1893, the indigenous monarchy of the Pacific Kingdom of Hawaii was overthrown in a coup led by American residents; the United States annexed the archipelago in 1898. Victory in the Spanish–American War the same year demonstrated that the United States was a world power and led to the annexation of Puerto Rico, Guam, and the Philippines.[76] The Philippines gained independence a half-century later; Puerto Rico and Guam remain U.S. territories. The emergence of many prominent industrialists at the end of the 19th century gave rise to the Gilded Age, a period of growing affluence and power among the business class. This period eventually ended with the beginning of the Progressive Era, a period of great reforms in many societal areas, including regulatory protection for the public, greater antitrust measures, and attention to living conditions for the working classes. President Theodore Roosevelt was one leading proponent of progressive reforms.";

  private String sentenceLanguage = "en";
  private int testCount = 1000;

  private static EtmMonitor etmMonitor;

  @BeforeClass
  public static void setup() {
    BasicEtmConfigurator.configure(true);
    etmMonitor = EtmManager.getEtmMonitor();
    etmMonitor.start();
    stemmingService.initializeAnalyzers(new HashSet<String>());
    stemmingService.getNormalizedTerms("ambreléa", "fr");
  }

  @AfterClass
  public static void tearDown() {
    etmMonitor.render(new SimpleTextRenderer());
    etmMonitor.stop();
  }

  @Test
  public void testSentenceStemmingPerformanceWithEtmMonitor() {
    // test
    for (int i = 0; i < testCount; i++) {
      EtmPoint p = etmMonitor.createPoint("sentencePerformance");
      stemmingService.getNormalizedTerms(sentance2, sentenceLanguage);
      p.collect();
    }

  }

  @Test
  public void testWordStemmingPerformanceWithEtmMonitor() {
    // text to word preparing
    String[] words = sentance2.split(" ");
    // test
    for (int i = 0; i < testCount; i++) {
      for (int j = 0; j < words.length; j++) {
        EtmPoint p = etmMonitor.createPoint("wordPerformance");
        stemmingService.getNormalizedTerms(words[j], sentenceLanguage);
        p.collect();
      }
    }
  }
}
