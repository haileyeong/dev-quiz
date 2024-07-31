<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css"> 
<c:if test="${empty loginMember}">
    <%@ include file="/WEB-INF/jsp/include/header_bf.jspf" %>
</c:if>

<c:if test="${not empty loginMember}">
    <%@ include file="/WEB-INF/jsp/include/header_af.jspf" %>
</c:if>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>커뮤니티 즐겨찾기 게시글 목록 : 오송민</title>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<style>
	#container { margin: auto; }
	h1, h3, p { text-align: center; }
	
	table { width: 1000px; border-collapse: collapse; }

	.center { text-align: center; }
	
	.border-none, .border-none td { border: none; }
	
	.left-community { 
 		width: 180px;
		/*float: left;
		position: relative;	 */
		margin: 0px 10px 20px 10px;
	}
	
	.right-community { 
		width: 1000px;
		float: center;
		position: relative;	
		margin: 0 auto;
	}
	
	table td {
	  padding: 8px;
	  text-align: left;
	}
	
	table td:nth-child(1) { width: 10%; text-align: center; }
	table td:nth-child(2) { width: 12%; text-align: center; }
	table td:nth-child(3) { width: 40%; }
	table td:nth-child(4) { width: 13%; text-align: center; }
	table td:nth-child(5) { width: 15%; text-align: center; }
	table td:nth-child(6) { width: 10%; text-align: center; }
	
	nav {
    	display: flex;
    	justify-content: center;
  	}
  	
  	.aBlack { 
	  color: black;
	}	
	
	.aBlack:visited {
	  color: black;
	}
	
	.highlight,
	.theadColor {
		color: #587CB2; 
	}
	
	.heart-icon {
		color: #EE634D; 
	} 
	
	#writeBtn {
        background-color: #EE634D;
        border: none;
        float: right;
    }

    #writeBtn:hover {
        background-color: #E73116;
        border: none;
        float: right;
    }
    
  	#getListBtn {
        background-color: #3D5A80;
        border: none;
    }

    #getListBtn:hover {
        background-color: #587CB2;
        border: none;
    }
    
    #boldBlue {
    	border: 1px solid #3D5A80; 
    	margin: 10px 0; 
    }
  	
  	#aLinkColor {
    	color: #3D5A80; 
	}

	#activePage {
    	background-color: #3D5A80; 
   		border-color: #3D5A80;
    	color: #fff; 
	}

	.list-group-item.list-group-item-action.active {
	    background-color: #3D5A80;
	    color: #fff; 
	    border-color: #3D5A80; 
	}
	
	.table-hover {
		border-bottom: solid 0.5px;
		border-color: #DEE2E6;
	}
	
	.sidenav {
      	background-color: #F5F5F5;
      	width: 250px;
      	text-align: center;
    }
	
</style>
</head>
<script> 
	
	
</script>

<body>
<div id="container">
	<nav class="col-2 sidenav" style="float: left;">
		<div class="left-community">
			<br>
			<c:if test="${loginMember != null}">
				<a href="get_community_list_by_bookmark?memberIdx=${loginMember.memberIdx }" class="list-group-item list-group-item-action active">즐겨찾기</a>
			</c:if>
			<c:forEach var="cate" items="${communityCate }">
				<a href="get_community_list_by_cate?cateIdx=${cate.cateIdx }" class="list-group-item list-group-item-action">${cate.cateName }</a>
			</c:forEach>
			<br>
		</div>
	</nav>
	
	<div class="right-community">
		<br>		
        <div>
        	<i class="bi bi-heart-fill heart-icon"></i><span class="highlight"> 즐겨찾기</span>
        </div>
        
        <hr id="boldBlue"><br>
        
        <table class="table table-hover">
		  	<thead class="theadColor">
		    	<tr>
			      	<td>번호</td>
			      	<td>카테고리</td>
			     	<td>제목</td>
			      	<td>작성자</td>
			      	<td>작성일</td>
			      	<td>조회수</td>
		    	</tr>
		  	</thead>
		  	<tbody>
		  		<c:forEach var="community" items="${communityPagingList }">
			    	<tr>
			      		<td>${community.boardIdx }</td>
			      		<td>${community.cateName }</td>
						<td>
							<a href="get_community?boardIdx=${community.boardIdx }&page=${page }" class="aBlack">${community.title }&nbsp;</a>
							<!-- 구현 안되면 삭제 -->
							<c:if test="${community.boardOri != null}"><i class="bi bi-card-image"></i></c:if>
						</td>
						<td>${community.nickname }</td>
						<td>${community.regDate }</td>
						<td>${community.hit }</td>
			    	</tr>
		    	</c:forEach>
		    	<c:if test="${empty communityPagingList }">
					<tr>
						<td colspan="6" class="center">즐겨찾기 게시글이 없습니다</td>
					</tr>
				</c:if>
		  	</tbody>
		</table>
	    
	    <br><hr id="boldBlue">
	    
	    <div>
			<a class="btn btn-primary" href="get_community_list_by_cate"  id="getListBtn" role="button">
				<i class="bi bi-list"> </i>전체 목록
			</a>
			
			<c:if test="${loginMember != null}">
				<a class="btn btn-primary" href="insert_community_page" id="writeBtn" role="button">
					<i class="bi bi-pencil-square"> </i>글 쓰기
				</a>
			</c:if>	
	    </div>
	
		<div><!-- 검색을 위한 폼 -->
			<form id="searchCommunity" action="get_community_list_by_keyword" method="get">
				<table class="border-none">
					<tr>
						<td>
							<select name="searchCondition">
							<c:forEach var="option" items="${conditionMap }">
								<option value="${option.value }">${option.key }</option>
							</c:forEach>
							</select>
							<input type="text" name="searchKeyword">
							<input type="submit" value="검색">
						</td>
					</tr>
				</table>
			</form>
		</div>
		
		<nav aria-label="pagingNav">
		  	<ul class="pagination">
		  		<c:choose>
		        <%-- 이전 버튼 누르면 이전 블록의 첫번째 페이지로 이동 --%>
		        <c:when test="${paging.page <= paging.blockLimit}">
		        	<li class="page-item disabled">
		     	 		<span class="page-link">이전</span>
		    		</li>
		        </c:when>
		        <c:otherwise>
		        	<li class="page-item">
		        		<a class="page-link" id="aLinkColor" href="get_community_list_by_bookmark?page=${paging.blockPreStartPage}&memberIdx=${loginMember.memberIdx }">이전</a>
			    	</li>
		        </c:otherwise>
		    	</c:choose>
		    	
		    	<%-- 번호 누르면 해당 페이지로 이동 --%>
			    <c:forEach begin="${paging.startPage}" end="${paging.endPage}" var="i" step="1">
			        <c:choose>
			            <%-- 요청한 페이지에 있는 경우 현재 페이지 번호는 텍스트만 보이게 --%>
			            <c:when test="${i eq paging.page}">
			            	<li class="page-item active">
			                	<span class="page-link" id="activePage">${i}</span>
			                </li>
			            </c:when>
			
			            <c:otherwise>
			            	<li class="page-item">
			                	<a class="page-link" id="aLinkColor" href="get_community_list_by_bookmark?page=${i}&memberIdx=${loginMember.memberIdx }">${i}</a>
		                	</li>
			            </c:otherwise>
			        </c:choose>
			    </c:forEach>
			    
			    <%-- 다음 버튼 누르면 다음 블록의 첫번째 페이지로 이동 --%>
		    	<c:choose>
			        <c:when test="${paging.blockNextStartPage > paging.maxPage}">
			        	<li class="page-item disabled">
			            	<span class="page-link">다음</span>
		            	</li>
			        </c:when>
			        <c:otherwise>
			        	<li class="page-item">
			                <a class="page-link" id="aLinkColor" href="get_community_list_by_bookmark?page=${paging.blockNextStartPage}&memberIdx=${loginMember.memberIdx }">다음</a>
		                </li>
			        </c:otherwise>
			    </c:choose>
		  	</ul>
		</nav>
	</div>	
</div>
<br><br><br><br><br><br><br><br><br><br><br><br>
</body>
<%@ include file="/WEB-INF/jsp/include/include-footer.jspf"%>
</html>