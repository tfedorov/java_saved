<html>
<head>
<title>Chat - Customer Module</title>
<link rel="stylesheet" href="style/style.css" media="all">
<link rel="stylesheet" href="style/adipoli.css" type="text/css" />
</head>
<body id="body">
	<div id="wrapper">
		<div id="menu">
			<p id="welcome">
				Welcome <span id="welcome_name"></span> <b></b>
			</p>
		</div>
		
		<!-- hidden field -->
		<input type="hidden" name="userId" id="userId" value="" /><br /> 
		<input type="hidden" name="lastMessageTime" id="lastMessageTime" value="0" /><br />
		<input type="hidden" name="seesionId" id="seesionId" value="" /><br />
		<!-- hidden field -->
		
		<div id="scoreArea" class="area">Total score:<span id="totalScore">0</span> Round score:<span id="roundScore">0</span></div>
		<div id="userPhtotosArea" class="area"></div>
		<div id="stepsArea" class="area"><div id="stepLogs"></div><div id="curentStep"></div></div>
		<div id="answerArea" class="area">	
			<input type="text" size="40" name="answerInp" id="answerInp" />
			<input name="submitAnswer" type="submit" id="submitAnswer" value="Answer" /><br />
		</div>	
	</div>
	<script type="text/javascript" src="js/jquery-1.8.3.js"></script>
	<script type="text/javascript" src="js/jquery.adipoli.js"></script>
	<script type="text/javascript" src="js/game.js"></script>
	<!-- VK-->
	<script src="http://vk.com/js/api/xd_connection.js?2" type="text/javascript"></script>
</body>
</html>
