<html>
<head>  
<title>Chat - Customer Module</title>  
<!-- link type="text/css" rel="stylesheet" href="style.css" /-->  
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
<script type="text/javascript" src="chat.js"></script>
</head>  
<body>
<div id="wrapper">  
    <div id="menu">  
        <p id="welcome">Welcome, <%=request.getRemoteAddr()%> <b></b></p>   
    </div>  
      
    <div id="chatbox"></div>  
      <input type="hidden" name="userName" id="userName" value="<%=request.getRemoteAddr()%>" /><br />
      <input type="hidden" name="lastMessageTime" id="lastMessageTime" value="0" /><br />
      
		<table name="logChat" id="logChat"></table><p/>  
        <textarea rows="4" cols="45" name="userText" id="userText"></textarea><p/>
        <input name="submitmsg" type="submit"  id="submitmsg" value="SendMessage" />  
    <!--/form-->  
    
</div>
</body>
</html>
