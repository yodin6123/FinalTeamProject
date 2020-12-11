<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
    
<% String ctxPath = request.getContextPath(); %>

<style type="text/css">

  #miniHeader {
  	 display: inline-block;
  	 
  }
  
  #trash {
  	 border: 0;
  	 background-color: white; 
  }
  
  ul#miniHeaderGroup, .miniHeaderList {
  	 list-style-type: none;
  	 display: inline-block;
  }
  
  .miniHeaderList {
  	 margin-right: 15px; 
  }
  
  #trash:hover {
  	 background-color: #EEEEEE;
  }

  #note_searchType, #searchWord, 
  #note_serachWord, #note_search,
  #btnSearch {
  	 display: inline-block;
  }
  
  #note_searchType {
  	 margin-left: 49%;
  }
  
</style>


<div class="row bg-title">
	<div class="col-lg-3 col-md-4 col-sm-4 col-xs-12">
	    <h4 class="page-title" style="color:#233B49;">휴지통</h4>
	</div>
	
    <div id="note_searchType" style="display: inline-block;">
        <select class="form-control" data-placeholder="Choose a Category" tabindex="1">
	    	<option value="note_tilte">제목</option>
	    	<option value="fk_emp_name">사원명</option>			
        </select>  
    </div>	 
    
    <div id="note_serachWord">
        <input id="searchWord" type="text" class="form-control" placeholder="John doe">
    </div>        
    
    <div class="input-group" id="note_search"> 
    	<!-- <span class="input-group-btn"> -->
        <button type="button" id="btnSearch" class="btn waves-effect waves-light btn-info" style="height: 35px;">
        	<i class="fa fa-search"></i>
        </button>
        <!-- </span> -->
    </div>		
	
</div> 

<div class="row">
    <div class="col-sm-12">
        <div class="white-box">
			<div id="miniHeader">
				<ul id="mimiHeaderGroup" style="padding: 0px; margin-bottom: -15px;">
					<li class="miniHeaderList ">
						<button type="button" id="trash">
							<i class="fa fa-trash-o fa-fw" aria-hidden="true"></i>
							삭제
						</button>					
					</li>
					<li class="miniHeaderList">
						<button type="button" id="trash" >
							<i class="fa fa-envelope-o fa-fw" aria-hidden="true"></i>
							읽음
						</button>
					</li>
					
					<li class="miniHeaderList dropdown " style="margin-left: 720px; ">
						<button type="button" id="trash" class="dropdown-toggle " data-toggle="dropdown" >
							<i class="fa fa-sort-amount-desc fa-fw" aria-hidden="true"></i>
							정렬
							<span class="fa fa-angle-down fa-fw"></span>
						</button>
					    <ul class= "dropdown-menu">
					      <li><a href="#">보낸사람</a></li>
					      <li><a href="#">제목</a></li>
					      <li><a href="#">받은날짜</a></li>
					    </ul>							
					</li>
					
					<li class="miniHeaderList">
						<button type="button" id="trash">
							<i class="fa fa-repeat fa-fw" aria-hidden="true"></i>
							새로고침
						</button>
					</li>
					<li class="miniHeaderList" style="margin-top: 5px;">
						<select class="form-control" id="sizePerPage" name="sizePerPage" style="height: 32px;">
								<option value="20" selected="selected">20</option>
								<option value="40">40</option>			
								<option value="60">60</option>
						</select>
					</li>
				</ul>
			</div>
			
            <div class="table-responsive" style="margin-top: 20px;">
                <table class="table">
                     
                    <thead>
                        <tr>
                            <th style="width: 40px;">
								<input type="checkbox">
							</th>
                            <th  style="width: 40px;">
                            	<span class="fa fa-star"></span>
                            </th>
                            <th  style="width: 40px;">
                            	<span class= "fa fa-paperclip"></span>
                            </th>
                            <th  style="width: 100px;">사원ID</th>
                            <th  style="width: 550px;">제목</th>
                            <th  style="width: 100px;">날짜</th>
                            <th  style="width: 40px;">크기</th>
                        </tr>
                    </thead>
                    
                    <tbody>
                    	<c:forEach begin="0" end="10" varStatus="loop">
	                        <tr>
	                            <td style="width: 40px;"> 
	                            	<input type="checkbox" class="check${loop.index}">
	                            </td>
	                            <td style="width: 40px;">
	                            	<span class="fa fa-star"></span>
	                            </td>
	                            <td style="width: 40px;">
	                            	<span class= "fa fa-paperclip"></span> 	
	                            </td>
	                            <td style="width: 100px;">@Genelia</td>
	                            <td style="width: 550px;">admin</td>
	                            <td style="width: 100px;">20-12-09 22:10</td>
	                            <td style="width: 40px;">25.2KB</td>
	                        </tr>                    		
                    	</c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<!-- /.row -->        