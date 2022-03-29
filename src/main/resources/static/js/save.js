/**
 * 生成代码
 */
function createCode(){
	var $htmlStr = $("#mainArea").clone();
	$htmlStr.find(".autocoding-el").each(function(index, el) {
		// 移除tabindex=0属性
		if ($(el).attr("tabindex") == 0) {
			$(el).removeAttr("tabindex");
		}
		// 移除style属性
		$(el).removeAttr("style");
		// 移除contentEditable属性
		$(el).removeAttr("contentEditable");
		// 移除title属性
		$(el).removeAttr("title");
		// 移除drag-box
		$(el).find(".drag-box").remove();
		// 若是i-check，把input元素拎出来，同时移除中间渲染出来的代码
		if ($(el).hasClass("i-checks")) {
			var $input = $(el).find("input");
			$input.removeAttr("style");
			$(el).prepend($input);
			$(el).find(".iradio_square-green").remove();
			$(el).find(".icheckbox_square-green").remove();
		}
		// 若是img标签的话，将img的width和height属性设为.img-box的width和height，并将img从.img-box中剥离出来，最后移除.img-box
		if ($(el).hasClass("img-box")) {
			var width = getRectPos($(el)).width;
			var height = getRectPos($(el)).height;
			var $img = $(el).find("img");
			$img.eq(0).removeAttr("style");
			$img.eq(0).attr("width", width);
			$img.eq(0).attr("height", height);
			$(el).after($img);
			$(el).remove();
		} else { // 若不是，则移除autocoding-el类
			$(el).removeClass("autocoding-el strut-active");
		}
	});
	
	if ($htmlStr.html() != "") {
		var title = $("#pageTitle").val();
		// 美化并下载代码
		beautyCode($htmlStr.html());
		// 将另一种美化方式打印代码到控制台
		var frame = "";
		frame +='<#include "/pub/commonInclude.ftl">\n';
		frame +='<!doctype html>\n';
		frame +='<html>\n';
		frame +='<head>\n';
		frame +='<meta charset="utf-8">\n';
		frame +='<meta name="renderer" content="webkit">\n';
		frame +='<meta name="viewport" content="width=device-width, initial-scale=1.0">\n';
		frame +='<meta name="apple-mobile-web-app-capable" content="yes" />\n';
		frame +='<meta name="apple-mobile-web-app-title" content="HISP">\n';
		frame +='<title>'+title+'</title>\n';
		frame +='<link rel="stylesheet" type="text/css" href="${Env.global_static_url}/css/min/min.css?v=${Env.global_css_version!}">\n';
		frame +='<link rel="stylesheet" type="text/css" href="${Env.global_static_url}/css/page/page.css?v=${Env.global_css_version!}">\n';
		frame +='<style></style>\n';
		frame +='</head>\n';
		frame +='<body>\n';
		frame += '<script type="text/javascript" src="${Env.global_static_url}/js/loading.page/loading.page.js?v=${Env.global_js_version!}"></script>\n';
		frame += style_html($htmlStr.html(), 4, ' ', 100);
		frame += '\n<script type="text/javascript" src="${Env.global_static_url}/js/min/min.js?v=${Env.global_js_version!}"></script>\n';
		frame += '<script type="text/javascript" src="${Env.global_static_url}/js/main/main.js?v=${Env.global_js_version!}"></script>\n';
		frame += '<script>\n';
		frame += '$(document).ready(function(){\n';
		frame += '});\n';
		frame += '</script>\n';
		frame += '</body>\n';
		frame += '</html>\n';
		console.log(frame);
	}
}

/**
 * 美化并下载代码
 */
function beautyCode(souceCode) {
	var souceCode = souceCode.replace(/%/g, "%25");
	var title = $("#pageTitle").val();
	var a = document.createElement('a');
    var url = '/beautyCode?title=' + title + '&code=' + souceCode;
    a.href=url;
    a.download = 'autoCoding.txt';
    a.click();
}
