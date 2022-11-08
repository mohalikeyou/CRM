<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
	<base href = "<%=base%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function(){
		// 实现回车登录，为整个窗口绑定keydown事件
		$(window).on("keydown", function (e) {
			if (e.keyCode == 13) {
				$("#login").click()
			}
		})
		$("#login").on("click", function(){
			var loginAct = $.trim($("#loginAct").val())
			var loginPwd = $.trim($("#loginPwd").val())
			var isRemembered = $("#isRemembered").prop("checked");

			// 表单验证
			if (loginAct =="") {
				alert("账号不能为空")
				return
			} else if(loginPwd == "") {
				alert("密码不能为空")
				return
			}

			$.ajax({
				url : "settings/qx/user/login.do",
				data : {
					"loginAct" : loginAct,
					"loginPwd" : loginPwd,
					"isRemembered" : isRemembered
				},
				type : "post",
				dataType : "json",
				beforeSend : function () { // ajax请求发送前的操作，该函数返回True，代表继续发送，否则不发送请求：本意可用来进行表单验证
					$("#msg").text("正在努力验证中.........")
					return true
				},
				success: function (resp) {
					if (resp.code == "0") {
						$("#msg").text(resp.message)
					}else {
						window.location.href = "workbench/index.do"
					}
				}

			})

		})
	})
</script>
</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2019&nbsp;动力节点</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="settings/qx/user/login.do" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" value="${cookie.loginAct.value}" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd" value="${cookie.loginPwd.value}">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						<label>
							<c:if test="${not empty cookie.loginAct.value and not empty cookie.loginPwd.value}">
								<input id = "isRemembered" type="checkbox" checked>
							</c:if>
							<c:if test="${empty cookie.loginAct.value or empty cookie.loginPwd.value}">
								<input id = "isRemembered" type="checkbox">
							</c:if>
							十天内免登录
						</label>
						&nbsp;&nbsp;
						<span id="msg" style="color: red"></span>
					</div>
					<button type="button" id="login" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>