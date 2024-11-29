package com.maha.service.impl;

import com.maha.service.CommonService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommonServiceImpl implements CommonService{

    @Override
    public void removeSessionMessage() {
       HttpServletRequest req= ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
       HttpSession session=req.getSession();
       session.removeAttribute("Error");
       session.removeAttribute("Success");
    }
    
}
