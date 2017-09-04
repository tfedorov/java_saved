<html>
<head>
<title>Make Bet</title>
<link rel="stylesheet" href="style/custom.css" media="all">
</head>
<body id="body">
	<p id="startId">Hello world!</p>
	<span id="changed"></span>
	<script type="text/javascript" src="js/jquery-1.8.3.js"></script>
	<script type="text/javascript" src="js/make-bet.js"></script>
	<script type="text/javascript" src="//vk.com/js/api/openapi.js?105"></script>

	<!--
Put this div tag to the place, where Auth block will be 
	<div id="vk_auth"></div>
	-->

	<!--  -->
	<div id="vk_api_transport"></div>

	<script type="text/javascript">
		function showLogin() {
			$('#vk_logout').hide();
			$('#vk_login').show();
			$('#vk_auth').show();
		}
		function showLogout() {
			$('#vk_logout').show();
			$('#vk_login').hide();
			$('#vk_auth').hide();
		}
		function authInfo(response) {
			if (response.session) {
				//console.log('user: ' + response.session.mid);
				var userUid = response.session.mid;
				showLogout();
				/*
				VK.api("photos.getProfile", {
					uid : userUid,
					limit : 1
				}, function(vkdata) {
					console.log("Photo -" + JSON.stringify(vkdata));
					//var imageSrc = vkdata.response[0].src;
					//console.log("imageSrc= " + imageSrc);
					//$('#userPhtotosArea').html("<img class = 'userImages' src='"+imageSrc+"'/>");
				});
				*/
				VK.api("users.get", {
					user_ids : userUid,
					fields : "sex,photo_200,city,verified",
						name_case: "Nom"
				}, function(vkdata) {
					console.log("Get " + JSON.stringify(vkdata));
					var imageSrc = vkdata.response[0].photo_200;
					console.log("imageSrc= " + imageSrc);
					$('#userPhtotosArea').html("<img class = 'userImages' src='"+imageSrc+"'/>");
				});
			} else {
				showLogin();
			}
		}

		setTimeout(function() {
			var el = document.createElement("script");
			el.type = "text/javascript";
			el.src = "http://vkontakte.ru/js/api/openapi.js";
			el.async = true;
			document.getElementById("vk_api_transport").appendChild(el);

		}, 0);

		window.vkAsyncInit = function() {

			VK.init({
				apiId : 4056613
			});

			VK.Observer.subscribe('auth.login', function(response) {
				console.log('login');
			});
			VK.Observer.subscribe('auth.logout', function() {
				console.log('logout');
			});
			VK.Observer.subscribe('auth.statusChange', function(response) {
				console.log('statusChange');
			});
			VK.Observer.subscribe('auth.sessionChange', function(r) {
				console.log('sessionChange');
			});
			VK.Auth.getLoginStatus(function(response) {
				console.log("getLoginStatus ");
				console.log(JSON.stringify(response));
				if (response.session) {
					showLogout();
				} else {
					showLogin();
				}
			});
			/*
			VK.Widgets.Auth("vk_auth", {
				width : "200px",
				onAuth : function(data) {
					alert('user ' + data['uid'] + ' authorized');
					$('#vk_auth').hide();
				}
			});
			 */
			VK.UI.button('vk_login');
		};
	</script>
	<div id="vk_login" onclick="VK.Auth.login(authInfo,4)"></div>
	<div id="userPhtotosArea"></div>
	<div id="vk_logout" onclick="VK.Auth.logout(authInfo)">[logout]</div>
</body>
</html>
