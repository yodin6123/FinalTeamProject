package com.spring.groupware.yongjin.model;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IndexDAO implements InterIndexDAO {
	
	@Resource
	private SqlSessionTemplate sqlsession;

	
	// === 기본정보 수정하기 === //
	@Override
	public int profileRevise(EmployeeVO empvo) {
		int result = sqlsession.update("yongjin.profileRevise", empvo);
		return result;
	}// end of public int profileRevise(EmployeeVO empvo) {}----------------------


	// === 비밀번호 변경하기 === //
	@Override
	public int pwdChangeEnd(Map<String, String> paraMap) {
		int result = sqlsession.update("yongjin.pwdChangeEnd", paraMap);
		return result;
	}// end of public int pwdChangeEnd(Map<String, String> paraMap) {}--------------------


	// === 게시판 글 목록 불러오기 === //
	@Override
	public List<Map<String, String>> getIntegratedBoard() {
		List<Map<String, String>> boardMap = sqlsession.selectList("yongjin.getIntegratedBoard");
		return boardMap;
	}// end of public List<Map<String, String>> getIntegratedBoard() {}----------------------


	// === 일반게시판 글 목록 불러오기 === //
	@Override
	public List<Map<String, String>> getGeneralBoard() {
		List<Map<String, String>> boardMap = sqlsession.selectList("yongjin.getGeneralBoard");
		return boardMap;
	}// end of public List<Map<String, String>> getGeneralBoard() {}-----------------------


	// === 받은 쪽지함, 보낸 쪽지함 목록 불러오기 === //
	@Override
	public List<Map<String, String>> getReceivedAndSendNote(Map<String, String> paraMap) {
		List<Map<String, String>> noteList = sqlsession.selectList("yongjin.getReceivedAndSendNote", paraMap);
		return noteList;
	}// end of public List<Map<String, String>> getReceivedAndSendNote(String fk_emp_no) {}-----------------------


	// === 로그인 기록 불러오기 === //
	@Override
	public List<LoginHistoryVO> getLoginHistory(String fk_emp_no) {
		List<LoginHistoryVO> lhList = sqlsession.selectList("yongjin.getLoginHistory", fk_emp_no);
		return lhList;
	}// end of public LoginHistoryVO getLoginHistory(String fk_emp_no) {}---------------------

}
