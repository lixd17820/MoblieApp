package com.jwt.web;


import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalData;

public class RestfulDaoFactory {

    public static RestfulDao getDao() {
        return getDao(GlobalData.connCata);
    }

    public static RestfulDao getDao(ConnCata conn) {
        if (conn == ConnCata.OUTSIDECONN)
            return new OutRestfulDao();
        //else if (conn == ConnCata.INSIDECONN)
        return new ThreeTeamDao();
    }

}
