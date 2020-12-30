<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<% String ctxPath = request.getContextPath(); %>

<%-- 지은 추가한 부분 시작--%>
<script src="<%= request.getContextPath()%>/resources/plugins/bower_components/jquery/dist/jquery.min.js"></script>	
<script type="text/javascript">
	
	function goTrashClear() {
		// 휴지통 비우기 기능
		
		location.href = "<%= ctxPath%>/groupware/jieun/note/trashClear.os";
	}

</script>	
<%-- 지은 추가한 부분 끝--%>

        <!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top m-b-0">
            <div class="navbar-header"> <a class="navbar-toggle hidden-sm hidden-md hidden-lg "
                    href="javascript:void(0)" data-toggle="collapse" data-target=".navbar-collapse"><i
                        class="fa fa-bars"></i></a>
                <div class="top-left-part"><a class="logo" href="index.html"><b><img
                                src="../plugins/images/pixeladmin-logo.png" alt="home" /></b><span
                            class="hidden-xs"><img src="../plugins/images/pixeladmin-text.png" alt="home" /></span></a>
                </div>
                <ul class="nav navbar-top-links navbar-left m-l-20 hidden-xs">
                    <li>
                        <form role="search" class="app-search hidden-xs">
                            <input type="text" placeholder="Search..." class="form-control"> <a href=""><i
                                    class="fa fa-search"></i></a>
                        </form>
                    </li>
                </ul>
                <ul class="nav navbar-top-links navbar-right pull-right">
                    <li>
                        <a class="profile-pic" href="#"> <img src="../plugins/images/users/varun.jpg" alt="user-img"
                                width="36" class="img-circle"><b class="hidden-xs">Steave</b> </a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-header -->
            <!-- /.navbar-top-links -->
            <!-- /.navbar-static-side -->
        </nav>
        <!-- Left navbar-header -->
        <div class="navbar-default sidebar" role="navigation">
            <div class="sidebar-nav navbar-collapse slimscrollsidebar">
                <ul class="nav" id="side-menu">
                    <li style="padding: 10px 0 0;">
                        <a href="index.html" class="waves-effect"><i class="fa fa-clock-o fa-fw"
                                aria-hidden="true"></i><span class="hide-menu">Dashboard</span></a>
                    </li>
                    <li>
                        <a href="profile.html" class="waves-effect"><i class="fa fa-user fa-fw"
                                aria-hidden="true"></i><span class="hide-menu">Profile</span></a>
                    </li>
                    <li>
                        <a href="basic-table.html" class="waves-effect"><i class="fa fa-table fa-fw"
                                aria-hidden="true"></i><span class="hide-menu">Basic Table</span></a>
                    </li>
                    <li>
                        <a href="fontawesome.html" class="waves-effect"><i class="fa fa-font fa-fw"
                                aria-hidden="true"></i><span class="hide-menu">Icons</span></a>
                    </li>
                    <li>
                        <a href="map-google.html" class="waves-effect"><i class="fa fa-globe fa-fw"
                                aria-hidden="true"></i><span class="hide-menu">Google Map</span></a>
                    </li>
                    <li>
                        <a href="blank.html" class="waves-effect"><i class="fa fa-columns fa-fw"
                                aria-hidden="true"></i><span class="hide-menu">Blank Page</span></a>
                    </li>
                    <%-- 지은 header 추가 시작 --%>
                    <li>
                        <a href="blank.html" class="waves-effect" id="note">
	                        <i class="fa fa-envelope-o fa-fw" aria-hidden="true"></i>
	                        <span class="hide-menu">쪽지</span>
	                        <span class="fa arrow"></span>
                        </a>			         	
                        <ul class="nav nav-third-level">
			            	<li><a href="/groupware/jieun/note/write.os">쪽지쓰기</a></li>
			            	<li><a href="/groupware/jieun/note/sendList.os">보낸쪽지함</a></li>
			            	<li><a href="/groupware/jieun/note/receiveList.os">받은쪽지함</a></li>
			            	<li><a href="/groupware/jieun/note/importantList.os">중요쪽지함</a></li>
			            	<li><a href="/groupware/jieun/note/tempList.os">임시보관함</a></li>
			            	<li><a href="/groupware/jieun/note/reservationList.os">예약쪽지함</a></li>
			            	<li>
			            		<a style="width: 130px; display:inline-block; " href="/groupware/jieun/note/trash.os">휴지통
			            		</a>
		            			<button type="button" onclick="goTrashClear();" style="width: 0px; display:inline-block; background-color: #516673; border: 0; margin-left: 40px; color:#ffffff;" data-toggle="tooltip" data-placement="top" title="비우기">
		            				<i class= "fa fa-trash-o fa-fw" aria-hidden="true"></i>
		            			</button>
			            	</li>
                        </ul>
                    </li>
                    <%-- 지은 header 추가 끝 --%> 
                    <li>
                        <a href="404.html" class="waves-effect"><i class="fa fa-info-circle fa-fw"
                                aria-hidden="true"></i><span class="hide-menu">Error 404</span></a>
                    </li>
                </ul>
                <div class="center p-20">
                    <span class="hide-menu"><a href="http://wrappixel.com/templates/pixeladmin/" target="_blank"
                            class="btn btn-danger btn-block btn-rounded waves-effect waves-light">Upgrade to
                            Pro</a></span>
                </div>
            </div>
        </div>