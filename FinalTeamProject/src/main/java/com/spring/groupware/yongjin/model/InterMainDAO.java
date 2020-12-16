package com.spring.groupware.yongjin.model;

import java.util.Map;

public interface InterMainDAO {

	// 로그인 처리하기
	EmployeeVO loginEnd(Map<String, String> paraMap);

	// 비밀번호 찾기 결과 보여주기
	int pwdFindEnd(Map<String, String> paraMap);

	// 임시 비밀번호를 DB 에 업데이트
	int certPassword(Map<String, String> paraMap);

}
