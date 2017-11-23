package com.tfedorov.social.intention;

public class Qualification {

	private String qualificationStr;

	private int before;

	private int after;

	private Operator operator;

	public Qualification(String qualificationStr, int before, int after,
			Operator operator) {
		super();
		this.qualificationStr = qualificationStr;
		this.before = before;
		this.after = after;
		this.operator = operator;
	}

	public Qualification() {
		super();
	}

	public Qualification(String qsStr) {
		super();
		buildQualificationObj(qsStr);
	}

	@Override
	public String toString() {
		return operator.getOperator() + before + "." + after + " "
				+ qualificationStr;
	}

	private void buildQualificationObj(String qsStr) {
		// &3.3 me
		String[] s = qsStr.split("\\s+");
		// TODO - add support of operators in future
		String opStr = s[0].substring(0, 1);
		String befStr = s[0].substring(1, 2);
		String aftStr = s[0].substring(3, 4);

		this.operator = Operator.INCLUDED;
		this.before = Integer.valueOf(befStr);
		this.after = Integer.valueOf(aftStr);
		this.qualificationStr = s[1];
	}

	/**
	 * @return the qualificationStr
	 */
	public String getQualificationStr() {
		return qualificationStr;
	}

	/**
	 * @param qualificationStr
	 *            the qualificationStr to set
	 */
	public void setQualificationStr(String qualificationStr) {
		this.qualificationStr = qualificationStr;
	}

	/**
	 * @return the before
	 */
	public int getBefore() {
		return before;
	}

	/**
	 * @param before
	 *            the before to set
	 */
	public void setBefore(int before) {
		this.before = before;
	}

	/**
	 * @return the after
	 */
	public int getAfter() {
		return after;
	}

	/**
	 * @param after
	 *            the after to set
	 */
	public void setAfter(int after) {
		this.after = after;
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

}
