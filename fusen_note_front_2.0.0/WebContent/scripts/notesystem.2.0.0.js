/**
@fileOverview
@name notesystem.2.0.0.js
@author rmat
@version 2.0
@license
*/

/**
 * 
 * @class Basicnoteapp
 */
var Basicnoteapp = {};

/***********************************************************/
/* 定数、汎用メソッド
/***********************************************************/
/**
* 名前空間を定義・作成する
* @function namespace
* @param {String} ns_str namespace
* @return {Object} created namespace object
* @memberOf Basicnoteapp
*/
Basicnoteapp.namespace = function(ns_str){ 
    var parts = ns_str.split('.'),
          parent = Basicnoteapp,
          i;
    if(parts[0] === "Basicnoteapp"){
        parts = parts.slice(1);
    }
    
    for(i=0;i < parts.length; i += 1){
        if(typeof parent[parts[i]] === "undefined"){
            parent[parts[i]] = {};
        }
        parent = parent[parts[i]];
    }
    
    return parent;
};

Basicnoteapp.NOTE_ID_HEADER = "specialnote_";
Basicnoteapp.BOARD_ID_HEADER = "specialboard_";
Basicnoteapp.NOTECLASSNAME = "specialnote";
Basicnoteapp.NOTETEXT = "specialnote_text";
Basicnoteapp.NOTECLSBTN = "specialnote_closebtn";
Basicnoteapp.NOTELIST = "specialnote_list";

Basicnoteapp.NOTE_STATUS_ARRAY = "specialnote_note_status_array";

Basicnoteapp.SYNC_INTERVAL_TIME = 10*1000;
Basicnoteapp.MAX_NOTE_NUMBER = 50;

Basicnoteapp.GUIDECANVAS_ID = "specialguidecanvas";
Basicnoteapp.DELETENOTEDLG_ID = "specialdeletenotedlg";
Basicnoteapp.INFORMATIONDLG_ID = "informationdlg";

//note
Basicnoteapp.NOTEJSON_NOTEID = "noteid";
Basicnoteapp.NOTEJSON_ORDER = "order";
Basicnoteapp.NOTEJSON_TITLE = "title";
Basicnoteapp.NOTEJSON_CONTENT = "content";
Basicnoteapp.NOTEJSON_LEFT = "positionx";
Basicnoteapp.NOTEJSON_TOP = "positiony";
Basicnoteapp.NOTEJSON_WIDTH = "width";
Basicnoteapp.NOTEJSON_HEIGHT = "height";
Basicnoteapp.NOTEJSON_UPDATEDATE = "update_time";




/**
* 付箋IDを作成する
* @function makeNoteIDFromNo
* @param {String} note_no 番号
* @return {String} note_id 付箋ID
* @memberOf Basicnoteapp
*/
Basicnoteapp.makeNoteIDFromNo = function(note_no){
    if(note_no != null){ 
        return this.NOTE_ID_HEADER + note_no;
    }else{
        return null;
    }
};

/**
* 付箋番号を取得する
* @function getNoteNoFromID
* @param {String} note_id 付箋ID
* @return {String} note_no 付箋番号
* @memberOf Basicnoteapp
*/
Basicnoteapp.getNoteNoFromID = function(note_id){
    if(typeof note_id == "string"){
        var len = this.NOTE_ID_HEADER.length;
        return note_id.substr(len);
    }else{
        return null;
    }

};

/**
* 掲示板IDを作成する
* @function makeBoardIDFromNo
* @param {String} board_no 番号
* @return {String} board_id 掲示板ID
* @memberOf Basicnoteapp
*/
Basicnoteapp.makeBoardIDFromNo = function(board_no){
    if(board_no != null){
        return this.BOARD_ID_HEADER + board_no;
    }else{
        return null;
    }
};


/**
* 掲示板番号を取得する
* @function getBoardNoFromID
* @param {String} board_id 掲示板ID
* @return {String} board_no 掲示板番号
* @memberOf Basicnoteapp
*/
Basicnoteapp.getBoardNoFromID = function(board_id){
    if(typeof board_id == "string"){
        var len = this.BOARD_ID_HEADER.length;
        return board_id.substr(len);
    }else{
        return null;
    }
};


/***********************************************************/
/* 掲示板
/***********************************************************/
Basicnoteapp.namespace("Basicnoteapp.Board");
/**
* 掲示板クラス
* @namespace Basicnoteapp
* @class Board
* @constructor
* @param {Object} board_elem DOM要素
* @param {Number} max_note_num 作成可能最大付箋数
*/
Basicnoteapp.Board = (function(){
	
    /*public */
    Constr = function(board_elem,max_note_num){

        console.log("[Basicnoteapp.Board()][INFO]start");

        if(typeof board_elem != "object"){
            console.log("[Basicnoteapp.Board()][ERROR]board_elem type error");
            return;
        }
    	if(typeof board_elem != "object"){
            console.log("[Basicnoteapp.Board()][ERROR]max_note_num type error");
            return;
        }
        
    	/* DOM要素にBoardオブジェクトを紐付ける*/
    	board_elem.board = this;
    		
    	/**
         * 付箋DOM要素
         * @property board_element
         * @type Object
         * @public
         */
	    this.board_element = board_elem;

     	/**
          * 付箋管理要素
          * @property board_note_manager
          * @type Object
          * @public
          */
        this.board_note_manager = null;  
        this.board_note_manager = new Basicnoteapp.NoteManager(max_note_num);
  	    if(this.board_note_manager == null){
	    	console.log("[Basicnoteapp.Board()][ERROR]board_note_manager is null");
	    }
         
        /**
         * 付箋機能要素
         * @property board_note_function
         * @type Object
         * @public
         **/
        this.board_note_function = new Basicnoteapp.NoteFunction();
   	    if(this.board_note_function == null){
	    	console.log("[Basicnoteapp.Board()][ERROR]board_note_function is null");
	    }
       
   	    /**
        * データから付箋のロードをおこなう
        * @function loadNotes
        * @param {Object} note_array
        * @return {Number} 成否（0:成功 -1:失敗）
        * @public
        * @memberOf Basicnoteapp.Board
        */
        this.loadNotes = function(note_array){
            
            var board_elem = this.board_element;
        	try{
        		if(note_array == null || typeof note_array != "object"){ 		
    	    		throw new Error("note_array type error");
    	    	}
    	    	
    	    	//付箋マネージャの初期化
    	    	var result = this.board_note_manager.initialize();
    	    	if(result == 0){
    	    	    //掲示板のクリア 
    	    	    this.board_note_function.eraceNotesAll(this.board_element);
    	    	}
    	    	
    	    	for(var i = 0;i < note_array.length;i++){
    	    		//付箋番号の払い出し
    	    		var note_no = this.board_note_manager.createNote();
    	    		if(note_no >= 0){
    	    			note_array[i][Basicnoteapp.NOTEJSON_NOTEID] = Basicnoteapp.makeNoteIDFromNo(note_no);
                        //付箋DOMを生成
                        var note_elem = this.board_note_function.createNote2(note_array[i]);
                        if(note_elem  != null){
                            board_elem.appendChild(note_elem);
                        //付箋情報をローカルストレージへ格納
                        //Basicnoteapp.LocalStorageManager.writeNote(note_array[i]);
                        }
                    }
    	    	}
    	    	
    	    	return 0;
    	    }
            catch(e){
   	    		console.log("[Basicnoteapp.Board.loadNotes()][ERROR]" + e);
   	    		return -1;
   	    	}
        };
        
        /**
         * 付箋情報をjson文字列で応答する
         * @function getNotes
         * @return {String} 付箋情報json文字列
         * @public
         * @memberOf Basicnoteapp.Board
         */
        this.getNotes = function(){
        	console.log("[Basicnoteapp.Board.getNotes()][INFO]start");
        	var notes = [];
        	var notes_json = null;
        	try{
        		var note_status_array = this.board_note_manager.getNoteStatusArray();
        		for(var i=0;i < note_status_array.length; i++){
        			if(note_status_array[i].delete_flg != true){
        				var note_no = note_status_array[i].note_no;
        				//DOMの付箋情報を取得
        				var note = this.board_note_function.getNote(note_no);
        			
        				//とりあえずUPDATE_DATEには今の日付を入れておく（今後ちゃんと改善）
        				note[Basicnoteapp.NOTEJSON_UPDATEDATE] = Basicnoteapp.Util.getTime();
        				note[Basicnoteapp.NOTEJSON_ORDER] = "1";

        			notes.push(note);
        			}
        		}
        		//JSON文字列化する
        		notes_json = JSON.stringify(notes);
        	}
        	catch(e){
        		console.log("[Basicnoteapp.Board.getNotes()][ERROR]" + e);
        		throw e;
        	}
        	
        	return notes_json;
        };
        
        
   	    //ダブルクリック→付箋作成
   	    var _dblClickAction = function(e){
   	    	if(!e){ e = window.event;}
   	    	
   	    	var myboard = this.board;
   	    	try{
            
                //付箋番号の払い出し
   	    		var note_no = myboard.board_note_manager.createNote();
   	    		if(note_no >= 0){
                
                    //付箋DOMを生成
   	    			var note_elem = myboard.board_note_function.createNote(note_no,e.offsetX,e.offsetY,this);
   	    			if(note_elem  != null){
   	    			
   	    				this.appendChild(note_elem);
   	    			
   	    				//付箋情報をローカルストレージへ格納
   	    				Basicnoteapp.LocalStorageManager.writeNote(note_elem);
   	    			}
   	    		}
   	    	}catch(e){
   	    		console.log("[_dblClickAction()][ERROR]" + e);
   	    	}
   	    	
   	    };

        
        //ここからコンストラクタ
        
   	    this.board_element.ondblclick = _dblClickAction;
   	    
    };
    
    Constr.prototype = {
    	
    };
    
    return Constr;
}());


/***************************************
*付箋管理
***************************************/
Basicnoteapp.namespace("Basicnoteapp.NoteManager");

Basicnoteapp.NoteManager.MINIMAM_EMPTY_NO = "specialnote_min_empty_no";
Basicnoteapp.NoteManager.NUMBER_OF_NOTES = "specialnote_number_of_notes";
Basicnoteapp.NOTE_STATUS_ARRAY = "specialnote_note_status_array";
Basicnoteapp.NoteManager.LOCK_NOTE_FLG = "specialnote_lock_flg";

/**
* 付箋管理クラス
* @namespace Basicnoteapp
* @class NoteManager
* @constructor
*/
Basicnoteapp.NoteManager = (function(){

	Constr = function(max_note_num){

		console.log("[Basicnoteapp.NoteManager()][INFO]start");
	
        if(typeof max_note_num != "number" ){
	    	console.log("[Basicnoteapp.NoteManager()][ERROR]max_note_number type error");
	    	return;
	    }

        /**
        * 付箋状態管理ロック
        * @property _lock_flg
        * @type String
        * @private
        * @memberOf Basicnoteapp.NoteManager
        */
        var _lock_flg = "unlock";
        
        /**
        * 作成可能最大付箋数
        * @property _max_note_number
        * @type Number
        * @private
        * @memberOf Basicnoteapp.NoteManager
        */
        var _max_note_number = max_note_num;

        /**
        * 未使用付箋のうち最小番号
        * @property _min_empty_noteid
        * @type Number
        * @private
        * @memberOf Basicnoteapp.NoteManager
        */
        var _min_empty_note_number;
	
        /**
        * 現在の付箋枚数
        * @property _exist_note_number
        * @type Number
        * @private
        * @memberOf Basicnoteapp.NoteManager
        */
        var _exist_note_number;

        /**
        * 払い出し済み付箋状態オブジェクト格納配列
        * @property _note_status_array
        * @type Array
        * @private
        * @memberOf Basicnoteapp.NoteManager
        */
        var _note_status_array = null;
        
        /**
        * 出払っていない番号のうち最小のものを計算する
        * @function _calcMinEmptyNoteNumber
        * @return {Number} 最小付箋番号 エラーの場合は例外
        * @private
        * @memberOf Basicnoteapp.NoteManager
        */
        var _calcMinEmptyNoteNumber = function(note_status_array){
        	
    	    console.log("[Basicnoteapp.NoteManager._calcMinEmptyNoteNumber()][INFO]start");
 
    	    try{
    	    	if(note_status_array == null || typeof note_status_array != "object"){
    	    		
    	    		throw new Error("note_status_array type error");
    	    	}
    	    	
    	    	var str = ',';
    	    	for(var i = 0; i < note_status_array.length;i++){
    	    		str = str + note_status_array[i].note_no + ',';
    	    	}
            
    	    	for(var j = 1; j < 999; j++){
    	    		var isexist = str.indexOf(',' + j + ',',0);
    	    		if(isexist == -1){
    	    			break;
    	    		}
    	    	}
            
    	    	console.log("[Basicnoteapp.NoteManager._calcMinEmptyNoteNumber()][INFO]min empty no =" + j);
                return j;
                
    	    }catch(e){
    	    	console.log("[Basicnoteapp.NoteManager._calcMinEmptyNoteNumber()][ERROR]" + e);
    	    	throw e;
    	    }
    	    
        };

        /**
        * 現在の付箋枚数を計算する
        * @function _calcExistNoteNumber
        * @private
        * @return {Number} 現在付箋枚数 エラーの場合はnull
        * @memberOf Basicnoteapp.NoteManager
        */
        var _calcExistNoteNumber = function(note_status_array){
        	
    	    console.log("[Basicnoteapp.NoteManager._calcExistNoteNumber()][INFO]start");

    	    try{
    	    	//付箋状態配列がnullの場合は0を返す
    	    	if(note_status_array == null){
    	    		return 0;
    	    	}
    	    		
    	    	if(typeof note_status_array != "object"){	
    	    		throw new Error("note_status_array type error");
    	    	}
    	        
    	    	var cnt = 0;
    	    	for(var i = 0; i < note_status_array.length; i++){
    	    		if(note_status_array[i].delete_flg == false){
    	    			cnt = cnt + 1;
    	    		}
    	    	}
            
    	    	console.log("[Basicnoteapp.NoteManager._calcExistNoteNumber()][INFO]number of notes ="+ cnt);
                return cnt;
                
    	    }catch(e){
    	    	console.log("[Basicnoteapp.NoteManager._calcExistNoteNumber()][ERROR]" + e);
    	    	throw e;

    	    }            
        };

        /**
		 * 付箋管理配列のディープコピーを作成する
		 * @function _cloneStatusArray
		 * @param {Object} note_status_array 付箋管理配列
    	 * @return {Object} 付箋管理配列コピー 作成失敗した場合は例外
    	 * @memberOf Basicnoteapp.NoteManager
		 */
        var _cloneStatusArray = function(note_status_array){
        	
        	console.log("[Basicnoteapp.NoteManager._cloneStatusArray()][INFO]start");
        	try{

        		if(note_status_array == null || typeof note_status_array != "object"){
        			throw new Error("note_status_array type error");
        		}
        		var clonearray = [];
        		for(var i=0;i < note_status_array.length;i++){
        			var clnobj = new Basicnoteapp.NoteStatus(note_status_array[i].note_no);
        			clnobj.update_flg = note_status_array[i].update_flg;
        			clnobj.delete_flg = note_status_array[i].delete_flg;
        			clonearray.push(clnobj);
        		}
        		return clonearray;

        	}catch(e){
        		console.log("[Basicnoteapp.NoteManager._cloneStatusArray()][ERROR]" + e);
                throw e;
        	}

        };
        
        /**
		 * 付箋管理配列のディープコピーを作成する(delte_flgが立っているものはコピーしない)
		 * @function _cloneStatusArrayAfterDelete
		 * @param {Object} note_status_array 付箋管理配列
    	 * @return {Object} 付箋管理配列コピー 作成失敗した場合は例外
    	 * @memberOf Basicnoteapp.NoteManager
		 */

        var _cloneStatusArrayAfterDelete = function(note_status_array){
        	
        	console.log("[Basicnoteapp.NoteManager._cloneStatusArrayAfterDelete()][INFO]start");
        	try{

        		if(note_status_array == null || typeof note_status_array != "object"){
        			throw new Error("note_status_array type error");
        		}
        		var clonearray = [];
        		for(var i=0;i < note_status_array.length;i++){
        			if(note_status_array[i].delete_flg != true){
        			    var clnobj = new Basicnoteapp.NoteStatus(note_status_array[i].note_no);
        			    clnobj.update_flg = note_status_array[i].update_flg;
        			    clnobj.delete_flg = note_status_array[i].delete_flg;
        			    clonearray.push(clnobj);
        			}
        		}
        		return clonearray;

        	}catch(e){
        		console.log("[Basicnoteapp.NoteManager._cloneStatusArray()][ERROR]" + e);
                throw e;
        	}

        };
        
        /**
		 * 付箋管理配列を応答する(getter)
		 * @function getNoteStatusArray
    	 * @return {Object} 付箋管理配列
    	 * @memberOf Basicnoteapp.NoteManager
		 */
        this.getNoteStatusArray = function(){
        	return _note_status_array;
        };
        
        /**
         * マネージャのリセット（初期化）
         * @function initialize
         * @return {Number} 0:成功 -1:失敗
         * @memberOf Basicnoteapp.NoteManager
         */
        this.initialize = function(){
        	console.log("[Basicnoteapp.NoteManager.initialize()][INFO]start");
        	
        	//付箋データ管理をバックアップ
            var _bk_min_empty_note_number = _min_empty_note_number;
            var _bk_exist_note_number = _exist_note_number;
            var _bk_note_status_array = _cloneStatusArray(_note_status_array);
            
            try{
            	//付箋管理状態をチェックし、ロックしていなかったらロックする
            	if(_lock_flg == "unlock"){
            		_lock_flg = "lock";
            	}else{
            		console.log("[Basicnoteapp.NoteManager.createNote()][INFO]now locked");
            		return -1;
            	}
            	
                //初期化
                _exist_note_number = 0;
                _note_status_array = [];
                _min_empty_note_number = _calcExistNoteNumber(_note_status_array);

                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return 0;
                
            }catch(e){
            	console.log("[Basicnoteapp.NoteManager.createNote()][ERROR]" + e);
                
                //バックアップの戻し
                _min_empty_note_number = _bk_min_empty_note_number;
                _exist_note_number = _bk_exist_note_number;
                _note_status_array = _bk_note_status_array;
                
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return -1;
            }
        };
        
		/**
		 * 付箋作成時実行処理(付箋番号払い出し)
		 * @function createNote
		 * @return {Number} note_no (成功：払い出された付箋番号 失敗：-1)
		 * @memberOf Basicnoteapp.NoteManager
		 */
		this.createNote = function(){
        
			console.log("[Basicnoteapp.NoteManager.createNote()][INFO]start");
            //付箋データ管理をバックアップ
            var _bk_min_empty_note_number = _min_empty_note_number;
            var _bk_exist_note_number = _exist_note_number;
            var _bk_note_status_array = _cloneStatusArray(_note_status_array);		
            
            try{
            	//付箋管理状態をチェックし、ロックしていなかったらロックする
            	if(_lock_flg == "unlock"){
            		_lock_flg = "lock";
            	}else{
            		console.log("[Basicnoteapp.NoteManager.createNote()][INFO]now locked");
            		return -1;
            	}
            	
                //現在の付箋枚数と最大作成付箋枚数を比較
                if(_max_note_number <= _exist_note_number){
                	console.log("[Basicnoteapp.NoteManager.createNote()][INFO]max note number over!!");
                    throw new Error("max note number over!!");
                }
            
                //未払い出しの最小付箋番号で付箋状態クラスを作成
                var note_num = _min_empty_note_number;

                var note_status = new Basicnoteapp.NoteStatus(note_num);
                if(typeof note_status != "object" || note_status == null){
                	throw new Error("note_status type error");
                }
                                
                //付箋状態オブジェクト格納配列に追加
                _note_status_array.push(note_status);
			
                //付箋状態をローカルストレージに保存する
                if(Basicnoteapp.LocalStorageManager.isWebStorageAvailable()){
                	Basicnoteapp.LocalStorageManager.setNoteStatusArray(_note_status_array);
                }
                
                //未払い出しの最小付箋番号を再計算する
                _min_empty_note_number = _calcMinEmptyNoteNumber(_note_status_array);
			
                //現在の付箋枚数を再計算する
                _exist_note_number = _calcExistNoteNumber(_note_status_array);
			
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return note_num;
            }catch(e){
                console.log("[Basicnoteapp.NoteManager.createNote()][ERROR]" + e);
                
                //バックアップの戻し
                _min_empty_note_number = _bk_min_empty_note_number;
                _exist_note_number = _bk_exist_note_number;
                _note_status_array = _bk_note_status_array;
                
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return -1;
            }
		};


		/**
		 * 付箋削除時実行処理
		 * @function deleteNote
		 * @param {Number} note_no 削除対象付箋番号
		 * @return {Number} note_no (成功：消去した付箋番号 失敗：-1 対象なし：-2 削除済み：-3)
		 * @memberOf Basicnoteapp.NoteManager 
		 */
		this.deleteNote = function(note_no){
        
			console.log("[Basicnoteapp.NoteManager.deleteNote()][INFO]start note_no" + note_no);
			//付箋データ管理をバックアップ
            var _bk_min_empty_note_number = _min_empty_note_number;
            var _bk_exist_note_number = _exist_note_number;
            var _bk_note_status_array = _cloneStatusArray(_note_status_array);
            
            try{
            	
            	//付箋管理状態をチェックし、ロックしていなかったらロックする
            	if(_lock_flg == "unlock"){
            		_lock_flg = "lock";
            	}else{
            		console.log("[Basicnoteapp.NoteManager.deleteNote()][INFO]now locked");
            		return -1;
            	}
            	
                
                var note_status = null;
                for(var i=0;i < _note_status_array.length;i++){
                    if(_note_status_array[i].note_no == note_no){
                        note_status = _note_status_array[i];
                        break;
                    }
                }
                
                //削除対象の付箋が付箋管理されていない場合は-2を応答
                if(note_status == null){
                	
                	//付箋管理状態をアンロックする
                    _lock_flg = "unlock";
                    
                    return -2;
                }
            
                //削除済みの場合は-3を応答
                if(note_status.delete_flg){
                	
                	//付箋管理状態をアンロックする
                    _lock_flg = "unlock";
                    
                    return -3;
                }
                
                //削除済み状態へ更新する
                note_status.delete_flg = true;
                
                //付箋状態をローカルストレージに保存する
                if(Basicnoteapp.LocalStorageManager.isWebStorageAvailable()){
                	Basicnoteapp.LocalStorageManager.setNoteStatusArray(_note_status_array);
                }
                
                //現在の付箋枚数を再計算する
                _exist_note_number = _calcExistNoteNumber(_note_status_array);
			
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return note_no;
                
            }catch(e){
            	console.log("[Basicnoteapp.NoteManager.deleteNote()][ERROR]" + e);
                
                //バックアップの戻し
                _min_empty_note_number = _bk_min_empty_note_number;
                _exist_note_number = _bk_exist_note_number;
                _note_status_array = _bk_note_status_array;
                
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return -1;
            }
            
		};
		
		/**
		 * 付箋更新時実行処理
		 * @function updateNote
		 * @param {Number} note_no 更新対象付箋番号
		 * @return {Number} note_no (成功：更新した付箋番号 失敗：-1 対象なし：-2 削除済み：-3)
		 * @memberOf Basicnoteapp.NoteManager 
		 */
		this.updateNote = function(note_no){

			console.log("[Basicnoteapp.NoteManager.updateNote()][INFO]start note_no=" + note_no);
            
			//付箋データ管理をバックアップ
            var _bk_min_empty_note_number = _min_empty_note_number;
            var _bk_exist_note_number = _exist_note_number;
            var _bk_note_status_array = _cloneStatusArray(_note_status_array);
            
            try{
            	
            	//付箋管理状態をチェックし、ロックしていなかったらロックする
            	if(_lock_flg == "unlock"){
            		_lock_flg = "lock";
            	}else{
            		console.log("[Basicnoteapp.NoteManager.updateNote()][INFO]now locked");
            		return -1;
            	}
            	
                //削除対象の付箋が付箋管理されていない場合は-2を応答
                var note_status = null;
                for(var i=0;i < _note_status_array.length;i++){
                    if(_note_status_array[i].note_no == note_no){
                        note_status = _note_status_array[i];
                        break;
                    }
                }
                if(note_status == null){
                	console.log("[Basicnoteapp.NoteManager.updateNote()][ERROR]no note status object.");
                	
                	//付箋管理状態をアンロックする
                    _lock_flg = "unlock";
                    
                    return -2;
                }

                //削除済みの場合は-3を応答
                if(note_status.delete_flg){
                	console.log("[Basicnoteapp.NoteManager.updateNote()][ERROR]no note status object.");
                	
                	//付箋管理状態をアンロックする
                    _lock_flg = "unlock";
                    
                    return -3;
                }
                
                //更新済み状態へ更新する（既に更新済みとなっている場合も同様）
                note_status.update_flg = true;
               
                //付箋状態をローカルストレージに保存する
                if(Basicnoteapp.LocalStorageManager.isWebStorageAvailable()){
                	Basicnoteapp.LocalStorageManager.setNoteStatusArray(_note_status_array);
                }
                
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return note_no;
                
            }catch(e){
            	console.log("[Basicnoteapp.NoteManager.updateNote()][ERROR]"+ e);
                
                //バックアップの戻し
                _min_empty_note_number = _bk_min_empty_note_number;
                _exist_note_number = _bk_exist_note_number;
                _note_status_array = _bk_note_status_array;
                
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return -1;
            }
		};

		/**
		 * GoogleDrive反映後実行処理
		 * @function commitNotes
		 * @return {Number} 成否 (成功：0 失敗：-1 )
		 * @memberOf Basicnoteapp.NoteManager 
		 */
         this.commitNotes = function(){
         
            console.log("[Basicnoteapp.NoteManager.updateNote()][INFO]commitNotes");
            
         	//付箋データ管理をバックアップ
            var _bk_min_empty_note_number = _min_empty_note_number;
            var _bk_exist_note_number = _exist_note_number;
            var _bk_note_status_array = _cloneStatusArray(_note_status_array);
            
            try{
            	
            	//付箋管理状態をチェックし、ロックしていなかったらロックする
            	if(_lock_flg == "unlock"){
            		_lock_flg = "lock";
            	}else{
            		console.log("[Basicnoteapp.NoteManager.commitNotes()][INFO]now locked");
            		return -1;
            	}
                

                //更新成功した付箋の更新状態をリセットする
                for(var j=0;j < _note_status_array.length;j++){
                        _note_status_array[j].update_flg = false;
                }

                //削除成功した付箋を管理配列から削除する
                var clone_status_array = _cloneStatusArrayAfterDelete(_note_status_array);
                _note_status_array = clone_status_array;
                
                //付箋状態をローカルストレージに保存する
                if(Basicnoteapp.LocalStorageManager.isWebStorageAvailable()){
                	Basicnoteapp.LocalStorageManager.setNoteStatusArray(_note_status_array);
                }
                            
                //未払い出しの最小付箋番号を再計算する
                _min_empty_note_number = _calcMinEmptyNoteNumber(_note_status_array);

                //現在の付箋枚数を再計算する
                _exist_note_number = _calcExistNoteNumber(_note_status_array);

                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return 0;
                
            }catch(e){
            	console.log("[Basicnoteapp.NoteManager.commitNotes()][ERROR]" + e);
                
                //バックアップの戻し
                _min_empty_note_number = _bk_min_empty_note_number;
                _exist_note_number = _bk_exist_note_number;
                _note_status_array = _bk_note_status_array;
               
                //付箋管理状態をアンロックする
                _lock_flg = "unlock";
                
                return -1;
            }
            
         };


        //コンストラクタ処理のメインはここから(ここは例外処理はしない)
          
          _note_status_array = [];
          
          
          //未払い出し付箋番号最小値を計算
         _min_empty_note_number = _calcMinEmptyNoteNumber(_note_status_array);
         //現在の付箋枚数を計算
         _exist_note_number = _calcExistNoteNumber(_note_status_array);
         	
         	//もしどれかでもnullの場合は初期値化する
         if(_min_empty_note_number == null || _exist_note_number == null || _note_status_array == null){
         		_min_empty_note_number = 1;
             	_exist_note_number = 0;
             	_note_status_array = [];
         }
         
	};
	
	Constr.prototype = {

	};
	
	return Constr;
}());


Basicnoteapp.namespace("Basicnoteapp.NoteFunction");
/**
* 付箋機能クラス
* @namespace Basicnoteapp
* @class NoteFunction
* @constructor
*/
Basicnoteapp.NoteFunction = (function(){
	
	Constr = function(){
		
		console.log("[Basicnoteapp.NoteFunction()][INFO]start");
		
		
		/**
		 * 付箋DOM要素を作成する
		 * @function createNote
		 * @param {Number} note_no 付箋番号
		 * @param {Number} x x座標
		 * @param {Number} y y座標
    	 * @return {Object} 付箋DOM要素 （作成失敗した場合はnull）
    	 * @memberOf Basicnoteapp.NoteFunction
		 */
		this.createNote = function(note_no,x,y){
			
			console.log("[Basicnoteapp.NoteFunction.createNote()][INFO]start note_no=" + note_no + ",x=" + x + ",y=" + y);
			
			if(note_no == null || x == null || y == null){
				return null;
			}
			
			
			//付箋IDの作成
			var note_id = Basicnoteapp.makeNoteIDFromNo(note_no);
			console.log("[Basicnoteapp.NoteFunction.createNote()][INFO] note_id=" + note_id);
			
			//付箋DOM要素の生成
			var note_elem=document.createElement('div');
	        note_elem.className=Basicnoteapp.NOTECLASSNAME;
	        note_elem.setAttribute('id',note_id);
	        var div2=document.createElement('div');
	        div2.setAttribute('align','right');
	        note_elem.appendChild(div2);
	        var img1=document.createElement('img');
	        img1.className=Basicnoteapp.NOTECLSBTN;
	        img1.setAttribute('src','images/close_btn_off.png');
	        div2.appendChild(img1);
	        var textarea1=document.createElement('textarea');
	        textarea1.className=Basicnoteapp.NOTETEXT;
	        textarea1.setAttribute('id',Basicnoteapp.NOTETEXT + '_'+ note_id);
	        note_elem.appendChild(textarea1);
	        
	        note_elem.style.left = x + "px";
	        note_elem.style.top = y + "px";
	        note_elem.style.position = 'absolute';

	        //jQueryでドラッグ可能、リサイズ可能にする
	        $(note_elem).draggable({ scroll:"false",
	                           containment:"parent",
	                           stop:_dragStopAction
	                         })
	                .resizable({ stop:_resizeStopAction});
	        
	        note_elem.ondblclick = _dblclickAction;
	        note_elem.onclick = _clickAction;
	        //note.onmouseover = .mouseOverAction;
	        //note.onmouseout = .mouseOutAction;
	        note_elem.onmousedown = _mouseDownAction;
	        textarea1.onChange = _textChageAction;
	        //textarea1.onblur = .blurAction;
	         
	        img1.onmouseover = function(){this.src = 'images/close_btn_on.png'};
	        img1.onmouseout = function(){this.src = 'images/close_btn_off.png'};
	        img1.onclick = _clsbtnclickAction;
	         
	        return note_elem;
		};
		
		/**
		 * 付箋DOM要素を作成する2
		 * @function createNote2
		 * @param {Object} note 付箋情報
    	 * @return {Object} 付箋DOM要素 （作成失敗した場合はnull）
    	 * @memberOf Basicnoteapp.NoteFunction
		 */
		this.createNote2 = function(note){
			
			console.log("[Basicnoteapp.NoteFunction.createNote2()][INFO]start");
			
			if(note == null){
				console.log("[Basicnoteapp.NoteFunction.createNote2()][ERROR]arg null");
				return null;
			}
			if(note[Basicnoteapp.NOTEJSON_NOTEID] == null){
				console.log("[Basicnoteapp.NoteFunction.createNote2()][ERROR]arg.noteid is null.");
				return null;
			}
					
			if(note[Basicnoteapp.NOTEJSON_LEFT] == null){
				console.log("[Basicnoteapp.NoteFunction.createNote2()][ERROR]arg.positionx is null.");
				return null;
			}
			else{
				//px文字を削除する
				note[Basicnoteapp.NOTEJSON_LEFT] = note[Basicnoteapp.NOTEJSON_LEFT].replace("px","");
			}
			
			if(note[Basicnoteapp.NOTEJSON_TOP] == null){
				console.log("[Basicnoteapp.NoteFunction.createNote2()][ERROR]arg.positiony is null.");
				return null;
			}
			else{
				//px文字を削除する
				note[Basicnoteapp.NOTEJSON_TOP] = note[Basicnoteapp.NOTEJSON_TOP].replace("px","");
			}
			
            //付箋DOM要素の生成
			
			var note_elem=document.createElement('div');
	        note_elem.className=Basicnoteapp.NOTECLASSNAME;
	        note_elem.setAttribute('id',note[Basicnoteapp.NOTEJSON_NOTEID]);
	        var div2=document.createElement('div');
	        div2.setAttribute('align','right');
	        note_elem.appendChild(div2);
	        var img1=document.createElement('img');
	        img1.className=Basicnoteapp.NOTECLSBTN;
	        img1.setAttribute('src','images/close_btn_off.png');
	        div2.appendChild(img1);
	        var textarea1=document.createElement('textarea');
	        textarea1.className=Basicnoteapp.NOTETEXT;
	        textarea1.setAttribute('id',Basicnoteapp.NOTETEXT + '_'+ note[Basicnoteapp.NOTEJSON_NOTEID]);
	        if(note[Basicnoteapp.NOTEJSON_CONTENT] != null){
	        	textarea1.value = note[Basicnoteapp.NOTEJSON_CONTENT];
	        }
	        note_elem.appendChild(textarea1);
	        
	        note_elem.style.left = note[Basicnoteapp.NOTEJSON_LEFT] + "px";
	        note_elem.style.top = note[Basicnoteapp.NOTEJSON_TOP] + "px";
	        note_elem.style.width = note[Basicnoteapp.NOTEJSON_WIDTH];
	        note_elem.style.height = note[Basicnoteapp.NOTEJSON_HEIGHT];
	        note_elem.style.position = 'absolute';

	        //jQueryでドラッグ可能、リサイズ可能にする
	        $(note_elem).draggable({ scroll:"false",
	                            containment:"parent",
	                            stop:_dragStopAction
	                         })
	                .resizable({ stop:_resizeStopAction});
	        
	        //note.ondblclick = _dblclickAction;
	        note_elem.onclick = _clickAction;
	        //note.onmouseover = .mouseOverAction;
	        //note.onmouseout = .mouseOutAction;
	        note_elem.onmousedown = _mouseDownAction;
	        textarea1.onfocus = _textChageAction;

	         
	        img1.onmouseover = function(){this.src = 'images/close_btn_on.png'};
	        img1.onmouseout = function(){this.src = 'images/close_btn_off.png'};
	        img1.onclick = _clsbtnclickAction;
	         
	        return note_elem;
		};
		
		/**
         * 付箋DOM要素を削除する
         * @function eraceNote
         * @param {Number} note_no 付箋番号
         * @return {Number} 成否 （0:成功 -1:失敗）
         * @memberOf Basicnoteapp.NoteFunction
         */
		this.eraceNote = function(note_no){
		    
		    console.log("[Basicnoteapp.NoteFunction.eraceNote()][INFO]start: note_no=" + note_no);
		    if(note_no == null){
		        console.log("[Basicnoteapp.NoteFunction.eraceNote()][ERROR]note_no is null");
		        return -1;
		    }
		    
		    try{ 
                var noteid = Basicnoteapp.makeNoteIDFromNo(note_no);
                var note_elem = $('#'+ noteid).get(0);
                var board_elem = note_elem.parentNode;
            
               
                // DOMを削除(DOMは必ず削除をさせるので最初に実行)
                board_elem.removeChild(note_elem);
            }
            catch(e){
                console.log("[Basicnoteapp.NoteFunction.eraceNote()][ERROR]" + e);
                return -1;
            }
            
            return 0;
		};
		
		/**
         * 付箋DOM要素を削除する(全削除)
         * @function eraceNotesAll
         * @param {Object} board_elem 掲示板DOM要素
         * @return {Number} 成否 （0:成功 -1:失敗）
         * @memberOf Basicnoteapp.NoteFunction
         */
		this.eraceNotesAll = function(board_elem){
		    
		    console.log("[Basicnoteapp.NoteFunction.eraceNotesAll()][INFO]start");
		    if(board_elem == null || typeof board_elem != "object"){
		        console.log("[Basicnoteapp.NoteFunction.createNote2()][ERROR]arg null");
                return -1;
		    }
		    
		    try{
		        for (var i =board_elem.childNodes.length-1; i>=0; i--) {
                board_elem.removeChild(board_elem.childNodes[i]);
                }
		    }
		    catch(e){
		        console.log("[Basicnoteapp.NoteFunction.eraceNotesAll()][ERROR]" + e);
                return -1;
		    }
		    
		    return 0;
		};
		
		/**
         * 付箋情報を応答する
         * @function getNote
         * @param {Object} board_elem 掲示板DOM要素
         * @param {Number} note_no 付箋番号
         * @return {Object} 付箋情報object エラーの場合null
         * @memberOf Basicnoteapp.NoteFunction
         */
		this.getNote = function(note_no){
			console.log("[Basicnoteapp.NoteFunction.getNote()][INFO]start");
			if(note_no == null){
		        console.log("[Basicnoteapp.NoteFunction.getNote()][ERROR]note_no is null");
		        return null;
		    }
		    
			var rtnobj = {};
			var noteid;
		    try{ 
                noteid = Basicnoteapp.makeNoteIDFromNo(note_no);
                rtnobj[Basicnoteapp.NOTEJSON_NOTEID] = $("#" + noteid).attr("id");
                rtnobj[Basicnoteapp.NOTEJSON_ORDER] = null;
                rtnobj[Basicnoteapp.NOTEJSON_TITLE] = null;
                rtnobj[Basicnoteapp.NOTEJSON_CONTENT] = $("#" + Basicnoteapp.NOTETEXT + "_"+ noteid).val();
                console.log(rtnobj[Basicnoteapp.NOTEJSON_CONTENT]);
                rtnobj[Basicnoteapp.NOTEJSON_LEFT] = $("#" + noteid).css("left");
                rtnobj[Basicnoteapp.NOTEJSON_TOP] = $("#" + noteid).css("top");
                rtnobj[Basicnoteapp.NOTEJSON_WIDTH] = $("#" + noteid).css("width");
                rtnobj[Basicnoteapp.NOTEJSON_HEIGHT] = $("#" + noteid).css("height");
                rtnobj[Basicnoteapp.NOTEJSON_UPDATEDATE] = null;
                
            }
            catch(e){
                console.log("[Basicnoteapp.NoteFunction.getNote()][ERROR]" + e);
                return null;
            }
			
            return rtnobj;
		};
		
		
		var _clickAction = function(e) {
			e.stopPropagation();
	    };
	    
	    var _mouseDownAction = function(e){
	    	e.stopPropagation();
	    };

	    var _dblclickAction = function(e){
	    	if(!e){ e = window.event;}
            
            //ダイアログOK時に呼ばれるコールバックを定義
            var callback = function(){
                var note_elem = this;
	            var board_elem = note_elem.parentNode;
	            var board = board_elem.board;
	            
	            // DOMを削除(DOMは必ず削除をさせるので最初に実行)
	            board_elem.removeChild(note_elem);
	            
	            // ローカルストレージを削除
	            //Basicnoteapp.LocalStorageManager.removeNote(note_elem.id);
	            
	            // 付箋管理で削除処理を実行
	            board.board_note_manager.deleteNote(Basicnoteapp.getNoteNoFromID(note_elem.id));
                
                //
                $('#' + Basicnoteapp.DELETENOTEDLG_ID).dialog('close');
            };
            
            $('#' + Basicnoteapp.DELETENOTEDLG_ID).dialog({
                autoOpen: true,
                title: 'Fusen Note',
                closeOnEscape: false,
                modal: true,
                buttons: { "OK": callback}
            });
               	
	    	e.stopPropagation();
	    };
	    
	    var _dragStopAction = function(e,ui){
	    	if(!e){ e = window.event;}
	    	var note_elem = this;
            var board_elem = note_elem.parentNode;
            var board = board_elem.board;
            console.log(board_elem.id);
            
            // ローカルストレージを更新
            //Basicnoteapp.LocalStorageManager.writeNote(note_elem);
            
            //付箋情報を更新
            var note_no = Basicnoteapp.getNoteNoFromID(note_elem.id);
            board.board_note_manager.updateNote(note_no);
	    	
	    };
	  
	    var _resizeStopAction = function(e,ui){
	    	if(!e){ e = window.event;}
	    	var note_elem = this;
            var board_elem = note_elem.parentNode;
            var board = board_elem.board;
            console.log(board_elem.id);
            
            // ローカルストレージを更新
            //Basicnoteapp.LocalStorageManager.writeNote(note_elem);
            
            //付箋情報を更新
            var note_no = Basicnoteapp.getNoteNoFromID(note_elem.id);
            board.board_note_manager.updateNote(note_no);
	    	
	    };
	    
	    var _clsbtnclickAction = function(e){
	    	
	    	if(!e){ e = window.event;}
            
            //どの付箋が削除されるかを、ダイアログへ渡すため、ID属性を付与する。
            var note_elem = this.parentNode.parentNode;
            $('#' + Basicnoteapp.DELETENOTEDLG_ID).attr("note_id",note_elem.id);
            //ダイアログ初期化／表示
            $('#' + Basicnoteapp.DELETENOTEDLG_ID).dialog({
                autoOpen: true,
                title: 'Delete note?',
                closeOnEscape: false,
                modal: true,
                buttons: { 
                    "OK": function(event){
                        //ダイアログに付与された付箋IDより付箋削除処理を実施
                        var note_elem = $("#" + $('#' + Basicnoteapp.DELETENOTEDLG_ID).attr("note_id")).get(0);
                        var board_elem = note_elem.parentNode;
                        var board = board_elem.board;
                        
                        // DOMを削除(DOMは必ず削除をさせるので最初に実行)
                        board_elem.removeChild(note_elem);
	            
                        // ローカルストレージを削除
                        //Basicnoteapp.LocalStorageManager.removeNote(note_elem.id);
	            
                        // 付箋管理で削除処理を実行
                        board.board_note_manager.deleteNote(Basicnoteapp.getNoteNoFromID(note_elem.id));
                        
                        //ダイアログ消去
                        $('#' + Basicnoteapp.DELETENOTEDLG_ID).dialog('close');
                    }
                }
            });

	        e.stopPropagation();

	    };
	    
	    var _textChageAction = function(e){
	    	if(!e){ e = window.event;}
	    	var note_elem = this.parentNode;
            var board_elem = note_elem.parentNode;
            var board = board_elem.board;
            console.log(board_elem.id);
            
            // ローカルストレージを更新
            //Basicnoteapp.LocalStorageManager.writeNote(note_elem);
            
            //付箋情報を更新
            var note_no = Basicnoteapp.getNoteNoFromID(note_elem.id);
            board.board_note_manager.updateNote(note_no);
	    	
	    };
	    
	    var _focusAction = function(e) {
	        var note = this.parentNode;
	        var board = note.parentNode;
	        board.insertBefore(note, board.lastChild.nextSibling);
	    };
	     
	    var _blurAction = function() {

	    };
	    
	};
	
	Constr.prototype = function(){
		
	};
	
	return Constr;
	
}());


Basicnoteapp.namespace("Basicnoteapp.NoteStatus");
/**
* 付箋状態管理クラス
* @namespace Basicnoteapp
* @class NoteStatus
* @constructor
* @param {Number} note_no 付箋番号
*/
Basicnoteapp.NoteStatus = (function(){

    Constr = function(note_no){
    	
    	console.log("[Basicnoteapp.NoteStatus()][INFO]start note_no=" + note_no);
        /*private */
        /**
        * 更新フラグ
        * @property _update_flg
        * @type Boolean
        * @private
        * @memberOf Basicnoteapp.NoteStatus
        */
        this.update_flg = false;
	
        /**
        * 削除フラグ
        * @property _delete_flg
        * @type Boolean
        * @memberOf Basicnoteapp.NoteStatus
        */
        this.delete_flg = false;
	
        /**
        * 付箋番号
        * @property note_no
        * @type Number
        * @public
        * @memberOf Basicnoteapp.NoteStatus
        */
        this.note_no = note_no;
    
    };
    
    Constr.prototype = {
   	
    };
    
    return Constr;
}());


/**
* ローカルストレージ管理クラス
* @namespace Basicnoteapp
* @class LocalStorageManager
* @constructor
*/
Basicnoteapp.LocalStorageManager = {

     /**
   	 * WebStorage機能利用可能チェック
     * @function isWebStorageAvailable
     * @return {boolean} 使用可・不可（true：可 false：不可）
     * @memberOf Basicnoteapp.LocalStorageManager
   	 */
   	isWebStorageAvailable : function(){
   		
   		console.log("[Basicnoteapp.LocalStorageManager.isWebStorageAvailable()][INFO]start");
   		
   		if (!window.localStorage){
   			return false;
   		}
   		else{
   			return true;
   		}
   	},
   	
   	/**
   	 * 付箋登録
     * @function writeNote
     * @param {Object} 付箋DOM要素
     * @memberOf Basicnoteapp.LocalStorageManager
   	 */   	
   	writeNote : function(note_elem){
   		
    	try{
    		console.log("[Basicnoteapp.LocalStorageManager.writeNote()][INFO]start");
   		
    		if(note_elem == null){
    			throw new Error("note_elem is null");
    		}
    		var main_key = note_elem.id;
           
    		var note_text = $("#" + Basicnoteapp.NOTETEXT + "_" + main_key).get(0);
    		if(note_text == null){
    			throw new Error("failed to get note_text");
    		}
			
			var note ={};
			note[Basicnoteapp.NOTEJSON_NOTEID] = note_elem.id;
			note[Basicnoteapp.NOTEJSON_ORDER] = "";
			note[Basicnoteapp.NOTEJSON_CONTENT] = note_text.value;
			note[Basicnoteapp.NOTEJSON_TITLE] = "";
			note[Basicnoteapp.NOTEJSON_LEFT] = note_elem.style.left;
			note[Basicnoteapp.NOTEJSON_TOP] = note_elem.style.top;
			note[Basicnoteapp.NOTEJSON_WIDTH] = note_elem.style.width;
			note[Basicnoteapp.NOTEJSON_HEIGHT] = note_elem.style.height;
			note[Basicnoteapp.NOTEJSON_UPDATEDATE] = Basicnoteapp.Util.getTime();
			
			//付箋情報を登録
			console.log(JSON.stringify(note));
			localStorage[main_key] = JSON.stringify(note);
           
    	}catch(e){
    		console.log("[Basicnoteapp.LocalStorageManager.writeNote()][ERROR]" + e);
    		throw e;
    	}
   	},

   	/**
   	 * 付箋削除
   	 * @function removeNote
   	 * @param {String} 付箋ID
   	 * @memberOf Basicnoteapp.LocalStorageManager
   	 */ 
   	removeNote : function(note_id){
   		
   		console.log("[Basicnoteapp.LocalStorageManager.removeNote()][INFO]start");
   		
   		try{
   			if(note_id == null){
   				throw new Error("note_id is null");
   			}
   			var main_key = note_id;
   		
   			//付箋情報を削除
   			localStorage.removeItem(main_key);
   			
   		}catch(e){
   			console.log("[Basicnoteapp.LocalStorageManager.removeNote()][ERROR]" + e);
    		throw e;
   		}
           
   	},
   	
   	/**
   	 * 付箋選択
   	 * @function selectNote
   	 * @param {String} 付箋ID
   	 * @return {Object} 付箋情報配列 
   	 * @memberOf Basicnoteapp.LocalStorageManager
   	 */ 
   	 selectNote : function(note_id){
   		 
   		console.log("[Basicnoteapp.LocalStorageManager.selectNote()][INFO]start");
   		
   		try{
   			if(note_id == null){
   				throw new Error("note_id is null");
   			}
   			var main_key = note_id;
   			var note_array = JSON.parse(localStorage.getItem(main_key))
   		
   			return note_array;
   			
   		}catch(e){
   			console.log("[Basicnoteapp.LocalStorageManager.selectNote()][ERROR]" + e);
   			throw e;
   		}
   		
   	},
	
	/**
	 * 付箋状態オブジェクト配列取得
	 * @function getNoteStatusArray
	 * @return {Array} 付箋状態オブジェクト配列
	 * @memberOf Basicnoteapp.LocalStorageManager
	 */ 
	 getNoteStatusArray : function(){
		
		console.log("[Basicnoteapp.LocalStorageManager.getNoteStatusArray()][INFO]start");
		try{
			//JSON化して格納してあったものを配列に戻す
			return JSON.parse(localStorage.getItem(Basicnoteapp.NOTE_STATUS_ARRAY));
		}catch(e){
			console.log("[Basicnoteapp.LocalStorageManager.getNoteStatusArray()][ERROR]" + e);
			throw e;
		}
	},
	
	/**
	 * 付箋状態オブジェクト配列保存
	 * @function setNoteStatusArray
	 * @param {Array} note_status_array 付箋状態オブジェクト配列
	 * @return {Number} 0：成功 (失敗は例外)
	 * @memberOf Basicnoteapp.LocalStorageManager
	 */ 
	 setNoteStatusArray : function(note_status_array){
		
		console.log("[Basicnoteapp.LocalStorageManager.setNoteStatusArray()][INFO]start");
		try{
			if(typeof note_status_array != "object" || note_status_array == null){
				throw new Error("note_status_array type error");
			}  		
  			
  			//JSON化して格納する
  			localStorage.setItem(Basicnoteapp.NOTE_STATUS_ARRAY,JSON.stringify(note_status_array));
  			
  			return 0;
  		}catch(e){
  			console.log("[Basicnoteapp.LocalStorageManager.setNoteStatusArray()][ERROR]" + e);
  			throw e;
  		}
	}
	
};


Basicnoteapp.Util = {

	/**
	 * 日付取得 yyyymmddhh24miss
	 * @function getTime
	 * @return {String} datetime
	 * @memberOf Basicnoteapp.Util
	 */   	
    getTime : function(){
    	var date = new Date();  
		var year = date.getFullYear();  
		var month = date.getMonth() + 1;  
		var day = date.getDate();  
		var hour = date.getHours();
		var minute = date.getMinutes();
		var second = date.getSeconds();
  
		if ( month < 10 ) {month = '0' + month;}
		if ( day < 10 ) {day = '0' + day;}
		if (hour < 10) {hour = "0" + hour;}
		if (minute < 10) {minute = "0" + minute;}
		if (second < 10) {second = "0" + second;}
        //console.log(year + ',' + month + ',' + day + ',' + hour + ',' + minute + ',' + second);
		var str = year.toString(10) + month.toString(10) + day.toString(10) + hour.toString(10) + minute.toString(10) + second.toString(10);
		return str;
	},


};

