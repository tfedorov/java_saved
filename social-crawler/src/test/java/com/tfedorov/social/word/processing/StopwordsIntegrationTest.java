package com.tfedorov.social.word.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tfedorov.social.word.dao.WordsDaoImpl;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tfedorov.social.word.Word.WORD_TYPE;

public class StopwordsIntegrationTest
{
	WordsDaoImpl stopwordsDao;
	@Test
	public void testFile()
	{
		List<String> englishWords = new ArrayList<String>();
		List<String> spanishWords = new ArrayList<String>();
		List<String> russianWords = new ArrayList<String>();

		readFromFile(englishWords, spanishWords, russianWords);

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/social_crawler");
		dataSource.setUsername("dmp");
		dataSource.setPassword("dmp01");

		stopwordsDao = new WordsDaoImpl();
		stopwordsDao.setDataSource(dataSource);

		List<String> stopwords = stopwordsDao.selectWordStrings(WORD_TYPE.stop_words);

		StringBuilder insertEnglish = new StringBuilder("insert into stop_words (word, description) values ");

		for (String englishWord : englishWords)
		{
			if (!stopwords.contains(englishWord))
			{
				insertEnglish.append("(\"" + englishWord + "\", \"additional\"), ");
			}
		}
		insertEnglish.append("(\"lol\", \"additional\");");
		String engInsert = insertEnglish.toString();

		StringBuilder insertSpanish = new StringBuilder("insert into stop_words (word, description, language) values ");
		for (String spanish : spanishWords)
		{
			insertSpanish.append("(\"" + spanish + "\", \"additional\", \"es\"), ");
		}
		int commaIndex =	 insertSpanish.toString().lastIndexOf(",");

		String spanishInsert = insertSpanish.toString().substring(0, commaIndex) + ";";

		StringBuilder insertRussian = new StringBuilder("insert into stop_words (word, description, language) values ");
		for (String russian : russianWords)
		{
			insertRussian.append("(\"" + russian + "\", \"additional\", \"ru\"), ");
		}
		commaIndex = insertRussian.toString().lastIndexOf(",");
		String russianInsert = insertRussian.toString().substring(0, commaIndex) + ";";
		String commonInsert = engInsert + "\r\n\r\n" + spanishInsert + "\r\n\r\n" + russianInsert;
		try
		{
			//	writeToFile(engInsert, "stop_words_additional_english.sql", "UTF-8");
			//	writeToFile(spanishInsert, "stop_words_additional_spanish.sql", "UTF-8");
			writeToFile(commonInsert, "stop_words_additional.sql", "UTF-8");
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}


	private void writeToFile(String engInsert, String fileName, String encoding) throws IOException
	{


		File write = new File("/Users/apple/Desktop/" + fileName);
		if (!write.exists())
		{
			write.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(write);
		byte[] data = engInsert.getBytes(encoding);
		out.write(data);
		out.close();
	}

	private void readFromFile(List<String> englishWords, List<String> spanishWords, List<String> russianWords)
	{
		try {

			File file = new File("/Users/apple/Desktop/Stop Words (E,S,R)-utf.txt");

			InputStream inStream = new FileInputStream(file);

			BufferedReader br = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
			String output;

			while ((output = br.readLine()) != null)
			{
				String englishWord = output.substring(0, output.indexOf("\t"));

				if (englishWord != null && !englishWord.isEmpty())
				{
					englishWords.add(englishWord);
				}

				int spanishStart = output.indexOf("\t") + 1;
				int spanishEnd = output.lastIndexOf("\t");
				String spanishWord = output.substring(spanishStart, spanishEnd);
				if (spanishWord != null && !spanishWord.isEmpty())
				{
					spanishWords.add(spanishWord);
				}

				int russianStart = output.lastIndexOf("\t") + 1;
				int russianEnd = output.length();
				String russianWord = output.substring(russianStart, russianEnd);
				if (russianWord != null && !russianWord.isEmpty())
				{
					russianWords.add(russianWord);
				}
			}

			br.close();
			inStream.close();

			System.out.println(englishWords);
			System.out.println(spanishWords);
			System.out.println(russianWords);
		} catch (IOException e) {
			System.out.println("File Read Error");
		}
	}
}
