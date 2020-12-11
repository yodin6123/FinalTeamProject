<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% String ctxPath = request.getContextPath(); %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>   

<style type="text/css">

   table, th, td, input, textarea {border: solid black 1px;}
   
   #table {border-collapse: collapse;
          width: 1050px;
          }
   #table th, #table td{padding: 5px;}
   #table th{ background-color: #6F808A; color: white;}
   #table td{ }
   .long {width: 470px;}
   .short {width: 120px;}
   
   .buttonList {
   		display: inline-block;
   		list-style-type: none;
   		margin-right: 15px;
   }
   #buttonGroup {
   		padding: 0px;
   }
   
   button {
   		border-radius: 3px !important;
   }
   /*input file의 기본 모습을 제거*/
   #note_filename_upload {
	    width: 0.1px;
		height: 0.1px;
		opacity: 0;
		overflow: hidden;
		position: absolute;
		z-index: -1;   
   }
   
   #note_filename_upload + label {
	    border: 1px solid gray; /*1px solid #d9e1e8;*/
	    background-color: #fff;
	    color: black; /*#2b90d9;*/
	    padding: 6px 12px 0px 12px;
	    font-weight: 400;
	    font-size: 14px;
	    box-shadow: 1px 2px 3px 0px #f2f2f2;
	    outline: none;
	    margin-left: 10px;
	    margin-bottom: 0px;
	    height: 30px;
   }
 
   #note_filename_upload:focus + label,
   #note_filename_upload + label:hover {
    	cursor: pointer;
   }
   
   /* named upload */ 
   .upload-name { 
   		display: inline-block; 
   		padding: .5em .75em;
   		font-size: inherit; 
   		font-family: inherit; 
   		line-height: normal; 
   		vertical-align: middle; 
   		background-color: #f5f5f5; 
   		border: 1px solid #ebebeb; 
   		border-bottom-color: #e2e2e2; 
   		-webkit-appearance: none; /* 네이티브 외형 감추기 */ 
   		-moz-appearance: none; 
   		appearance: none; 
   		height: 30px;
   		margin-bottom: .30em;
   		width: 250px;
   	}


</style>

<script src="<%= request.getContextPath()%>/resources/plugins/bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript">
  $(document).ready(function () {
  	
	  	// 선택된  파일에서 파일명만 추출
		var fileTarget = $('#note_filename_upload'); 
		fileTarget.on('change', function(){ // 값이 변경되면 
		
			alert("파일 선택 완료");	
			if(window.FileReader){ // modern browser 
				var filename = $(this)[0].files[0].name;  // 첫번째 파일명
			} 
			else { // old IE 
				var filename = $(this).val().split('/').pop().split('\\').pop(); // 파일명만 추출 
			} // 추출한 파일명 삽입 
			
			$(this).siblings('.upload-name').val(filename);  
	    });   
		
		// 다시쓰기 버튼 이벤트
		$("#reset").click(function(){
			
			var inputTextVal = $("input[type=text]").val("");
			var textareaVal = $("textarea#note_content").val("");

			if( $("input[type=checkbox]").is(":checked") == true ) {
				// 체크 되어 있다면
				$("input[type=checkbox]").prop("checked", false);
			}
			
			$('.upload-name').val("선택된 파일이 없습니다.");  
			
			
		}); 
		
		// 작성 중 새로고침이나 뒤로가기 버튼 눌렀을때 alert 띄우기 
		var checkUnload = true;
	    $(window).on("beforeunload", function(){
	        if(checkUnload) return "이 페이지를 벗어나면 작성된 내용은 저장되지 않습니다.";
	    });
	    
	    // 쓰기 버튼을 클릭해서 글을 저장한 후 페이지를 이동할때도 저런 메시지가 뜨기 때문에, 고럴땐 checkUnload 값을 false 로 바꿔준 후 submit 이나 페이지를 이동해야 한다~
		
	    // 보내기 버튼 클릭 이벤트
	    $("button#send").click(function(){
	    	
	    	checkUnload = false;
	    	
	    	var frm = document.noteWriteFrm;
	    	frm.method = "GET";
	    	frm.action = "<%= ctxPath%>/jieun/note/writeEnd.action";
	    	frm.submit();
	    	
	    });
	    

		
  });
</script>

<div class="row bg-title">
	<div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
	    <h4 class="page-title" style="color:#233B49;">쪽지 쓰기</h4>
	</div>
	
</div>

<div class="row" style="padding-left: 7%;">
 <div style="width: 1100px; " >  
   <div class="white-box">
 <!--  
 <h3 style="font-weight: bold; margin-bottom: 20px;">쪽지쓰기</h3>
 --> 	

 <ul id="buttonGroup">
 	<li class="buttonList">
 		<button type="button" id="send" class="btn btn-primary" style="background-color:#2C5CA9; border: 0px; width: 100px;" onclick="goSend();">
 			<i class="fa fa-send-o fa-fw" aria-hidden="true"></i>
 		보내기</button>
 	</li>
 	<li class="buttonList">
 		
 		<button type="button" id="tempSave" class="btn btn-primary" style="background-color:#2C5CA9; border: 0px; width: 110px;" onclick="goTmepSave();">
 			<i class="fa fa-pencil-square-o fa-fw" aria-hidden="true"></i>
 		임시저장</button>
 	</li>
 	<li class="buttonList">
 		<button type="button" id="reset" class="btn waves-effect waves-light btn-info" style=" border: 0px; width: 110px;" onclick="goReset();">
 			<i class="fa fa-refresh fa-fw" aria-hidden="true"></i>
 		다시쓰기</button>
 	</li>  	
 </ul>
 
 <!-- 
 <div class="form-group">
      <label class="col-sm-12">File upload</label>
      <div class="col-sm-12">
      	<div class="fileinput fileinput-new input-group" data-provides="fileinput">
      		<div class="form-control" data-trigger="fileinput"> <i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
      		<span class="input-group-addon btn btn-default btn-file"> <span class="fileinput-new">Select file</span> <span class="fileinput-exists">Change</span>
      		<input type="file" name="...">
      		</span> <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a> 
        </div>
      </div>
 </div>
 -->
 
 
 <form name="noteWriteFrm" style="margin-top:20px;"> 
      <table id="table">
         <tr>
            <th width="13%">받는사람</th>
            <td width="85%">
               <input type="text" name="fk_emp_no" id="fk_emp_no" style="width:85%; margin-left:10px; margin-right: 4%;" />
               <button type="button" id="empList" class="btn btn-primary" style="background-color:#2C5CA9; border: 0px;" >주소록</button>       
            </td>
         </tr>
         <tr>
            <th width="13%">
            	 <span style="margin-right: 36px;">제목</span>
           		 <input type="checkbox" name="note_important" id="note_important" />&nbsp;&nbsp;중요!
            </th>
            <td width="85%">
               <input type="text" name="note_title" id="note_title" style="width:97%; margin-left:10px; margin-top: 3px; margin-bottom: 3px;" />       
            </td>
         </tr>

         <tr>
            <th width="13%">첨부파일</th>
            <td width="85%" style="padding-top : 9px;">
	               <input type="file" name="note_filename" id="note_filename_upload" style="width:97%; margin-left:10px;" />
	               <label for='note_filename_upload'>
	               <i class="fa fa-file-o"></i>&nbsp;파일선택</label>
	               <input class="upload-name" value="선택된 파일이 없습니다." disabled="disabled" />
	               
            </td>
         </tr>          
      </table>
      <!-- 
         <tr>
            <th width="13%">내용</th> 
            <td>
               <textarea rows="20" cols="100" style="width: 97%; height: 412px; margin-left:10px;" name="note_content" id="note_content"></textarea>       
            </td>
         </tr>
       -->
       
       <br>
       
       <table style="border: 0;">
         <tr style="border: 0;">
            <td width="1050px;" style="border: 0;">
               <textarea rows="20" cols="100" style=" width: 1050px; height: 412px;" name="note_content" id="note_content"></textarea>       
            </td>
         </tr>       
       </table>
       
         
   </form>
   
   <ul id="buttonGroup" style="margin-top:20px;">
   	 <li class="buttonList">
 		<button type="button" id="reservation" class="btn btn-primary" style="background-color:#2C5CA9; border: 0px; width: 110px;" onclick="goReset();">
 			<i class="fa fa-clock-o fa-fw" aria-hidden="true"></i>
 		예약발송</button>
 	 </li> 
   </ul>
   
   </div>
   </div>
</div>    
