package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.HTTPUtils;
import com.example.demo.service.MyService;

@Controller
@RequestMapping("/")
public class MyController {
	private MyService mService;

	@Autowired
	public MyController(MyService service) {
		this.mService = service;
	}

	// 请求组件
	@RequestMapping("components")
	public String getComponents(HttpServletRequest request) {
		String pageId = request.getParameter("id");
		if (pageId == null) {
			return "home";
		} else {

			return "components/" + pageId;
		}
	}

	// 请求插件
	@RequestMapping("plugins")
	public String getPlugin(HttpServletRequest request) {
		String pageId = request.getParameter("id");
		if (pageId == null) {
			return "home";
		} else {

			return "plugins/" + pageId;
		}
	}

	// 请求jqgrid帮助文档
	@RequestMapping("jqgrid")
	public String tablePaginationByService() {
		return "jqgrid/useJqgrid";
	}

	// 请求提示板内容
	@RequestMapping("jqgrid/note")
	public String tableNotePaginationByService() {
		return "jqgrid/note";
	}

	// 请求前端分页页面
	@RequestMapping("jqgrid/frontPager")
	public String tableFrontPaginationByService() {
		return "jqgrid/frontPager";
	}

	// 请求后端分页页面
	@RequestMapping("jqgrid/servePager")
	public String tableServePaginationByService() {
		return "jqgrid/servePager";
	}

	// 请求后端分页表格数据
	@RequestMapping("jqgrid/serveTableData")
	@ResponseBody
	public String tableServePaginationTableData(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer length,
			@RequestParam(value = "sidx", required = false) String sortName,
			@RequestParam(value = "sord", required = false) String sortType) {
		String resultJson = mService.queryTableList(page, length, sortName, sortType);
		return resultJson;
	}

	// 请求自动编码工具
	@RequestMapping("tools/autoCoding")
	public String autoCoding() {
		return "tools/autoCoding";
	}

	// 美化并下载代码
	@RequestMapping(value = "/beautyCode", method = RequestMethod.GET)
	public void beautyCode(@RequestParam(value = "code", required = false) String souceCode,
			@RequestParam(value = "title", required = false) String title, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("operate", "beauty");
		map.put("code", souceCode);
		// String result =
		// mService.httpsRequest("https://tool.lu/html/ajax.html", "POST",
		// map.toString());
		// String result = mService.doPost("https://tool.lu/html/ajax.html",
		// map.toString(), "utf-8");
//		String result = souceCode;
		try {
			// result = HTTPUtils.post("https://tool.lu/html/ajax.html", map,
			// null);
			String html = Jsoup.parse(souceCode).body().html();
			// result = html.replace("/ </g", " <");

			Pattern p = Pattern.compile("\\ +<");
			Matcher m = p.matcher(html);

			StringBuffer sb = new StringBuffer("");
			while (m.find()) {
				int spaceLength = (m.end() - m.start() - 1) * 4;
				String spaceStr = "";
				for (int i = 0; i < spaceLength; i++) {

					if (i == spaceLength - 1) {
						spaceStr += " <";
					} else {
						spaceStr += " ";
					}

				}

				m.appendReplacement(sb, spaceStr);
			}
			m.appendTail(sb);

			mService.downLoadCodeFile(request, response, sb.toString(), title);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
