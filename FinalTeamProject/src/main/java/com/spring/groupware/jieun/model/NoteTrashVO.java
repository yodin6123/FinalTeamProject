package com.spring.groupware.jieun.model;

public class NoteTrashVO { // 휴지통

	private int note_no;					// 편지번호
	private int fk_emp_no_send;				// 보낸사원번호	
	private int fk_emp_no_receive;			// 받은사원번호
	private String fk_emp_name;				// 받은사원명
	private String note_title;				// 편지제목 
	private String note_content;			// 편지내용
	private String note_fileName;			// 첨부파일명(톰캣에 저장되는 파일명)
	private String note_orgFileName;		// 원본파일명(선택한 파일명)
	private int note_fileSize;				// 파일크기 
	private int note_important;				// 중요도(중요한 파일인지 아닌지 여부, 중요함 체크 : 1, 아니면 0)
	private int note_reservation_status;	// 예약여부(예약 발송 여부 , 예약발송 : 1, 일반발송 : 0)
	private int note_read_status;			// 열람여부(쪽지 수신여부, 읽었으면 : 1, 안읽었으면 : 0)
	private String note_write_date;			// 작성시간
	
	public NoteTrashVO() {}

	public NoteTrashVO(int note_no, int fk_emp_no_send, int fk_emp_no_receive, String fk_emp_name, String note_title,
			String note_content, String note_fileName, String note_orgFileName, int note_fileSize, int note_important,
			int note_reservation_status, int note_read_status, String note_write_date) {
		super();
		this.note_no = note_no;
		this.fk_emp_no_send = fk_emp_no_send;
		this.fk_emp_no_receive = fk_emp_no_receive;
		this.fk_emp_name = fk_emp_name;
		this.note_title = note_title;
		this.note_content = note_content;
		this.note_fileName = note_fileName;
		this.note_orgFileName = note_orgFileName;
		this.note_fileSize = note_fileSize;
		this.note_important = note_important;
		this.note_reservation_status = note_reservation_status;
		this.note_read_status = note_read_status;
		this.note_write_date = note_write_date;
	}

	public int getNote_no() {
		return note_no;
	}

	public void setNote_no(int note_no) {
		this.note_no = note_no;
	}

	public int getFk_emp_no_send() {
		return fk_emp_no_send;
	}

	public void setFk_emp_no_send(int fk_emp_no_send) {
		this.fk_emp_no_send = fk_emp_no_send;
	}

	public int getFk_emp_no_receive() {
		return fk_emp_no_receive;
	}

	public void setFk_emp_no_receive(int fk_emp_no_receive) {
		this.fk_emp_no_receive = fk_emp_no_receive;
	}

	public String getFk_emp_name() {
		return fk_emp_name;
	}

	public void setFk_emp_name(String fk_emp_name) {
		this.fk_emp_name = fk_emp_name;
	}

	public String getNote_title() {
		return note_title;
	}

	public void setNote_title(String note_title) {
		this.note_title = note_title;
	}

	public String getNote_content() {
		return note_content;
	}

	public void setNote_content(String note_content) {
		this.note_content = note_content;
	}

	public String getNote_fileName() {
		return note_fileName;
	}

	public void setNote_fileName(String note_fileName) {
		this.note_fileName = note_fileName;
	}

	public String getNote_orgFileName() {
		return note_orgFileName;
	}

	public void setNote_orgFileName(String note_orgFileName) {
		this.note_orgFileName = note_orgFileName;
	}

	public int getNote_fileSize() {
		return note_fileSize;
	}

	public void setNote_fileSize(int note_fileSize) {
		this.note_fileSize = note_fileSize;
	}

	public int getNote_important() {
		return note_important;
	}

	public void setNote_important(int note_important) {
		this.note_important = note_important;
	}

	public int getNote_reservation_status() {
		return note_reservation_status;
	}

	public void setNote_reservation_status(int note_reservation_status) {
		this.note_reservation_status = note_reservation_status;
	}

	public int getNote_read_status() {
		return note_read_status;
	}

	public void setNote_read_status(int note_read_status) {
		this.note_read_status = note_read_status;
	}

	public String getNote_write_date() {
		return note_write_date;
	}

	public void setNote_write_date(String note_write_date) {
		this.note_write_date = note_write_date;
	} 
	
	
	

	
	
}
