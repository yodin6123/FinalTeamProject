package com.spring.groupware.jieun.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.spring.groupware.common.common.FileManager; // 호연이
import com.spring.groupware.common.common.MyUtil;
import com.spring.groupware.jieun.model.NoteReservationTempVO;
import com.spring.groupware.jieun.model.NoteTempVO;
import com.spring.groupware.jieun.model.NoteTrashVO;
import com.spring.groupware.jieun.model.NoteVO;
import com.spring.groupware.jieun.service.InterNoteService;
import com.spring.groupware.yongjin.model.EmployeeVO;

@Component

@Controller
public class NoteController {
	
	@Autowired
	private InterNoteService service;
	
	@Autowired
	private FileManager fileManager; // bean에 올라가져 있기 때문에 Spring 컨테이너가 알아서 FileManager 클래스 객체를 알아서 fileManager에 넣어준다. 	
	
	// === 쪽지함의 쪽지 쓰기 폼페이지 요청 === //
	@ResponseBody
	@RequestMapping(value="/jieun/note/write.os", produces="text/plain; charset=UTF-8")
	public ModelAndView requiredLogin_write(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
		
		// System.out.println("로그인 사원번호 ==> " + loginemp.getEmp_no());
		
		mav.addObject("loginemp", loginemp);
		
		// 전체 리스트 조회 
		List<EmployeeVO> empAllList = service.findEmpListAll();
		mav.addObject("empAllList", empAllList);
		
		for (EmployeeVO empAll : empAllList) {
			System.out.println("조직 안누르고 전체 테스트 시작");
			System.out.println("사원이름==>" + empAll.getEmp_name());
			System.out.println("직위명==>" + empAll.getPosition_name());
			System.out.println("부서명==>" + empAll.getDept_name());
			System.out.println("사원번호==>" + empAll.getEmp_no());
			System.out.println("조직 안누르고 전체 테스트 끝");
		}
		
		// 쪽지 쓰기 폼을 띄우자 
		mav.setViewName("jieun/note/write.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/write.jsp 파일
		
		return mav;		

	}	
	
	// === 쪽지함의 쪽지쓰기의 주소록에서 조직 클릭한 경우(ajax) === //
	@ResponseBody
	@RequestMapping(value="/jieun/note/writeAddAddress.os", produces="text/plain; charset=UTF-8")
	public String writeAddAddress(HttpServletRequest request) { 
		
		String fk_dept_no = request.getParameter("fk_dept_no");
		System.out.println("모달창에서 클릭한 부서 번호 ==> " + fk_dept_no);
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("fk_dept_no", fk_dept_no);
		
		// 부서에 따른 사원조회
		List<EmployeeVO> empList = service.findEmpList(paraMap);
		
		for (EmployeeVO emp : empList) {
			System.out.println("사원이름==>" + emp.getEmp_name());
			System.out.println("직위명==>" + emp.getPosition_name());
			System.out.println("부서명==>" + emp.getDept_name());
			System.out.println("사원번호==>" + emp.getEmp_no());
		}
		
		JSONArray jsonArr = new JSONArray();
		
		if(empList != null) {
			for(EmployeeVO vo : empList) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("emp_name", vo.getEmp_name());       
				jsonObj.put("position_name", vo.getPosition_name());    
				jsonObj.put("dept_name", vo.getDept_name());
				jsonObj.put("emp_no", vo.getEmp_no());
				
				// {"no":101, "name":"이순신", "writeday":"2020-11-24 16:20:30"}  {"no":1004, "name":"신호연", "writeday":"2020-11-24 16:20:30"}
				
				jsonArr.put(jsonObj);
			}
			
		}
		
		return jsonArr.toString();
	}	
	
	// === 쪽지쓰기의 주소록 내에서 검색(ajax)  === //
	@ResponseBody
	@RequestMapping(value="/jieun/note/writeAddressSearch.os", produces="text/plain; charset=UTF-8")
	public String writeAddressSearch(HttpServletRequest request) {
		
		String searchWord = request.getParameter("searchWord");
		
		// System.out.println("검색어는 ? ==> " + searchWord);
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchWord", searchWord);
		
		List<EmployeeVO> empSearchList = service.empSearchList(paraMap);
		
		JSONArray jsonArr = new JSONArray();
		
		if(empSearchList != null) {
			for(EmployeeVO vo : empSearchList) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("emp_name", vo.getEmp_name());       
				jsonObj.put("position_name", vo.getPosition_name());    
				jsonObj.put("dept_name", vo.getDept_name());
				jsonObj.put("emp_no", vo.getEmp_no());
				
				jsonArr.put(jsonObj);
			}
			
		}
		
		// System.out.println("jsonArr.toString() ========>" + jsonArr.toString());
		
		return jsonArr.toString();
	}
	
	
	// === 쪽지함의 쪽지 쓰기 완료 페이지 요청 === //
	@RequestMapping(value="/jieun/note/writeEnd.os", method = {RequestMethod.POST})
	public String writeEnd(MultipartHttpServletRequest mrequest) {
		
		// note_no를 시퀀스로 하는게 아닌 임의의 숫자를 만들어서 insert 하자 (휴지통으로 이동시, 쪽지함, 임시보관함, 예약보관함에서 시퀀스가 겹쳐서 발생하는 현상 방지)
		// SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMddHHmmss");
		// Date time = new Date();
		// String today = format1.format(time);
		// System.out.println("@@@@@ 현재시간 구하기  =========> " + today);
		
		// 받는사원 ID
		String fk_emp_no_receive = mrequest.getParameter("fk_emp_no_receive");
		
		System.out.println("fk_emp_no_receive 받아와 지니? ==> " +  fk_emp_no_receive);
		
		String[] arr_fk_emp_no_receive = fk_emp_no_receive.split(",");
		
		for(int i=0; i<arr_fk_emp_no_receive.length; i++) {
			System.out.println("arr_fk_emp_no_receive 배열" + i + "번째 값은 ==> " + arr_fk_emp_no_receive[i]);
		}
		
		// 받는사원명
		String fk_emp_name = mrequest.getParameter("fk_emp_name");
		
		System.out.println("fk_emp_name 받아와 지니? ==> " +  fk_emp_name);
		
		String[] arr_fk_emp_name = fk_emp_name.split(",");
		
		for(int i=0; i<arr_fk_emp_name.length; i++) {
			System.out.println("arr_fk_emp_name 배열" + i + "번째 값은 ==> " + arr_fk_emp_name[i]);
		}		
		
		// 제목
		String note_title = mrequest.getParameter("note_title");
		
		// 쪽지쓰기에서 체크박스에 체크되어있으면 1이 넘어오고 아니면 0이 넘어오는데  이 값을 notevo에 set 해서 insert 할 것임
		String note_importantVal = mrequest.getParameter("note_importantVal");
		// notevo.setNote_important(Integer.parseInt(note_importantVal));
		
		// 작성자
		String fk_emp_no_send = mrequest.getParameter("fk_emp_no_send");
		
		// 첨부파일 
		MultipartFile attach = mrequest.getFile("attach");
		System.out.println("attach 상태==> " + attach.isEmpty());
		
		// 쪽지 내용
		String note_content = mrequest.getParameter("note_content");
		
		//////////////////////////////////////////
		
		// 임시보관함에서 수정할 경우 임시보관함의 번호가 넘어온다. 
		String note_temp_no = mrequest.getParameter("note_temp_no");
		System.out.println("임시보관함에서 제목 클릭 후 넘어온 임시보관함의 temp_no ==> " + note_temp_no);
		
		HttpSession session1 = mrequest.getSession();
		session1.setAttribute("note_temp_no", note_temp_no);

		// 임시보관함에 있는 첨부파일을 쪽지쓰기에서 파일을 변경안하고 보내면 파일이 없는 것으로 인식된다. 그래서 임시보관함에 저장된 파일이름, 파일원본이름, 파일크기를 hidden 타입으로 받아와서 디비에 저장시킨다. 
		
		String tmp_note_filename = mrequest.getParameter("note_filename");
		String tmp_note_orgfilename = mrequest.getParameter("note_orgfilename");
		String tmp_note_filesize = mrequest.getParameter("note_filesize");
		
		System.out.println("임시보관함에 저장된 파일들이 넘어왔닝??? note_filename ==> " + tmp_note_filename);
		System.out.println("임시보관함에 저장된 파일들이 넘어왔닝??? note_orgfilename ==> " + tmp_note_orgfilename);
		System.out.println("임시보관함에 저장된 파일들이 넘어왔닝??? note_filesize ==> " + tmp_note_filesize);
		
		//////////////////////////////////////////
		
		
		// === 사용자가 쓴 글에 파일이 첨부되어 있는 것인지, 아니면 파일이 첨부가 안되것인지 구분을 지어줘야 한다. ====
		// === 첨부파일이 있는 경우 시작 !!! ===		 
		
		// MultipartFile attach = notevo.getAttach();
		
		NoteVO nvo= new NoteVO(); // 파일 담기위한 NoteVO
		
		if( !attach.isEmpty()) { // 파일이 있냐 없냐 할때 사용하는 isEmpty(); 파일이 없을때는 true를 반환하고 파일이 있으면 false를 반환한다. 
			// attach(첨부파일)가 비어있지 않으면(즉, 첨부파일이 있는 경우)
			
			/*
				1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
				>>> 파일이 업로드 되어질 특정경로(폴더) 지정해주기
				       우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
				       조심할 것은 Package Explorer 에서 files 라는 폴더를 만드는 것이 아니다. 
			       
		    */			
			
			// WAS의 webapp 의 절대경로를 알아와야 한다.
			HttpSession session = mrequest.getSession();
			String root = session.getServletContext().getRealPath("/");

			String path = root + "resources" + File.separator +"files";
			
			/*
				2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기
		    */
			
			String newFilename = "";
			// WAS(톰캣)의 디스크에 저장될 파일명 
			
			byte[] bytes = null;
			// 첨부파일의 내용물
			
			long fileSize = 0;
			// 첨부파일의 크기
			
			try {
				bytes = attach.getBytes(); 
				// 첨부파일의 내용물을 읽어오는 것
				
				newFilename = fileManager.doFileUpload(bytes, attach.getOriginalFilename() , path);
				// 첨부되어진 파일을 업로드 하도록 하는 것이다. 
				// attach.getOriginalFilename() 은 첨부파일의 파일명(예: 강아지.png) 이다.
				
				System.out.println(">>> 확인용 newFilename ===>" + newFilename);
				// >>> 확인용 newFilename ===>20201209103856236590533787400.PNG
				
				/*
					3. NoteVO notevo에 fileName 값과 orgFilename 값과 fileSize 값을 넣어줘야 한다. 
				*/
				
				nvo.setNote_filename(newFilename);
				// WAS(톰캣)에 저장될 파일명(20201208092715353243254235235234.png)
				
				nvo.setNote_orgfilename(attach.getOriginalFilename());
				// 게시판 페이지에서 첨부된 파일(강아지.png)을 보여줄 때 사용한다.
				// 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용 
				
				fileSize = attach.getSize(); // 첨부파일의 파일 크기(단위는 byte 임)
				nvo.setNote_filesize(String.valueOf(fileSize));
				
			} 
			catch(Exception e) {
				e.printStackTrace();
			}
		
		} // end of if()------------------------------------------------------
		
		int n = 0;
		// 1. 첨부파일이 없는 경우라면
		if(attach.isEmpty()) {
			// 쪽지테이블에 첨부파일이 없는 쪽지 insert 하기
			
			if(tmp_note_filename == null) {
				
				System.out.println("임시보관함에서 제목 눌러서 안온 경우이다. ");
				
				for(int i =0; i<arr_fk_emp_no_receive.length; i++) {

					// String now = String.valueOf(today);
					
					// StringBuffer strBuf = new StringBuffer();
					// strBuf.append(now);
					// String str_note_no = strBuf.reverse().toString();
					
					// long iToday = Long.parseLong(str_note_no);
					// iToday = iToday + i;
					
					// String note_no = String.valueOf(iToday);
					
					// System.out.println("@@@@@ 현재시간 구하기 iToday  =========> " + iToday);

					
					// System.out.println("@@@@@ 현재시간 구하기 역순  =========> " + note_no);						
					
					NoteVO notevo = new NoteVO();
					notevo.setFk_emp_no_receive(Integer.parseInt(arr_fk_emp_no_receive[i]));
					notevo.setFk_emp_name(arr_fk_emp_name[i]);
					notevo.setNote_title(note_title);
					notevo.setNote_important(Integer.parseInt(note_importantVal));
					notevo.setFk_emp_no_send(Integer.parseInt(fk_emp_no_send));
					notevo.setNote_content(note_content);
					
					n = service.write(notevo);
				}
			}
			// attach 파일이 없다고 어짜피 없다고 인식할거니까 그냥 디비에 박아보자
			if(tmp_note_filename != null) { // 파일이 있는 경우
				
				System.out.println("여기로 왔지롱ㅎㅎㅎㅎㅎ ");
				
				for(int i =0; i<arr_fk_emp_no_receive.length; i++) {
					NoteVO notevo = new NoteVO();
					notevo.setFk_emp_no_receive(Integer.parseInt(arr_fk_emp_no_receive[i]));
					notevo.setFk_emp_name(arr_fk_emp_name[i]);
					notevo.setNote_title(note_title);
					notevo.setNote_important(Integer.parseInt(note_importantVal));
					notevo.setFk_emp_no_send(Integer.parseInt(fk_emp_no_send));
					notevo.setNote_content(note_content);
					notevo.setNote_filename(tmp_note_filename);
					notevo.setNote_orgfilename(tmp_note_orgfilename);
					notevo.setNote_filesize(tmp_note_filesize);
										
					
					n = service.writeTempFileInsert(notevo);
				}
			}
			
			
		}
		else {
		// 2. 첨부파일이 있는 경우라면
			
			// nvo에 set한걸 끄집어 낸다. 
			String sNote_file_name = nvo.getNote_filename();
			String sNote_orgfile_name = nvo.getNote_orgfilename();
			String sNote_file_size = nvo.getNote_filesize();
			
			for(int i =0; i<arr_fk_emp_no_receive.length; i++) {
				NoteVO notevo = new NoteVO();
				notevo.setFk_emp_no_receive(Integer.parseInt(arr_fk_emp_no_receive[i]));
				notevo.setFk_emp_name(arr_fk_emp_name[i]);
				notevo.setNote_title(note_title);
				notevo.setNote_important(Integer.parseInt(note_importantVal));
				notevo.setFk_emp_no_send(Integer.parseInt(fk_emp_no_send));
				
				notevo.setNote_filename(sNote_file_name);
				notevo.setNote_orgfilename(sNote_orgfile_name);
				notevo.setNote_filesize(sNote_file_size);
				
				notevo.setNote_content(note_content);
				
				// 쪽지테이블에 첨부파일이 있는 쪽지 insert 하기
				n = service.write_withFile(notevo);
				
				// 임시테이블에서 쪽지받아서 첨부파일 하는 insert를 따로 또 해준다. 
				
			}			
		}
		
		if(n==1) {
			
			// insert 에 성공했으니 임시보관함에서 session에 저장된 note_no를 넘겨서 삭제하자!
			
			String str_note_temp_no = (String)session1.getAttribute("note_temp_no");
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("note_no", str_note_temp_no);
			
			// 임시보관함에서 쪽지 쓰기의 보내기를 한 경우 클릭해서 들어온 임시보관함의 note_no를 삭제
			int m = service.deleteFromTblTemp(paraMap);
			
			if(m == 1 ) {
				System.out.println("@@@@@ 임시보관함에서 " + str_note_temp_no + "번호를 삭제하였습니다.  @@@@@");
			}
			else {
				System.out.println("@@@@@ 임시보관함에서 " + str_note_temp_no + "번호를 삭제에 실패하였습니다.  @@@@@");
			}
			
			// 성공하면 보낸 쪽지함으로 이동
			return "redirect:/jieun/note/sendList.os"; 
			// list.action 페이지로 redirect(페이지 이동) 해라는 뜻이다. 
		} 
		else {
			// 실패하면 글 쓰기 페이지로 이동
			return "redirect:/jieun/note/write.os"; 
			// add.action 페이지로 redirect(페이지 이동) 해라는 뜻이다. 			
		}
	}
	
	// === 임시보관함에 저장된 쪽지 수정 완료 === //
	// 임시보관함에 저장된 쪽지에서 제목을 누르면 쪽지 수정 페이지로 이동한다.
	// 수정 페이지에서 보내기 버튼을 눌렀을때만 delete 임시보관함에서 delete 시키고
	// 보내기 버튼을 안 눌렀다면 임시보관함 DB에 저장된 내용과 쪽지 작성한 내용을 비교해서 같으면 변경된 내용이 없으니까 insert 안 하고
	// 하나라도 변경된게 있으면 임시보관함 DB에 다시 insert 하자
	@RequestMapping(value="/jieun/note/writeModify.os")
	public ModelAndView writeModify (HttpServletRequest request, ModelAndView mav) {
		
		String note_no = request.getParameter("note_no");
		System.out.println("임시보관함에 저장된 쪽지에서 제목 클릭시 넘어온 쪽지 번호 ===> " + note_no);
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("note_no", note_no);
		
		// 임시보관함에서 쪽지번호로 수정할 쪽지 조회해오기 (select)
		NoteTempVO noteTempvo = service.writeModifySelect(paraMap);
		
		mav.addObject("noteTempvo", noteTempvo);
		mav.setViewName("jieun/note/write.tiles1");
		
		return mav;
	}
	
	// === #sendList.os 보낸쪽지함 페이지 요청 === // 
	@RequestMapping(value="/jieun/note/sendList.os")
	public ModelAndView requiredLogin_sendList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		List<NoteVO> noteSendList = null; // 글을 하나도 작성하지 않았을 수가 있기 때문에 null 지정
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
	
		System.out.println("보낸쪽지함페이지에서 로그인 ID 받아오기 ===> " + loginemp.getEmp_no());
		
		String fk_emp_no_send = String.valueOf(loginemp.getEmp_no());
		
		// 정렬 타입 받아오기
		String orderType = request.getParameter("orderType");
		System.out.println("!!!!!!!!!! 넘어온 정렬 타입은 =====> " + orderType);
		
		// 보낸 쪽지함 리스트에서 검색 타입과 검색어를 받아오기
		String searchType = request.getParameter("searchType"); 
		String searchWord = request.getParameter("searchWord");
		
		System.out.println("검색 타입은 ??????? " + searchType);
		System.out.println("검색어는 ??????? " + searchWord);		
		
		if(searchType == null) { 
			searchType = "";			
		}		
		
		if(searchWord == null || searchWord.trim().isEmpty()) { // searchWord.trim().isEmpty() : 공백을 제거한 후에 텅 비어있냐
			searchWord = "";			
		}
		
		// 리스트 페이징 처리하기 시작
		
		/*
	      	페이징 처리를 통한 글목록 보여주기는 예를 들어 3페이지의 내용을 보고자 한다라면 
           	검색을 할 경우는 아래와 같이
     		list.action?searchType=subject&searchWord=안녕&currentShowPageNo=3 와 같이 해주어야 한다.
           	또는 
           	검색이 없는 전체를 볼때는 아래와 같이 
     		list.action?searchType=subject&searchWord=&currentShowPageNo=3 와 같이 해주어야 한다.
		*/		
		
		// 한 페이지 당 보여줄 리스트 개수 
		String sizePerPage = request.getParameter("sizePerPage");
		
		// sizePerPage가 null 이고 20또는 5 또는 40 이 아니라면 sizePerPage가 20이다.
		if(sizePerPage == null || 
				!("20".equals(sizePerPage) || "40".equals(sizePerPage) || "60".equals(sizePerPage)) ) {
				sizePerPage = "20";
		}
		
		System.out.println("*** 페이징 처리할 리스트 개수는 ===> *** " + sizePerPage);
		
		// 페이지 바에서 보고있는 현재 페이지 번호
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("fk_emp_no_send", fk_emp_no_send);
		
		paraMap.put("orderType", orderType);
		
		paraMap.put("searchWord", searchWord); // note_tilte or fk_emp_name 넘어옴
		paraMap.put("searchType", searchType);
		
	    // 검색이 있을 때만 넘겨주자(검색어 타입, 검색어 유지 시키기)
	    if(!"".equals(searchType)) {
	    	
	    	System.out.println("검색어가 있다!!!!!!!!!!!!!!");
	    	
	    	mav.addObject("searchWord", searchWord);
	    	mav.addObject("searchType", searchType);
	    }	
	    
		System.out.println("검색어가 없다 XXXX!!!!!!!!!!!!!!");
	    
		// 먼저 총 게시물 건수(totalCount) 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총 게시물 건수
	 // int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함.
		int totalPage = 0; 		   // 총 페이지수(웹브라우저상에 보여줄 총 페이지 개수, 페이지바)		
		
		int startRno = 0; 		   // 시작 행 번호
		int endRno = 0;   		   // 끝 행 번호
		
		// 총 게시물 건수(totalCount) 
		totalCount = service.getNoteSendTotalCount(paraMap);
		System.out.println("확인용 보낸 쪽지함 totalCount : " + totalCount);
		
		paraMap.put("totalCount", String.valueOf(totalCount));
		
		if(totalCount == 0) {
			System.out.println("전체 count 가 0이다 게시글 없음 ;;;;;;;;;;");
			noteSendList = service.sendList(paraMap);	
		}
		else {		
		
			// 만약에 총 게시물 건수(totalCount)가 127개 이라면
			// 총 페이지 수(totalPage)는 13개가 되어야 한다. 	    
			
			totalPage = (int)Math.ceil((double)totalCount/Integer.parseInt(sizePerPage)); 
			// (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> 13.0 ==> (int)13.0 ==> 13
			// (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> 12.0 ==> (int)12.0 ==> 12
			
			// 보낸쪽지함에서 보여지는 초기화면은 null값이다. null 대신 1페이지를 보여주게 하자.
			if(str_currentShowPageNo == null) {
				currentShowPageNo = 1;
			}
			else {
				try {
					currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
					
					if(currentShowPageNo < 1 || currentShowPageNo > totalPage ) {
						// 유저의 장난으로 현재페이지가 1보다 작거나 전체 페이지보다(만약 21) 크면 1페이지로 가게 하자.
						currentShowPageNo = 1; 
					}
				}
				catch(NumberFormatException e) {
					currentShowPageNo = 1;
				}
			}
			
			// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
		    /*
		            currentShowPageNo      startRno     endRno
		           --------------------------------------------
		               1 page        ===>    1           10
		               2 page        ===>    11          20
		               3 page        ===>    21          30
		               4 page        ===>    31          40
		               ......                ...         ...
		    */
		   
		    startRno = ((currentShowPageNo - 1 ) * Integer.parseInt(sizePerPage)) + 1;
		    endRno = startRno + Integer.parseInt(sizePerPage) - 1; 
			
		    paraMap.put("startRno", String.valueOf(startRno));
		    paraMap.put("endRno", String.valueOf(endRno));
		    
			// 로그인한 사원번호로 보낸편지를 쪽지테이블에서 select 해서 보여주기 (페이징 처리)
			noteSendList = service.sendList(paraMap);
			
			for(NoteVO sendvo : noteSendList) {
				System.out.println("받은사원ID==>" + sendvo.getFk_emp_no_receive());
				System.out.println("사원명==>" + sendvo.getFk_emp_name());
				System.out.println("제목==>" + sendvo.getNote_title());
				System.out.println("날짜==>" + sendvo.getNote_write_date());	
				System.out.println("쪽지 번호 ==> " + sendvo.getNote_no());
				System.out.println("쪽지 열람 여부 ==> " + sendvo.getNote_read_status());
				
				/*
				if(sendvo.getNote_read_status() == 1) {
					mav.addObject("note_read_status", sendvo.getNote_read_status());
				}
				*/
			}
			
		    // 페이지바 만들기
		    String pageBar = "<ul style='list-style:none;'>";
		    
		    int blockSize = 1; 
		    // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		    /*
		            1 2 3 4 5 6 7 8 9 10  다음                   -- 1개블럭
		             이전  11 12 13 14 15 16 17 18 19 20  다음    -- 1개블럭
		             이전  21 22 23
		    */
		    
		    int loop = 1; // 10번 반복
		    /*
	           loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		    */
		    
		    int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
		    // *** !! 공식이다. !! *** //
		    
		    /*
		       1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
		       11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
		       21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
		       
		       currentShowPageNo         pageNo
		      ----------------------------------
		            1                      1 = ((1 - 1)/10) * 10 + 1
		            2                      1 = ((2 - 1)/10) * 10 + 1
		            3                      1 = ((3 - 1)/10) * 10 + 1
		            4                      1
		            5                      1
		            6                      1
		            7                      1 
		            8                      1
		            9                      1
		            10                     1 = ((10 - 1)/10) * 10 + 1
		           
		            11                    11 = ((11 - 1)/10) * 10 + 1
		            12                    11 = ((12 - 1)/10) * 10 + 1
		            13                    11 = ((13 - 1)/10) * 10 + 1
		            14                    11
		            15                    11
		            16                    11
		            17                    11
		            18                    11 
		            19                    11 
		            20                    11 = ((20 - 1)/10) * 10 + 1
		            
		            21                    21 = ((21 - 1)/10) * 10 + 1
		            22                    21 = ((22 - 1)/10) * 10 + 1
		            23                    21 = ((23 - 1)/10) * 10 + 1
		            ..                    ..
		            29                    21
		            30                    21 = ((30 - 1)/10) * 10 + 1
		   */	
		    
		    String url = "sendList.os";
		      
		    // === [이전] 만들기 === 
		    if(pageNo != 1) {
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'> &#60;&#60; </a></li>"; // [맨처음]
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'> &#60; </a></li>"; // [이전]
		      }
	
		    while( !(loop > blockSize || pageNo > totalPage) ) {
		         
		         if(pageNo == currentShowPageNo) {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
		         }
		         else {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
		         }
		         
		         loop++;
		         pageNo++;
		         
		      }// end of while------------------------------
		      
		    // === [다음] 만들기 ===
		    if( !(pageNo > totalPage) ) {
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'> &#62;</a></li>"; //[다음]
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'> &#62; &#62;</a></li>"; // [마지막]
		    }
		    
		    pageBar += "</ul>";
		    
		    mav.addObject("pageBar",pageBar);
	    
	    }
	    
	    // === #123. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
	    //			 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //			 현재 페이지 주소를 뷰단으로 넘겨준다. 
	    String goBackURL = MyUtil.getCurrentURL(request);
	    // System.out.println("~~~ 확인용 goBackURL ==> " +goBackURL);
	    // ~~~ 확인용 goBackURL ==> list.action?searchType=&searchWord=&currentShowPageNo=2
	    
	    mav.addObject("goBackURL", goBackURL);		
		
		mav.addObject("noteSendList", noteSendList);
		mav.addObject("login_emp_no", fk_emp_no_send);
		
		mav.setViewName("jieun/note/sendList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/sendList.jsp 파일
		
		return mav;
	}		
	
	// === 보낸쪽지함에서 상세 쪽지 보기  === //
	@RequestMapping(value="/jieun/note/sendOneDetail.os")
	public ModelAndView sendOneDetail(HttpServletRequest request, ModelAndView mav) {
		
		String note_no = request.getParameter("note_no");
		
	    //  페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //	 현재 페이지 주소를 뷰단으로 넘겨준다.
		String goBackURL = request.getParameter("goBackURL");
		
		if(goBackURL != null) {
		
			goBackURL = goBackURL.replaceAll(" ", "&"); // 이전글, 다음글을 클릭해서 넘어온 것임.		
			System.out.println("############ 확인용 goBackURL : " + goBackURL);
			
			goBackURL = goBackURL.substring(11);
			
			System.out.println("############ SubString으로 자른 후 확인용 goBackURL : " + goBackURL);
			
			// ############ 확인용 goBackURL : list.action?searchType=&searchWord=&currentShowPageNo=2
			
			mav.addObject("goBackURL",goBackURL);
		}	
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("note_no", note_no);
		
		NoteVO notevo = service.sendOneDetail(paraMap);
		
		mav.addObject("notevo", notevo);
		mav.setViewName("jieun/note/sendOneDetail.tiles1");		
		
		return mav;
	}
	
	// === 보낸쪽지함에서 선택한 쪽지 삭제하기  === //
	//     먼저 휴지통 테이블에 쪽지 번호로 select 한 결과를 insert 해주고 나서 
	//     보낸쪽지함에서 해당 note_no를 가진 쪽지를 삭제하자 X
	//     보낸쪽지함에서 해당 받은 사원 삭제 여부의 상태를 1로 변경하자  
	@ResponseBody
	@RequestMapping(value="/jieun/note/moveToTrashcanSend.os", produces="text/plain; charset=UTF-8")
	public String moveToTrashcanSend(HttpServletRequest request) {
		
		// 삭제할 번호(문자열)
		String str_note_no = request.getParameter("str_note_no");
		System.out.println("하하하 str_note_no ===> " + str_note_no);
		
		// 삭제할 개수
		String cnt = request.getParameter("cnt");
		System.out.println("하하하 cnt ===> " + cnt);	
		
		// 삭제한 사원의 id
		String login_emp_no = request.getParameter("login_emp_no");
		
		
		// 배열의 [, ] 제거
		str_note_no = str_note_no.replaceAll("\\[", "");
		str_note_no = str_note_no.replaceAll("\\]", "");
		str_note_no = str_note_no.replaceAll("\"", "");
		str_note_no = str_note_no.trim(); // 공백 제거 
		
		System.out.println("문자열에서 [, ], 쌍따움표 , 공백 제고 -->" + str_note_no);
		
		String[] arr_note_no = str_note_no.split(",");
		
		int n = 0;
		int m = 0;
		int result = 0;
		
		// 현재 날짜 구하기
	    Calendar currentDate = Calendar.getInstance(); // 현재날짜와 시간을 얻어온다. 
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
		for(int i=0; i<arr_note_no.length; i++) {
			System.out.println("하하하 arr_note_no ==> " + arr_note_no[i]);
			
			Map<String, String > paraMap = new HashMap<String, String>();
			paraMap.put("note_no", arr_note_no[i]);
			
			// note_no로 select 한 결과를 vo에 담아오자
			NoteVO notevo = service.sendOneDetail(paraMap);
			
			System.out.println( notevo.getNote_no() +  "@@@ " + notevo.getFk_emp_no_send() + "@@@@"
					+ notevo.getFk_emp_no_receive() + "@@@@" + notevo.getFk_emp_name() + "@@@@"
					+ notevo.getNote_title() + "@@@@" + notevo.getNote_content() + "@@@@"
					+ notevo.getNote_filename() + "@@@@" + notevo.getNote_orgfilename() + "@@@@"
					+ notevo.getNote_filesize() + "@@@@" + notevo.getNote_important() + "@@@@" 
					+ notevo.getNote_reservation_status() + "@@@@" + notevo.getNote_reservation_date() + "@@@@"
					+ notevo.getNote_send_del_status() + "@@@@" + notevo.getNote_receive_del_status() + "@@@@"
					+ notevo.getNote_write_date());
			
			
			paraMap.put("fk_emp_no_send",String.valueOf(notevo.getFk_emp_no_send()));
			paraMap.put("fk_emp_no_receive",String.valueOf(notevo.getFk_emp_no_receive()));
			paraMap.put("fk_emp_name", notevo.getFk_emp_name());
			paraMap.put("note_title", notevo.getNote_title());
			paraMap.put("note_content", notevo.getNote_content());
			paraMap.put("note_write_date", notevo.getNote_write_date());
			paraMap.put("note_filename", notevo.getNote_filename());
			paraMap.put("note_orgfilename", notevo.getNote_orgfilename());
			paraMap.put("note_filesize", notevo.getNote_filesize());
   			paraMap.put("note_important", String.valueOf(notevo.getNote_important()));
   			paraMap.put("note_reservation_status", String.valueOf(notevo.getNote_reservation_status()));
   			paraMap.put("note_read_status", String.valueOf(notevo.getNote_read_status()));
   			
   			if(notevo.getNote_reservation_date() != null) {
   				System.out.println("예약 값 있음이 넘ㅇ어간다 ------------------");   				
   				paraMap.put("note_reservation_date", notevo.getNote_reservation_date());
   			}
   			else {
   				System.out.println("예약 값 없음면 예약 컬럼에 그냥 현재 시간  넘ㅇ어간다 ------------------");
   				paraMap.put("note_reservation_date", df.format(currentDate.getTime()));
   			}
   			
   			
   			paraMap.put("note_send_del_status", String.valueOf(notevo.getNote_send_del_status()));
   			paraMap.put("note_receive_del_status", String.valueOf(notevo.getNote_receive_del_status()));
   		
			// 삭제버튼 누른 사원 id를 paraMap에 넣자 
			paraMap.put("note_del_login_emp_no", login_emp_no);
			
			// 먼저 받은 사원 삭제 여부 상태를 1로 바꾼 후에 insert 를 해야 하는건지..
			// 아니면 insert를 하고 update를 해야하는건지
			
			
			// 문제가 되는 부분 첨부파일 null 값, reservation_date 컬럼 null 값
			// 첨부파일 여부 확인해서 null 일때 아닐때 넣기
			if(notevo.getNote_filename() != null) {
				// 먼저 휴지통 테이블에 insert 하기
				n = service.moveToTrashcan(paraMap);
			}
			else {
				n = service.moveToTrashcanNoFile(paraMap);
			}
			
			
			// insert 성공하면 쪽지테이블에서 해당 note_no를 삭제하기 
			// 
			if(n == 1) {
				// m = service.deleteFromTblNote(paraMap);
				// 쪽지 테이블에서 삭제가 아니라 note_no에 해당하는 보낸 사원 삭제 여부(보낸쪽지함에서 삭제 여부)의 상태를 1로 변경하기
				m = service.updateFromTblNoteSendDelStatus(paraMap);
			}			
		}	
		
		JSONObject jsonObj = new JSONObject();
	      
		if(n== 1 && m == 1) {
			result = 1;
			jsonObj.put("result", result);		
		}
		
		
		return jsonObj.toString();
	}	
		

	// === #receiveList.os 받은쪽지함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/receiveList.os")
	public ModelAndView requiredLogin_receiveList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 정렬 타입 받아오기 
		String orderType = request.getParameter("orderType");
		
		List<NoteVO> noteReceiveList = null; // 글을 하나도 작성하지 않았을 수가 있기 때문에 null 지정
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
	
		System.out.println("받은쪽지함페이지에서 로그인 ID 받아오기 ===> " + loginemp.getEmp_no());
		
		String fk_emp_no_receive = String.valueOf(loginemp.getEmp_no());
		
		// 받은 쪽지함 리스트에서 검색 타입과 검색어를 받아오기
		String searchType = request.getParameter("searchType"); 
		String searchWord = request.getParameter("searchWord");
		
		System.out.println("검색 타입은 ??????? " + searchType);
		System.out.println("검색어는 ??????? " + searchWord);		
		
		if(searchType == null) { 
			searchType = "";			
		}		
		
		if(searchWord == null || searchWord.trim().isEmpty()) { // searchWord.trim().isEmpty() : 공백을 제거한 후에 텅 비어있냐
			searchWord = "";			
		}
		
		// 리스트 페이징 처리하기 시작
		
		// 한 페이지 당 보여줄 리스트 개수 
		String sizePerPage = request.getParameter("sizePerPage");
		
		// sizePerPage가 null 이고 20또는 5 또는 40 이 아니라면 sizePerPage가 20이다.
		if(sizePerPage == null || 
				!("20".equals(sizePerPage) || "40".equals(sizePerPage) || "60".equals(sizePerPage)) ) {
				sizePerPage = "20";
		}		
		
		System.out.println("*** 페이징 처리할 리스트 개수는 ===> *** " + sizePerPage);
		
		// 페이지 바에서 보고있는 현재 페이지 번호
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");		
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("fk_emp_no_receive", fk_emp_no_receive);
		
		paraMap.put("searchWord", searchWord); // note_tilte or emp_name 넘어옴
		paraMap.put("searchType", searchType);
		
		paraMap.put("orderType", orderType); // note_title or emp_name or note_write_date 넘어옴
		
	    // 검색이 있을 때만 넘겨주자(검색어 타입, 검색어 유지 시키기)
	    if(!"".equals(searchType)) {
	    	mav.addObject("searchWord", searchWord);
	    	mav.addObject("searchType", searchType);

	    }
	    
		// 먼저 총 게시물 건수(totalCount) 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총 게시물 건수
	 // int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함.
		int totalPage = 0; 		   // 총 페이지수(웹브라우저상에 보여줄 총 페이지 개수, 페이지바)		
		
		int startRno = 0; 		   // 시작 행 번호
		int endRno = 0;   		   // 끝 행 번호
		
		// 받은 쪽지함 총 게시물 건수(totalCount) 
		totalCount = service.getNoteReceiveTotalCount(paraMap);
		System.out.println("확인용 보낸 쪽지함 totalCount : " + totalCount);
		
		paraMap.put("totalCount", String.valueOf(totalCount));
		
		if(totalCount == 0) {
			System.out.println("전체 count 가 0이다 게시글 없음 ;;;;;;;;;;");
			noteReceiveList = service.receiveList(paraMap);	
		}
		else {
		
			// 만약에 총 게시물 건수(totalCount)가 127개 이라면
			// 총 페이지 수(totalPage)는 13개가 되어야 한다. 	    
	
			totalPage = (int)Math.ceil((double)totalCount/Integer.parseInt(sizePerPage)); 
			// (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> 13.0 ==> (int)13.0 ==> 13
			// (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> 12.0 ==> (int)12.0 ==> 12
			
			// 보낸쪽지함에서 보여지는 초기화면은 null값이다. null 대신 1페이지를 보여주게 하자.
			if(str_currentShowPageNo == null) {
				currentShowPageNo = 1;
			}
			else {
				try {
					currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
					
					if(currentShowPageNo < 1 || currentShowPageNo > totalPage ) {
						// 유저의 장난으로 현재페이지가 1보다 작거나 전체 페이지보다(만약 21) 크면 1페이지로 가게 하자.
						currentShowPageNo = 1; 
					}
				}
				catch(NumberFormatException e) {
					currentShowPageNo = 1;
				}
			}
			
		    startRno = ((currentShowPageNo - 1 ) * Integer.parseInt(sizePerPage)) + 1;
		    endRno = startRno + Integer.parseInt(sizePerPage) - 1; 
			
		    paraMap.put("startRno", String.valueOf(startRno));
		    paraMap.put("endRno", String.valueOf(endRno));
		    
			// 로그인한 사원번호로 받은편지를 쪽지테이블에서 select 해서 보여주기 (페이징 처리)
			noteReceiveList = service.receiveList(paraMap);
			
			for(NoteVO receivevo : noteReceiveList) {
				System.out.println("보낸사원ID==>" + receivevo.getFk_emp_no_send());
				System.out.println("사원명==>" + receivevo.getEmp_name());
				System.out.println("제목==>" + receivevo.getNote_title());
				System.out.println("날짜==>" + receivevo.getNote_write_date());	
				System.out.println("쪽지 번호 ==> " + receivevo.getNote_no());
			}
			
		    // 페이지바 만들기
		    String pageBar = "<ul style='list-style:none;'>";
		    
		    int blockSize = 1; 
		    // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		    
		    int loop = 1; // 10번 반복
		    /*
	           loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		    */
		    
		    int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
		    // *** !! 공식이다. !! *** //
		    
		    String url = "receiveList.os";
		      
		    // === [이전] 만들기 === 
		    if(pageNo != 1) {
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'> &#60;&#60; </a></li>"; // [맨처음]
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'> &#60; </a></li>"; // [이전]
		      }
	
		    while( !(loop > blockSize || pageNo > totalPage) ) {
		         
		         if(pageNo == currentShowPageNo) {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
		         }
		         else {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
		         }
		         
		         loop++;
		         pageNo++;
		         
		      }// end of while------------------------------
		      
		    // === [다음] 만들기 ===
		    if( !(pageNo > totalPage) ) {
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'> &#62;</a></li>"; //[다음]
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'> &#62; &#62;</a></li>"; // [마지막]
		    }
		    
		    pageBar += "</ul>";
		    
		    mav.addObject("pageBar",pageBar);
	    
	    }
	    
	    // === #123. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
	    //			 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //			 현재 페이지 주소를 뷰단으로 넘겨준다. 
	    String goBackURL = MyUtil.getCurrentURL(request);
	    // System.out.println("~~~ 확인용 goBackURL ==> " +goBackURL);
	    // ~~~ 확인용 goBackURL ==> list.action?searchType=&searchWord=&currentShowPageNo=2
	    
	    mav.addObject("goBackURL", goBackURL);			    
		
		mav.addObject("noteReceiveList", noteReceiveList);	
		mav.addObject("login_emp_no", fk_emp_no_receive);
		
		mav.setViewName("jieun/note/receiveList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/receiveList.jsp 파일
		
		return mav;
	}
	
	// === 받은쪽지함에서 쪽지 상세 보기 === //
	//     쪽지 리스트에서 제목을 클릭하는 순간 update 문으로 note_read_status 증가 시키자 
	@RequestMapping(value="/jieun/note/receiveOneDetail.os")
	public ModelAndView receiveOneDetail(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 쪽지 번호 받기
		String note_no = request.getParameter("note_no");
		
	    //  페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //	 현재 페이지 주소를 뷰단으로 넘겨준다.
		String goBackURL = request.getParameter("goBackURL");
		
		if(goBackURL != null) {
		
			goBackURL = goBackURL.replaceAll(" ", "&"); // 이전글, 다음글을 클릭해서 넘어온 것임.		
			System.out.println("############ 확인용 goBackURL : " + goBackURL);
			
			goBackURL = goBackURL.substring(11);
			
			System.out.println("############ SubString으로 자른 후 확인용 goBackURL : " + goBackURL);
			
			// ############ 확인용 goBackURL : list.action?searchType=&searchWord=&currentShowPageNo=2
			
			mav.addObject("goBackURL",goBackURL);
		}		
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("note_no", note_no);
		
		// update 먼저 해주기 
		int n = service.receiveReadCountChange(paraMap);
		
		if(n==1) {
			System.out.println("@@@note_read_status 상태 변경 성공!");
		}
		else {
			System.out.println("@@@note_read_status 상태 변경 실패ㅠㅠㅠ");
		}
		
		NoteVO notevo = service.receiveOneDetail(paraMap);
		
		mav.addObject("notevo", notevo);
		
		mav.setViewName("jieun/note/receiveOneDetail.tiles1");
		
		return mav;
	}
	
	// === 받은쪽지함에서 선택한 쪽지 삭제하기  === //
	//     먼저 휴지통 테이블에 쪽지 번호로 select 한 결과를 insert 해주고 나서 
	//     받은 쪽지함에서 해당 note_no를 가진 쪽지를 삭제하자 X
	//     받은 쪽지함에서 해당 받은 사원 삭제 여부의 상태를 1로 변경하자  
	// 	     중요쪽지함에서도 삭제
	@ResponseBody
	@RequestMapping(value="/jieun/note/moveToTrashcanReceive.os", produces="text/plain; charset=UTF-8")
	public String moveToTrashcanReceive(HttpServletRequest request) {
		
		// 삭제할 번호(문자열)
		String str_note_no = request.getParameter("str_note_no");
		System.out.println("하하하 str_note_no ===> " + str_note_no);
		
		// 삭제할 개수
		String cnt = request.getParameter("cnt");
		System.out.println("하하하 cnt ===> " + cnt);		
		
		// 삭제 버튼을 누를 사원 id 
		String login_emp_no = request.getParameter("login_emp_no");
		System.out.println("#### 넘어온 login_emp_no의  값은??????????????????????? " + login_emp_no);
		
		// 배열의 [, ] 제거
		str_note_no = str_note_no.replaceAll("\\[", "");
		str_note_no = str_note_no.replaceAll("\\]", "");
		str_note_no = str_note_no.replaceAll("\"", "");
		str_note_no = str_note_no.trim(); // 공백 제거 
		
		System.out.println("문자열에서 [, ], 쌍따움표 , 공백 제고 -->" + str_note_no);
		
		String[] arr_note_no = str_note_no.split(",");
		
		int n = 0;
		int m = 0;
		int result = 0;
		
		// 현재 날짜 구하기
	    Calendar currentDate = Calendar.getInstance(); // 현재날짜와 시간을 얻어온다. 
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    
		
		for(int i=0; i<arr_note_no.length; i++) {
			System.out.println("하하하 arr_note_no ==> " + arr_note_no[i]);
			
			Map<String, String > paraMap = new HashMap<String, String>();
			paraMap.put("note_no", arr_note_no[i]);
			
			// 먼저 휴지통 테이블에 insert 하기
			// n = service.moveToTrashcan(paraMap);
			
			// 1. note_no로 select 한 결과를 vo에 담아오자
			NoteVO notevo = service.receiveOneDetail(paraMap);
			
			System.out.println( notevo.getNote_no() +  "@@@ " + notevo.getFk_emp_no_send() + "@@@@"
					+ notevo.getFk_emp_no_receive() + "@@@@" + notevo.getFk_emp_name() + "@@@@"
					+ notevo.getNote_title() + "@@@@" + notevo.getNote_content() + "@@@@"
					+ notevo.getNote_filename() + "@@@@" + notevo.getNote_orgfilename() + "@@@@"
					+ notevo.getNote_filesize() + "@@@@" + notevo.getNote_important() + "@@@@" 
					+ notevo.getNote_reservation_status() + "@@@@" + notevo.getNote_reservation_date() + "@@@@"
					+ notevo.getNote_send_del_status() + "@@@@" + notevo.getNote_receive_del_status() + "@@@@"
					+ notevo.getNote_write_date());
			
			
			paraMap.put("fk_emp_no_send",String.valueOf(notevo.getFk_emp_no_send()));
			paraMap.put("fk_emp_no_receive",String.valueOf(notevo.getFk_emp_no_receive()));
			paraMap.put("fk_emp_name", notevo.getFk_emp_name());
			paraMap.put("note_title", notevo.getNote_title());
			paraMap.put("note_content", notevo.getNote_content());
			paraMap.put("note_write_date", notevo.getNote_write_date());
			paraMap.put("note_filename", notevo.getNote_filename());
			paraMap.put("note_orgfilename", notevo.getNote_orgfilename());
			paraMap.put("note_filesize", notevo.getNote_filesize());
   			paraMap.put("note_important", String.valueOf(notevo.getNote_important()));
   			paraMap.put("note_reservation_status", String.valueOf(notevo.getNote_reservation_status()));
   			paraMap.put("note_read_status", String.valueOf(notevo.getNote_read_status()));
   			
   			if(notevo.getNote_reservation_date() != null) {
   				System.out.println("예약 값 있음이 넘ㅇ어간다 ------------------");   				
   				paraMap.put("note_reservation_date", notevo.getNote_reservation_date());
   			}
   			else {
   				System.out.println("예약 값 없음면 예약 컬럼에 그냥 현재 시간  넘ㅇ어간다 ------------------");
   				paraMap.put("note_reservation_date", df.format(currentDate.getTime()));
   			}
   			
   			
   			paraMap.put("note_send_del_status", String.valueOf(notevo.getNote_send_del_status()));
   			paraMap.put("note_receive_del_status", String.valueOf(notevo.getNote_receive_del_status()));
   		
			// 삭제버튼 누른 사원 id를 paraMap에 넣자 
			paraMap.put("note_del_login_emp_no", login_emp_no);
			
			// 먼저 받은 사원 삭제 여부 상태를 1로 바꾼 후에 insert 를 해야 하는건지..
			// 아니면 insert를 하고 update를 해야하는건지
			
			
			// 문제가 되는 부분 첨부파일 null 값, reservation_date 컬럼 null 값
			// 첨부파일 여부 확인해서 null 일때 아닐때 넣기
			if(notevo.getNote_filename() != null) {
				// 먼저 휴지통 테이블에 insert 하기
				n = service.moveToTrashcan(paraMap);
			}
			else {
				n = service.moveToTrashcanNoFile(paraMap);
			}
			
			
			// insert 성공하면 쪽지테이블에서 해당 note_no를 삭제하기 
			// 
			if(n == 1) {
				// m = service.deleteFromTblNote(paraMap);
				// 쪽지 테이블에서 삭제가 아니라 note_no에 해당하는 받은 사원 삭제 여부의 상태를 1로 변경하기
				m = service.updateFromTblNoteReceiveDelStatus(paraMap);
			}			
		}
		
		JSONObject jsonObj = new JSONObject();
	      
		if(n== 1 && m == 1) {
			result = 1;
			jsonObj.put("result", result);		
		}
		
		
		return jsonObj.toString();
	}
	
	// === 받은쪽지함에서 선택한 쪽지 읽은 상태로 변경하기  === //
	// === 중요쪽지함에서 선택한 쪽지 읽은 상태로 변경하기  === //
	@ResponseBody
	@RequestMapping(value="/jieun/note/changeReadStatus.os", produces="text/plain; charset=UTF-8")
	public String changeReadStatus(HttpServletRequest request) {
		
		// 읽음 체크할 번호(문자열)
		String str_note_no = request.getParameter("str_note_no");
		
		// 읽을 개수
		String cnt = request.getParameter("cnt");	
		
		// 배열의 [, ] 제거
		str_note_no = str_note_no.replaceAll("\\[", "");
		str_note_no = str_note_no.replaceAll("\\]", "");
		str_note_no = str_note_no.replaceAll("\"", "");
		str_note_no = str_note_no.trim(); // 공백 제거 
				
		String[] arr_note_no = str_note_no.split(",");
		
		int n = 0;
		
		for(int i=0; i<arr_note_no.length; i++) {
			System.out.println("하하하 arr_note_no ==> " + arr_note_no[i]);
			
			Map<String, String > paraMap = new HashMap<String, String>();
			paraMap.put("note_no", arr_note_no[i]);

			// update 먼저 해주기 
			n = service.receiveReadCountChange(paraMap);
		}
		
		JSONObject jsonObj = new JSONObject();
	
		jsonObj.put("n", n);		
	
		
		return jsonObj.toString();		
	}

	// === #importantList.os 중요쪽지함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/importantList.os")
	public ModelAndView requiredLogin_importantList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 정렬 타입 받아오기 
		String orderType = request.getParameter("orderType");
		
		List<NoteVO> noteImportantList = null; // 글을 하나도 작성하지 않았을 수가 있기 때문에 null 지정
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
	
		System.out.println("받은쪽지함페이지에서 로그인 ID 받아오기 ===> " + loginemp.getEmp_no());
		String fk_emp_no_receive = String.valueOf(loginemp.getEmp_no());
		
		// 중요 쪽지함 리스트에서 검색 타입과 검색어를 받아오기
		String searchType = request.getParameter("searchType"); 
		String searchWord = request.getParameter("searchWord");
		
		System.out.println("검색 타입은 ??????? " + searchType);
		System.out.println("검색어는 ??????? " + searchWord);	
	
		if(searchType == null) { 
			searchType = "";			
		}		
		
		if(searchWord == null || searchWord.trim().isEmpty()) { // searchWord.trim().isEmpty() : 공백을 제거한 후에 텅 비어있냐
			searchWord = "";			
		}
		
		// 리스트 페이징 처리하기 시작
		
		// 한 페이지 당 보여줄 리스트 개수 
		String sizePerPage = request.getParameter("sizePerPage");
		
		// sizePerPage가 null 이고 20또는 5 또는 40 이 아니라면 sizePerPage가 20이다.
		if(sizePerPage == null || 
				!("20".equals(sizePerPage) || "40".equals(sizePerPage) || "60".equals(sizePerPage)) ) {
				sizePerPage = "20";
		}		
		
		System.out.println("*** 페이징 처리할 리스트 개수는 ===> *** " + sizePerPage);
		
		// 페이지 바에서 보고있는 현재 페이지 번호
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");		
		
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("fk_emp_no_receive", fk_emp_no_receive);
		
		paraMap.put("searchWord", searchWord); // note_tilte or emp_name 넘어옴
		paraMap.put("searchType", searchType);		

	    // 검색이 있을 때만 넘겨주자(검색어 타입, 검색어 유지 시키기)
	    if(!"".equals(searchType)) {
	    	mav.addObject("searchWord", searchWord);
	    	mav.addObject("searchType", searchType);

	    }
	    
		// 먼저 총 게시물 건수(totalCount) 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총 게시물 건수
	 // int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함.
		int totalPage = 0; 		   // 총 페이지수(웹브라우저상에 보여줄 총 페이지 개수, 페이지바)		
		
		int startRno = 0; 		   // 시작 행 번호
		int endRno = 0;   		   // 끝 행 번호
		
		// 중요 쪽지함 총 게시물 건수(totalCount) 
		totalCount = service.getNoteImportantTotalCount(paraMap);
		System.out.println("확인용 보낸 쪽지함 totalCount : " + totalCount);
		
		paraMap.put("totalCount", String.valueOf(totalCount));
		
		if(totalCount == 0) {
			System.out.println("전체 count 가 0이다 게시글 없음 ;;;;;;;;;;");
			noteImportantList = service.importantList(paraMap);	
		}
		else {			
		
			// 만약에 총 게시물 건수(totalCount)가 127개 이라면
			// 총 페이지 수(totalPage)는 13개가 되어야 한다. 	    
			
			totalPage = (int)Math.ceil((double)totalCount/Integer.parseInt(sizePerPage)); 
			// (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> 13.0 ==> (int)13.0 ==> 13
			// (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> 12.0 ==> (int)12.0 ==> 12
	
			// 보낸쪽지함에서 보여지는 초기화면은 null값이다. null 대신 1페이지를 보여주게 하자.
			if(str_currentShowPageNo == null) {
				currentShowPageNo = 1;
			}
			else {
				try {
					currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
					
					if(currentShowPageNo < 1 || currentShowPageNo > totalPage ) {
						// 유저의 장난으로 현재페이지가 1보다 작거나 전체 페이지보다(만약 21) 크면 1페이지로 가게 하자.
						currentShowPageNo = 1; 
					}
				}
				catch(NumberFormatException e) {
					currentShowPageNo = 1;
				}
			}	
	
		    startRno = ((currentShowPageNo - 1 ) * Integer.parseInt(sizePerPage)) + 1;
		    endRno = startRno + Integer.parseInt(sizePerPage) - 1; 
			
		    paraMap.put("startRno", String.valueOf(startRno));
		    paraMap.put("endRno", String.valueOf(endRno));
			
			paraMap.put("orderType", orderType);
				
			// 로그인한 사원번호로 받은편지 중 중요한 편지를 쪽지테이블에서 select 해서 보여주기
			noteImportantList = service.importantList(paraMap);
		
			for(NoteVO importantvo : noteImportantList) {
				System.out.println("보낸사원ID==>" + importantvo.getFk_emp_no_send());
				System.out.println("사원명==>" + importantvo.getEmp_name());
				System.out.println("제목==>" + importantvo.getNote_title());
				System.out.println("날짜==>" + importantvo.getNote_write_date());	
				System.out.println("쪽지 번호 ==> " + importantvo.getNote_no());
			}
			
		    // 페이지바 만들기
		    String pageBar = "<ul style='list-style:none;'>";
		    
		    int blockSize = 1; 
		    // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		    /*
		            1 2 3 4 5 6 7 8 9 10  다음                   -- 1개블럭
		             이전  11 12 13 14 15 16 17 18 19 20  다음    -- 1개블럭
		             이전  21 22 23
		    */
		    
		    int loop = 1; // 10번 반복
		    /*
	           loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		    */
		    
		    int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
		    
		    String url = "importantList.os";
		      
		    // === [이전] 만들기 === 
		    if(pageNo != 1) {
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'> &#60;&#60; </a></li>"; // [맨처음]
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'> &#60; </a></li>"; // [이전]
		      }
	
		    while( !(loop > blockSize || pageNo > totalPage) ) {
		         
		         if(pageNo == currentShowPageNo) {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
		         }
		         else {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
		         }
		         
		         loop++;
		         pageNo++;
		         
		      }// end of while------------------------------
		      
		    // === [다음] 만들기 ===
		    if( !(pageNo > totalPage) ) {
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'> &#62;</a></li>"; //[다음]
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'> &#62; &#62;</a></li>"; // [마지막]
		    }
		    
		    pageBar += "</ul>";
		    
		    mav.addObject("pageBar",pageBar);
	    
		}
	    
	    String goBackURL = MyUtil.getCurrentURL(request);
	    // System.out.println("~~~ 확인용 goBackURL ==> " +goBackURL);
	    // ~~~ 확인용 goBackURL ==> list.action?searchType=&searchWord=&currentShowPageNo=2
	    
	    mav.addObject("goBackURL", goBackURL);	    
	
		mav.addObject("noteImportantList", noteImportantList);
		mav.addObject("login_emp_no", fk_emp_no_receive);
		
		mav.setViewName("jieun/note/importantList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/importantList.jsp 파일
		
		return mav;
	}	
	
	// === 중요 쪽지함의 목록에서 상세보기(제목누른경우) 페이지 요청 === //
	@RequestMapping(value="/jieun/note/importantOneDetail.os")
	public ModelAndView importantOneDetail(HttpServletRequest request, ModelAndView mav) {
		
		String note_no = request.getParameter("note_no");
		
	    //  페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //	 현재 페이지 주소를 뷰단으로 넘겨준다.
		String goBackURL = request.getParameter("goBackURL");
		
		if(goBackURL != null) {
		
			goBackURL = goBackURL.replaceAll(" ", "&"); // 이전글, 다음글을 클릭해서 넘어온 것임.		
			System.out.println("############ 확인용 goBackURL : " + goBackURL);
			
			goBackURL = goBackURL.substring(11);
			
			System.out.println("############ SubString으로 자른 후 확인용 goBackURL : " + goBackURL);
			
			// ############ 확인용 goBackURL : list.action?searchType=&searchWord=&currentShowPageNo=2
			
			mav.addObject("goBackURL",goBackURL);
		}		
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("note_no", note_no);
		
		// update 먼저 해주기 
		int n = service.receiveReadCountChange(paraMap);
		
		if(n==1) {
			System.out.println("@@@중요쪽지함의 note_read_status 상태 변경 성공!");
		}
		else {
			System.out.println("@@@중요쪽지함의 note_read_status 상태 변경 실패ㅠㅠㅠ");
		}
		
		NoteVO notevo = service.receiveOneDetail(paraMap);		
		
		mav.addObject("notevo", notevo);
		mav.setViewName("jieun/note/importantOneDetail.tiles1");
		
		return mav;
	}
	
	// === 쪽지쓰기에서 예약 발송 눌러서 보내기 누른 경우 페이지 완료 요청 === //
	//     예약 임시 테이블에 insert 한다. 
	@RequestMapping(value="/jieun/note/writeReservationEnd.os", method = RequestMethod.POST)
	public String writeReservationEnd(MultipartHttpServletRequest mrequest) {
		
		int n = 0;
		
		System.out.println("~~~~~~~~~~예약 임시보관함 테이블  테스트~~~~~~~~~~~~~~~");
		
		// 예약 날짜 받아오기
		String note_reservation_date = mrequest.getParameter("note_reservation_date"); 
		System.out.println("예약 날짜 받아와 지닝???  ====> " +  note_reservation_date);
		// 예약 날짜 받아와 지닝???  ====> 2020-12-22 20:30
		
		// 받는사원 ID
		String fk_emp_no_receive = mrequest.getParameter("fk_emp_no_receive");
		
		System.out.println("fk_emp_no_receive 받아와 지니? ==> " +  fk_emp_no_receive);
		
		String[] arr_fk_emp_no_receive = fk_emp_no_receive.split(",");
		
		for(int i=0; i<arr_fk_emp_no_receive.length; i++) {
			System.out.println("arr_fk_emp_no_receive 배열" + i + "번째 값은 ==> " + arr_fk_emp_no_receive[i]);
		}
		
		// 받는사원명
		String fk_emp_name = mrequest.getParameter("fk_emp_name");
		
		System.out.println("fk_emp_name 받아와 지니? ==> " +  fk_emp_name);
		
		String[] arr_fk_emp_name = fk_emp_name.split(",");
		
		for(int i=0; i<arr_fk_emp_name.length; i++) {
			System.out.println("arr_fk_emp_name 배열" + i + "번째 값은 ==> " + arr_fk_emp_name[i]);
		}		
		
		// 제목
		String note_title = mrequest.getParameter("note_title");
		
		// 쪽지쓰기에서 체크박스에 체크되어있으면 1이 넘어오고 아니면 0이 넘어오는데  이 값을 notevo에 set 해서 insert 할 것임
		String note_importantVal = mrequest.getParameter("note_importantVal");
		// notevo.setNote_important(Integer.parseInt(note_importantVal));
		
		// 작성자
		String fk_emp_no_send = mrequest.getParameter("fk_emp_no_send");
		
		// 첨부파일 
		MultipartFile attach = mrequest.getFile("attach");
		
		// 쪽지 내용
		String note_content = mrequest.getParameter("note_content");
		
		
		// === 사용자가 쓴 글에 파일이 첨부되어 있는 것인지, 아니면 파일이 첨부가 안되것인지 구분을 지어줘야 한다. ====
		// === 첨부파일이 있는 경우 시작 !!! ===		 
		
		// MultipartFile attach = notevo.getAttach();
		
		NoteReservationTempVO nResTempvo= new NoteReservationTempVO(); // 파일 담기위한 NoteReservationTempVO
		
		if( !attach.isEmpty()) { // 파일이 있냐 없냐 할때 사용하는 isEmpty();
			// attach(첨부파일)가 비어있지 않으면(즉, 첨부파일이 있는 경우)
			
			/*
				1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
				>>> 파일이 업로드 되어질 특정경로(폴더) 지정해주기
				       우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
				       조심할 것은 Package Explorer 에서 files 라는 폴더를 만드는 것이 아니다. 
			       
		    */			
			
			// WAS의 webapp 의 절대경로를 알아와야 한다.
			HttpSession session = mrequest.getSession();
			String root = session.getServletContext().getRealPath("/");

			String path = root + "resources" + File.separator +"files";
			
			/*
				2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기
		    */
			
			String newFilename = "";
			// WAS(톰캣)의 디스크에 저장될 파일명 
			
			byte[] bytes = null;
			// 첨부파일의 내용물
			
			long fileSize = 0;
			// 첨부파일의 크기
			
			try {
				bytes = attach.getBytes(); 
				// 첨부파일의 내용물을 읽어오는 것
				
				newFilename = fileManager.doFileUpload(bytes, attach.getOriginalFilename() , path);
				// 첨부되어진 파일을 업로드 하도록 하는 것이다. 
				// attach.getOriginalFilename() 은 첨부파일의 파일명(예: 강아지.png) 이다.
				
				System.out.println(">>> 확인용 newFilename ===>" + newFilename);
				// >>> 확인용 newFilename ===>20201209103856236590533787400.PNG
				
				/*
					3. NoteVO notevo에 fileName 값과 orgFilename 값과 fileSize 값을 넣어줘야 한다. 
				*/
				
				nResTempvo.setNote_filename(newFilename);
				// WAS(톰캣)에 저장될 파일명(20201208092715353243254235235234.png)
				
				nResTempvo.setNote_orgfilename(attach.getOriginalFilename());
				// 게시판 페이지에서 첨부된 파일(강아지.png)을 보여줄 때 사용한다.
				// 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용 
				
				fileSize = attach.getSize(); // 첨부파일의 파일 크기(단위는 byte 임)
				nResTempvo.setNote_filesize(String.valueOf(fileSize));
				
			} 
			catch(Exception e) {
				e.printStackTrace();
			}
		
		} // end of if()------------------------------------------------------
		
		// 1. 첨부파일이 없는 경우라면
		if(attach.isEmpty()) {
			
			for(int i =0; i<arr_fk_emp_no_receive.length; i++) {
				NoteReservationTempVO noteResTempvo = new NoteReservationTempVO();
				noteResTempvo.setFk_emp_no_receive(Integer.parseInt(arr_fk_emp_no_receive[i]));
				noteResTempvo.setFk_emp_name(arr_fk_emp_name[i]);
				noteResTempvo.setNote_title(note_title);
				noteResTempvo.setNote_important(Integer.parseInt(note_importantVal));
				noteResTempvo.setFk_emp_no_send(Integer.parseInt(fk_emp_no_send));
				noteResTempvo.setNote_content(note_content);
				
				// 예약 발송 버튼을 눌렀을 경우 예약 여부 컬럼의 상태 값을 1로 넣어준다. 
				
				noteResTempvo.setNote_reservation_status(1);
				noteResTempvo.setNote_reservation_date(note_reservation_date);


				// 쪽지 예약 임시 보관함테이블에 첨부파일에 첨부파일이 없는 쪽지 insert 하기
				n = service.writeReservationTemp(noteResTempvo); 
			}
			
			
		}
		else {
		// 2. 첨부파일이 있는 경우라면
			
			// nResTempvo에 set한걸 끄집어 낸다. 
			String sNote_file_name = nResTempvo.getNote_filename();
			String sNote_orgFile_name = nResTempvo.getNote_orgfilename();
			String sNote_file_size = nResTempvo.getNote_filesize();
			
			for(int i =0; i<arr_fk_emp_no_receive.length; i++) {
				NoteReservationTempVO noteResTempvo = new NoteReservationTempVO();
				noteResTempvo.setFk_emp_no_receive(Integer.parseInt(arr_fk_emp_no_receive[i]));
				noteResTempvo.setFk_emp_name(arr_fk_emp_name[i]);
				noteResTempvo.setNote_title(note_title);
				noteResTempvo.setNote_important(Integer.parseInt(note_importantVal));
				noteResTempvo.setFk_emp_no_send(Integer.parseInt(fk_emp_no_send));
				noteResTempvo.setNote_content(note_content);
				
				noteResTempvo.setNote_filename(sNote_file_name);
				noteResTempvo.setNote_orgfilename(sNote_orgFile_name);
				noteResTempvo.setNote_filesize(sNote_file_size);
				
				noteResTempvo.setNote_reservation_status(1);
				noteResTempvo.setNote_reservation_date(note_reservation_date);
				
				// 쪽지 예약 임시 보관함테이블에 첨부파일에 첨부파일이 있는 쪽지 insert 하기
				n = service.writeReservationTemp_withFile(noteResTempvo);
			}			
		}		
		
		
		if(n==1) {
			// 성공하면 현재 시간을 읽어서 select 해주기 
			service.reservationNoteSending();
			
			return "redirect:/jieun/note/reservationList.os"; 
			// list.action 페이지로 redirect(페이지 이동) 해라는 뜻이다. 
		} 
		else {
			// 실패하면 글 쓰기 페이지로 이동
			return "redirect:/jieun/note/write.os"; 
			// add.action 페이지로 redirect(페이지 이동) 해라는 뜻이다. 			
		}
		
		// return "";
		
	}	
	
	// === #reservationList.os 예약쪽지함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/reservationList.os")
	public ModelAndView requiredLogin_reservationList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
	
		List<NoteReservationTempVO> reservationTempList = null;
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
	
		System.out.println("예약쪽지함 리스트에서 로그인 ID 받아오기 ===> " + loginemp.getEmp_no());
		
		String fk_emp_no_send = String.valueOf(loginemp.getEmp_no());
		String login_emp_no = String.valueOf(loginemp.getEmp_no());
		
		// 정렬 타입 받아오기
		String orderType = request.getParameter("orderType");
		
		// 예약 쪽지함 리스트에서 검색 타입과 검색어를 받아오기
		String searchType = request.getParameter("searchType"); 
		String searchWord = request.getParameter("searchWord");
		
		System.out.println("검색 타입은 ??????? " + searchType);
		System.out.println("검색어는 ??????? " + searchWord);	
		
		if(searchType == null) { 
			searchType = "";			
		}		
		
		if(searchWord == null || searchWord.trim().isEmpty()) { // searchWord.trim().isEmpty() : 공백을 제거한 후에 텅 비어있냐
			searchWord = "";			
		}		
		
		// 리스트 페이징 처리하기 시작
		
		// 한 페이지 당 보여줄 리스트 개수 
		String sizePerPage = request.getParameter("sizePerPage");
		
		// sizePerPage가 null 이고 20또는 5 또는 40 이 아니라면 sizePerPage가 20이다.
		if(sizePerPage == null || 
				!("20".equals(sizePerPage) || "40".equals(sizePerPage) || "60".equals(sizePerPage)) ) {
				sizePerPage = "20";
		}
		
		System.out.println("*** 페이징 처리할 리스트 개수는 ===> *** " + sizePerPage);
		
		// 페이지 바에서 보고있는 현재 페이지 번호
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");		
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("fk_emp_no_send", fk_emp_no_send);
		paraMap.put("orderType", orderType); 
		
		paraMap.put("searchWord", searchWord); // note_tilte or fk_emp_name 넘어옴
		paraMap.put("searchType", searchType);

	    // 검색이 있을 때만 넘겨주자(검색어 타입, 검색어 유지 시키기)
	    if(!"".equals(searchType)) {
	    	mav.addObject("searchWord", searchWord);
	    	mav.addObject("searchType", searchType);

	    }
	    
		// 먼저 총 게시물 건수(totalCount) 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총 게시물 건수
	 // int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함.
		int totalPage = 0; 		   // 총 페이지수(웹브라우저상에 보여줄 총 페이지 개수, 페이지바)		
		
		int startRno = 0; 		   // 시작 행 번호
		int endRno = 0;   		   // 끝 행 번호
		
		// 예약 쪽지함 총 게시물 건수(totalCount) 
		totalCount = service.getNoteReservationTotalCount(paraMap);
		System.out.println("확인용 보낸 쪽지함 totalCount : " + totalCount);
		
		paraMap.put("totalCount", String.valueOf(totalCount));
		
		if(totalCount == 0) {
			System.out.println("전체 count 가 0이다 게시글 없음 ;;;;;;;;;;");
			reservationTempList = service.getReservationTempList(paraMap);
		}
		else {
			
			System.out.println("전체 count 가 0이 아니다 게시글 존재재재재재 ;;;;;;;;;;");
			
			// 만약에 총 게시물 건수(totalCount)가 127개 이라면
			// 총 페이지 수(totalPage)는 13개가 되어야 한다. 	    
			
			totalPage = (int)Math.ceil((double)totalCount/Integer.parseInt(sizePerPage)); 
			// (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> 13.0 ==> (int)13.0 ==> 13
			// (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> 12.0 ==> (int)12.0 ==> 12
			
			// 보낸쪽지함에서 보여지는 초기화면은 null값이다. null 대신 1페이지를 보여주게 하자.
			if(str_currentShowPageNo == null) {
				currentShowPageNo = 1;
			}
			else {
				try {
					currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
					
					if(currentShowPageNo < 1 || currentShowPageNo > totalPage ) {
						// 유저의 장난으로 현재페이지가 1보다 작거나 전체 페이지보다(만약 21) 크면 1페이지로 가게 하자.
						currentShowPageNo = 1; 
					}
				}
				catch(NumberFormatException e) {
					currentShowPageNo = 1;
				}
			}	    
			
		    startRno = ((currentShowPageNo - 1 ) * Integer.parseInt(sizePerPage)) + 1;
		    endRno = startRno + Integer.parseInt(sizePerPage) - 1; 
			
		    paraMap.put("startRno", String.valueOf(startRno));
		    paraMap.put("endRno", String.valueOf(endRno));		
			
			// 예약 임시테이블을 select 해서 넘기자
			// select 해서 mav로 넘기기 위함
			reservationTempList = service.getReservationTempList(paraMap);
			
		    // 페이지바 만들기
		    String pageBar = "<ul style='list-style:none;'>";
		    
		    int blockSize = 1; 
		    // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		    /*
		            1 2 3 4 5 6 7 8 9 10  다음                   -- 1개블럭
		             이전  11 12 13 14 15 16 17 18 19 20  다음    -- 1개블럭
		             이전  21 22 23
		    */
		    
		    int loop = 1; // 10번 반복
		    /*
	           loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		    */
		    
		    int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;	
		    
		    String url = "reservationList.os";
		      
		    // === [이전] 만들기 === 
		    if(pageNo != 1) {
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'> &#60;&#60; </a></li>"; // [맨처음]
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'> &#60; </a></li>"; // [이전]
		      }

		    while( !(loop > blockSize || pageNo > totalPage) ) {
		         
		         if(pageNo == currentShowPageNo) {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
		         }
		         else {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
		         }
		         
		         loop++;
		         pageNo++;
		         
		      }// end of while------------------------------
		      
		    // === [다음] 만들기 ===
		    if( !(pageNo > totalPage) ) {
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'> &#62;</a></li>"; //[다음]
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'> &#62; &#62;</a></li>"; // [마지막]
		    }
		    
		    pageBar += "</ul>";
		    
		    mav.addObject("pageBar",pageBar);			
		}
		

	    
	    // === #123. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
	    //			 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //			 현재 페이지 주소를 뷰단으로 넘겨준다. 
	    String goBackURL = MyUtil.getCurrentURL(request);
	    // System.out.println("~~~ 확인용 goBackURL ==> " +goBackURL);
	    // ~~~ 확인용 goBackURL ==> list.action?searchType=&searchWord=&currentShowPageNo=2
	    
	    mav.addObject("goBackURL", goBackURL);				
		
		mav.addObject("reservationTempList", reservationTempList);
		mav.addObject("login_emp_no", login_emp_no);
		
		mav.setViewName("jieun/note/reservationList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/reservationList.jsp 파일
		
		return mav;
	}
	
	// === 예약 쪽지함에서 제목클릭해서 온 경우 하나의 예약 쪽지 상세글 보여주기 === // 
	@RequestMapping(value="/jieun/note/reservationOneDetail.os")
	public ModelAndView reservationOneDetail(HttpServletRequest request, ModelAndView mav) {
		
		String note_no = request.getParameter("note_no");
		
	    //  페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //	 현재 페이지 주소를 뷰단으로 넘겨준다.
		String goBackURL = request.getParameter("goBackURL");
		
		if(goBackURL != null) {
		
			goBackURL = goBackURL.replaceAll(" ", "&"); // 이전글, 다음글을 클릭해서 넘어온 것임.		
			System.out.println("############ 확인용 goBackURL : " + goBackURL);
			
			goBackURL = goBackURL.substring(11);
			
			System.out.println("############ SubString으로 자른 후 확인용 goBackURL : " + goBackURL);
			
			// ############ 확인용 goBackURL : list.action?searchType=&searchWord=&currentShowPageNo=2
			
			mav.addObject("goBackURL",goBackURL);
		}		
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("note_no", note_no);
		
		// 예약 쪽지함 상세 쪽지 보기
		NoteReservationTempVO noteReservationTempvo = service.reservationOneDetail(paraMap);
		
		mav.addObject("noteReservationTempvo", noteReservationTempvo);
		mav.setViewName("jieun/note/reservationOneDetail.tiles1");
		
		return mav;
	}
	
	// === 예약 쪽지함에서 삭제 버튼 눌러서 오면 예약 임시보관테이블에 있는 내역 삭제해서 예약 발송 안되도록 하기 === //
	@ResponseBody	
	@RequestMapping(value="/jieun/note/moveToTrashcanReservation.os", produces="text/plain; charset=UTF-8")
	public String moveToTrashcanReservation(HttpServletRequest request) {
		
		// 삭제할 번호(문자열)
		String str_note_no = request.getParameter("str_note_no");
		System.out.println("하하하 str_note_no ===> " + str_note_no);
		
		// 삭제할 개수
		String cnt = request.getParameter("cnt");
		System.out.println("하하하 cnt ===> " + cnt);		
		
		// 예약 보관함에서 삭제한 사람 id가 있어야함 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		String login_emp_no = request.getParameter("login_emp_no");
		
		// 배열의 [, ] 제거
		str_note_no = str_note_no.replaceAll("\\[", "");
		str_note_no = str_note_no.replaceAll("\\]", "");
		str_note_no = str_note_no.replaceAll("\"", "");
		str_note_no = str_note_no.trim(); // 공백 제거 
		
		System.out.println("문자열에서 [, ], 쌍따움표 , 공백 제고 -->" + str_note_no);
		
		String[] arr_note_no = str_note_no.split(",");
		
		int n = 0;
		int m = 0;
		int result = 0;
		
	    // === 현재시각 나타내기 === //
	    Calendar currentDate = Calendar.getInstance(); // 현재날짜와 시간을 얻어온다. 
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		
		for(int i=0; i<arr_note_no.length; i++) {
			System.out.println("하하하 arr_note_no ==> " + arr_note_no[i]);
			
			System.out.println("왜 안되는거야야야야야 예약 보관함 삭제");
			
			Map<String, String > paraMap = new HashMap<String, String>();
			paraMap.put("note_no", arr_note_no[i]);
			
			System.out.println("note_no ======> " + paraMap.get("note_no"));
			
			// 먼저 note_no로 하나의 글 조회해서 VO로 받아오기
			NoteReservationTempVO noteReservationTempvo = service.reservationOneDetail(paraMap);
			
			
			System.out.println("@@@@@@ 예약임시보관함에서 note_no로 가져온 결과 @@@@@@"  
					+ noteReservationTempvo.getNote_no() +  "@@@ " + noteReservationTempvo.getFk_emp_no_send() + "@@@@"
					+ noteReservationTempvo.getFk_emp_no_receive() + "@@@@" + noteReservationTempvo.getFk_emp_name() + "@@@@"
					+ noteReservationTempvo.getNote_title() + "@@@@" + noteReservationTempvo.getNote_content() + "@@@@"
					+ noteReservationTempvo.getNote_filename() + "@@@@" + noteReservationTempvo.getNote_orgfilename() + "@@@@"
					+ noteReservationTempvo.getNote_filesize() + "@@@@" + noteReservationTempvo.getNote_important() + "@@@@" 
					+ noteReservationTempvo.getNote_reservation_status() + "@@@@" + noteReservationTempvo.getNote_reservation_date() + "@@@@"
					+ noteReservationTempvo.getNote_send_del_status() + "@@@@" + noteReservationTempvo.getNote_receive_del_status() + "@@@@"
					+ noteReservationTempvo.getNote_write_date());
			
			
			paraMap.put("fk_emp_no_send",String.valueOf(noteReservationTempvo.getFk_emp_no_send()));
			paraMap.put("fk_emp_no_receive",String.valueOf(noteReservationTempvo.getFk_emp_no_receive()));
			paraMap.put("fk_emp_name", noteReservationTempvo.getFk_emp_name());
			paraMap.put("note_title", noteReservationTempvo.getNote_title());
			paraMap.put("note_content", noteReservationTempvo.getNote_content());
			paraMap.put("note_write_date", noteReservationTempvo.getNote_write_date());
			paraMap.put("note_filename", noteReservationTempvo.getNote_filename());
			paraMap.put("note_orgfilename", noteReservationTempvo.getNote_orgfilename());
			paraMap.put("note_filesize", noteReservationTempvo.getNote_filesize());
   			paraMap.put("note_important", String.valueOf(noteReservationTempvo.getNote_important()));
   			paraMap.put("note_reservation_status", String.valueOf(noteReservationTempvo.getNote_reservation_status()));
   			paraMap.put("note_read_status", String.valueOf(noteReservationTempvo.getNote_read_status()));
   			
   			if(noteReservationTempvo.getNote_reservation_date() != null) {
   				System.out.println("예약 값 있음이 넘ㅇ어간다 ------------------");   				
   				paraMap.put("note_reservation_date", noteReservationTempvo.getNote_reservation_date());
   			}
   			else {
   				System.out.println("예약 값 없음면 예약 컬럼에 그냥 현재 시간  넘ㅇ어간다 ------------------");
   				paraMap.put("note_reservation_date", df.format(currentDate.getTime()));
   			}
   			
   			
   			paraMap.put("note_send_del_status", String.valueOf(noteReservationTempvo.getNote_send_del_status()));
   			paraMap.put("note_receive_del_status", String.valueOf(noteReservationTempvo.getNote_receive_del_status()));			
			
			// 삭제버튼 누른 사원 id를 paraMap에 넣자 
			paraMap.put("note_del_login_emp_no", login_emp_no);
			
			// 문제가 되는 부분 첨부파일 null 값, reservation_date 컬럼 null 값
			// 첨부파일 여부 확인해서 null 일때 아닐때 넣기
			if(noteReservationTempvo.getNote_filename() != null) {
				// 먼저 예약임시보관함 테이블을 휴지통 테이블에 insert 하기
				n = service.moveToTrashcanReservation(paraMap);				
			}
			else {
				// 먼저 예약임시보관함 테이블을 휴지통 테이블에 첨부파일 없는 경우 insert 하기 moveToTrashcanReservationNoFile
				n = service.moveToTrashcanReservationNoFile(paraMap);	
			}   			
   			
			if(n == 1) {
				// 예약임시보관함 테이블은 임시저장 기능이니까 테이블 자체를 비워도 됨
				// 예약 임시보관함 테이블에서 해당 note_no를 삭제
				m = service.deleteFromTblNoteReservationTemp(arr_note_no[i]);			
				
			}			
			
			 
		}
		
		JSONObject jsonObj = new JSONObject();
	      
		if(n== 1 && m == 1) {
			result = 1;
			jsonObj.put("result", result);		
		}
		
		
		return jsonObj.toString();
	}	
	
	
	// === 임시보관함으로 쪽지쓰기 페이지 완료 요청(임시보관함 테이블로 insert) === //
	@RequestMapping(value="/jieun/note/writeTempEnd.os", method = RequestMethod.POST)
	public String writeTempEnd(MultipartHttpServletRequest mrequest) {
		
		System.out.println("~~~~~~~~~~임시보관함 테이블  테스트~~~~~~~~~~~~~~~");
		
		// 받는사원 ID
		String fk_emp_no_receive = mrequest.getParameter("fk_emp_no_receive");
		
		System.out.println("fk_emp_no_receive 받아와 지니? ==> " +  fk_emp_no_receive);
		
		String[] arr_fk_emp_no_receive = fk_emp_no_receive.split(",");
		
		for(int i=0; i<arr_fk_emp_no_receive.length; i++) {
			System.out.println("arr_fk_emp_no_receive 배열" + i + "번째 값은 ==> " + arr_fk_emp_no_receive[i]);
		}
		
		// 받는사원명
		String fk_emp_name = mrequest.getParameter("fk_emp_name");
		
		System.out.println("fk_emp_name 받아와 지니? ==> " +  fk_emp_name);
		
		String[] arr_fk_emp_name = fk_emp_name.split(",");
		
		for(int i=0; i<arr_fk_emp_name.length; i++) {
			System.out.println("arr_fk_emp_name 배열" + i + "번째 값은 ==> " + arr_fk_emp_name[i]);
		}		
		
		// 제목
		String note_title = mrequest.getParameter("note_title");
		
		// 쪽지쓰기에서 체크박스에 체크되어있으면 1이 넘어오고 아니면 0이 넘어오는데  이 값을 notevo에 set 해서 insert 할 것임
		String note_importantVal = mrequest.getParameter("note_importantVal");
		// notevo.setNote_important(Integer.parseInt(note_importantVal));
		
		// 작성자
		String fk_emp_no_send = mrequest.getParameter("fk_emp_no_send");
		
		// 첨부파일 
		MultipartFile attach = mrequest.getFile("attach");
		
		// 쪽지 내용
		String note_content = mrequest.getParameter("note_content");
		
		// === 사용자가 쓴 글에 파일이 첨부되어 있는 것인지, 아니면 파일이 첨부가 안되것인지 구분을 지어줘야 한다. ====
		// === 첨부파일이 있는 경우 시작 !!! ===		 
		
		// MultipartFile attach = notevo.getAttach();
		
		NoteTempVO nTempvo= new NoteTempVO(); // 파일 담기위한 NoteVO
		
		if( !attach.isEmpty()) { // 파일이 있냐 없냐 할때 사용하는 isEmpty();
			// attach(첨부파일)가 비어있지 않으면(즉, 첨부파일이 있는 경우)
			
			/*
				1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
				>>> 파일이 업로드 되어질 특정경로(폴더) 지정해주기
				       우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
				       조심할 것은 Package Explorer 에서 files 라는 폴더를 만드는 것이 아니다. 
			       
		    */			
			
			// WAS의 webapp 의 절대경로를 알아와야 한다.
			HttpSession session = mrequest.getSession();
			String root = session.getServletContext().getRealPath("/");

			String path = root + "resources" + File.separator +"files";
			
			/*
				2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기
		    */
			
			String newFilename = "";
			// WAS(톰캣)의 디스크에 저장될 파일명 
			
			byte[] bytes = null;
			// 첨부파일의 내용물
			
			long fileSize = 0;
			// 첨부파일의 크기
			
			try {
				bytes = attach.getBytes(); 
				// 첨부파일의 내용물을 읽어오는 것
				
				newFilename = fileManager.doFileUpload(bytes, attach.getOriginalFilename() , path);
				// 첨부되어진 파일을 업로드 하도록 하는 것이다. 
				// attach.getOriginalFilename() 은 첨부파일의 파일명(예: 강아지.png) 이다.
				
				System.out.println(">>> 확인용 newFilename ===>" + newFilename);
				// >>> 확인용 newFilename ===>20201209103856236590533787400.PNG
				
				/*
					3. NoteVO notevo에 fileName 값과 orgFilename 값과 fileSize 값을 넣어줘야 한다. 
				*/
				
				nTempvo.setNote_filename(newFilename);
				// WAS(톰캣)에 저장될 파일명(20201208092715353243254235235234.png)
				
				nTempvo.setNote_orgfilename(attach.getOriginalFilename());
				// 게시판 페이지에서 첨부된 파일(강아지.png)을 보여줄 때 사용한다.
				// 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용 
				
				fileSize = attach.getSize(); // 첨부파일의 파일 크기(단위는 byte 임)
				nTempvo.setNote_filesize(String.valueOf(fileSize));
				
			} 
			catch(Exception e) {
				e.printStackTrace();
			}
		
		} // end of if()------------------------------------------------------
		
		int n = 0;
		// 1. 첨부파일이 없는 경우라면
		if(attach.isEmpty()) {
			
			for(int i =0; i<arr_fk_emp_no_receive.length; i++) {
				NoteTempVO noteTempvo = new NoteTempVO();
				noteTempvo.setFk_emp_no_receive(Integer.parseInt(arr_fk_emp_no_receive[i]));
				noteTempvo.setFk_emp_name(arr_fk_emp_name[i]);
				noteTempvo.setNote_title(note_title);
				noteTempvo.setNote_important(Integer.parseInt(note_importantVal));
				noteTempvo.setFk_emp_no_send(Integer.parseInt(fk_emp_no_send));
				noteTempvo.setNote_content(note_content);

				// 쪽지임시보관테이블에 첨부파일이 없는 쪽지 insert 하기
				n = service.writeTemp(noteTempvo);
			}
			
			
		}
		else {
		// 2. 첨부파일이 있는 경우라면
			
			// nTempvo에 set한걸 끄집어 낸다. 
			String sNote_file_name = nTempvo.getNote_filename();
			String sNote_orgFile_name = nTempvo.getNote_orgfilename();
			String sNote_file_size = nTempvo.getNote_filesize();
			
			for(int i =0; i<arr_fk_emp_no_receive.length; i++) {
				NoteTempVO noteTempvo = new NoteTempVO();
				noteTempvo.setFk_emp_no_receive(Integer.parseInt(arr_fk_emp_no_receive[i]));
				noteTempvo.setFk_emp_name(arr_fk_emp_name[i]);
				noteTempvo.setNote_title(note_title);
				noteTempvo.setNote_important(Integer.parseInt(note_importantVal));
				noteTempvo.setFk_emp_no_send(Integer.parseInt(fk_emp_no_send));
				noteTempvo.setNote_content(note_content);
				
				noteTempvo.setNote_filename(sNote_file_name);
				noteTempvo.setNote_orgfilename(sNote_orgFile_name);
				noteTempvo.setNote_filesize(sNote_file_size);
				
				// 쪽지임시보관테이블에 첨부파일이 있는 쪽지 insert 하기
				n = service.writeTemp_withFile(noteTempvo);
			}			
		}
		
		if(n==1) {
			// 성공하면 임시보관함으로 이동
			return "redirect:/jieun/note/tempList.os"; 
			// list.action 페이지로 redirect(페이지 이동) 해라는 뜻이다. 
		} 
		else {
			// 실패하면 글 쓰기 페이지로 이동
			return "redirect:/jieun/note/write.os"; 
			// add.action 페이지로 redirect(페이지 이동) 해라는 뜻이다. 			
		}
	}

	// === #tempList.os 임시보관함 페이지 요청 === //
	@RequestMapping(value="/jieun/note/tempList.os")
	public ModelAndView requiredLogin_tempList(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		List<NoteTempVO> noteTempList = null; // 글을 하나도 작성하지 않았을 수가 있기 때문에 null 지정
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
	
		System.out.println("임시보관함페이지에서 로그인 ID 받아오기 ===> " + loginemp.getEmp_no());
		
		String fk_emp_no_send = String.valueOf(loginemp.getEmp_no());		
		
		// 정렬 타입 받아오기
		String orderType = request.getParameter("orderType");
		
		// 임시보관함 리스트에서 검색 타입과 검색어를 받아오기
		String searchType = request.getParameter("searchType"); 
		String searchWord = request.getParameter("searchWord");
		
		System.out.println("검색 타입은 ??????? " + searchType);
		System.out.println("검색어는 ??????? " + searchWord);	
		
		if(searchType == null) { 
			searchType = "";			
		}		
		
		if(searchWord == null || searchWord.trim().isEmpty()) { // searchWord.trim().isEmpty() : 공백을 제거한 후에 텅 비어있냐
			searchWord = "";			
		}		
		
		// 리스트 페이징 처리하기 시작
		
		// 한 페이지 당 보여줄 리스트 개수 
		String sizePerPage = request.getParameter("sizePerPage");
		
		// sizePerPage가 null 이고 20또는 5 또는 40 이 아니라면 sizePerPage가 20이다.
		if(sizePerPage == null || 
				!("20".equals(sizePerPage) || "40".equals(sizePerPage) || "60".equals(sizePerPage)) ) {
				sizePerPage = "20";
		}
		
		System.out.println("*** 페이징 처리할 리스트 개수는 ===> *** " + sizePerPage);
		
		// 페이지 바에서 보고있는 현재 페이지 번호
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");		

		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("fk_emp_no_send", fk_emp_no_send);
		paraMap.put("orderType", orderType);
		
		paraMap.put("searchWord", searchWord); // note_tilte or fk_emp_name 넘어옴
		paraMap.put("searchType", searchType);
		
	    // 검색이 있을 때만 넘겨주자(검색어 타입, 검색어 유지 시키기)
	    if(!"".equals(searchType)) {
	    	mav.addObject("searchWord", searchWord);
	    	mav.addObject("searchType", searchType);

	    }
	    
		// 먼저 총 게시물 건수(totalCount) 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총 게시물 건수
	 // int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함.
		int totalPage = 0; 		   // 총 페이지수(웹브라우저상에 보여줄 총 페이지 개수, 페이지바)		
		
		int startRno = 0; 		   // 시작 행 번호
		int endRno = 0;   		   // 끝 행 번호
		
		// 임시보관함 총 게시물 건수(totalCount) 
		totalCount = service.getNoteTempTotalCount(paraMap);
		System.out.println("확인용 보낸 쪽지함 totalCount : " + totalCount);
		
		paraMap.put("totalCount", String.valueOf(totalCount));
		
		if(totalCount == 0) {
			System.out.println("전체 count 가 0이다 게시글 없음 ;;;;;;;;;;");
			// 쪽지를 쓰다가 임시저장했을때 임시보관함 테이블에서 임시저장된 쪽지를 select 해서 보여주기
			noteTempList = service.tempList(paraMap);			
		}
		else {		
		
			// 만약에 총 게시물 건수(totalCount)가 127개 이라면
			// 총 페이지 수(totalPage)는 13개가 되어야 한다. 	    
			
			totalPage = (int)Math.ceil((double)totalCount/Integer.parseInt(sizePerPage)); 
			// (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> 13.0 ==> (int)13.0 ==> 13
			// (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> 12.0 ==> (int)12.0 ==> 12
			
			// 보낸쪽지함에서 보여지는 초기화면은 null값이다. null 대신 1페이지를 보여주게 하자.
			if(str_currentShowPageNo == null) {
				currentShowPageNo = 1;
			}
			else {
				try {
					currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
					
					if(currentShowPageNo < 1 || currentShowPageNo > totalPage ) {
						// 유저의 장난으로 현재페이지가 1보다 작거나 전체 페이지보다(만약 21) 크면 1페이지로 가게 하자.
						currentShowPageNo = 1; 
					}
				}
				catch(NumberFormatException e) {
					currentShowPageNo = 1;
				}
			}
			
			// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
		    /*
		            currentShowPageNo      startRno     endRno
		           --------------------------------------------
		               1 page        ===>    1           10
		               2 page        ===>    11          20
		               3 page        ===>    21          30
		               4 page        ===>    31          40
		               ......                ...         ...
		    */
		   
		    startRno = ((currentShowPageNo - 1 ) * Integer.parseInt(sizePerPage)) + 1;
		    endRno = startRno + Integer.parseInt(sizePerPage) - 1; 
			
		    paraMap.put("startRno", String.valueOf(startRno));
		    paraMap.put("endRno", String.valueOf(endRno));	    
			
			// 쪽지를 쓰다가 임시저장했을때 임시보관함 테이블에서 임시저장된 쪽지를 select 해서 보여주기
			noteTempList = service.tempList(paraMap);
			
			for(NoteTempVO tempvo : noteTempList) {
				System.out.println("쪽지를 누구에게 보내려다가 임시저장 했어?==>" + tempvo.getFk_emp_no_receive());
				System.out.println("받는사원명==>" + tempvo.getFk_emp_name());
				System.out.println("제목==>" + tempvo.getNote_title());
				System.out.println("날짜==>" + tempvo.getNote_write_date());			
			}		
			
		    // 페이지바 만들기
		    String pageBar = "<ul style='list-style:none;'>";
		    
		    int blockSize = 1; 
		    // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		    /*
		            1 2 3 4 5 6 7 8 9 10  다음                   -- 1개블럭
		             이전  11 12 13 14 15 16 17 18 19 20  다음    -- 1개블럭
		             이전  21 22 23
		    */
		    
		    int loop = 1; // 10번 반복
		    /*
	           loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		    */
		    
		    int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
	
		    String url = "tempList.os";
		      
		    // === [이전] 만들기 === 
		    if(pageNo != 1) {
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'> &#60;&#60; </a></li>"; // [맨처음]
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'> &#60; </a></li>"; // [이전]
		      }
	
		    while( !(loop > blockSize || pageNo > totalPage) ) {
		         
		         if(pageNo == currentShowPageNo) {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
		         }
		         else {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
		         }
		         
		         loop++;
		         pageNo++;
		         
		      }// end of while------------------------------
		      
		    // === [다음] 만들기 ===
		    if( !(pageNo > totalPage) ) {
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'> &#62;</a></li>"; //[다음]
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'> &#62; &#62;</a></li>"; // [마지막]
		    }
		    
		    pageBar += "</ul>";
		    
		    mav.addObject("pageBar",pageBar);
		    
		}
		
	    
	    // === #123. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
	    //			 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //			 현재 페이지 주소를 뷰단으로 넘겨준다. 
		/*
	    String goBackURL = MyUtil.getCurrentURL(request);
	    // System.out.println("~~~ 확인용 goBackURL ==> " +goBackURL);
	    // ~~~ 확인용 goBackURL ==> list.action?searchType=&searchWord=&currentShowPageNo=2
	    
	    mav.addObject("goBackURL", goBackURL);				
		*/
		
		mav.addObject("noteTempList", noteTempList);
		mav.addObject("login_emp_no", fk_emp_no_send);
		
		mav.setViewName("jieun/note/tempList.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/importantList.jsp 파일
		
		return mav;
	}		
	
	// === 임시 보관 쪽지함 테이블에서 삭제 버튼 눌러서 오면 임시보관 쪽지테이블에 있는 내역 삭제하기 === //
	@ResponseBody	
	@RequestMapping(value="/jieun/note/moveToTrashcanTemp.os", produces="text/plain; charset=UTF-8")
	public String moveToTrashcanTemp(HttpServletRequest request) {
		
		// 삭제할 번호(문자열)
		String str_note_no = request.getParameter("str_note_no");
		System.out.println("하하하 str_note_no ===> " + str_note_no);
		
		// 삭제할 개수
		String cnt = request.getParameter("cnt");
		System.out.println("하하하 cnt ===> " + cnt);		
		
		// 삭제 버튼을 누를 사원 id 
		String login_emp_no = request.getParameter("login_emp_no");
		System.out.println("#### 임시보관함 넘어온 login_emp_no의  값은??????????????????????? " + login_emp_no);		
		
		// 배열의 [, ] 제거
		str_note_no = str_note_no.replaceAll("\\[", "");
		str_note_no = str_note_no.replaceAll("\\]", "");
		str_note_no = str_note_no.replaceAll("\"", "");
		str_note_no = str_note_no.trim(); // 공백 제거 
		
		System.out.println("문자열에서 [, ], 쌍따움표 , 공백 제고 -->" + str_note_no);
		
		String[] arr_note_no = str_note_no.split(",");
		
		int n = 0;
		int m = 0;
		int result = 0;
		
		// 현재 날짜 구하기
	    Calendar currentDate = Calendar.getInstance(); // 현재날짜와 시간을 얻어온다. 
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	    System.out.println("배열 길이 출력 -----< > <> " + arr_note_no.length);
	    
		for(int i=0; i<arr_note_no.length; i++) {
			System.out.println("하하하 arr_note_no ==> " + arr_note_no[i]);
			
			System.out.println("왜 안되는거야야야야야 예약 보관함 삭제");
			
			Map<String, String > paraMap = new HashMap<String, String>();
			paraMap.put("note_no", arr_note_no[i]);
			
			// 임시보관함 테이블에서 note_no로 select 한 결과를 vo에 담아오자
			NoteTempVO noteTempvo = service.writeModifySelect(paraMap);
			
			System.out.println("@@@@@@ 임시보관함에서 note_no로 가져온 결과 @@@@@@"  
					+ noteTempvo.getNote_no() +  "@@@ " + noteTempvo.getFk_emp_no_send() + "@@@@"
					+ noteTempvo.getFk_emp_no_receive() + "@@@@" + noteTempvo.getFk_emp_name() + "@@@@"
					+ noteTempvo.getNote_title() + "@@@@" + noteTempvo.getNote_content() + "@@@@"
					+ noteTempvo.getNote_filename() + "@@@@" + noteTempvo.getNote_orgfilename() + "@@@@"
					+ noteTempvo.getNote_filesize() + "@@@@" + noteTempvo.getNote_important() + "@@@@" 
					+ noteTempvo.getNote_reservation_status() + "@@@@" + noteTempvo.getNote_reservation_date() + "@@@@"
					+ noteTempvo.getNote_send_del_status() + "@@@@" + noteTempvo.getNote_receive_del_status() + "@@@@"
					+ noteTempvo.getNote_write_date());
			
			
			paraMap.put("fk_emp_no_send",String.valueOf(noteTempvo.getFk_emp_no_send()));
			paraMap.put("fk_emp_no_receive",String.valueOf(noteTempvo.getFk_emp_no_receive()));
			paraMap.put("fk_emp_name", noteTempvo.getFk_emp_name());
			paraMap.put("note_title", noteTempvo.getNote_title());
			paraMap.put("note_content", noteTempvo.getNote_content());
			paraMap.put("note_write_date", noteTempvo.getNote_write_date());
			paraMap.put("note_filename", noteTempvo.getNote_filename());
			paraMap.put("note_orgfilename", noteTempvo.getNote_orgfilename());
			paraMap.put("note_filesize", noteTempvo.getNote_filesize());
   			paraMap.put("note_important", String.valueOf(noteTempvo.getNote_important()));
   			paraMap.put("note_reservation_status", String.valueOf(noteTempvo.getNote_reservation_status()));
   			paraMap.put("note_read_status", String.valueOf(noteTempvo.getNote_read_status()));
   			
   			if(noteTempvo.getNote_reservation_date() != null) {
   				System.out.println("예약 값 있음이 넘ㅇ어간다 ------------------");   				
   				paraMap.put("note_reservation_date", noteTempvo.getNote_reservation_date());
   			}
   			else {
   				System.out.println("예약 값 없음면 예약 컬럼에 그냥 현재 시간  넘ㅇ어간다 ------------------");
   				paraMap.put("note_reservation_date", df.format(currentDate.getTime()));
   			}
   			
   			
   			paraMap.put("note_send_del_status", String.valueOf(noteTempvo.getNote_send_del_status()));
   			paraMap.put("note_receive_del_status", String.valueOf(noteTempvo.getNote_receive_del_status()));			
			
			// 삭제버튼 누른 사원 id를 paraMap에 넣자 
			paraMap.put("note_del_login_emp_no", login_emp_no);
			
			// 문제가 되는 부분 첨부파일 null 값, reservation_date 컬럼 null 값
			// 첨부파일 여부 확인해서 null 일때 아닐때 넣기
			if(noteTempvo.getNote_filename() != null) {
				// 먼저 임시보관함 테이블을 휴지통 테이블에 insert 하기
				n = service.moveToTrashcanTemp(paraMap);
			}
			else {
				// 먼저 임시보관함 테이블을 휴지통 테이블에 첨부파일 없는 경우 insert 하기
				n = service.moveToTrashcanTempNoFile(paraMap);
			}   			
   			

			
			if(n == 1) {
				// 임시보관함 테이블은 임시저장 기능이니까 테이블 자체를 비워도 됨
				// 임시보관함 테이블에서 해당 note_no를 삭제
				m = service.deleteFromTblTemp(paraMap);					
				
			}			
		} // end of for()-------------------
		
		JSONObject jsonObj = new JSONObject();
	      
		if(n== 1 && m == 1) {
			result = 1;
			jsonObj.put("result", result);		
		}
		
		
		return jsonObj.toString();
	}		
	
	// === #trash.os 휴지통 페이지 요청 === //
	@RequestMapping(value="/jieun/note/trash.os")
	public ModelAndView requiredLogin_trash(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
				
		List<NoteTrashVO> noteTrashList = null; // 글이 하나도 삭제되지 않았을 수가 있기 때문에 null 지정
		
		// 정렬 타입 받아오기 
		String orderType = request.getParameter("orderType");		
		
		// 휴지통 리스트에서 검색 타입과 검색어를 받아오기
		String searchType = request.getParameter("searchType"); 
		String searchWord = request.getParameter("searchWord");
		
		System.out.println("검색 타입은 ??????? " + searchType);
		System.out.println("검색어는 ??????? " + searchWord);			
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
	
		System.out.println("휴지통에서 로그인 ID 받아오기 ===> " + loginemp.getEmp_no());
		
		String fk_emp_no_receive = String.valueOf(loginemp.getEmp_no());
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("fk_emp_no_receive", fk_emp_no_receive);
		paraMap.put("orderType", orderType);
		
		paraMap.put("searchWord", searchWord); // note_tilte or fk_emp_name 넘어옴
		paraMap.put("searchType", searchType);
		
		if(searchType == null) { 
			searchType = "";			
		}		
		
		if(searchWord == null || searchWord.trim().isEmpty()) { // searchWord.trim().isEmpty() : 공백을 제거한 후에 텅 비어있냐
			searchWord = "";			
		}		
		
	    // 검색이 있을 때만 넘겨주자(검색어 타입, 검색어 유지 시키기)
	    if(!"".equals(searchType)) {
	    	mav.addObject("searchWord", searchWord);
	    	mav.addObject("searchType", searchType);

	    }			
		
		// 리스트 페이징 처리하기 시작
		
		// 한 페이지 당 보여줄 리스트 개수 
		String sizePerPage = request.getParameter("sizePerPage");
		
		// sizePerPage가 null 이고 20또는 5 또는 40 이 아니라면 sizePerPage가 20이다.
		if(sizePerPage == null || 
				!("20".equals(sizePerPage) || "40".equals(sizePerPage) || "60".equals(sizePerPage)) ) {
				sizePerPage = "20";
		}		
		
		// 페이지 바에서 보고있는 현재 페이지 번호
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");		
	
		
		// 먼저 총 게시물 건수(totalCount) 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총 게시물 건수
	 // int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함.
		int totalPage = 0; 		   // 총 페이지수(웹브라우저상에 보여줄 총 페이지 개수, 페이지바)		
		
		int startRno = 0; 		   // 시작 행 번호
		int endRno = 0;   		   // 끝 행 번호
		
		// 휴지통 총 게시물 건수(totalCount) 
		totalCount = service.getNoteTrashTotalCount(paraMap);
		System.out.println("확인용 보낸 쪽지함 totalCount : " + totalCount);
		
		paraMap.put("totalCount", String.valueOf(totalCount));
		
		if(totalCount == 0) {
			System.out.println("전체 count 가 0이다 게시글 없음 ;;;;;;;;;;");
			noteTrashList = service.trashList(paraMap);		
		}
		else {	
		
			// 만약에 총 게시물 건수(totalCount)가 127개 이라면
			// 총 페이지 수(totalPage)는 13개가 되어야 한다. 	    
			
			totalPage = (int)Math.ceil((double)totalCount/Integer.parseInt(sizePerPage)); 
			// (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> 13.0 ==> (int)13.0 ==> 13
			// (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> 12.0 ==> (int)12.0 ==> 12
			
			// 보낸쪽지함에서 보여지는 초기화면은 null값이다. null 대신 1페이지를 보여주게 하자.
			if(str_currentShowPageNo == null) {
				currentShowPageNo = 1;
			}
			else {
				try {
					currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
					
					if(currentShowPageNo < 1 || currentShowPageNo > totalPage ) {
						// 유저의 장난으로 현재페이지가 1보다 작거나 전체 페이지보다(만약 21) 크면 1페이지로 가게 하자.
						currentShowPageNo = 1; 
					}
				}
				catch(NumberFormatException e) {
					currentShowPageNo = 1;
				}
			}
			
			// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
		    /*
		            currentShowPageNo      startRno     endRno
		           --------------------------------------------
		               1 page        ===>    1           10
		               2 page        ===>    11          20
		               3 page        ===>    21          30
		               4 page        ===>    31          40
		               ......                ...         ...
		    */
		   
		    startRno = ((currentShowPageNo - 1 ) * Integer.parseInt(sizePerPage)) + 1;
		    endRno = startRno + Integer.parseInt(sizePerPage) - 1; 
			
		    paraMap.put("startRno", String.valueOf(startRno));
		    paraMap.put("endRno", String.valueOf(endRno));	    
		    
			// 쪽지를 보다가 삭제 버튼을 눌렀을때 쪽지테이블에서 휴지통 테이블로 이동한 후에 쪽지테이블에 해당 쪽지 번호가 삭제된다. 
			// 쪽지 삭제했을때 휴지통에 있는 목록 select 해서 보여주기
			noteTrashList = service.trashList(paraMap);
			
			for(NoteTrashVO trashvo : noteTrashList) {
				System.out.println("휴지통 시작");
				System.out.println("받는사원명==>" + trashvo.getFk_emp_name());
				System.out.println("제목==>" + trashvo.getNote_title());
				System.out.println("날짜==>" + trashvo.getNote_write_date());			
			}		
			
		    // 페이지바 만들기
		    String pageBar = "<ul style='list-style:none;'>";
		    
		    int blockSize = 1; 
		    // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		    /*
		            1 2 3 4 5 6 7 8 9 10  다음                   -- 1개블럭
		             이전  11 12 13 14 15 16 17 18 19 20  다음    -- 1개블럭
		             이전  21 22 23
		    */
		    
		    int loop = 1; // 10번 반복
		    /*
	           loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		    */
		    
		    int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
		    
		    String url = "trash.os";
		      
		    // === [이전] 만들기 === 
		    if(pageNo != 1) {
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'> &#60;&#60; </a></li>"; // [맨처음]
		         pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'> &#60; </a></li>"; // [이전]
		      }
	
		    while( !(loop > blockSize || pageNo > totalPage) ) {
		         
		         if(pageNo == currentShowPageNo) {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
		         }
		         else {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
		         }
		         
		         loop++;
		         pageNo++;
		         
		      }// end of while------------------------------
		      
		    // === [다음] 만들기 ===
		    if( !(pageNo > totalPage) ) {
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'> &#62;</a></li>"; //[다음]
		       pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'> &#62; &#62;</a></li>"; // [마지막]
		    }
		    
		    pageBar += "</ul>";
		    
		    mav.addObject("pageBar",pageBar);
	    
		}
	    
	    // === #123. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
	    //			 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //			 현재 페이지 주소를 뷰단으로 넘겨준다. 
	    String goBackURL = MyUtil.getCurrentURL(request);
	    // System.out.println("~~~ 확인용 goBackURL ==> " +goBackURL);
	    // ~~~ 확인용 goBackURL ==> list.action?searchType=&searchWord=&currentShowPageNo=2
	    
	    mav.addObject("goBackURL", goBackURL);				
		
		mav.addObject("noteTrashList", noteTrashList);		
		
		mav.setViewName("jieun/note/trash.tiles1");
		//		/WEB-INF/views/tiles1/jieun/note/trash.jsp 파일
		
		return mav;
	}	
	
	// === 휴지통 쪽지 상세 보기 페이지 요청 === //
	@RequestMapping(value="/jieun/note/trashOneDetail.os")
	public ModelAndView trashOneDetail(HttpServletRequest request, ModelAndView mav) {
		
		String note_no = request.getParameter("note_no");
		
	    //  페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해 
	    //	 현재 페이지 주소를 뷰단으로 넘겨준다.
		String goBackURL = request.getParameter("goBackURL");
		
		if(goBackURL != null) {
		
			goBackURL = goBackURL.replaceAll(" ", "&"); // 이전글, 다음글을 클릭해서 넘어온 것임.		
			System.out.println("############ 확인용 goBackURL : " + goBackURL);
			
			goBackURL = goBackURL.substring(11);
			
			System.out.println("############ SubString으로 자른 후 확인용 goBackURL : " + goBackURL);
			
			// ############ 확인용 goBackURL : list.action?searchType=&searchWord=&currentShowPageNo=2
			
			mav.addObject("goBackURL",goBackURL);
		}		
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("note_no", note_no);
		
		// 휴지통 쪽지 상세 쪽지 보기 
		NoteTrashVO noteTrashvo = service.trashOneDetail(paraMap);
		mav.addObject("noteTrashvo", noteTrashvo);
		
		mav.setViewName("jieun/note/trashOneDetail.tiles1");
		
		return mav;
	}
	
	// === 휴지통에서 삭제버튼 눌렀을때 휴지통에서 note_no를 삭제 === // 
	@ResponseBody
	@RequestMapping(value="/jieun/note/deleteFromTblTrash.os")
	public String deleteFromTblTrash(HttpServletRequest request) {
		
		// 삭제할 번호(문자열)
		String str_note_no = request.getParameter("str_note_no");
		System.out.println("하하하 str_note_no ===> " + str_note_no);
		
		// 삭제할 개수
		String cnt = request.getParameter("cnt");
		System.out.println("하하하 cnt ===> " + cnt);		
		
		// 배열의 [, ] 제거
		str_note_no = str_note_no.replaceAll("\\[", "");
		str_note_no = str_note_no.replaceAll("\\]", "");
		str_note_no = str_note_no.replaceAll("\"", "");
		str_note_no = str_note_no.trim(); // 공백 제거 
		
		System.out.println("문자열에서 [, ], 쌍따움표 , 공백 제고 -->" + str_note_no);
		
		String[] arr_note_no = str_note_no.split(",");
		
		int n = 0;
		int m = 0;
		int result = 0;		
		
		for(int i=0; i<arr_note_no.length; i++) {
			System.out.println("하하하 arr_note_no ==> " + arr_note_no[i]);
			
			System.out.println("왜 안되는거야야야야야 휴지통 삭제");
			
			Map<String, String > paraMap = new HashMap<String, String>();
			paraMap.put("note_no", arr_note_no[i]);
			
			n = service.deleteFromTblTrash(paraMap);
					
		} // end of for()-------------------		
		
		JSONObject jsonObj = new JSONObject();
	      
		if(n== 1) {
			result = 1;
			jsonObj.put("result", result);		
		}
		
		return jsonObj.toString();	
	}
	
	// === 휴지통 비우기 페이지 요청 === //
	@RequestMapping(value="/jieun/note/trashClear.os")
	public ModelAndView trashClear(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 로그인 세션 받아오기
		HttpSession session = request.getSession();
		EmployeeVO loginemp = (EmployeeVO)session.getAttribute("loginemp");
	
		System.out.println("휴지통 비우기에서 로그인 ID 받아오기 ===> " + loginemp.getEmp_no());		
		
		int note_del_login_emp_no = loginemp.getEmp_no();
		
		// 휴지통 테이블에서 휴지통 자체를 비우기!!!(해당 로그인한 사람의 휴지통을 비우자)
		int n = service.deleteFromTblTrashClear(note_del_login_emp_no);
		
		String message = "";
		String loc = "";
		
		
		if(n > 0) {
			// delete 된 행이 존재
			message = "휴지통을 비웠습니다.";
			loc = request.getContextPath() + "/jieun/note/trash.os";
			
			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");					
		}
		else {
			// delete 된 행이 없다
			message = "휴지통 비울게 없습니다.";
			loc = request.getContextPath() + "/jieun/note/trash.os";
			
			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");					
		}
		
		return mav;
	}		
	
	// === 첨부파일 다운로드 받기  === //
	@RequestMapping(value="/jieun/note/download.os")
    public void requiredLogin_download(HttpServletRequest request, HttpServletResponse response) {
    	
    	String note_no = request.getParameter("note_no");
    	// 첨부파일이 있는 쪽지번호
    	
    	/*
    		첨부파일이 있는  쪽지번호에서 
    		20201209114628240642365709200.PNG 처럼
    		이러한 fileName 값을 DB에서 가져와야 한다. 
    		또한 orgFilename(60_3.PNG) 값도 DB에서 가져와야 한다.
    	*/
    	
    	response.setContentType("text/html; charset=UTF-8");
    	PrintWriter writer = null;
    	// 웹 브라우저라고 보면 된다. 
    	
    	try {
    		
    		Integer.parseInt(note_no); // int 타입으로 변경
    	
    		// 글 조회수 증가 없이 한개만 조회하는 서비스 호출
    		// BoardVO boardvo = service.getViewWithNoAddCount(note_no);
    		
    		Map<String, String> paraMap = new HashMap<String, String>();
    		paraMap.put("note_no", note_no);
    		
    		// note_no를 이용해서 쪽지 한개 조회하는 서비스 호출
    		NoteVO notevo = service.receiveOneDetail(paraMap);
    		String note_filename = notevo.getNote_filename(); // 20201209114628240642365709200.PNG 이것이 바로 WAS(톰캣) 디스크에 저장된 파일명이다.
    		String note_orgfilename = notevo.getNote_orgfilename(); // 60_3.PNG 다운로드시 보여줄 파일명
    		
    		// 첨부파일이 저장되어 있는 
    	    // WAS(톰캣)의 디스크 경로명을 알아와야만 다운로드를 해줄수 있다. 
    	    // 이 경로는 우리가 파일첨부를 위해서 /addEnd.action 에서 설정해두었던 경로와 똑같아야 한다.
    	    // WAS 의 webapp 의 절대경로를 알아와야 한다.
    		
    		HttpSession session = request.getSession();
			String root = session.getServletContext().getRealPath("/");
			
			// System.out.println("~~~~ webapp 의 절대경로 ====> " + root);
			// ~~~~ webapp 의 절대경로 ====> C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\
			
			String path = root + "resources" + File.separator +"files";
			/* 
			 	File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
            	운영체제가 Windows 이라면 File.separator 는  "\" 이고,
            	운영체제가 UNIX, Linux 이라면  File.separator 는 "/" 이다. 
			*/    		
    		
    		
    		// **** file 다운로드 하기  **** //
    		boolean flag = false;  // file 다운로드의 성공,실패를 알려주는 용도
    		flag = fileManager.doFileDownload(note_filename, note_orgfilename, path, response);
    		// file 다운로드 성공시 flag는 true
    		// file 다운로드 실패시 flag는 false 를 가진다.  
    		
    		if(!flag) {
    			// flag가 false 이라면 즉, 다운로드가 실패할 경우 메세지를 띄워준다. 
    			
    			 try {
					writer = response.getWriter();
					// 웹 브라우저상에서 메세지를 쓰기 위한 객체 생성.
					
					writer.println("<script type='text/javascript'>alert('파일 다운로드를 할 수 없습니다.'); history.back();</script>");
				 } 
    			 catch (IOException e) { } 
				
    		}
    		
    		
    	} 
    	catch(NumberFormatException e) {
    		// 오류 발생하면(숫자가 아니면)
    		
    		try {
				writer = response.getWriter();
				// 웹 브라우저상에서 메세지를 쓰기 위한 객체 생성.
				
				writer.println("<script type='text/javascript'>alert('파일 다운로드를 할 수 없습니다.'); history.back();</script>");
				
			} 
    		catch (IOException e1) {
    			
			}
    		
    	}
    }
	
	// === 주소록 모달창 페이지 요청 === // 
	@RequestMapping(value="/jieun/findEmpList.os")
	public ModelAndView findEmpList(HttpServletRequest request, EmployeeVO empvo , HttpServletResponse response, ModelAndView mav) {
		

		mav.setViewName("jieun/findEmpList");
		
		return mav;
	}
	
	// === 예약 발송 모달창 페이지 요청 === //
	@RequestMapping(value="/jieun/reservationSend.os")
	public ModelAndView reservationSend(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("jieun/reservationSend");
		
		return mav;
	}
	
}


