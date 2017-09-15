// IReadCardService.aidl
package com.jwt.update;

// Declare any non-default types here with import statements

interface IReadCardService {
    //开启读卡线程
        	boolean startReadCard();
        	//关闭读卡线程
        	boolean stopReadCard();
        	//未实现
        	boolean readCard();
        	//获取sam模块号
        	String  getSAMID();
        	//获取单片机版本号
        	String  getBoardVersion();
        	//获取读卡板序列号
        	String  getBoardSN();
}
