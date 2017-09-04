package com.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.game.exception.GameDataAccesException;
import com.game.exception.GameException;
import com.game.exception.GameLogicException;
import com.game.party.PartyThread;
import com.game.util.JsonUtil;

@Service
public class PartyThreadBalancerService {

	private static final int USERS_IN_PARTY_SIZE = 1;

	private Map<String, PartyThread> poolOfGames = new ConcurrentHashMap<String, PartyThread>();
	private Map<String, String> usersSessionMap = new ConcurrentHashMap<String, String>();

	AtomicLong sessionCounter = new AtomicLong(0);

	public void answer(final String sessionId, final String user, final String info) throws GameLogicException {
		PartyThread currentGame = poolOfGames.get(sessionId);// dao.getGameById(sessionId);
		if (currentGame == null) {
			throw new GameLogicException("There are no game with such id");
		}
		currentGame.doAnswerStep(user,info);
	}

	public String getGame(String sessionId) throws GameException {
		return poolOfGames.get(sessionId).getGameJson();
	}

	public String getGompletedParty(String user) throws GameException {
		String sessionForUser = usersSessionMap.get(user);
		if (sessionForUser == null || sessionForUser.isEmpty()) {
			return JsonUtil.getNotCompletedSessionJson();
		}
		List<String> usersInSessia = new ArrayList<String>();
		for (Entry<String, String> entry : usersSessionMap.entrySet()) {
			if (sessionForUser.equalsIgnoreCase(entry.getValue())) {
				usersInSessia.add(entry.getKey());
			}
		}
		try {
			return JsonUtil.getCompletedSessionJson(sessionForUser, usersInSessia);
		} catch (Exception e) {
			throw new GameLogicException(e.getMessage());
		}

	}

	public String registerCandidat(String user) throws GameDataAccesException {
		if (user == null || user.isEmpty() || usersSessionMap.containsKey(user)) {
			// log error
			return null;
		}
		usersSessionMap.put(user, "");

		List<String> gamerCandidats = new ArrayList<String>(USERS_IN_PARTY_SIZE);
		for (Entry<String, String> entry : usersSessionMap.entrySet()) {
			if (entry.getValue() == null || entry.getValue().isEmpty()) {
				gamerCandidats.add(entry.getKey());
				if (gamerCandidats.size() >= USERS_IN_PARTY_SIZE)
					break;
			}

		}
		//Game createdGame = new Game(gamerCandidats);
		PartyThread thread = new PartyThread(gamerCandidats);
		thread.run();

		String gameSessionId = ((Long) sessionCounter.incrementAndGet()).toString();
		poolOfGames.put(gameSessionId, thread);

		for (String gamer : gamerCandidats) {
			usersSessionMap.put(gamer, gameSessionId);
		}

		return null;
	}

}
