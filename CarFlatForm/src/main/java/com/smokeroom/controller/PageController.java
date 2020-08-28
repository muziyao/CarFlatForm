package com.smokeroom.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

 
 
@Controller
@RequestMapping("page")
public class PageController {
	
	@GetMapping("videoplay.html")
	public ModelAndView home(Model model,ModelAndView mv ,String devId,String rtmpurl) {
		model.addAttribute("devId", devId);
		model.addAttribute("rtmpurl", rtmpurl);
		System.out.println( "rtmpurl="+rtmpurl);
		mv.setViewName("videoplay");
		return mv;
	}
	
	 
	 
}
