package com.tfedorov.social.word.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;

public class SortingTest
{
	@Test
	public void testDistinctAndSorting()
	{

		List<String> tweetTermsWithoutStopWords = new ArrayList<String>();

		tweetTermsWithoutStopWords.add("Windows");
		tweetTermsWithoutStopWords.add("Apple");
		tweetTermsWithoutStopWords.add("MacOs");
		tweetTermsWithoutStopWords.add("Apple");
		tweetTermsWithoutStopWords.add("Microsoft");


		List<String> arr = new ArrayList<String>(new TreeSet<String>(tweetTermsWithoutStopWords));

		//Collections.sort(arr);

		for (String s : arr)
		{
			System.out.println(s);
		}

		Iterator<String> iterator = arr.iterator();

		String current = "";

		while (iterator.hasNext())
		{
			String next = iterator.next();
			if (current.isEmpty() || current == null)
			{
				current = next;
				continue;
			}
			int comparer = current.compareTo(next);

			Assert.assertFalse("Wrong order", comparer > 0);

			Assert.assertFalse("Strings are equal", comparer == 0);

			Assert.assertTrue("It's ok", comparer < 0);

			current = next;
		}

	}
}
