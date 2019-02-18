<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.rmat.fusen.bl.function.GoogleOAuthManager"%>
    
<%
	String email = null;
	email = (String)request.getSession().getAttribute(GoogleOAuthManager.USER_EMAIL_KEY);
	boolean login_flg = false;
	if(email != null){
		login_flg = true;
	}

%>

<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Fusen Note</title>

<!-- Favicon -->
<link rel="shortcut icon" href="images/favicon.ico"
	type="image/vnd.microsoft.icon" />

<!-- javascript -->
<script type="text/javascript" src="./scripts/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="./scripts/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="./scripts/jquery.flickable-1.0b3.js"></script>
<link rel="stylesheet" type="text/css" href="./css/smoothness/jquery-ui-1.8.16.custom.css" />
<script type="text/javascript" src="./scripts/jquery.mousewheel.js"></script>

<!-- css -->
<link rel="stylesheet" href="./css/fusen_style_1.1.css" type="text/css">

<script type="text/javascript">
var toLogout = function(){
	location.replace("./logout.do");
};

$(document).ready( function(){
	
	//mousewheelスクロール無効
	$("body").mousewheel(function(event, delta, deltaX, deltaY) {
		if (event.preventDefault) {
	        event.preventDefault();
	    }
	    event.returnValue = false;
    });
	
	//アコーディオン
	$("div#account").hover(function() {
		$(this).children('ul').show();
		$(this).children('span').css('color','#1b5ee5');
		$(this).children('span').css('text-decoration', 'underline');
	}, function() {
		$(this).children('ul').hide();
		$(this).children('span').css('color','');
		$(this).children('span').css('text-decoration', 'none');
	});
});
</script>

</head>

<body>
<div style="margin:0 auto; width:980px;">

<div class="headerspace">
	<div class="headerleft">
		<div><a style="text-decoration: none;" href="./">
				<img id="logo-img1" src="./images/logo_1.png" border="0" alt="Fusen Note" />
			</a>
		</div>
		<br>
		<div>
			<img id="logo-img2" src="./images/logo_2.png" border="0" alt="Online Fusen Application" />
		</div>
	</div>

	<div class="headerright">
		<div id="account">
<% if(login_flg){ %>
			<span><%=email %></span>
			<ul id="account-menu">
				<li onclick="toLogout()">
				<img id="user_icon" border="0" src="./images/icon/logout_icon_grey.png" style="height:20px; vertical-align:middle;">
            	<span style="color:#778899;">Logout</span>
			</li>
			</ul>
<% } %>
		</div>
		
	</div>
	
</div>
<div class="bodyspace">
    <div class="bodyleft">
    	<h4>オンラインで利用できる付箋サービスです。<br>付箋は自分のGoogleDriveに保存することができます。</h4>
    </div>
    <div class="bodyright">
    	<div class="loginwindow">
    		利用開始<br><br>
			
			<br>
    		<a style="text-decoration: none;" href="./authgoogledrive.do?page_id=oauth">
    			<div class="usualbutton" id="usegoogledrivebtn">
    				<img style="height:30px;" src="./images/icon/googledrive_logo.png" border="0"></img><br>
    				Googleドライブに保存する
    			</div>
    		</a>
    		<br><br>
    		
    		<br>

    		<div style="position:absolute; bottom: 5px; font-size:80%;">Googleアカウントが必要です。
    		</div>		
    	</div>
    </div>
</div>
</div>

<div class="footerspace">
	<div align="center"><p>Copyright © rmat All Rights Reserved.</p></div>
</div>
	
</body>
</html>