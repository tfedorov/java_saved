/**
 * 
 */
package com.game.party;

import java.util.List;

/**
 * @author taras
 *
 */
public class RoundFactory {

	public static RoundBean create(final int roundCount, final List<String> users){
		return new RoundBean(roundCount, users);
	}
	
	public static RoundBean createStartRound(final List<String> users){
		return create(0,users);
	}
}
