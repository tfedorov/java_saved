/**
 * 
 */
package com.game.util;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.game.party.PartyBean;

/**
 * @author taras
 * 
 */
public class JsonUtil {

	private static ObjectMapper mapper = new ObjectMapper();


	public static String printGame(PartyBean game) throws JsonGenerationException, JsonMappingException, IOException{
		return mapper.writeValueAsString(game);
	}

	public static String getNotCompletedSessionJson() {
			return "{\"isCompleted\":\"false\"}";
		}

	public static String getCompletedSessionJson(String sessionId, List<String> users) throws JsonGenerationException, JsonMappingException, IOException {
		SessionCompleteBean bean = new SessionCompleteBean(sessionId, users);
		return mapper.writeValueAsString(bean);
		
	}
	
}
