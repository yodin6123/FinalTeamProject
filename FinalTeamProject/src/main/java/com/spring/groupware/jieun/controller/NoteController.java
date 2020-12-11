package com.spring.groupware.jieun.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.groupware.jieun.service.InterNoteService;

@Component

@Controller
public class NoteController {
	
	/*
	@Autowired
	private InterNoteService service;
	*/
	
	// === 메인페이지 요청 === //
	@RequestMapping(value="/main.os")
	public ModelAndView main(ModelAndView mav) {
		
		
		mav.setViewName("main_test.tiles1");
		// /WEB-INF/views/tiles1/main_test.jsp 파일 생성한다. 
		
		return mav;
	}	
	
	// === 쪽지함의 쪽지 쓰기 폼페이지 요청 === //
	@RequestMapping(value="/jieun/note/write.os")
	public ModelAndView write(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 쪽지 쓰기 폼을 띄우자
		mav.setViewName("jieun/note/write.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/write.jsp 파일
		
		return mav;
	}	
	
	// === 보낸쪽지함 페이지 요청 === // 
	@RequestMapping(value="/jieun/note/sendList.os")
	public ModelAndView sendList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/note/sendList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/sendList.jsp 파일
		
		return mav;
	}		
	
	// === 받은쪽지함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/receiveList.os")
	public ModelAndView receiveList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/note/receiveList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/receiveList.jsp 파일
		
		return mav;
	}		

	// === 중요쪽지함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/importantList.os")
	public ModelAndView importantList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/note/importantList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/importantList.jsp 파일
		
		return mav;
	}	
	
	// === 예약쪽지함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/reservationList.os")
	public ModelAndView reservationList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/note/reservationList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/reservationList.jsp 파일
		
		return mav;
	}
	
	// === 임시보관함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/tempList.os")
	public ModelAndView tempList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/note/tempList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/importantList.jsp 파일
		
		return mav;
	}	
	
	// === 휴지통 페이지 요청 === //
	@RequestMapping(value="/jieun/note/trash.os")
	public ModelAndView trash(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/note/trash.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/trash.jsp 파일
		
		return mav;
	}	
	
	// === 휴지통 비우기 페이지 요청 === //
	@RequestMapping(value="/jieun/note/trashClear.os")
	public ModelAndView trashClear(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/note/trashClear.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/trashClear.jsp 파일
		// 나중에 지울 파일, String message, String loc로 alert 띄울예정
		
		return mav;
	}		
	
	
}

