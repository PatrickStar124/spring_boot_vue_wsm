package com.wms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping("")
    public String home() {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><title>📚 图书购物车系统 API</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }" +
                "h1 { color: #333; }" +
                "h3 { color: #555; margin-top: 30px; }" +
                ".api-item { margin: 10px 0 20px 20px; }" +
                ".method { font-weight: bold; padding: 2px 6px; border-radius: 3px; }" +
                ".get { background: #d4edda; color: #155724; }" +
                ".post { background: #fff3cd; color: #856404; }" +
                ".put { background: #cce5ff; color: #004085; }" +
                ".delete { background: #f8d7da; color: #721c24; }" +
                ".test-area { margin-top: 15px; }" +
                "input, button, textarea { padding: 8px; margin: 5px; }" +
                "textarea { width: 400px; height: 100px; font-family: monospace; }" +
                ".json-input { width: 400px; height: 120px; font-family: monospace; }" +
                ".file-input { border: 1px solid #ccc; padding: 6px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1>📚 图书购物车系统 API 测试页面</h1>" +
                "<p>当前时间: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>" +

                "<h3>📁 文件上传模块</h3>" +
                "<div class='api-item'>" +
                "<span class='method post'>POST</span> /file/upload (图片上传)" +
                "<div class='test-area'>" +
                "<input type='file' id='imageFile' accept='image/*' class='file-input'>" +
                "<button onclick=\"uploadImage()\">上传图片</button>" +
                "<div id='uploadResult' style='margin-top:10px;'></div>" +
                "<script>" +
                "async function uploadImage() {" +
                "    const fileInput = document.getElementById('imageFile');" +
                "    if (!fileInput.files[0]) {" +
                "        alert('请选择图片文件');" +
                "        return;" +
                "    }" +
                "    const formData = new FormData();" +
                "    formData.append('file', fileInput.files[0]);" +
                "    document.getElementById('uploadResult').innerHTML = '上传中...';" +
                "    try {" +
                "        const response = await fetch('http://localhost:8090/file/upload', {" +
                "            method: 'POST'," +
                "            body: formData" +
                "        });" +
                "        const result = await response.json();" +
                "        if (result.code === 200) {" +
                "            document.getElementById('uploadResult').innerHTML = " +
                "                '✅ 上传成功！<br>图片URL: <a href=\"' + result.data + '\" target=\"_blank\">' + result.data + '</a><br>' +" +
                "                '<img src=\"' + result.data + '\" style=\"max-width: 200px; margin-top: 10px;\">';" +
                "        } else {" +
                "            document.getElementById('uploadResult').innerHTML = '❌ 上传失败: ' + result.msg;" +
                "        }" +
                "    } catch (error) {" +
                "        document.getElementById('uploadResult').innerHTML = '❌ 请求失败: ' + error.message;" +
                "    }" +
                "}" +
                "</script>" +
                "</div></div>" +

                "<h3>👤 用户模块</h3>" +
                "<div class='api-item'>" +
                "<span class='method post'>POST</span> /user/login" +
                "<div class='test-area'>" +
                "<input id='loginNo' placeholder='账号' value='admin'>" +
                "<input id='loginPwd' placeholder='密码' value='123456'>" +
                "<button onclick=\"testPost('/user/login', " +
                "{no: document.getElementById('loginNo').value, " +
                "password: document.getElementById('loginPwd').value})\">测试登录</button>" +
                "</div></div>" +

                "<div class='api-item'>" +
                "<span class='method post'>POST</span> /user/register" +
                "<div class='test-area'>" +
                "<input id='regNo' placeholder='账号' value='test001'>" +
                "<input id='regPwd' placeholder='密码' value='123456'>" +
                "<input id='regName' placeholder='姓名' value='测试用户'>" +
                "<button onclick=\"testPost('/user/register', " +
                "{no: document.getElementById('regNo').value, " +
                "password: document.getElementById('regPwd').value, " +
                "name: document.getElementById('regName').value})\">测试注册</button>" +
                "</div></div>" +

                "<div class='api-item'>" +
                "<span class='method get'>GET</span> /user/info" +
                "<div class='test-area'>" +
                "<input id='userId' placeholder='用户ID' value='1'>" +
                "<button onclick=\"testGet('/user/info?id=' + document.getElementById('userId').value)\">测试查询</button>" +
                "</div></div>" +

                "<h3>📚 图书模块</h3>" +

                "<div class='api-item'>" +
                "<span class='method post'>POST</span> /book/save" +
                "<div class='test-area'>" +
                "<textarea id='bookJson' class='json-input' placeholder='输入图书JSON数据'>" +
                "{\n" +
                "  \"name\": \"Java编程思想\",\n" +
                "  \"author\": \"Bruce Eckel\",\n" +
                "  \"price\": 108.00,\n" +
                "  \"stock\": 50,\n" +
                "  \"imageUrl\": \"这里可以填入上面上传得到的图片URL\"\n" +
                "}" +
                "</textarea><br>" +
                "<button onclick=\"testPost('/book/save', JSON.parse(document.getElementById('bookJson').value))\">新增图书</button>" +
                "</div></div>" +

                "<div class='api-item'>" +
                "<span class='method get'>GET</span> /book/list" +
                "<div class='test-area'>" +
                "<button onclick=\"testGet('/book/list')\">查看所有图书</button>" +
                "</div></div>" +

                "<div class='api-item'>" +
                "<span class='method get'>GET</span> /book/{id}" +
                "<div class='test-area'>" +
                "<input id='bookId' placeholder='图书ID' value='1'>" +
                "<button onclick=\"testGet('/book/' + document.getElementById('bookId').value)\">查询图书</button>" +
                "</div></div>" +

                "<div class='api-item'>" +
                "<span class='method delete'>DELETE</span> /book/delete/{id}" +
                "<div class='test-area'>" +
                "<input id='delBookId' placeholder='图书ID' value='1'>" +
                "<button onclick=\"testDelete('/book/delete/' + document.getElementById('delBookId').value)\">删除图书</button>" +
                "</div></div>" +

                "<h3>🛒 购物车模块</h3>" +

                "<div class='api-item'>" +
                "<span class='method post'>POST</span> /cart/add" +
                "<div class='test-area'>" +
                "<input id='addUserId' placeholder='用户ID' value='1'>" +
                "<input id='addBookId' placeholder='图书ID' value='1'>" +
                "<button onclick=\"testPost('/cart/add?userId=' + document.getElementById('addUserId').value + " +
                "'&bookId=' + document.getElementById('addBookId').value, {})\">加入购物车</button>" +
                "</div></div>" +

                "<div class='api-item'>" +
                "<span class='method delete'>DELETE</span> /cart/remove" +
                "<div class='test-area'>" +
                "<input id='removeUserId' placeholder='用户ID' value='1'>" +
                "<input id='removeBookId' placeholder='图书ID' value='1'>" +
                "<button onclick=\"testDelete('/cart/remove?userId=' + document.getElementById('removeUserId').value + " +
                "'&bookId=' + document.getElementById('removeBookId').value)\">移出购物车</button>" +
                "</div></div>" +

                "<div class='api-item'>" +
                "<span class='method get'>GET</span> /cart/list" +
                "<div class='test-area'>" +
                "<input id='cartUserId' placeholder='用户ID' value='1'>" +
                "<button onclick=\"testGet('/cart/list?userId=' + document.getElementById('cartUserId').value)\">查看购物车</button>" +
                "</div></div>" +

                "<hr>" +
                "<h3>📝 测试结果</h3>" +
                "<pre id='result' style='background: #f5f5f5; padding: 15px; border-radius: 5px; max-height: 300px; overflow: auto;'>点击上面的按钮测试...</pre>" +

                "<script>" +
                "async function testGet(url) {" +
                "    showLoading();" +
                "    try {" +
                "        const response = await fetch('http://localhost:8090' + url);" +
                "        const data = await response.json();" +
                "        showResult(data);" +
                "    } catch (error) {" +
                "        showResult({ error: error.message });" +
                "    }" +
                "}" +

                "async function testPost(url, body) {" +
                "    showLoading();" +
                "    try {" +
                "        const response = await fetch('http://localhost:8090' + url, {" +
                "            method: 'POST'," +
                "            headers: { 'Content-Type': 'application/json' }," +
                "            body: JSON.stringify(body)" +
                "        });" +
                "        const data = await response.json();" +
                "        showResult(data);" +
                "    } catch (error) {" +
                "        showResult({ error: error.message });" +
                "    }" +
                "}" +

                "async function testDelete(url) {" +
                "    showLoading();" +
                "    try {" +
                "        const response = await fetch('http://localhost:8090' + url, {" +
                "            method: 'DELETE'" +
                "        });" +
                "        const data = await response.json();" +
                "        showResult(data);" +
                "    } catch (error) {" +
                "        showResult({ error: error.message });" +
                "    }" +
                "}" +

                "function showLoading() {" +
                "    document.getElementById('result').innerHTML = '请求中...';" +
                "}" +

                "function showResult(data) {" +
                "    document.getElementById('result').innerHTML = JSON.stringify(data, null, 2);" +
                "}" +
                "</script>" +
                "</body></html>";
    }
}