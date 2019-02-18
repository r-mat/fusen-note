
/***********************************************************/
/* タイマー 
/***********************************************************/

Basicnoteapp.namespace("Basicnoteapp.BoardTimer");
/**
* タイマークラス
* @namespace Basicnoteapp
* @class BoardTimer
*/
Basicnoteapp.BoardTimer = {
	
	/**
	* タイマー呼び出し時実行関数
	* @function mytimer
	* @memberOf Basicnoteapp.BoardTimer
	*/
	mytimer : function() {
		
		//サーバ同期処理
	
		//
		timerID = setTimeout("Basicnoteapp.BoardTimer.mytimer()",Basicnoteapp.SYNC_INTERVAL_TIME);

	},

	/**
	* タイマー呼び出し関数
	* @function mytimer
	* @memberOf Basicnoteapp.BoardTimer
	*/
	start : function(){
		console.log("[Basicnoteapp.BoardTimer.start()][INFO]start");
		timerID = setTimeout("Basicnoteapp.BoardTimer.mytimer()",Basicnoteapp.SYNC_INTERVAL_TIME);
	},
		
	/**
	* タイマー終了関数
	* @function mytimer
	* @memberOf Basicnoteapp.BoardTimer
	*/
	end : function() {
			
	}
};

/***********************************************************/
/* ガイドウィンドウ 
/***********************************************************/

Basicnoteapp.namespace("Basicnoteapp.GuideWindow");
/**
* ガイドウィンドウクラス
* @namespace Basicnoteapp
* @class GuideWindow
* @param {Object} guide_elem ガイドウィンドウDOM要素(div)
* @constructor
*/
Basicnoteapp.GuideWindow = (function(){
	
	Constr = function(){
	
        //ガイドウィンドウDOM要素
		var _guide_elem = null;
		var _canvas_elem = null;
		//対象フレームDOM要素
		var _box_elem = null;
		
		//対象要素の最初のスクロール位置
		var _init_scrollLeft = null;
		var _init_scrollTop = null;
		
		/**
        * ガイドウィンドウ初期化
        * @function init
        * @param {Object} guide_elem ガイドウィンドウDOM要素
        * @param {Object} box_elem 対象フレームDOM要素
        * @return {Number} 0:成功 -1:失敗
        * @public
        * @memberOf Basicnoteapp.GuideWindow
        */		
		this.init = function(guide_elem,box_elem){
			console.log("[Basicnoteapp.GuideWindow.init()][INFO]start");
			if(typeof guide_elem != "object" || typeof box_elem != "object"){
				console.log("[Basicnoteapp.GuideWindow.init()][ERROR]type error");
				return -1;
			}

			_guide_elem = guide_elem;
			_box_elem = box_elem;
			
			
			
			//canva要素の作成
			var canvas=document.createElement('canvas');
			/* canvas要素の存在チェックとCanvas未対応ブラウザの対処 */
  			if ( ! canvas || ! canvas.getContext ) {
  				console.log("[Basicnoteapp.GuideWindow.init()][ERROR]canvas not available");
    			return -1;
    		}
			canvas.id = Basicnoteapp.GUIDECANVAS_ID;
			canvas.width = 200;
			canvas.height = 100;
			_guide_elem.appendChild(canvas);

			//対象要素の最初のスクロール位置を格納（これを初期値とする）
			_init_scrollLeft = _box_elem.scrollLeft;
			_init_scrollTop = _box_elem.scrollTop;
			
			_canvas_elem = canvas;
			
			return 0;
		};

		/**
        * ガイドウィンドウ描画
        * @function draw
        * @type Object
        * @public
        * @memberOf Basicnoteapp.GuideWindow
        */		
		this.draw = function(){
		
			_guide_elem.style.left = ($("#boardflame").get(0).offsetWidth - 210) + "px";
			_guide_elem.style.top = "30px";
			 
			var ctx = _canvas_elem.getContext('2d');
	 		ctx.clearRect(0, 0, 200, 100);
	 		/* 外枠を描く */
	 		ctx.beginPath();
	 		ctx.strokeStyle = 'rgb(190, 190, 190)';
	 		ctx.fillStyle = 'rgba(20,20,20,0.5)';
	 		ctx.lineWidth = 3;
	 		ctx.fillRect(0, 0, 200, 100);
	 
	 		fx = (_box_elem.scrollLeft - _init_scrollLeft) / 6000 * 200;
	 		fy = (_box_elem.scrollTop - _init_scrollTop) / 3000 * 100;
	 		fwin_width = _box_elem.offsetWidth / 6000 * 200;
	 		fwin_height = _box_elem.offsetHeight / 3000 * 100;
	 		ctx.lineWidth = 2;
	 		ctx.strokeStyle = 'rgb(192, 192, 192)';
	 		ctx.strokeRect(fx , fy , fwin_width, fwin_height);
		};
		
		/**
        * ガイドウィンドウ表示非表示切替え
        * @property _guide_elem
        * @param {Boolean} req 表示非表示（true:表示 false:非表示）
        * @memberOf Basicnoteapp.GuideWindow
        */	
		this.show = function(req){
		
			console.log("[Basicnoteapp.GuideWindow.show()][INFO]start");
			if(_guide_elem == null || typeof req != "boolean"){
				console.log("[Basicnoteapp.GuideWindow.show()][ERROR]");
				return;
			}else{
				if(req == true){
					_guide_elem.style.display = "block";
				}else if(req == false){
					_guide_elem.style.display = "none";
				}
			}
		};
		
	};
	
	Constr.prototype = {

	    	
    };
    
    return Constr;
}());


/***********************************************************/
/* 通信時画面ブロッキング
/***********************************************************/
Basicnoteapp.namespace("Basicnoteapp.DisplayBlock");

/**
 * Ajax通信時などに画面のブロッキングをおこなう
 * @namespace Basicnoteapp
 * @class DisplayBlock
 */
Basicnoteapp.DisplayBlock = {
        
        /**
        * 画面ロック
        * @function lockWindow
        * @type Object
        * @public
        * @memberOf Basicnoteapp.DisplayBlock
        */
        lockWindow : function(){
            var $window = $(window),
            $img = $("#displaylock > img");
            $img.css({"marginTop":(($window.height() - $img.height()) / 2) + "px"});
            $("#displaylock").fadeIn();
        },
  
        /**
        * 画面アンロック
        * @function unlockWindow
        * @type Object
        * @public
        * @memberOf Basicnoteapp.DisplayBlock
        */
        unlockWindow : function(){
            $("#displaylock").fadeOut();
        }
    
};

/***********************************************************/
/* Ajax関連
/***********************************************************/

Basicnoteapp.namespace("Basicnoteapp.DateSynchronizer");
/**
* GoogleDriveからデータの読み出し表示、データ保存をおこなう
* @namespace Basicnoteapp
* @class DateSynchronizer
*/
Basicnoteapp.DateSynchronizer = {

		/**
        * データ読み出し
        * @function showDataFromDrive
        * @type Object
        * @public
        * @memberOf Basicnoteapp.DateSynchronizer
        */		
		showDataFromDrive : function(){
			console.log("[Basicnoteapp.DateSynchronizer.showDataFromDrive()][INFO]start");
            //画面ロック
            Basicnoteapp.DisplayBlock.lockWindow();
            
			//GoogleDriveからデータを読み込む
			$.ajax({
            	url:'/notedownload.do',
            	type:'GET',
            	error:function(){},
            	//取得したデータはBoardへ渡して表示処理を実行
            	success:function(data){
            		
            		//URLデコーディング
            		//data = decodeURIComponent(data);
            		
            		Basicnoteapp.DateSynchronizer.showData(data);
            		//アンロック
            		Basicnoteapp.DisplayBlock.unlockWindow();
            	},
            	//dataType:'json',
            	processData :true}
            );
            
            
		},

        
		/**
        * データ保存
        * @function saveDataToDrive
        * @type Object
        * @public
        * @memberOf Basicnoteapp.DateSynchronizer
        */		
		saveDataToDrive : function(){
			console.log("[Basicnoteapp.DateSynchronizer.saveDataToDrive()][INFO]start");
			//画面ロック
            Basicnoteapp.DisplayBlock.lockWindow();
            
            //dataを作成
            var data = Basicnoteapp.DateSynchronizer.createData();
            //URLエンコーディング
            data = encodeURIComponent(data);
            console.log("[Basicnoteapp.DateSynchronizer.saveDataToDrive()][INFO]data" + data);
            
            //GoogleDriveへデータを保存
            $.ajax({
            	url:'/noteupload.do',
            	type:'POST',
            	data:data,
            	error:function(){},
            	//取得したデータはBoardへ渡して表示処理を実行
            	success:function(data){
            		//アンロック
            		Basicnoteapp.DisplayBlock.unlockWindow();
            	},
            	//dataType:'json',
            	processData :true}
            );
		},
		
		//取得データ表示処理
		showData : function(data) {
			console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]start");
			
			//URLデコーディング
			data = decodeURIComponent(data);
			
			var obj;
			var code;
			var subcode;
			try{
				//JSON化
				obj = JSON.parse(data);
				//var obj = JSON.parse(data);

				//サーバ側エラーであった場合
				code = obj.code;
				subcode = obj.subcode;
				if(typeof code != "undefined"){
					Basicnoteapp.DisplayBlock.unlockWindow();
					switch(code){
					//more than 1 files exist
					case "01":
						console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]more than 1 files exist");
						$('#' + Basicnoteapp.INFORMATIONDLG_ID).text("2つ以上のファイルがGoogleDriveに存在します。");
						$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog({
			                autoOpen: true,
			                title: 'Error',
			                closeOnEscape: false,
			                modal: true,
			                buttons: { "OK": function(){$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog('close');}}
			            });
						break;
					//02: file not found
					case "02":
						console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]file not found");
						$('#' + Basicnoteapp.INFORMATIONDLG_ID).text("ファイルがGoogleDriveに存在しません。");
						$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog({
			                autoOpen: true,
			                title: 'Error',
			                closeOnEscape: false,
			                modal: true,
			                buttons: { "OK": function(){$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog('close');}}
			            });
						break;
					//99: error
					case "99":
						console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]error");
						switch(subcode){
						//01:login error
						case "01":
							console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]login error");
							$('#' + Basicnoteapp.INFORMATIONDLG_ID).text("ログイン状態が解除されました。");
							$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog({
				                autoOpen: true,
				                title: 'Error',
				                closeOnEscape: false,
				                modal: true,
				                buttons: { "OK": function(){$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog('close');}}
				            });
							break;
						//02: file download error
						case "02":
							console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]failed to download");
							$('#' + Basicnoteapp.INFORMATIONDLG_ID).text("ダウンロードに失敗しました。");
							$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog({
				                autoOpen: true,
				                title: 'Error',
				                closeOnEscape: false,
				                modal: true,
				                buttons: { "OK": function(){$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog('close');}}
				            });
							break;
						//03: contents check error
						case "03":
							console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]file content is invalid");
							$('#' + Basicnoteapp.INFORMATIONDLG_ID).text("ファイル内容が不正です。");
							$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog({
				                autoOpen: true,
				                title: 'Error',
				                closeOnEscape: false,
				                modal: true,
				                buttons: { "OK": function(){$('#' + Basicnoteapp.INFORMATIONDLG_ID).dialog('close');}}
				            });
							break;
						default:
							console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]errorcode ??");
							break;
						}						
						break;
					}
					console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]eaaaaaaaaaaaaa");
					return -1;
					
				}

			}
			catch(e){
				console.log("[Basicnoteapp.DateSynchronizer.showData()][ERROR]" + e);
				return -1;
			}
			
			console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]download success");
			console.log("[Basicnoteapp.DateSynchronizer.showData()][INFO]data:" + data);
			//付箋情報が正常に応答された場合
			//Boardへ処理をわたし、付箋内容を表示する
			var note_array = obj;
			if(global_board.loadNotes(note_array) != 0){
				console.log("[Basicnoteapp.DateSynchronizer.showData()][ERROR]failed to load notes");
			    return -1;
			};
			return 0;
		},
		
		//Drive保存用付箋情報文字列の作成
		createData : function(){
			console.log("[Basicnoteapp.DateSynchronizer.createData()][INFO]start");
			var notes_json = null;
			try{
				notes_json = global_board.getNotes();
			}
			catch(e){
				console.log("[Basicnoteapp.DateSynchronizer.createData()][ERROR]" + e);
				return null;
			}
			
			console.log("[Basicnoteapp.DateSynchronizer.createData()][INFO]success:" + notes_json);
			return notes_json;
		}
		
		
};

/***********************************************************/
/* 初期読み込み処理
/***********************************************************/

function addLoadEvent(func) {  
	var oldonload = window.onload;  
	if (typeof window.onload != 'function') {  
		window.onload = func;  
	} else {  
		window.onload = function() {  
			if (oldonload) {  
		        oldonload();  
			}  
			func();  
		}  
	}  
}  

addLoadEvent(function() {  

    
	//ローカルストレージ機能が利用できることをチェック
	if(Basicnoteapp.LocalStorageManager.isWebStorageAvailable()){
		//不必要なローカルデータが存在した場合は削除する
		
		//ローカルデータの整合性チェックをおこなう
		
	}else{
		
	}
	
	//掲示板DOM要素オブジェクトを取得
	var boardelem = $('.specialboard').get(0);
    if(boardelem == null){
    	console.log(boardelem);
    }
    
    //掲示板オブジェクトを作成
    global_board = new Basicnoteapp.Board(boardelem,Basicnoteapp.MAX_NOTE_NUMBER);

	
	//付箋データのロード
	//global_datasynchronizer.loadAllNotes(global_board);

	//タイマースタート

    //掲示板押下時のカーソル変更
    $(".specialboard").mousedown(function(){
    	$(this).css("cursor","url(/images/grip01.cur),default");
    });
    $(".specialboard").mouseup(function(){
    	$(this).css("cursor","default");
    });
    
    //掲示板のドラッグドロップ機能を実装
	$("#boardflame").flickable(
		{
			friction: 0.8,
			elasticConstant: 0.15,
			dragStart: function(e){
				//global_guide_window.show(true);
			},
			drag: function(e){
				global_guide_window.show(true);
				global_guide_window.draw();
			},
			dragStop: function(e){
				global_guide_window.show(false);
			}
			
		}
	
	);
	
	//ガイドウィンドウ初期化
    //ウィンドウの位置を決める
    $('#guidewindow').get(0).style.left = ($("#boardflame").get(0).offsetWidth - 210) + "px";
    $('#guidewindow').get(0).style.top = "30px";
    //ウィンドウ管理クラスのインスタンス化
	global_guide_window = new Basicnoteapp.GuideWindow();
	global_guide_window.init($('#guidewindow').get(0),$('#boardflame').get(0));
	
    //動的サブメニュー機能実装
	$(document).ready(function(){  
        $("ul#menu li").click(function() { //When trigger is clicked...  
            //Following events are applied to the subnav itself (moving subnav up and down)  
            $(this).find("ul.submenu").slideDown('fast').show(); //Drop down the subnav on click  
  
            $(this).parent().hover(function() {}, 
                                    function(){$(this).parent().find("ul.submenu").slideUp('slow');});  
  
        //Following events are applied to the trigger (Hover events for the trigger)  
        }).hover(function() { $(this).addClass("subhover");}, 
                function(){ $(this).removeClass("subhover"); });  
  
    });
});
