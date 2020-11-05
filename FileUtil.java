package com.planm.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class FileUtil {
  
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	// 파일 업로드
    public String fileUpload(MultipartHttpServletRequest mtfRequest, String uploadPath) {
    	CommonUtil commonUtil = new CommonUtil();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat ( "yyyyMMddHHmm");		
		Date date = new Date();
    	
    	String PATH 		  = "D:\\PLANM\\file\\" + uploadPath + "\\";	// 파일 저장 경로
		String fileName 	  = "";																	// 파일 이름(확장자 제외)
		String fileFullName   = "";																	// 파일 이름(확장자 포함)
		String fileType 	  = "";																		// 파일 확장자				
		String fileUploadTime = sdf.format(date);											// 파일 업로드 시간(yyyymmddhhmm)			
		String uploadFileName = "";																// 최종 업로드 파일명(DB에 저장)
		
		try {
			// 파일 저장 경로가 없다면 생성
			File dir = new File(PATH);
			
			if (!dir.isDirectory()) {
				dir.mkdirs(); 
			}

			Iterator<String> itr =  mtfRequest.getFileNames();
			
			if(itr.hasNext()) {				
        		List<MultipartFile> mpf = mtfRequest.getFiles((String) itr.next());
        		
        		if(!mpf.get(0).getOriginalFilename().equals("")) {
        			
        			for(int i = 0; i < mpf.size(); i++) {
              			fileName 	   = FilenameUtils.getBaseName(mpf.get(i).getOriginalFilename());
              			fileFullName   = mpf.get(i).getOriginalFilename();            			
            			fileType 	   = fileFullName.substring(fileFullName.lastIndexOf(".") + 1, fileFullName.length());
            			uploadFileName = uploadFileName + fileName + "_" + fileUploadTime + "." + fileType + ",";      		
            			
            			File file = new File(PATH + fileName + "_" + fileUploadTime + "." + fileType);          				
                  		            					
               		 	logger.info("----------------------- FILE UPLOAD START ---------------------------");
               		 	logger.info("FILE : " + file.getAbsolutePath());
               		 	logger.info("SIZE : " + mpf.get(i).getSize() + "bytes");
               		 	logger.info("----------------------- FILE UPLOAD END -----------------------------");
               		            		 	
               		 	mpf.get(i).transferTo(file);	// 파일 전송
	               		
               		 	if(fileType.equals("png")) {
	         				logger.info("----------------------- Convert PNG to JPG  ---------------------------");
	                		 	
	         				File beforeFile = new File(PATH + fileName + "_" + fileUploadTime + "." + fileType);
	         				File afterFile  = new File(PATH + fileName + "_" + fileUploadTime + ".jpg");

	         				BufferedImage beforeImg = ImageIO.read(beforeFile);
	         				BufferedImage afterImg  = new BufferedImage(beforeImg.getWidth(), beforeImg.getHeight(), BufferedImage.TYPE_INT_RGB);
	         				
	         				afterImg.createGraphics().drawImage(beforeImg, 0, 0, Color.white, null);
	         				
	         				ImageIO.write(afterImg, "jpg", afterFile);		// 이미지 출력
	         						
	                		logger.info("------------------------- Convert Success -----------------------------");
               		 	}	           		
               	 	}    
        			
        			uploadFileName = commonUtil.returnRmvStr(uploadFileName);
        		}        		        		           	         	
        	}        	  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	return uploadFileName; 
    }
        
}
