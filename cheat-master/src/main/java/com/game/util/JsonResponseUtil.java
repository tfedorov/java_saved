/**
 * 
 */
package com.game.util;

import java.util.Iterator;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.game.party.PartyBean;

/**
 * @author taras
 * 
 */
public class JsonResponseUtil {

	public static String sendSimpleSucces() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String sendSteps(String steps) {
		// TODO Auto-generated method stub
		return steps;
	}

	public static String sendSession(String session) {
		return "{ \"session\" : \"" + session + "\" }";
	}

	public static String sessionComplete(List<String> users) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String sessionNotComplete(List<String> users) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sendSessionNotCompletes() {
		return "{ \"sessionCompleted\" : false}";
	}

	public String sendSessionCompletes(String session, List<String> usersInSessia) {
		StringBuilder userJson = new StringBuilder();
		Iterator<String> iterator = usersInSessia.iterator();
		userJson.append("\"").append(iterator.next()).append("\"");
		while (iterator.hasNext()) {
			userJson.append(",\"").append(iterator.next()).append("\"");
			
		}
		return "{ \"sessionCompleted\" : true , \"session\": \"" + session + "\" , \"usersInSessia\" : [" + userJson.toString() + "]}";
	}

	public String print(PartyBean existingGame) {
		throw new NotImplementedException();
	}

}
