function updeteMessages(result, textStatus, jqXHR) {

	if (result !== undefined) {
		el = $("#logChat");
		$.each(result, function(index, value) {
			message = "<tr><td>[" + value.messageDate + "]</td><td> from [" + value.userName + "]</td><td> " + value.message + "</td></tr>";
			el.append(message);
		});

	}
	;
	$("#lastMessageTime").val(jqXHR.getResponseHeader("last_update_time"));
};

function sendWritedMessageAjax(reqData) {

	$.ajax({
		url : '/log.json?last_update=' + $("#lastMessageTime").val(),
		//url : '/game/step/1?last_update=' + $("#lastMessageTime").val(),
		type : 'POST',
		dataType : 'json',
		data : reqData,
		success : updeteMessages
	});
};

$().ready(function() {

	$("#submitmsg").click(function() {
		sendWritedMessageAjax({
			isWrited : true,
			userName : $("#userName").val(),
			userText : $("#userText").val()
		});
		$("#userText").val('');
	});

	setInterval(sendWritedMessageAjax, 95000);

});