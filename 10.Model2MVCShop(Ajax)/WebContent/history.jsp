<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page contentType="text/html; charset=EUC-KR" %>

<html>
<head>

<title>열어본 상품 보기</title>

<script src="http://code.jquery.com/jquery-2.1.4.min.js"></script>
<script type="text/javascript">
	$(function(){
		$('span[name="showProd"]').on("click", function(){
			var prodNo = $(this).data("param1");
			$(opener.parent.frames["rightFrame"].location).attr("href","/product/getProduct?prodNo="+prodNo+"&menu=search");
		})
	})
</script>

</head>
<body>
	당신이 열어본 상품을 알고 있다
<br>
<br>
<%
	request.setCharacterEncoding("euc-kr");
	response.setCharacterEncoding("euc-kr");
	String history = null;
	Cookie[] cookies = request.getCookies();
	if (cookies!=null && cookies.length > 0) {
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equals("history")) {
				history = cookie.getValue();
			}
		}
		if (history != null) {
			String[] h = history.split(",");
			for (int i = 0; i < h.length; i++) {
				if (!h[i].equals("null")) {
%>
<%-- <a href="/product/getProduct?prodNo=<%=h[i]%>&menu=search"
	target="rightFrame"><%=h[i]%></a> --%>
	<span name="showProd" data-param1="<%=h[i]%>"><%=h[i]%></span>
<br>
<%
				}
			}
		}
	}
%>

</body>
</html>