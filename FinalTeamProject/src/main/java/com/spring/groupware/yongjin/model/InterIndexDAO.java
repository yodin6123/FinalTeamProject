package com.spring.groupware.yongjin.model;

import java.util.List;
import java.util.Map;

public interface InterIndexDAO {

	// 기본정보 수정하기
	int profileRevise(EmployeeVO empvo);

	// 비밀번호 변경하기
	int pwdChangeEnd(Map<String, String> paraMap);

	// 게시판 글 목록 불러오기
	List<Map<String, String>> getIntegratedBoard();

	// 일반게시판 글 목록 불러오기
	List<Map<String, String>> getGeneralBoard();

	// 받은 쪽지함, 보낸 쪽지함 목록 불러오기
	List<Map<String, String>> getReceivedAndSendNote(Map<String, String> paraMap);

	// 로그인 기록 불러오기
	List<LoginHistoryVO> getLoginHistory(String fk_emp_no);

}
