package com.tfedorov.social.intention;

public class Purchase {

	private Long id;

	private String primaryTest;

	private String secondaryTest;

	private String categoryLevel1;

	@Override
	public String toString() {
		return primaryTest + "," + secondaryTest + "," + categoryLevel1;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the primaryTest
	 */
	public String getPrimaryTest() {
		return primaryTest;
	}

	/**
	 * @param primaryTest
	 *            the primaryTest to set
	 */
	public void setPrimaryTest(String primaryTest) {
		this.primaryTest = primaryTest;
	}

	/**
	 * @return the secondaryTest
	 */
	public String getSecondaryTest() {
		return secondaryTest;
	}

	/**
	 * @param secondaryTest
	 *            the secondaryTest to set
	 */
	public void setSecondaryTest(String secondaryTest) {
		this.secondaryTest = secondaryTest;
	}

	/**
	 * @return the categoryLevel1
	 */
	public String getCategoryLevel1() {
		return categoryLevel1;
	}

	/**
	 * @param categoryLevel1
	 *            the categoryLevel1 to set
	 */
	public void setCategoryLevel1(String categoryLevel1) {
		this.categoryLevel1 = categoryLevel1;
	}

}
