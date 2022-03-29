package com.example.demo.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TableDao;
import com.example.demo.entity.TableData;
import com.example.demo.entity.TableObject;
import com.google.gson.Gson;

@Service
public class MyService {
	@Autowired
	private TableDao tableDao;

	public String queryTableList(int page, int length, String sortName, String sortType) {
		int start = (page - 1) * length;
		List<TableData> dataList = tableDao.queryUserList(start, length, sortName, sortType);
		TableObject tableObject = new TableObject();
		tableObject.setPage(page);
		tableObject.setRecords(1000);
		tableObject.setTotal(Math.ceil(1000 / (double) length));
		tableObject.setRows(dataList);
		Gson gson = new Gson();
		return gson.toJson(tableObject);
	}

	public String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		StringBuffer buffer = null;
		try {
			// 创建SSLContext
			SSLContext sslContext = SSLContext.getInstance("SSL");
			TrustManager[] tm = { new MyX509TrustManager() };
			// 初始化
			sslContext.init(null, tm, new java.security.SecureRandom());
			;
			// 获取SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			// 设置当前实例使用的SSLSoctetFactory
			conn.setSSLSocketFactory(ssf);
			conn.connect();
			// 往服务器端写内容
			if (null != outputStr) {
				OutputStream os = conn.getOutputStream();
				os.write(outputStr.getBytes("utf-8"));
				os.close();
			}

			// 读取服务器端返回的内容
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			buffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public String httpRequest(String requestUrl, String requestMethod, String outputStr) {
		StringBuffer buffer = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(requestMethod);
			conn.connect();
			// 往服务器端写内容 也就是发起http请求需要带的参数
			if (null != outputStr) {
				OutputStream os = conn.getOutputStream();
				os.write(outputStr.getBytes("utf-8"));
				os.close();
			}

			// 读取服务器端返回的内容
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			buffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public String doPost(String url, String jsonstr, String charset) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new SSLClient();
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json");
			StringEntity se = new StringEntity(jsonstr);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public void downLoadCodeFile(HttpServletRequest request, HttpServletResponse response, String code, String title)
			throws ServletException, IOException {
		
		StringBuffer sb = new StringBuffer();
		sb.append("<#include \"/pub/commonInclude.ftl\">");sb.append("\n");
		
		sb.append("<!doctype html>");sb.append("\n");
		sb.append("<html>");sb.append("\n");
		sb.append("<head>");sb.append("\n");
		sb.append("<meta charset=\"utf-8\">");sb.append("\n");
		sb.append("<meta name=\"renderer\" content=\"webkit\">");sb.append("\n");
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");sb.append("\n");
		sb.append("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />");sb.append("\n");
		sb.append("<meta name=\"apple-mobile-web-app-title\" content=\"HISP\">");sb.append("\n");
		sb.append("<title>" + title +"</title>");sb.append("\n");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"${Env.global_static_url}/css/min/min.css?v=${Env.global_css_version!}\">");sb.append("\n");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"${Env.global_static_url}/css/page/page.css?v=${Env.global_css_version!}\">");sb.append("\n");
		sb.append("<style></style>");sb.append("\n");
		sb.append("<body>");sb.append("\n");
		sb.append("<script type=\"text/javascript\" src=\"${Env.global_static_url}/js/loading.page/loading.page.js?v=${Env.global_js_version!}\"></script>");sb.append("\n");
		sb.append(code);sb.append("\n");
		sb.append("<script type=\"text/javascript\" src=\"${Env.global_static_url}/js/min/min.js?v=${Env.global_js_version!}\"></script>");sb.append("\n");
		sb.append("<script type=\"text/javascript\" src=\"${Env.global_static_url}/js/main/main.js?v=${Env.global_js_version!}\"></script>");sb.append("\n");
		sb.append("<script>");sb.append("\n");
		sb.append("\t$(document).ready(function(){");sb.append("\n");
		sb.append("\t});");sb.append("\n");
		sb.append("</script>");sb.append("\n");
		sb.append("</body>");sb.append("\n");
		sb.append("</html>");sb.append("\n");
//		 在SSH框架中，可以通过HttpServletResponse
//		 response=ServletActionContext.getResponse();取出Respond对象
		 //清空一下response对象，否则出现缓存什么的
//		 response.reset();
//		 //指明这是一个下载的respond
//		 response.setContentType("application/octet-stream");
//		 //这里是提供给用户默认的文件名
//		 String filename = "autoCoding.txt";
//		 //双重解码、防止乱码
//		 filename = URLEncoder.encode(filename,"UTF-8");
//		 response.setCharacterEncoding("UTF-8");
//		 response.addHeader("Content-Disposition","attachment;filename=" +
//		 filename);
		 /*
		 * 这里是最关键的一步。
		 * 直接把这个东西写到response输出流里面，给用户下载。
		 * 如果输出到c:\b.txt就是：
		 * PrintWriter printwriter = new PrintWriter(new
		 FileWriter("c:\\b.txt",true));
		 * 现在写好respond头，就写成：
		 * PrintWriter printwriter = new
		 PrintWriter(response.getOutputStream());
		 * 把PrintWriter的输出点改一下
		 */
//		 PrintWriter printwriter = new
//		 PrintWriter(response.getOutputStream());
//		 printwriter.println(sb.toString());
//		 //打印流的所有输出内容，必须关闭这个打印流才有效
//		 printwriter.close();
		 try {  
			 //指明这是一个下载的respond
			 response.setContentType("application/octet-stream");
			 //这里是提供给用户默认的文件名
			 String filename = "autoCoding.txt";
			 //双重解码、防止乱码
			 filename = URLEncoder.encode(filename,"UTF-8");
			 response.setCharacterEncoding("UTF-8");
			 response.addHeader("Content-Disposition","attachment;filename=" +
			 filename);
			 
			OutputStream out = response.getOutputStream();
			ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sb.toString().getBytes());
			BufferedInputStream in = new BufferedInputStream(tInputStringStream);
			byte[] b = new byte[1024];
			int n;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
