<%@page import="com.rmat.fusen.bl.function.GoogleOAuthManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	String email = (String)request.getSession().getAttribute(GoogleOAuthManager.USER_EMAIL_KEY);
	boolean login_flg = false;
	if(email != null){
		login_flg = true;
	}
	else{
		email = "";
	}

	String display = "";
	String loginstate = (String)request.getAttribute("loginstate");
	if(loginstate != null){
		if(loginstate.equals("nologin")){
			display= "display:none;";
		}
	}
%>

<!DOCTYPE html>

<html>
<head>
<meta charset="utf-8" />
<title>Fusen Note</title>

<meta name="description" content="">
<meta name="author" content="">

<meta name="viewport" content="width=device-width,initial-scale=1">
<!--
<link rel="stylesheet" href="css/topstyle.css" type="text/css">
-->
<!-- Favicon -->
<link rel="shortcut icon" href="images/favicon.ico"
	type="image/vnd.microsoft.icon" />

<!-- css -->

<link rel="stylesheet" href="css/basic_style.css" type="text/css">

<!-- javascript -->
<script type="text/javascript" src="./scripts/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="./scripts/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="./scripts/jquery.flickable-1.0b3.js"></script>
<link rel="stylesheet" type="text/css" href="./css/smoothness/jquery-ui-1.8.16.custom.css" />
<script type="text/javascript" src="./scripts/jquery.tipTip.js"></script>
<link rel="stylesheet" type="text/css" href="./scripts/tipTip.css" />

<script type="text/javascript" src="./scripts/notesystem.2.0.0.js"></script>

<script type="text/javascript" src="./scripts/basic_notes.2.0.0.js"></script>

<script type="text/javascript">

var loadfromDrive = function(){
	Basicnoteapp.DateSynchronizer.showDataFromDrive();
};

var toTop = function(){
	location.replace("./");
};

var toLogout = function(){
	location.replace("./logout.do");
};

var LoginwithGoogleDrive = function(){
	location.replace("./authgoogledrive.do?");
};
var saveToDrive = function(){
	Basicnoteapp.DateSynchronizer.saveDataToDrive();
};

jQuery.extend({
    stringify : function stringify(obj) {
        var t = typeof (obj);
        if (t != "object" || obj === null) {
            // simple data type
            if (t == "string") obj = '"' + obj + '"';
            return String(obj);
        } else {
            // recurse array or object
            var n, v, json = [], arr = (obj && obj.constructor == Array);
 
            for (n in obj) {
                v = obj[n];
                t = typeof(v);
                if (obj.hasOwnProperty(n)) {
                    if (t == "string") v = '"' + v + '"'; else if (t == "object" && v !== null) v = jQuery.stringify(v);
                    json.push((arr ? "" : '"' + n + '":') + String(v));
                }
            }
            return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
        }
    }
});

$(document).ready( function(){
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
	
	//ToolTip  - depend on jquery.tipTip.js
	$(".tooltip").tipTip({maxWidth: "auto", edgeOffset: 10});
	
	//initial load
	loadfromDrive();
});
	
</script>

<!-- メニューリンク押下時処理 -->
<script type="text/javascript" language="javascript">


</script>

</head>

<body id="base" onselectstart="return false;">

<!-- 付箋削除ダイアログ-->
<div id="specialdeletenotedlg" title="Delete Note" style="display : none;">
付箋を削除しますか。<br/>
</div>

<!-- INFOダイアログ-->
<div id="informationdlg" title="Info" style="display : none;">
</div>

<!-- 掲示板ベース -->
<div id="boardflame">
	<!-- 掲示板 -->
	<div class="specialboard" 
		id="myboard_001"
		 style=" background: url(./images/background/back-0008.gif); width: 6000px; height: 3000px;">
	</div>

	<!-- ガイドウィンドウ -->
	<div id="guidewindow" style="display : none;">
	</div>
</div>


<div class="menuboard">
    <!-- メニュー -->
    <div id="toplogo">
    	<a style="text-decoration: none;" href="./">
		<div onclick="toTop()">
		<img id="logo-img1" 
			src="./images/logo_1.png" 
			border="0"
			alt="Fusen Note" />
		</div>
		<div><img id="logo-img2"  
			src="./images/logo_2.png" 
			border="0"
			alt="Online Fusen Application" />
		</div>
		</a>
	</div>
<%
if(login_flg == true){

%>
	<div id="account">
		<span><%=email %></span>
		<ul id="account-menu">
			<li onclick="toLogout()">
				<img id="user_icon" border="0" src="./images/icon/logout_icon_grey.png" style="height:20px; vertical-align:middle;">
            	<span style="color:#778899;">Logout</span>
			</li>
		</ul>
	</div>
	
<%
}
else{
%>	
	<div id="account">
		<span onclick="LoginwithGoogleDrive()">ログイン</span>
	</div>
<%
}
%>	
    <!--メニューバー -->
    <ul id="menu">
        
        <li id="menu_newnote" class="menuicon">
        <div   class="tooltip" title="付箋を新規作成する"  style="text-decoration: none;" >
            <img id="newnote_icon" border="0" src="./images/icon/newnote_icon.png" style="height: 40px; margin:0 auto; vertical-align:middle;" >
        	<span style="color:#778899;">Create Fusen</span>
 		</div>  
        </li>

        <li id="menu_load" class="menuicon" style="<%=display %>" onclick="saveToDrive()">
		<div  href="" class="tooltip" title="GoogleDriveへ保存する" style="text-decoration: none;" >
            <img id="load_icon" border="0" src="./images/icon/googledrive_logo2.png" style="height: 40px; margin:0 auto; vertical-align:middle; " >
        	<span style="color:#778899;">Save to Drive</span>
        </div>
        </li>
    </ul>
    
</div>

<!-- 通信中ブロッキング -->
<div id="displaylock"><img src="./images/icon/nowloading_white.gif" /></div>


</body>
</html>