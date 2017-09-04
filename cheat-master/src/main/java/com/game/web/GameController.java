package com.game.web;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.game.exception.GameException;
import com.game.service.PartyThreadBalancerService;
import com.game.util.JsonResponseUtil;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GameController {


	@Autowired
	private PartyThreadBalancerService gameService;

	@RequestMapping(value = "/{sessionId}/{userid}/answer", method = RequestMethod.POST)
	public ResponseEntity<String> gameAnswer(@PathVariable("userid") String user, @PathVariable("sessionId") String sessionId,
			@RequestParam(required = true) String answer) {

		try {
			gameService.answer(sessionId, user, answer);
			return makeSuccesResponse(JsonResponseUtil.sendSimpleSucces());
		} catch (GameException ex) {
			return makeErrorResponse(ex);
		}
	}

	@RequestMapping(value = "/{sessionId}/action", method = RequestMethod.GET)
	public ResponseEntity<String> gameStatus(@PathVariable("sessionId") String sessionId, @RequestParam(required = false) Integer lastStep) {
		try {
		String response = gameService.getGame(sessionId);
		return makeSuccesResponse(response);
		} catch (GameException ex) {
			return makeErrorResponse(ex);
		}
	}
	
	@RequestMapping(value = "/party/user/{userid}", method = RequestMethod.GET)
	public ResponseEntity<String> getParty(@PathVariable("userid") String user) throws UnknownHostException {
		try {
			return makeSuccesResponse(gameService.getGompletedParty(user));
		} catch (GameException ex) {
			return makeErrorResponse(ex);
		}
	}

	@RequestMapping(value = "/party/user/{userid}", method = RequestMethod.POST)
	public ResponseEntity<String> createParty(@PathVariable("userid") String user) throws UnknownHostException {
		try {
			return makeSuccesResponse(gameService.registerCandidat(user));
		} catch (GameException ex) {
			return makeErrorResponse(ex);
		}
	}

	private ResponseEntity<String> makeSuccesResponse(String resp) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<String>(resp, responseHeaders, HttpStatus.CREATED);
	}
	
	private ResponseEntity<String> makeErrorResponse(GameException ex) {
		ex.printStackTrace();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<String>(ex.getMessage(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
