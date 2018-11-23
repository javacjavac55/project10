<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>        

<html><head>

<title>상품 목록조회</title>

<link rel="stylesheet" href="/css/admin.css" type="text/css">
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" >
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" >
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" ></script>
<script src="http://code.jquery.com/jquery-2.1.4.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script type="text/javascript">
<!--
function showInputField(){
	document.getElementById("minPrice").value = '';
	document.getElementById("maxPrice").value = '';
	document.getElementById("searchKeyword").value = '';
	if (document.getElementById("searchCondition").value == '1'){
		document.getElementById("priceInput").style.display="none";
		document.getElementById("searchKeyword").style.display="inline";
	}
	
	if (document.getElementById("searchCondition").value == '2'){
		document.getElementById("priceInput").style.display="inline";
		document.getElementById("searchKeyword").style.display="none";
	}
}

function checkPriceInput(){
	if (document.getElementById("searchCondition").value != '2') {
		fncGetItemList('1');
		return;
	}
	
	if (document.getElementById("minPrice").value != null && document.getElementById("minPrice").value.length>0 &&
			document.getElementById("maxPrice").value != null && document.getElementById("maxPrice").value.length>0
			&& document.getElementById("minPrice").value<=document.getElementById("maxPrice").value) {
		document.getElementById("searchKeyword").value = document.getElementById("minPrice").value+"-"+document.getElementById("maxPrice").value;
		fncGetItemList('1');
		return;
	}
	
	alert("알맞은 가격 범위를 지정해주세요");
}
-->

$(function(){
	$('form[name="detailForm"]').attr("method","post").attr("action","/product/listProduct");
	$("#filterCondition, #sortCondition").on("change" , function() {
		fncGetItemList('1');
	});
	
	$("#searchCondition").on("change" , function() {
		showInputField();
	});
	
	$("td.ct_btn01:contains('검색')").on("click" , function() {
		checkPriceInput();
	});
	
	$(".ct_list_pop td:nth-child(3)").on("click", function(){
		var prodNo = $(this).data('param1');
		var tranCode = $(this).data('param2');
		if (tranCode =='' || tranCode == null) {
			self.location = "/product/getProduct?prodNo="+prodNo+"&menu=${param.menu}";
		}
	})
	
	$(".ct_list_pop td:nth-child(3)").tooltip( {
		items:'[data-photo]',
		content: function(){
			var fileName = $(this).data('photo');
			return '<img src="/images/uploadFiles/'+fileName+'" width="300px;" height="300px;"/>';
		}
	} );
	
	var currentPage = ${resultPage.currentPage};
	var count = 9;
	$(window).scroll(function() {
	    if ($(window).scrollTop() == $(document).height() - 325) {
			$.ajax({
					url : "/product/json/listProduct",
					method : "POST",
					data : JSON.stringify({
								currentPage:currentPage+1,
								filterCondition:$("#filterCondition").val(),
								sortCondition:$("#sortCondition").val(),
								searchCondition:$("#searchCondition").val(),
								searchKeyword:$("#searchKeyword").val()
							}),
					dataType: "json",
					headers : {
						"Accept" : "application/json",
						"Content-Type" : "application/json"
					},
					success : function(JSONData, status) {						
						var list = JSONData["list"];
						currentPage++;
					
						list.forEach(function (item, index, array) {
							var tranCode = item['proTranCode']==null ? "판매중":"재고없음";
							$($(".line").last()).after("<tr class='ct_list_pop'><td align='center'>"+count+"</td><td></td>"+"<td align='left' data-param1='"+item['price']+"' data-param2='"+item['proTranCode']+"' data-photo='"+item['fileName']+"'>"+item['prodName']+"</td><td></td>"+"<td align='left'>"+item['price']+"</td><td></td>"+"<td align='left'>"+item['prodDetail']+"</td><td></td>"+"<td align='left'>"+tranCode+"</td></tr>");
							$($(".ct_list_pop").last()).after("<tr class='line'><td colspan='11' bgcolor='D6D7D6' height='1'></td></tr>");
							count++;
						});
					}
			});	
		}
	});
	
})
</script>

</head>

<body bgcolor="#ffffff" text="#000000">

<div style="width:98%; margin-left:10px;">

<form name="detailForm">

<input type="hidden" id="menu" name="menu" value="${param.menu}"/>

<table width="100%" height="37" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td width="15" height="37">
			<img src="/images/ct_ttl_img01.gif" width="15" height="37">
		</td>
		<td background="/images/ct_ttl_img02.gif" width="100%" style="padding-left:10px;">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="93%" class="ct_ttl01">상품 목록조회</td>
				</tr>
			</table>
		</td>
		<td width="12" height="37">
			<img src="/images/ct_ttl_img03.gif" width="12" height="37">
		</td>
	</tr>
</table>


<table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-top:10px;">
	<tr>
		<td align="right">
			<select id="filterCondition" name="filterCondition" class="ct_input_g" style="width:80px">
				<option value="4" ${!empty search.filterCondition && search.filterCondition == "4" ? 'selected' : ''}>모든 상품</option>
				<option value="0" ${!empty search.filterCondition && search.filterCondition == "0" ? 'selected' : ''}>판매중</option>
			</select>
			<select id="sortCondition" name="sortCondition" class="ct_input_g" style="width:100px">
				<option value="0" ${!empty search.sortCondition && search.sortCondition == "0" ? 'selected' : ''}>신상품순</option>
				<option value="1" ${!empty search.sortCondition && search.sortCondition == "1" ? 'selected' : ''}>낮은가격순</option>
				<option value="2" ${!empty search.sortCondition && search.sortCondition == "2" ? 'selected' : ''}>높은가격순</option>
			</select>
			<select id="searchCondition" name="searchCondition" id="searchCondition" class="ct_input_g" style="width:80px">
				<option value="1" ${!empty search.searchCondition && search.searchCondition == "1" ? 'selected' : ''}>상품명</option>
				<option value="2" ${!empty search.searchCondition && search.searchCondition == "2" ? 'selected' : ''}>상품가격</option>
			</select>
			<input type="text" name="searchKeyword" id="searchKeyword" value="${!empty search.searchKeyword ? search.searchKeyword : ''}" class="ct_input_g" style="width:200px; height:19px; display:${search.searchCondition == '2' ? 'none':'inline'}" />
			<div id="priceInput" style="display:${!empty search.searchKeyword && search.searchCondition == '2' ? 'inline' : 'none'}" align="right" width="210">
				<input type="text" name="minPrice" id="minPrice" value="${!empty search.searchKeyword ? fn:split(search.searchKeyword, '-')[0] : ''}" class="ct_input_g" style="width:100px; height:19px"/>
				-
				<input type="text" name="maxPrice" id="maxPrice" value="${!empty search.searchKeyword ? fn:split(search.searchKeyword, '-')[1] : ''}" class="ct_input_g" style="width:100px; height:19px"/>
			</div>
		</td>		
		<td align="right" width="70">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="17" height="23">
						<img src="/images/ct_btnbg01.gif" width="17" height="23">
					</td>
					<td background="/images/ct_btnbg02.gif" class="ct_btn01" style="padding-top:3px;">
						검색
					</td>
					<td width="14" height="23">
						<img src="/images/ct_btnbg03.gif" width="14" height="23">
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-top:10px;">
	<tr>
		<td colspan="11">전체  ${resultPage.totalCount} 건수, 현재 ${resultPage.currentPage} 페이지</td>
	</tr>
	<tr>
		<td class="ct_list_b" width="100">No</td>
		<td class="ct_line02"></td>
		<td class="ct_list_b" width="150">상품명</td>
		<td class="ct_line02"></td>
		<td class="ct_list_b" width="150">가격</td>
		<td class="ct_line02"></td>
		<td class="ct_list_b">상품상세정보</td>	
		<td class="ct_line02"></td>
		<td class="ct_list_b">현재상태</td>	
	</tr>
	<tr>
		<td colspan="11" bgcolor="808285" height="1"></td>
	</tr>
	
	<div class="row">
		<c:set var="i" value="0" />
		<c:forEach var="product" items="${list}">
			<c:set var="i" value="${i+1}" />
			<div class="col-sm-6 col-md-4">
				<div class="thumbnail">
					<img src="/images/uploadFiles/${product.fileName}" alt="Oops! Image is not ready :P" width="300px">
					<div class="caption">
						<h3>${product.prodName}</h3>
						<p>가격: ${product.price}</p>
						<p>상세정보: ${product.prodDetail}</p>
						<p>${product.proTranCode == null ? '판매중':'재고 없음'}</p>
						<p>
							<a href="#" class="btn btn-primary" role="button">Button</a> <a
								href="#" class="btn btn-default" role="button">Button</a>
						</p>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
			<%-- <tr class="ct_list_pop">
			<td align="center">${i}</td>
			<td></td>
			<td align="left" data-param1="${product.prodNo}" data-param2="${product.proTranCode}" data-photo="${product.fileName}">
				${product.prodName}
			</td>
			<td></td>
			<td align="left">${product.price}</td>
			<td></td>
			<td align="left">${product.prodDetail}</td>
			<td></td>
			<td align="left">${product.proTranCode == null ? '판매중':'재고 없음'}</td>
		</tr>
	<tr class="line">
		<td colspan="11" bgcolor="D6D7D6" height="1"></td>
	</tr> --%>	
	
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-top:10px;">
	<tr>
		<td align="center">
			<input type="hidden" id="currentPage" name="currentPage" value=""/>
			<jsp:include page="../common/pageNavigator.jsp"/>	
		</td>
	</tr>
</table>
<!--  페이지 Navigator 끝 -->

</form>

</div>

</body></html>