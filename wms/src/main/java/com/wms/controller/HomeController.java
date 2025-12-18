    package com.wms.controller;

    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

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
                    ".delete { background: #f8d7da; color: #721c24; }" +
                    ".test-area { margin-top: 15px; }" +
                    "input, button { padding: 8px; margin: 5px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h1>📚 图书购物车系统 API 测试页面</h1>" +

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