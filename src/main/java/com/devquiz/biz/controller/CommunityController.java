package com.devquiz.biz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import com.devquiz.biz.model.CategoryVO;
import com.devquiz.biz.model.CommentVO;
import com.devquiz.biz.model.CommunityPageVO;
import com.devquiz.biz.model.CommunityVO;
import com.devquiz.biz.model.MemberVO;
import com.devquiz.biz.service.CommunityService;
import com.devquiz.biz.service.MemberService;

@Controller
@SessionAttributes("community")
//@RequestMapping("/community") <- 안하면 잘됨 ㅠ
public class CommunityController {

	private CommunityService communityService;
	private MemberService memberService;

	public CommunityController() {
		System.out.println("===========> CommunityController() 객체 생성");
	}

	@Autowired
	public CommunityController(CommunityService communityService, MemberService memberService) {
		System.out.println("===========> CommunityController(communityService) 객체 생성");
		System.out.println("::: CommunityController communityService : " + communityService);
		this.communityService = communityService;
		this.memberService = memberService;
	}

	// @ModelAttribute 선언된 메소드는 @RequestMapping 메소드보다 먼저 실행
	@ModelAttribute("conditionMap")
	public Map<String, String> searchConditionMap() {
		System.out.println("====> Map searchConditionMap() 실행");
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("제목", "TITLE");
		conditionMap.put("내용", "CONTENT");
		return conditionMap;
	}

	//게시글 상세 보기 : 오송민
	@GetMapping("/get_community")
	public String getCommunity(@RequestParam("boardIdx") int boardIdx
			, @RequestParam(value = "page", required = false, defaultValue = "1") int page
			, @RequestParam(value = "cateIdx", required = false, defaultValue = "-1") int cateIdx
			, Model model, HttpSession session) { //이때의 vo는 boardIdx를 가지고 있을 것이다
		System.out.println(">>> 게시글 상세 보여주기");
		
		CommunityVO vo = new CommunityVO();
		vo.setBoardIdx(boardIdx);
		
		System.out.println("vo(boardIdx만 있음) : " + vo);
		
		CommunityVO community = communityService.getCommunity(vo);
		model.addAttribute("community", community);
		
		List<CommentVO> commentList = communityService.getCommentList(vo);
		model.addAttribute("commentList", commentList);
		
		List<CategoryVO> communityCate = communityService.getCommunityCate();
		model.addAttribute("communityCate", communityCate);
		
		model.addAttribute("page", page);
		
		if (cateIdx > 0) {
			model.addAttribute("cateIdx", cateIdx);
		}
		
		System.out.println("community : " + community);
		System.out.println("commentList : " + commentList);
	
		return "gaebal/community/getCommunity";
		
	}

	// 게시글 삭제 : 오송민
	@RequestMapping("/delete_community")
	public String deleteCommunity(@RequestParam(value = "page", required = false, defaultValue = "1") int page
			, @RequestParam(value = "cateIdx", required = false, defaultValue = "-1") int cateIdx
			, @RequestParam(value = "boardIdx") int boardIdx
			, CommunityVO vo, SessionStatus sessionStatus, Model model) {
		System.out.println(">>> 게시글 삭제");
		System.out.println("vo : " + vo);
		
		vo.setBoardIdx(boardIdx);
		
		communityService.deleteCommunity(vo);
		
		List<CategoryVO> communityCate = communityService.getCommunityCate();
		model.addAttribute("communityCate", communityCate);
		
		model.addAttribute("page", page);
		
		if (cateIdx > 0) {
			model.addAttribute("cateIdx", cateIdx);
		}
		
		return "redirect:get_community_list_by_cate";
	}

	//게시글 수정 페이지로 이동하기 : 오송민
	@RequestMapping("/update_community_page")
	public String updateCommunityPage(@RequestParam(value = "page", required = false, defaultValue = "1") int page
			, @RequestParam(value = "cateIdx", required = false, defaultValue = "-1") int cateIdx
			, CommunityVO vo, Model model) {
		System.out.println(">>>게시글 수정 페이지로 이동하기");
		System.out.println("vo : " + vo);
		
		CommunityVO community = communityService.getCommunity(vo);
		System.out.println("community : " + community);
		
		//치환
		String contentOri = community.getContent();
		String contentEdit = contentOri.replace("<br>", "\n");
		community.setContent(contentEdit);
		
		model.addAttribute("community", community); //Model 객체 사용 View로 데이터 전달
		
//		String selCateName = communityService.getSelCateName(community.getCateIdx());
//		System.out.println("selCateName : " + selCateName);
//		model.addAttribute("selCateName", selCateName);
		
		List<CategoryVO> communityCate = communityService.getCommunityCate();
		model.addAttribute("communityCate", communityCate);
		
		model.addAttribute("page", page);
		
		if (cateIdx > 0) {
			model.addAttribute("cateIdx", cateIdx);

		}
		
		return "gaebal/community/updateCommunityPage";
	}

	//게시글 수정 : 오송민
	@RequestMapping("/update_community")
	public String updateCommunity(@ModelAttribute("community") CommunityVO communityVO, @RequestParam(value = "page", required = false, defaultValue = "1") int page 
			, @RequestParam(value = "cateIdx", required = false, defaultValue = "-1") int cateIdx
			, Model model, HttpServletRequest request, HttpSession session) throws IllegalStateException, IOException {
		System.out.println(">>> 게시글 수정");
		System.out.println("communityVO : " + communityVO);
		
		MultipartFile uploadFile = communityVO.getUploadFile(); //게시글 작성 페이지에서 넘어온 uploadFile
		System.out.println("> uploadFile : " + uploadFile);
		
		String filename = null;
		System.out.println("filename : " + filename);
		
		if (uploadFile != null && !uploadFile.isEmpty()) {
			// 업로드된 이미지 파일의 원본 파일명
			filename = uploadFile.getOriginalFilename();
			System.out.println("::: 원본파일명 : " + filename);
			
			if (filename != null && !filename.isEmpty()) {
				communityVO.setBoardOri(filename);
			} else {
				// 파일명이 없는 경우에 대한 처리
				communityVO.setBoardOri("UnknownFilename");
			}
		}
		
		if (uploadFile == null) {
			System.out.println("::: uploadFile 파라미터가 전달되지 않은 경우");
			
		} else if (uploadFile.isEmpty()) {
			System.out.println("::: 전달받은 파일 데이터가 없는 경우");
		} else { // 업로드 파일이 존재하는 경우
			System.out.println("uploadFile.isEmpty() : " + uploadFile.isEmpty());
			
			UUID uuid = UUID.randomUUID();
		
			String savedFilename = uuid + "_" + uploadFile.getOriginalFilename();
			System.out.println("::: 저장파일명 : " + savedFilename);
			
			// 물리적 파일 복사
			String destPathFile = "C:/MyStudy/temp/" + savedFilename;
			uploadFile.transferTo(new File(destPathFile));

			// 원본 파일명을 communityVO에 설정
			communityVO.setBoardOri(filename);
			
			// 저장 파일명을 communityVO에 설정
			communityVO.setBoardFile(savedFilename);
			
		}
		
		//cateName도 바꿔주자
		String selCateName = communityService.getSelCateName(communityVO.getCateIdx());
		model.addAttribute("selCateName", selCateName);
		
		communityService.updateCommunity(communityVO);
		model.addAttribute("community", communityVO);
		
		List<CategoryVO> communityCate = communityService.getCommunityCate();
		model.addAttribute("communityCate", communityCate);
		
		model.addAttribute("page", page);
		
		
//		if (cateIdx > 0) {
//			model.addAttribute("cateIdx", cateIdx);
//		}
		
		return "gaebal/community/getCommunity";
	}

	//게시글 작성 페이지로 이동하기 : 오송민
	@RequestMapping("/insert_community_page")
	public String insertCommunityPage(@RequestParam(value = "page", required = false, defaultValue = "1") int page, Model model) {
		List<CategoryVO> communityCate = communityService.getCommunityCate();
		model.addAttribute("communityCate", communityCate);
		model.addAttribute("page", page);
		return "gaebal/community/insertCommunityPage";
	}
	
	//게시글 DB에 입력 : 오송민
	@RequestMapping("/insert_community")
	public String insertCommunity(CommunityVO communityVO, HttpServletRequest request, HttpSession session
			, RedirectAttributes redirectAttributes, Model model) throws IllegalStateException, IOException {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		communityVO.setMemberIdx(loginMember.getMemberIdx());
		
		System.out.println(">>> 게시글 입력");
		System.out.println("게시글 입력 전 communityVO : " + communityVO);
		
		MultipartFile uploadFile = communityVO.getUploadFile(); //게시글 작성 페이지에서 넘어온 uploadFile
		System.out.println("> uploadFile : " + uploadFile);
		
		String filename = null;
		System.out.println("filename : " + filename);
		
		if (uploadFile != null && !uploadFile.isEmpty()) {
			// 업로드된 이미지 파일의 원본 파일명
			filename = uploadFile.getOriginalFilename();
			System.out.println("::: 원본파일명 : " + filename);
			
			if (filename != null && !filename.isEmpty()) {
				communityVO.setBoardOri(filename);
			} else {
				// 파일명이 없는 경우에 대한 처리
				communityVO.setBoardOri("UnknownFilename");
			}
		}
		
		if (uploadFile == null) {
			System.out.println("::: uploadFile 파라미터가 전달되지 않은 경우");
		} else if (uploadFile.isEmpty()) {
			System.out.println("::: 전달받은 파일 데이터가 없는 경우");
		} else { // 업로드 파일이 존재하는 경우
			System.out.println("uploadFile.isEmpty() : " + uploadFile.isEmpty());
			
			UUID uuid = UUID.randomUUID();
		
			String savedFilename = uuid + "_" + uploadFile.getOriginalFilename();
			System.out.println("::: 저장파일명 : " + savedFilename);
			
			// 물리적 파일 복사
			String destPathFile = "C:/MyStudy/temp/" + savedFilename;
			uploadFile.transferTo(new File(destPathFile));

			// 원본 파일명을 communityVO에 설정
			communityVO.setBoardOri(filename);
			
			// 저장 파일명을 communityVO에 설정
			communityVO.setBoardFile(savedFilename);
			
		}

		communityService.insertCommunity(communityVO);
		System.out.println("글 작성 후 vo(boardIdx가 없음) : " + communityVO);
		
		//redirectAttributes.addAttribute("boardIdx", vo.getBoardIdx());
		
		//return "gaebal/community/getCommunity";
		return "redirect:get_community_list_by_cate";
			
	}

	
	//댓글 DB에 등록 : 오송민
	@RequestMapping("/insert_comment")
	public String insertComment(@RequestParam(value = "page", required = false, defaultValue = "1") int page
			, CommentVO vo, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");

		vo.setMemberIdx(loginMember.getMemberIdx());
		
		System.out.println(">>> 댓글 등록");
		System.out.println("댓글 등록 전 vo : " + vo);
		
		communityService.insertComment(vo);
		
		redirectAttributes.addAttribute("page", page);
		redirectAttributes.addAttribute("boardIdx", vo.getBoardIdx());
		
		return "redirect:get_community";
	}
	
	//댓글 삭제 : 오송민
	@RequestMapping("/delete_comment")
	@ResponseBody
	//public String deleteComment(CommentVO vo, RedirectAttributes redirectAttributes) {
	public String deleteComment(CommentVO vo, @RequestParam("commIdx") int commIdx) {
		System.out.println(">>> 댓글 삭제");
		
		vo.setCommIdx(commIdx);
		
		System.out.println("vo : " + vo);
		
		communityService.deleteComment(vo);
		
		return "success";

	}
	
	//댓글 수정 : 오송민
	@RequestMapping("/update_comment")
	@ResponseBody
	public String updateComment(@RequestParam("commIdx") int commIdx, @RequestParam("commContent") String commContent, CommentVO vo) {
		System.out.println(">>> 댓글 수정");
		
		vo.setCommIdx(commIdx);
		vo.setCommContent(commContent);
		
		communityService.updateComment(vo);
		
		System.out.println(">>> 댓글 수정 완료");
		
		return "success";
	}

	//해당 카테고리 게시글만 조회 : 오송민
	@GetMapping("/get_community_list_by_cate")
	public String paging(Model model,
	 					@RequestParam(value = "page", required = false, defaultValue = "1") int page,
	 					@RequestParam(value = "cateIdx", required = false, defaultValue = "-1") int cateIdx)  {
		System.out.println("page = " + page);
		System.out.println("cateIdx = " + cateIdx);
		
	    List<CommunityVO> communityPagingList;
	    CommunityPageVO pageVO;

	    if (cateIdx > 0) {
	        // 카테고리가 지정된 경우
	        System.out.println("page for category = " + page);
	        communityPagingList = communityService.getCommunityPagingListByCate(cateIdx, page);
	        pageVO = communityService.communityPagingParamByCate(cateIdx, page);

	        String cateName = communityService.getSelCateName(cateIdx);
	        model.addAttribute("selCateName", cateName);
	        model.addAttribute("selCateIdx", cateIdx);
	    } else {
	        // 카테고리가 지정되지 않은 경우
	        System.out.println("page = " + page);
	        communityPagingList = communityService.getCommunityPagingList(page);
	        pageVO = communityService.communityPagingParam(page);
	    }

	    System.out.println("pageVO : " + pageVO);
	    model.addAttribute("communityPagingList", communityPagingList);
	    model.addAttribute("paging", pageVO);

	    List<CategoryVO> communityCate = communityService.getCommunityCate();
	    model.addAttribute("communityCate", communityCate);
	    //model.addAttribute("page", page);
	    
	    return "gaebal/community/getCommunityListByCate";
	}


	//DB에 저장된 이미지 삭제
	@RequestMapping("/delete_community_img")
	@ResponseBody
	public String deleteCommunityImg(@RequestParam("boardIdx") int boardIdx, Model model) {
		System.out.println(">>> 이미지 삭제");

		System.out.println("boardIdx : " + boardIdx);
		
		communityService.deleteCommunityImg(boardIdx);
		
		CommunityVO vo = new CommunityVO();
		vo.setBoardIdx(boardIdx);
		
		System.out.println("vo(boardIdx만 있음) : " + vo);
		
		CommunityVO community = communityService.getCommunity(vo);
		model.addAttribute("community", community);
		
		return "success";

	}

	//검색 키워드 게시글(페이징 처리)
	@RequestMapping("/get_community_list_by_keyword")
	public String getSearchCommunityList(CommunityVO vo, Model model,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "searchCondition", required = false, defaultValue = "TITLE") String searchCondition,
			@RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword) {
		System.out.println(">>> 검색 키워드 게시글 조회");
		System.out.println("검색 키워드 vo : " + vo);
		
		System.out.println("page for keyword = " + page);
		
		List<CommunityVO> communityPagingList = communityService.getCommunityPagingListByKeyword(vo, page);
		System.out.println("키워드 검색 결과 : " + communityPagingList);
		
		
		CommunityPageVO pageVO;
		
		if (searchCondition == "TITLE") {
			pageVO = communityService.communityPagingParamByKeywordTitle(page, searchKeyword);	
		} else {
			pageVO = communityService.communityPagingParamByKeywordContent(page, searchKeyword);	
		}
		
		System.out.println("pageVO : " + pageVO);
		
	    model.addAttribute("communityPagingList", communityPagingList);
	    model.addAttribute("paging", pageVO);

	    List<CategoryVO> communityCate = communityService.getCommunityCate();
	    model.addAttribute("communityCate", communityCate);
	    
	    model.addAttribute("searchCondition", searchCondition);
	    model.addAttribute("searchKeyword", searchKeyword);
	    
	    return "gaebal/community/getCommunityListByKeyword";
	}

	// 내가 쓴 게시글 수정 페이지로 이동하기
	// (지홍)---------------------------------------------------------
	@RequestMapping("/update_my_write_page")
	public String updateMyWritePage(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			CommunityVO vo, Model model) {
		System.out.println(">>>게시글 수정 페이지로 이동하기");
		System.out.println("vo : " + vo);

		CommunityVO community = communityService.getCommunity(vo);
		System.out.println("community : " + community);

		// 치환
		String contentOri = community.getContent();
		String contentEdit = contentOri.replace("<br>", "\n");
		community.setContent(contentEdit);

		model.addAttribute("community", community); // Model 객체 사용 View로 데이터 전달

		List<CategoryVO> communityCate = communityService.getCommunityCate();
		model.addAttribute("communityCate", communityCate);

		model.addAttribute("page", page);

		return "gaebal/member/updateMyWrite";
	}

	// -----------------수정---------------지홍------------
	@RequestMapping("/update_my_write")
	public String updateMyWrite(@ModelAttribute("community") CommunityVO communityVO, Model model, HttpSession session)
			throws IllegalStateException, IOException {
		System.out.println(">>> 게시글 수정");
		System.out.println("communityVO : " + communityVO);

		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		int memberIdx = loginMember.getMemberIdx();
		List<CommunityVO> myWrite = memberService.myWrite(memberIdx);
		MultipartFile uploadFile = communityVO.getUploadFile(); // 게시글 작성 페이지에서 넘어온 uploadFile
		System.out.println("> uploadFile : " + uploadFile);

		String filename = null;
		System.out.println("filename : " + filename);

		if (uploadFile != null && !uploadFile.isEmpty()) {
			// 업로드된 이미지 파일의 원본 파일명
			filename = uploadFile.getOriginalFilename();
			System.out.println("::: 원본파일명 : " + filename);

			if (filename != null && !filename.isEmpty()) {
				communityVO.setBoardOri(filename);
			} else {
				// 파일명이 없는 경우에 대한 처리
				communityVO.setBoardOri("UnknownFilename");
			}
		}

		if (uploadFile == null) {
			System.out.println("::: uploadFile 파라미터가 전달되지 않은 경우");

		} else if (uploadFile.isEmpty()) {
			System.out.println("::: 전달받은 파일 데이터가 없는 경우");
		} else { // 업로드 파일이 존재하는 경우
			System.out.println("uploadFile.isEmpty() : " + uploadFile.isEmpty());

			UUID uuid = UUID.randomUUID();

			String savedFilename = uuid + "_" + uploadFile.getOriginalFilename();
			System.out.println("::: 저장파일명 : " + savedFilename);

			// 물리적 파일 복사
			String destPathFile = "C:/MyStudy/temp/" + savedFilename;
			uploadFile.transferTo(new File(destPathFile));

			// 원본 파일명을 communityVO에 설정
			communityVO.setBoardOri(filename);

			// 저장 파일명을 communityVO에 설정
			communityVO.setBoardFile(savedFilename);

		}

		communityService.updateCommunity(communityVO);
		model.addAttribute("community", communityVO);

		List<CategoryVO> communityCate = communityService.getCommunityCate();
		model.addAttribute("communityCate", communityCate);
		model.addAttribute("myWrite", myWrite);

		return "/gaebal/member/myWrite";
	}
}
