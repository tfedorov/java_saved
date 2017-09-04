/**
 * 
 */
package com.game.party;


/**
 * @author taras
 * 
 */
public class PartyBean {

	private int totalScore;
	private RoundBean currentRound;

	public PartyBean(RoundBean firstRound) {
		super();
		this.setCurrentRound(firstRound);
		this.setTotalScore(0);
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public RoundBean getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(RoundBean currentRound) {
		this.currentRound = currentRound;
	}
	
	
	
}
