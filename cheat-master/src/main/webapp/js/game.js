
$().ready(function() {

	//Vk iteraction
	//Get medium photos for users
	function loadOneGamerData(gamerUid){

	    VK.api("photos.getProfile", {uid: gamerUid , limit: 1}, function(vkdata) {
	    	imageSrc = vkdata.response[0].src;
	    	$('#userPhtotosArea').append("<img id='"+gamerUid+ "' class = 'userImages' src='"+imageSrc+"'/>");
	    });		
	    
	};
	function loadGamersData(usersInSessia){
		console.log("loadSessionGamerImages = " + usersInSessia);
		for (var i = 0; i < usersInSessia.length; i++) {
			console.log(" i = " + i	);
			loadOneGamerData(usersInSessia[i]);
		}
	};
	
	
	//AJAX wrapper functions
	function sendPostReq(url, reqData, succFunction) {	
		$.post(url, reqData, succFunction, "json").fail(printError);
	};
	function sendPostExtReq(url, reqData, succFunction, errFunction) {	
		$.post(url, reqData, succFunction, "json").fail(errFunction);
	};
	function sendGetReq(url, succFunction) {
		$.getJSON(url, succFunction).fail(printError);
	};
	function printError(jqxhr, textStatus, error) {
		alert(error);
	};
	
	//User do action mechanism
	function sendAnswerStep(reqData) {
		console.log("ANSWER");
		sendPostReq('/game/' + _sessionId + "/" +  _userId + "/answer", reqData);
	};
	// Register click action
	$("#submitAnswer").click(function() {
		sendAnswerStep({
			answer : $("#answerInp").val()
		});
		$("#answerInp").val('');
	});	
	
	
	// Game iteraction mechanism
	// Get game score ,active user , active ansewer etc 
	function iteractionHandler(result, textStatus, jqXHR) {
		console.log("iteractionHandler " );
		console.log(JSON.stringify(result));

		console.log("result.compLetedStep = " + result.compLetedStep);
	
		if(result.actualStep != undefined){
			console.log("result.actualStep.owner = " + result.actualStep.owner);
			var stepMessage = result.actualStep.owner + ":" +  result.actualStep.stepInfo + "<br/>";
			$('#stepsArea #curentStep').html(stepMessage);
			//highLight()
		}
		console.log("result.totalScore = " + result.totalScore);
		$('#scoreArea #totalScore').html(result.totalScore);		
	};
	function iteractionGetReq() {
		sendGetReq('/game/' + _sessionId + '/action', iteractionHandler);
	};
	
	
	// Ping app for completing party
	// Load users images
	// Start game iteraction mechanism
	function pingPartyHandler(result, textStatus, jqXHR) {
		console.log("pingPartyHandler");
		console.log(JSON.stringify(result));
		if(result.completed === true){
			_sessionId = result.session ;
			console.log(_sessionId);
			$('#seesionId').val(_sessionId);	
			
			loadGamersData(result.users);	
			setInterval(iteractionGetReq,2500);
			
		}else{
			console.log("Session is not completed");
			setTimeout(userSessionGetReq,5000);
		}
	}	
	function userSessionGetReq(){
		sendGetReq('/game/party/user/' + _userId, pingPartyHandler, function(){setTimeout(userSessionGetReq,5000);});
	}
	
	// Send request for registering user in party
	// Start ping completing party mechanism
	function startPartyHandler(result, textStatus, jqXHR) {	
		setTimeout(userSessionGetReq,5000);
	}	
	function startPartyReq(){
		//to do rewrite wrong handler
		sendPostExtReq('/game/party/user/' + _userId, startPartyHandler, function(){setTimeout(registerUserPost,5000);});
	}
	
	
	// entrance point
    VK.api("users.get", function(vkdata) {
    	console.log(JSON.stringify(vkdata));
    	_userId = vkdata.response[0].uid;
    	userData = vkdata.response;
    	
    	$("#userId").val(_userId);
    	$("#welcome_name").html(vkdata.response[0].first_name);
    	
    	startPartyReq();
    	
    });
});