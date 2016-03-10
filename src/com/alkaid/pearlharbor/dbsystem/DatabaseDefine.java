package com.alkaid.pearlharbor.dbsystem;

public class DatabaseDefine {
	public enum DatabaseFuncType
	{
		_DB_FUNC_UNKNOW_, //not define
		
        _DB_FUNC_LOAD_PLAYER_HOME_,
        _DB_FUNC_SAVE_PLAYER_HOME_,
        _DB_FUNC_LOAD_PLAYER_AVATAR_,
        _DB_FUNC_SAVE_PLAYER_AVATAR_,
	}
	
	public enum DatabaseError
	{
		_ERROR_UNKNOW_,
		_ERROR_OK_,
		
		_ERROR_DATABASE_,
        _ERROR_NO_DATABASE_,                    // 未能获得数据库
        _ERROR_NOT_CONNECT_DBSERVER_,           // 未连接至数据库服务器
        _ERROR_UNDEFINE_FUNCTYPE_,              // 数据库操作功能未定义
        _ERROR_ACCOUNT_NOT_FOUND_,              // 用户账户未查询到
        _ERROR_AVATAR_NOT_FOUND_,               // 角色未找到
        _ERROR_WRITE_INNER_BREAK_,              // 写入操作内部错误
        _ERROR_READ_INNER_BREAK_,               // 读取操作内部错误
        _ERROR_DBSERVER_BUSY_,                  // 数据库繁忙
	}
}
