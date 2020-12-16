package com.spring.groupware.yongjin.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.groupware.common.common.Sha256;
import com.spring.groupware.common.common.YongjinUtil;
import com.spring.groupware.yongjin.model.EmployeeVO;
import com.spring.groupware.yongjin.service.InterMainService;

@Controller
public class MainController {
	
	@Autowired
	private InterMainService service;

	
	// === 로그인 페이지 요청하기 === //
	@RequestMapping(value = "/login.os")
	public ModelAndView login(HttpServletRequest request, ModelAndView mav) {
		
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
		
		if(loginemp == null) {
			mav.setViewName("login");
		} else {
			mav.setViewName("home.tiles1");
		}// end of if(loginemp != null) {}---------------------
		
		return mav;
	}// end of public ModelAndView login(ModelAndView mav) {}-----------------------
	
	
	// === 로그인 처리하기 === //
	@RequestMapping(value = "/loginEnd.os", method = {RequestMethod.POST})
	public ModelAndView loginEnd(HttpServletRequest request, ModelAndView mav) {
		String emp_no = request.getParameter("emp_no");
		String emp_pwd = request.getParameter("emp_pwd");
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("emp_no", emp_no);
		paraMap.put("emp_pwd", Sha256.encrypt(emp_pwd));
		
		EmployeeVO loginemp = service.loginEnd(paraMap);
		
		if(loginemp == null) {
			String message = "없는 사원번호이거나, 잘못된 비밀번호입니다.";
			String loc = "javascript:history.back()";
			
			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
		} else {
			
			if(loginemp.getStatus() == 0) {
				// 퇴사한 사원이 로그인한 경우
				String message = "이미 퇴사한 사원입니다.";
				String loc = "javascript:history.back()";
				
				mav.addObject("message", message);
				mav.addObject("loc", loc);
				mav.setViewName("msg");
			} else {
				HttpSession session = request.getSession();
				session.setAttribute("loginemp", loginemp);
				
				mav.setViewName("home.tiles1");
			}// end of if(loginemp.getStatus() == 0) {}--------------------------
			
		}// end of if(loginemp == null) {}------------------------
		
		return mav;
	}// end of public ModelAndView loginEnd(ModelAndView mav) {}-----------------------
	
	
	// === 비밀번호 찾기 페이지 요청하기 === //
	@RequestMapping(value = "/pwdFind.os")
	public String pwdFind() {
		return "pwdFind";
	}// end of public String pwdFind() {}-----------------------
	
	
	// === 비밀번호 찾기 결과 보여주기 === //
	@ResponseBody
	@RequestMapping(value = "/pwdFindEnd.os", method = {RequestMethod.POST}, produces = "text/plain;charset=UTF-8")
	public String ajax_pwdFindEnd(HttpServletRequest request) {
		String emp_no = request.getParameter("emp_no");
		String email = request.getParameter("email");
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("emp_no", emp_no);
		paraMap.put("email", email);
		
		int count = service.pwdFindEnd(paraMap);
		boolean sendMailSuccess = true;  // 메일이 정상적으로 전송되었는지 확인하기 위한 용도이다.
		
		if(count == 1) {
			// 비밀번호 찾기에서 입력받은 정보와 일치하는 사원이 있는 경우
			sendMailSuccess = YongjinUtil.sendCertificationCode(email, request);
		}// end of if(count == 1) {}-------------------------
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("emp_no", emp_no);
		jsonObj.put("email", email);
		jsonObj.put("count", count);
		jsonObj.put("sendMailSuccess", sendMailSuccess);
		
		return jsonObj.toString();
	}// end of public String ajax_pwdFindEnd(HttpServletRequest request) {}-------------------------
	
	
	// === 인증키 확인하기 === //
	@RequestMapping(value = "/verifyCertification.os", method = {RequestMethod.POST})
	public ModelAndView verifyCertification(HttpServletRequest request, ModelAndView mav) {
		
		String emp_no = request.getParameter("emp_no");
		String email = request.getParameter("email");
		String empCertificationCode = request.getParameter("empCertificationCode");
		
		HttpSession session = request.getSession();
		String certificationCode = (String)session.getAttribute("certificationCode");
		
		String message = "";
		String loc = "";
		
		if(certificationCode.equals(empCertificationCode)) {
			
			// 임시 비밀번호 생성
			String certPassword = YongjinUtil.generateCertPassword(email, request);
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("emp_no", emp_no);
			paraMap.put("certPassword", certPassword);
			
			// 임시 비밀번호를 DB 에 업데이트
			int result = service.certPassword(paraMap);
			
			if(result == 1) {
				message = "인증되었습니다.";
				loc = request.getContextPath()+"/pwdCertificationEnd.os";
			} else {
				message = "임시 비밀번호 발급에 실패하였습니다.";
				loc = request.getContextPath()+"/pwdFind.os";
			}// end of if(result == 1) {}-----------------------
			
		} else {
			message = "인증번호가 일치하지 않습니다.";
			loc = request.getContextPath()+"/pwdFind.os";
		}// end of if(certificationCode.equals(empCertificationCode)) {}-----------------------
		
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		mav.setViewName("msg");
		
		// 세션에 저장된 인증키 삭제하기
		session.removeAttribute("certificationCode");
		
		return mav;
	}// end of public ModelAndView verifyCertification() {}-----------------------
	
	
	// === 임시 비밀번호 발급 완료 페이지 요청하기 === //
	@RequestMapping(value = "/pwdCertificationEnd.os")
	public String pwdCertificationEnd() {
		return "pwdCertificationEnd";
	}// end of public String pwdCertificationEnd() {}-----------------------
	
}