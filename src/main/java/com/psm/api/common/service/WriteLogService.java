package com.psm.api.common.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WriteLogService {
	
	@Value("${psm.schedule.logpath}")
	private String folderPath;

	

	public void createLogFile(String workloadId) throws IOException {
	
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
		String folderPath2 = folderPath + sdf2.format(new Date()).toString().substring(0, 4) + "\\" + sdf2.format(new Date()).toString().substring(4, 6) + "\\";

		System.out.println("바보냐");
		System.out.println(folderPath2);
		
		File path = new File(folderPath2);
		File file = new File(folderPath2 + workloadId + ".txt");
		
		if(!path.exists()) {
			path.mkdirs(); //폴더 생성합니다.
		}
		
		if(!file.exists()){
			file.createNewFile();
		}
	}
	
	public void writeLogFile(String workloadId, String msg){
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
		String folderPath2 = folderPath + sdf2.format(new Date()).toString().substring(0, 4) + "\\" + sdf2.format(new Date()).toString().substring(4, 6) + "\\";

		File file = new File(folderPath2 + workloadId + ".txt");
		FileWriter writer = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		
		try {
			//기존 파일의 내용에 이어서 쓰려면 true
			writer = new FileWriter(file, true);
			writer.write("[" + sdf.format(new Date()) + "]: " + msg + "\r\n");
			writer.flush();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) writer.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

}
