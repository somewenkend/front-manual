/*前端*/

实例化一个插件对象：
var editor = new SimpleEditor(elId, option);

option为配置项，可配置属性如下：
option = {
	height: 300, // 编辑器高度，默认为300
    insertAttach: true, // 默认允许插入附件
    insertImage: true, // 默认允许插入图片
    requestUrl: "", // 默认请求路径
    postData: {} // 默认提交后台的数据
}

注意事项：
1、option至少需要传一个请求路径
2、后台接收的文件name默认为"multipleFile"


/***后台（利用MultipartHttpServletRequest来接收前台传过来的文件）***/

传参数:HttpServletRequest request,



UpYunEncryption upYunEncryption = new UpYunEncryption();

                

MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

List<MultipartFile> multiFileList = multipartRequest.getFiles("multipleFile"); // 上传控件的name固定是multipleFile

int size = multiFileList.size();

List<Map<String, String>> fileList = new ArrayList<Map<String,String>>();

for(int i=0;i<size;i++){

    MultipartFile file = multiFileList.get(i);

    String fileUrl = upYunEncryption.uploadReturnUrl(file.getOriginalFilename(), Env.TEST_EDIT_FILE_UPYUN_FLIE_STORE + "U" + user.getId() + "/", file.getBytes());

    Map<String, String> map = new HashMap<String, String>();

    map.put("name", file.getOriginalFilename());

    map.put("url", upYunEncryption.getTimeLimitUrl(fileUrl));

    fileList.add(map);

}

JSONObject returnJson=new JSONObject();

returnJson.put("data", fileList);



result = gson.toJson(returnJson);

