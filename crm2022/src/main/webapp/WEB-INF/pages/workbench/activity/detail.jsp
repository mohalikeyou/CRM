<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});

		// 显示编辑和删除按钮（鼠标悬停事件），改造一下，给动态添加的备注也使用悬停事件！所以使用on的方式

		$("#remarkParentDiv").on("mouseover", ".remarkDiv", function () {
			$(this).children("div").children("div").show();
		})
		// $(".remarkDiv").on("mouseover", function () {
		// 	$(this).children("div").children("div").show();
		// })
		// $(".remarkDiv").mouseover(function(){
		// 	$(this).children("div").children("div").show();
		// });

		$("#remarkParentDiv").on("mouseout", ".remarkDiv", function () {
			$(this).children("div").children("div").hide();
		})
		// $(".remarkDiv").on("mouseout", function () {
		// 	$(this).children("div").children("div").hide();
		// })
		// $(".remarkDiv").mouseout(function(){
		// 	$(this).children("div").children("div").hide();
		// });

		$("#remarkParentDiv").on("mouseout", ".myHref", function () {
			$(this).children("span").css("color","red");
		})
		// $(".myHref").on("mouseover", function () {
		// 	$(this).children("span").css("color","red");
		// })
		// $(".myHref").mouseover(function(){
		// 	$(this).children("span").css("color","red");
		// });
		$("#remarkParentDiv").on("mouseout", ".myHref", function () {
			$(this).children("span").css("color","#E6E6E6");
		})
		// $(".myHref").on("mouseout", function () {
		// 	$(this).children("span").css("color","#E6E6E6");
		// })
		// $(".myHref").mouseout(function(){
		// 	$(this).children("span").css("color","#E6E6E6");
		// });

		// 为保存活动备注添加单击事件
		$("#saveActivityRemarkBtn").on("click", function () {
			var noteContent = $("#remark").val().trim();
			var activityId = "${activity.id}";

			if (noteContent == "") {
				alert("请输出备注内容！")
				return
			}
			$.ajax({
				url: "workbench/activity/saveActivityRemark.do",
				data: {
					noteContent : noteContent,
					activityId : activityId
				},
				type: "post",
				dataType: "json",
				success: function (data) {
					if (data.code == "0") {
						alert(data.message)
					} else {
						$("#remark").val("") // 清空输入框
						// 刷新备注列表
						var htmlStr="";
						htmlStr += "<div id=\"div_"+data.retData.id+"\" class=\"remarkDiv\" style=\"height: 60px;\">";
						htmlStr += "<img title=\"${sessionScope.sessionUser.name}\" src=\"image/user-thumbnail.png\" style=\"width: 30px; height:30px;\">";
						htmlStr += "<div style=\"position: relative; top: -40px; left: 40px;\" >";
						htmlStr += "<h5>"+data.retData.noteContent+"</h5>";
						htmlStr += "<font color=\"gray\">市场活动</font> <font color=\"gray\">-</font> <b>${activity.name}</b> <small style=\"color: gray;\"> "+data.retData.createTime+" 由${sessionScope.sessionUser.name}创建</small>";
						htmlStr += "<div style=\"position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;\">";
						htmlStr += "<a class=\"myHref\" name=\"editA\" remarkId=\""+data.retData.id+"\" href=\"javascript:void(0);\"><span class=\"glyphicon glyphicon-edit\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
						htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;";
						htmlStr += "<a class=\"myHref\" name=\"deleteA\" remarkId=\""+data.retData.id+"\" href=\"javascript:void(0);\"><span class=\"glyphicon glyphicon-remove\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
						htmlStr += "</div>";
						htmlStr += "</div>";
						htmlStr += "</div>";
						$("#remarkDiv").before(htmlStr)
					}
				}
			})
		})

		// 为所有备注的删除按钮都添加删除事件（由于有些备注是动态的，所以要使用on函数！）
		$("#remarkParentDiv").on("click", "a[name='deleteA']", function () {
			var id = $(this).attr("remarkId")

			$.ajax({
				url: "workbench/activity/deleteActivityRemarkById.do",
				data: {
					id : id
				},
				type: "post",
				dataType: "json",
				success: function (data) {
					if (data.code == "0") {
						alert(data.message)
 					} else {
						$("#div_" + id).remove();
					}
				}
			})
		})

		// 为每个活动备注的修改按钮添加单击事件(同样存在动态窗口)
		$("#remarkParentDiv").on("click", "a[name='editA']", function () {
			// 收集要显示的内容noteContent，以及在隐藏域中保存一个备注id;
			var id = $(this).attr("remarkId");
			$("#remarkId").val(id)
			var noteContent = $("#div_" + id + " h5").text();
			$("#noteContent").val(noteContent)
			$("#editRemarkModal").modal("show")
		})

		// 为修改模态窗口中的更新按钮添加单击事件
		$("#updateRemarkBtn").on("click", function () {
			var id = $("#remarkId").val();
			var noteContent = $("#noteContent").val();
			if (noteContent == "") {
				alert("备注内容不能为空")
				return
			}

			$.ajax({
				url: "workbench/activity/editActivityRemarkById.do",
				data: {
					id: id,
					noteContent: noteContent
				},
				type: "post",
				dataType: "json",
				success: function (data) {
					if (data.code == "0") {
						alert(data.message)
					} else {
						$("#editRemarkModal").modal("hide")
						$("#div_" + id + " h5").text(data.retData.noteContent)
						$("#div_" + id + " small").text(" " + data.retData.editTime + " 由 ${sessionScope.sessionUser.name} 修改")
					}
				}
			})
		})
	})
	
</script>

</head>
<body>
	
	<!-- 修改市场活动备注的模态窗口 -->
	<div class="modal fade" id="editRemarkModal" role="dialog">
		<%-- 备注的id --%>
		<input type="hidden" id="remarkId">
        <div class="modal-dialog" role="document" style="width: 40%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">修改备注</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="noteContent" class="col-sm-2 control-label">内容</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="noteContent"></textarea>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateRemarkBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

    

	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>市场活动-${activity.name} <small>${activity.startDate} ~ ${activity.endDate}</small></h3>
		</div>
		
	</div>
	
	<br/>
	<br/>
	<br/>

	<!-- 详细信息 -->
	<div style="position: relative; top: -70px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${activity.name}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>

		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">开始日期</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.startDate}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">结束日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${activity.endDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">成本</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.cost}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${activity.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${activity.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${activity.editBy == null ? activity.createBy : activity.editBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${activity.editTime == null ? activity.createTime : activity.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${activity.description}
<%--					市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等--%>
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 30px; left: 40px;" id="remarkParentDiv">
		<div class="page-header">
			<h4>备注</h4>
		</div>

		<c:forEach items="${activityRemarks}" var="ar">
			<div class="remarkDiv" style="height: 60px;" id="div_${ar.id}">
				<img title="${ar.createBy}" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
				<div style="position: relative; top: -40px; left: 40px;" >
					<h5>${ar.noteContent}</h5>
					<font color="gray">市场活动</font> <font color="gray">-</font> <b>${activity.name}</b> <small style="color: gray;"> ${ar.editFlag == "1" ? ar.editTime : ar.createTime} 由 ${ar.editFlag == "1" ? ar.editBy : ar.createBy} ${ar.editFlag == "1" ? "修改" : "创建"}</small>
					<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
						<a class="myHref" name="editA" remarkId="${ar.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<a class="myHref" name="deleteA" remarkId="${ar.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
					</div>
				</div>
			</div>
		</c:forEach>
<%--		<c:forEach items="${activityRemarks}" var="remark">--%>
<%--			<div id="div_${remark.id}" class="remarkDiv" style="height: 60px;">--%>
<%--				<img title="${remark.createBy}" src="image/user-thumbnail.png" style="width: 30px; height:30px;">--%>
<%--				<div style="position: relative; top: -40px; left: 40px;" >--%>
<%--					<h5>${remark.noteContent}</h5>--%>
<%--					<font color="gray">市场活动</font> <font color="gray">-</font> <b>${activity.name}</b> <small style="color: gray;"> ${remark.editFlag=='1'?remark.editTime:remark.createTime} 由${remark.editFlag=='1'?remark.editBy:remark.createBy}${remark.editFlag=='1'?'修改':'创建'}</small>--%>
<%--					<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">--%>
<%--						<a class="myHref" name="editA" remarkId="${remark.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--						&nbsp;&nbsp;&nbsp;&nbsp;--%>
<%--						<a class="myHref" name="deleteA" remarkId="${remark.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--					</div>--%>
<%--				</div>--%>
<%--			</div>--%>
<%--		</c:forEach>--%>
		
<%--		<!-- 备注1 -->--%>
<%--		<div class="remarkDiv" style="height: 60px;">--%>
<%--			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">--%>
<%--			<div style="position: relative; top: -40px; left: 40px;" >--%>
<%--				<h5>哎呦！</h5>--%>
<%--				<font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>--%>
<%--				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--					&nbsp;&nbsp;&nbsp;&nbsp;--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--				</div>--%>
<%--			</div>--%>
<%--		</div>--%>
<%--		--%>
<%--		<!-- 备注2 -->--%>
<%--		<div class="remarkDiv" style="height: 60px;">--%>
<%--			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">--%>
<%--			<div style="position: relative; top: -40px; left: 40px;" >--%>
<%--				<h5>呵呵！</h5>--%>
<%--				<font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>--%>
<%--				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--					&nbsp;&nbsp;&nbsp;&nbsp;--%>
<%--					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>--%>
<%--				</div>--%>
<%--			</div>--%>
<%--		</div>--%>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary " id="saveActivityRemarkBtn">保存</button>
				</p>
			</form>
		</div>
	</div>
	<div style="height: 200px;"></div>
</body>
</html>