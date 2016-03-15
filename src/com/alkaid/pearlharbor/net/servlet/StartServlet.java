package com.alkaid.pearlharbor.net.servlet;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alkaid.pearlharbor.game.Game;



public class StartServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
		
		BufferedReader reader = req.getReader();
		String code;
		StringBuffer result = new StringBuffer();
		while((code = reader.readLine()) != null)
		{
			result.append(code);
		}
		System.out.println("recieve get function" + result.toString());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		
		// server start up will call
		
		String realPath = this.getServletContext().getRealPath("/");
		System.setProperty("webroot", realPath);
		
		String logPorpertiesPath = realPath + this.getServletContext().getInitParameter("log4jConfigLocation");
		Game.getInstance().setLogPropertiesPath(logPorpertiesPath);
		
		String resPath = realPath + this.getServletContext().getInitParameter("resLocation");
		Game.getInstance().setResPath(resPath);

		Game.getInstance().startServer();
	}

}
