package com.alkaid.pearlharbor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileReaderHelper {
	
	private static int _line_ptr = 0;
	private static List<String> _line_array = new ArrayList<String>();
	private static int _element_ptr = 0;
	private static String[] _element_array = null;
	private static List<String> lines_temp = new ArrayList<String>();
	
	private static void _reset()
	{
		_line_ptr = 0;
		_line_array.clear();
		lines_temp.clear();
		_element_ptr = 0;
		_element_array = null;
	}
	
    private static String _next_element()
    {
        return _element_array[_element_ptr++];
    }

    public static boolean Load(String filePath)
    {
    	_reset();
    	
    	/*String webroot = System.getProperty("webroot");
    	webroot = webroot.replace('\\', '/');
    	if (!webroot.endsWith("/"))
    		webroot += "/";
    		
    	filePath = webroot + filePath;*/
    	
    	// read all lines
    	File file = new File(filePath);
    	if (file.isFile() && file.exists())
    	{
    		try
    		{
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
	    		String line = null;
	    		while((line = bufferedReader.readLine()) != null)
	    		{
	    			lines_temp.add(line);
	    		}
    		}
    		catch (Exception e)
    		{
    			return false;
    		}

    		// 去除注释行
    		DeleteComments();
    		
    		return true;
    	}
    	
    	return false;
    	
    }

    private static void DeleteComments()
    {
        for (String line : lines_temp)
        {
        	if (!line.startsWith("#") && !line.isEmpty())
        	{
        		_line_array.add(line);
        	}
        }
        
        lines_temp.clear();
    }

    public static boolean IsEnd()
    {
        if (_line_ptr >= _line_array.size())
            return true;

        String str = _line_array.get(_line_ptr);
        if (str == null || str.isEmpty() || str.startsWith("\t"))
            return true;

        return false;
    }

    public static void ReadLine()
    {
        _element_ptr = 0;
        _element_array = _line_array.get(_line_ptr).split("\t");
        _line_ptr ++;
    }

    public static int ReadInt()
    {
    	String n = _next_element();
    	
        return Integer.parseInt(n);
    }

    public static String ReadString()
    {
    	String n = _next_element();
        return n;
    }

    public static float ReadFloat()
    {
    	String n = _next_element();
        return Float.parseFloat(n);
    }

    public static boolean ReadBoolean()
    {
        String n = _next_element().toLowerCase();
        if (n == "1" || n == "true")
        {
            return true;
        }

        return false;
    }
}
