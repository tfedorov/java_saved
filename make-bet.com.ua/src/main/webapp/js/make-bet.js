$().ready(function() {

	// AJAX JQUERY wrapper functions
	function sendPostReq(url, reqData, succFunction) {
		$.post(url, reqData, succFunction, "json").fail(printError);
	}
	;
	function sendPostExtReq(url, reqData, succFunction, errFunction) {
		$.post(url, reqData, succFunction, "json").fail(errFunction);
	}
	;
	function sendGetReq(url, succFunction) {
		$.getJSON(url, succFunction).fail(printError);
	}
	;
	function printError(jqxhr, textStatus, error) {
		console.log(error);
	}

	var ITERACTION_TIMEOUT = 100000;
/*
	setInterval(function() {
		sendGetReq('/rates/hello', function(result, textStatus, jqXHR) {
			$('span#changed').text(result.changed);
		})
	}, ITERACTION_TIMEOUT);
*/
});
