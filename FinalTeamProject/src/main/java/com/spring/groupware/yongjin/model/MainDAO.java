package com.spring.groupware.yongjin.model;

import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MainDAO implements InterMainDAO {
	
	@Resource
	private SqlSessionTemplate sqlsession;

	
	// === 로그인 처리하기 === //
	@Override
	public EmployeeVO loginEnd(Map<String, String> paraMap) {
		EmployeeVO empvo = sqlsession.selectOne("yongjin.loginEnd", paraMap);
		return empvo;
	}// end of public EmployeeVO loginEnd(Map<String, String> paraMap) {}-----------------------


	// === 비밀번호 찾기 결과 보여주기 === //
	@Override
	public int pwdFindEnd(Map<String, String> paraMap) {
		int count = sqlsession.selectOne("yongjin.pwdFindEnd", paraMap);
		return count;
	}// end of public boolean pwdFindEnd(Map<String, String> paraMap) {}---------------------


	// === 임시 비밀번호를 DB 에 업데이트 === //
	@Override
	public int certPassword(Map<String, String> paraMap) {
		int result = sqlsession.update("yongjin.certPassword", paraMap);
		return result;
	}// end of public int certPassword(String certPassword) {}----------------------


	// === 접속기록 테이블에 입력하기 === //
	@Override
	public int insertLoginHistory(Map<String, String> paraMap) {
		int result = sqlsession.insert("yongjin.insertLoginHistory", paraMap);
		return result;
	}// end of public int insertLoginHistory(Map<String, String> paraMap) {}---------------------

}
