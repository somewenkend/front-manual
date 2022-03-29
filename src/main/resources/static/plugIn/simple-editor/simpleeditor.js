(function() {
    /**
     *.构造方法。selectorId:编辑器id，options:配置项
     */
    function SimpleEditor(selectorId, options) {
         if ($("#"+selectorId).length > 0) {
             this.$el = $("#"+selectorId);
         } else {
             alter("未找到#"+selectorId+"，请检查元素是否存在！");
             return false;
         }
         var defaultConf = {
            height: 300, // 编辑器高度，默认为300
            insertText: true, // 默认允许编辑文本
            insertAttach: true, // 默认允许插入附件
            insertImage: true, // 默认允许插入图片
            requestUrl: "", // 默认请求路径
            postData: {} // 默认提交后台的数据
        };
        this.options = $.extend(true, defaultConf, options);
        this.options.insertType = ""; // 插入的是附件还是图片，默认空
        // 初始化
        this.init();
    }
    
    SimpleEditor.prototype = {
        constructor: SimpleEditor,
        init: function() {
            this.buildHtml(); // 根据配置项构建html结构
            this.bindEvent(); // 绑定各种事件
        },
        buildHtml: function() {
            var _this = this;
            this.$el.addClass("custom-editor");
            var html = `
                <div class="edit-box" style="height:`+ this.options.height +`px">
                </div>
                <div class="insert-box">
                </div>`;
            this.$el.append(html);
            
            if (this.options.insertText) { // 开启文本编辑
                this.$el.find(".edit-box").prop("contenteditable", true);
            }
            
            if (this.options.insertAttach) { // 开启附件插入
                this.$el.find(".insert-box").append(`
               		<form class="attch-form" enctype="multipart/form-data">
                        <span class="btn btn-default btn-xs">
                            <i class="fa fa-paperclip mr5"></i>插入附件
                        </span>
                        <input type="file" name="multipleFile" class="insert-input" data-type="attch" multiple="multiple"/>
                    </form>
                `);
            	// 注册表单
                registForm(0);
            }
            
            if (this.options.insertImage) { // 开启图片插入
                this.$el.find(".insert-box").append(`
               		<form class="image-form" enctype="multipart/form-data">
                        <span class="btn btn-default btn-xs">
                            <i class="fa fa-picture-o mr5"></i>插入图片
                        </span>
                        <input type="file" name="multipleFile" class="insert-input" data-type="image" multiple="multiple" accept="image/*" />
                    </form>
                `);
            	// 注册表单
                registForm(1);
            }
            
            /**
             * .注册表单
             */
            function registForm(type) {
            	var $curForm;
            	if (type == 0) { // 插入附件
            		$curForm = _this.$el.find(".attch-form");
            	} else {
            		$curForm = _this.$el.find(".image-form");
            	}
            	// 注册附件上传form表单
                $curForm.ajaxForm({
                    url: _this.options.requestUrl,
                    type : "post", 
                    dataType : "json",
                    data: _this.options.postData,
                    clearForm:true,
                    restForm:true,
                    success: function(msg){
                        // append插入的内容进编辑区域
                        _this.appendInsertContent(msg.data);
                     	// 光标移至编辑框最后
                        _this.keepLastIndex(_this.$el.find(".edit-box")[0]);
                    },
                    error: function() {
                        alertCountDown("5", "通信失败，请重试！");
                    }
                });
            }
        },
        bindEvent: function() {
            var _this = this;
            // 点击span相当于点击span内部的input[type=file]
            this.$el.find(".insert-box span").on("click", function() {
                $(this).next("input").click();
            });
            // 为上传控件绑定change事件
            this.$el.find(".insert-input").on("change", function() {
            	// 确定是上传附件还是上传图片
            	_this.insertType = $(this).data("type");
                var files = $(this)[0].files;
                if (files.length > 0) {
                	if (_this.insertType == "image") {
                    	_this.$el.find(".image-form").submit(); // 提交插入的图片表单
                	} else {
                		_this.$el.find(".attch-form").submit(); // 提交插入的附件表单
                	}
                }
            });
        },
        appendInsertContent: function(data) {
            var filesHtml = "";
            if (this.insertType == "image") { // 插入的是图片
                for (var item of data) {
                    filesHtml += "<img src='"+ item.url +"' class='img-responsive' />";
                }
            } else { // 插入的是附件
            	for (var item of data) {
            		var icoSrc = this.getIcoSrc(item.url);
                    filesHtml += `
                    	<a href="`+ item.url +`" title="`+ item.name +`">
                    	<img src="`+ icoSrc +`" class='mr5' />
                    	`+ item.name +`
                    	</a>
                    `;
                }
            }
            // 将内容插入
            this.$el.find(".edit-box").append(filesHtml+"<br>");
        },
        keepLastIndex: function(obj) {
            if (window.getSelection) {//ie11 10 9 ff safari
                obj.focus(); //解决ff不获取焦点无法定位问题
                var range = window.getSelection();//创建range
                range.selectAllChildren(obj);//range 选择obj下所有子内容
                range.collapseToEnd();//光标移至最后
            }
            else if (document.selection) {//ie10 9 8 7 6 5
                var range = document.selection.createRange();//创建选择对象
                //var range = document.body.createTextRange();
                range.moveToElementText(obj);//range定位到obj
                range.collapse(false);//光标移至最后
                range.select();
            }
        },
        getIcoSrc: function(url) { // 根据文件类型
        	// 使用ueditor的图标，baseIcoPath为基础路径
        	var baseIcoPath = "/plugIn/ueditor1_4_3_3/dialogs/attachment/fileTypeImages/";
        	var ext = url.substr(url.lastIndexOf('.') + 1).split('?')[0].toLowerCase(), // 兼容加密库文件类型图标
            maps = {
                "rar":"icon_rar.gif",
                "zip":"icon_rar.gif",
                "tar":"icon_rar.gif",
                "gz":"icon_rar.gif",
                "bz2":"icon_rar.gif",
                "doc":"icon_doc.gif",
                "docx":"icon_doc.gif",
                "pdf":"icon_pdf.gif",
                "mp3":"icon_mp3.gif",
                "xls":"icon_xls.gif",
                "chm":"icon_chm.gif",
                "ppt":"icon_ppt.gif",
                "pptx":"icon_ppt.gif",
                "avi":"icon_mv.gif",
                "rmvb":"icon_mv.gif",
                "wmv":"icon_mv.gif",
                "flv":"icon_mv.gif",
                "swf":"icon_mv.gif",
                "rm":"icon_mv.gif",
                "exe":"icon_exe.gif",
                "psd":"icon_psd.gif",
                "txt":"icon_txt.gif",
                "jpg":"icon_jpg.gif",
                "png":"icon_jpg.gif",
                "jpeg":"icon_jpg.gif",
                "gif":"icon_jpg.gif",
                "ico":"icon_jpg.gif",
                "bmp":"icon_jpg.gif",
                "xlsx":"icon_xls.gif"
            };
        	return baseIcoPath + (maps[ext] ? maps[ext] : maps['txt']);
        },
        setContent: function(content) { // 设置编辑器中的内容
            this.$el.find(".edit-box").html(content);
        },
        getContent: function() { // 获取编辑器中的内容
            return this.$el.find(".edit-box").html();
        }
    }
    window.SimpleEditor = SimpleEditor;
})();