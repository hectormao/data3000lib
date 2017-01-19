package com.data3000.data3000lib.srv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.zkoss.zkplus.spring.SpringUtil;

import com.data3000.data3000lib.ngc.SistemaArchivoNgc;
import com.data3000.data3000lib.utl.ConstantesData3000;

/**
 * Servlet implementation class GetArchivo
 */
@WebServlet("/GetArchivo")
public class GetArchivo extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public GetArchivo() {
        
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
    	Long idVersion = 0L;
    	try{
    		idVersion = Long.parseLong(request.getParameter("idVersion"));
    	} catch(Exception ex){
    		response.sendError(404);
			return;    		
    	}
    	
		WebApplicationContext ac = ContextLoader.getCurrentWebApplicationContext();
		SistemaArchivoNgc sistemaArchivoNgc = (SistemaArchivoNgc) ac.getBean(ConstantesData3000.BEAN_SISTEMA);
		
		Path archivo = sistemaArchivoNgc.getArchivo(idVersion);
		
		
		
		if(archivo == null || ( archivo != null && ! Files.exists(archivo) )){
			
			response.sendError(404);
			return;
		}
		
		String contentType = getServletContext().getMimeType(archivo.getFileName().toString());
		byte[] buffer = Files.readAllBytes(archivo);
		
        
        response.setContentType(contentType);
        response.setContentLength(buffer.length);
        response.addHeader("Content-Disposition", "inline; filename=" + archivo.getFileName().toString());
        
        OutputStream out = response.getOutputStream();
        
        out.write(buffer);
        
        out.flush();
        out.close();
		
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		doGet(request, response);
	}

}
