<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/WEB-INF/jsp/common.jsp" />
</head>
<body>
<div id="tbr">
    <form id="frmQuery">
		<table class="form">
		   <tr>
	           <td class="form">
	               <a id="query" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
	           </td>
	           <c:forEach var="func" items="${ROLE_FUNCS}">
	               <td class="form"><a id="${func.code}" class="easyui-linkbutton" data-options="iconCls:'${func.icon}'">${func.funcName}</a></td>
	           </c:forEach>
			</tr>
		</table>
    </form>
</div>
<table id="dg" class="easyui-datagrid" toolbar="#tbr"
    data-options="url:'${urlPath}volunteerHelp/page.do',
                  pagination:true,
                  pagePosition:'bottom',
                  pageNumber:1,
                  pageSize:20,
                  pageList:[10,20,50,100],
                  singleSelect:true,
                  fit:true">
    <thead>
        <tr>
            <th data-options="field:'address', width:'100', halign: 'center', align:'left'">详细地址</th>
            <th data-options="field:'city', width:'100', halign: 'center', align:'left'">市</th>
            <th data-options="field:'createTime', width:'100', halign: 'center', align:'left'">创建时间</th>
            <th data-options="field:'description', width:'100', halign: 'center', align:'left'">简介</th>
            <th data-options="field:'latitude', width:'100', halign: 'center', align:'left'">纬度</th>
            <th data-options="field:'linkName', width:'100', halign: 'center', align:'left'">联系人</th>
            <th data-options="field:'linkPhone', width:'100', halign: 'center', align:'left'">联系电话</th>
            <th data-options="field:'longitude', width:'100', halign: 'center', align:'left'">经度</th>
            <th data-options="field:'planDate', width:'100', halign: 'center', align:'left'">计划日期</th>
            <th data-options="field:'profession', width:'100', halign: 'center', align:'left'">服务类型</th>
            <th data-options="field:'publisher', width:'100', halign: 'center', align:'left'">发布者</th>
            <th data-options="field:'serviceId', width:'100', halign: 'center', align:'left'">服务Id</th>
            <th data-options="field:'serviceTime', width:'100', halign: 'center', align:'left'">服务时间</th>
            <th data-options="field:'taskIntegral', width:'100', halign: 'center', align:'left'">任务积分</th>
            <th data-options="field:'updateTime', width:'100', halign: 'center', align:'left'">更新时间</th>
        	</tr>
    </thead>
</table>

<script src="${jsPath}volunteerHelp/index.js"></script>

</body>
</html>