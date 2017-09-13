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
           <td class="form">标题</td>
           <td class="form">
               <input class="easyui-textbox" name="title" type="text">
           </td>
           <td class="form">日期</td>
           <td class="form">
               <input class="easyui-datebox filter" name="beginCreateTime" type="text">
                            至
               <input class="easyui-datebox filter" name="endCreateTime" type="text">
           </td>
           <td class="form" colspan="7">
               <a id="query" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
           </td>
           <c:forEach var="func" items="${ROLE_FUNCS}">
               <td><a id="${func.code}" class="easyui-linkbutton" data-options="iconCls:'${func.icon}'">${func.funcName}</a></td>
           </c:forEach>
		</tr>
	</table>
    </form>
</div>
<table id="dg" class="easyui-datagrid" toolbar="#tbr"
    data-options="url:'${urlPath}information/page.do',
                  pagination:true,
                  pagePosition:'bottom',
                  pageNumber:1,
                  pageSize:20,
                  pageList:[10,20,50,100],
                  singleSelect:true,
                  multiSort:true,
                  rownumbers: true,
                  fit:true">
    <thead>
        <tr>
            <th data-options="field:'adminId', width:'6%', align:'center'">adminId</th>
            <th data-options="field:'browserNumber', width:'6%', align:'center'">browserNumber</th>
            <th data-options="field:'category', width:'6%', align:'center'">category</th>
            <th data-options="field:'content', width:'6%', align:'center'">content</th>
            <th data-options="field:'createTime', width:'6%', align:'center'">createTime</th>
            <th data-options="field:'id', width:'6%', align:'center'">id</th>
            <th data-options="field:'isTop', width:'6%', align:'center'">isTop</th>
            <th data-options="field:'pointNumber', width:'6%', align:'center'">pointNumber</th>
            <th data-options="field:'remark', width:'6%', align:'center'">remark</th>
            <th data-options="field:'status', width:'6%', align:'center'">status</th>
            <th data-options="field:'thumbnailUrl', width:'6%', align:'center'">thumbnailUrl</th>
            <th data-options="field:'title', width:'6%', align:'center'">title</th>
            <th data-options="field:'topTime', width:'6%', align:'center'">topTime</th>
            <th data-options="field:'updateTime', width:'6%', align:'center'">updateTime</th>
            <th data-options="field:'url', width:'6%', align:'center'">url</th>
        	</tr>
    </thead>
</table>

<script src="${jsPath}information/index.js"></script>

</body>
</html>