package com.devquiz.biz.service;

import java.util.List;

import com.devquiz.biz.model.BookmarkVO;
import com.devquiz.biz.model.CommunityPageVO;
import com.devquiz.biz.model.CommunityVO;

public interface BookmarkService {

	void addBookmark(BookmarkVO vo);//북마크 등록
	int getBookmarkStatus(BookmarkVO vo); //북마크 여부 조회
	//List<BookmarkVO> getBookmarkList(BookmarkVO vo);//북마크 목록 조회
	void deleteBookmark(BookmarkVO vo);//북마크 삭제
	List<CommunityVO> getCommunityPagingListByBookmark(int memberIdx, int page);
	CommunityPageVO communityPagingParamByBookmark(int memberIdx, int page);
	
}
