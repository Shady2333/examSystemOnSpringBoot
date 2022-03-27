# readme

## 一、前言

​	这篇使用文档会详细解释这个项目里的各个核心的功能模块，部分细节没有阐述。

## 二、代码部分

#### 	1、登录模块：

​		页面是login.html，前端通过form表单提交数据到后端进行逻辑处理。



**login.html:**

```js
                <div class="form-group has-feedback">
                    <span class="fa fa-user form-control-feedback"></span>
                    <input type="text" id="userName" name="username" class="form-control" placeholder="请输入					账号"required="true">
                </div>
                <div class="form-group has-feedback">
                    <span class="fa fa-lock form-control-feedback"></span>
                    <input type="password" id="password" name="password" class="form-control" 								placeholder="请输入密码"required="true">
                </div>

                <div class="row">
                    <div class="col-6">
                        <input type="text" class="form-control" name="verifyCode" placeholder="请输入验证码" 							required="true">
                    </div>
                    <div class="col-6">
                        <img alt="单击图片刷新！" class="pointer" th:src="@{/login/captcha}"
                             onclick="this.src='/login/captcha'">
                    </div>
                </div>
```

​	以上分三个div分别对应登录页面的  账户、密码、验证码，用户输入这三者后，点击登陆，就会通过form表单中的action属性将数据请求到后端的  “/login" 的controller中，下面讲讲/login做了什么事。





**loginController:**

```java
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,HttpSession session,HttpServletRequest request,HttpServletResponse response){

        if(session.getAttribute("verifyCode")==null||!session.getAttribute("verifyCode").equals(verifyCode)){
                session.setAttribute("errorMsg","验证码错误");
                return "login";
        }
        Subject subject = SecurityUtils.getSubject();
        //封装登录数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        //登录
        try{
            subject.login(token);
            //获取用户
            User user = (User) subject.getPrincipal();
            if("1".equals(user.getRole())){
                Teacher teacher = (Teacher) user;
                session.setAttribute("id",teacher.getTeacherId());
                //重定向cookie传不过去
                CookieUtil.setCookie(request,response,"id",String.valueOf(teacher.getTeacherId()));
                return "redirect:teacherIndex";
            }else if("2".equals(user.getRole())){
                Student student = (Student) user;
                //在session存储一些信息,shiro也有他自己的session,都可以用，这里用的是httpSession，因为旧的代码都是httpSession
                session.setAttribute("id",student.getStudentId());
                CookieUtil.setCookie(request,response,"id",String.valueOf(student.getStudentId()));
                return "redirect:studentIndex";
            }
            return "login";
        }catch (UnknownAccountException e){ //用户名不存在
            session.setAttribute("errorMsg","用户名不存在");
            return "login";
        }catch (IncorrectCredentialsException e){
            session.setAttribute("errorMsg","密码错误");
            return "login";
        }

    }
```

​	==总体流程==：form表单传给后端的参数名称对应是通过div中的name属性决定的。  username就是账户名，password就是密码，verifyCode就是验证码。把username和password用shiro框架生成一个token，如果这个这个用户密码对应的用户是学生(role=2)，就把当前登录的这个学生的id传入session，并且跳转到学生的页面，老师同理。  如果用户名 or 密码错误，那就会向session传入一个errorMsg再跳回login页面，并将这个errorMsg显示出来。

​	==关于验证码==： 这里我用了一个网上验证码的库。 验证码在生成的时候，会把正确的值存入session里，用户输入的验证码会在这里与session中正确的验证码进行对比，如果不对，那么也会通过errorMsg显示出来。

​	==关于加密==：用户输入密码后，点击“登录”，会在跳转页面之前执行一个js函数

```javascript
<script>
    function login_md5() {
        var password=document.getElementById('password');
        password.value=md5(password.value);
        return true;
    }
</script>
```

​	这个函数将id=password的div中的值取出，并通过md5加密算法进行加密，也就是说，请求到后端的真实密码数据是通过md5加密后的数据。

​	==关于shiro==：前面说过，username和password会被封装为一个token。在shiroconfig包下有一个UserRealm类，这个类继承了AuthorizingRealm父类，并且重写了AuthorizingRealm方法，这个方法将token作为参数传入，然后在方法里调用Service层的方法对数据库进行检索，如果找不到有该用户名的记录，那么就会抛出一个UnknownAccountException异常，这个异常在/login里会被catch到。同样的，密码错误就会抛出IncorrectCredentialsException异常，也会在/login被catch到然后处理。





#### 2、注册模块

**Register.html**

```js
                <div class="form-group has-feedback">
                    <span class="fa fa-user form-control-feedback"></span>
                    <input type="text" id="username" name="username" class="form-control" placeholder="请输入					  学号"required="true">
                </div>
                    <span id="msg1"></span>

                <div class="form-group has-feedback">
                    <span class="fa fa-lock form-control-feedback"></span>
                    <input type="password" id="fake_password"  class="form-control" placeholder="请输入密码"
                           required="true">
                </div>
                <input type="hidden" id="md5_password" name="pwd">
                <div class="form-group has-feedback"></div>
                <div class="row">
                    <div class="col-4">
                    </div>
                    <div class="col-4">
                        <button  type="submit" onclick="my_md5()" class="btn btn-primary btn-block btn-								flat">注册
                        </button>
                    </div>
                </div>
```

​	用户输入密码和账号并且点击注册后，会执行一个js函数：

```js
<script>
    function my_md5() {
        var fake_password=document.getElementById('fake_password');
        var true_password=document.getElementById('md5_password');
        true_password.value=md5(fake_password.value);

        $.ajax({
            url:'/registration_check',
            data:{"username":$("#username").val(),"pwd":$("#md5_password").val()},
            success:function (data) {

                if(data===0){
                    $('#msg1').css("color","red");
                    $('#msg1').html("账号重复，注册失败！");

                }
                else{
                    alert("注册成功！");
                    window.location.href = '/toLogin'
                }
            }
        })

        return true;
    }
</script>
```

​	取出id为fake_password的div中的数据，并且将其用md5算法加密，输入到true_password里。然后开始ajax。

异步请求'/registration_check'的controller，并且将用户输入的账号和被加密过密码传入。



**/registration_check**

```java
@RequestMapping("/registration_check")
    @ResponseBody
    public int Check(@RequestParam("username")String username,@RequestParam("pwd")String pwd){

        Student student = new Student();
        student.setUsername(username);
        student.setPwd(pwd);
        if(studentService.findByUsername(student.getUsername())!=null || teacherService.findByUserName(student.getUsername())!=null){
            return 0;
        }
        student.setRole("2");  
        List<Student> students=studentService.findAll();
        if(students.size()==0){
            student.setStudentId(1);
        }
        else{
            int lastStudentId=studentService.findOnlyStudentId();
            student.setStudentId(lastStudentId+1);
        }
        studentService.add(student);
        return 1;
    }

```

​	传过来的username和pwd被封装进student对象里，并且用这个student对象对数据库进行查找，如果在学生库or老师库中查找出一条用户名重复的记录，那么就会给前端返回一个0。反之，会设置这个用户的权限为2（因为老师是不需要注册的，所以注册的人都是学生），然后找出学生库里所有的记录，对studentId进行降序排序，拿到第一条记录，获得最大的studentId，然后将新注册的用户的studentId设置为studentId+1，再把这个student对象的所有属性（包括加密后的密码，大概长这样--》621deb64455f3f9ae506a59ef427386f）insert到数据库中，然后给前端返回一个1。





这里我们再看ajax。

```js
        $.ajax({
            url:'/registration_check',
            data:{"username":$("#username").val(),"pwd":$("#md5_password").val()},
            success:function (data) {

                if(data===0){
                    $('#msg1').css("color","red");
                    $('#msg1').html("账号重复，注册失败！");

                }
                else{
                    alert("注册成功！");
                    window.location.href = '/toLogin'
                }
            }
        })

        return true;
    }
```

​	 success:function (data) 中的data代表/registration_check这个controller return的值，如果是0，说明用户名重复了，那么就会将错误信息渲到id名为msg1的div里显示出来。如果是1，那就直接显示注册成功，并且请求/toLogin，这个controller用来跳转到login页面。



#### 3、学生考试功能

​	如果学生登陆成功的话，就会进入/student/dashboard.html这个页面。这边主要涉及到两个子功能模块：1、考试。2、查分。

​	我们先来讲讲考试功能。

​	点击“参加考试”之后，会走一个/toStudentExamPage的controller并且进入到student/exam/list.html这个页面。

**list.html:**

```js
    layui.use('table', function(){
        var table = layui.table;

        //温馨提示：默认由前端自动合计当前行数据。从 layui 2.5.6 开始： 若接口直接返回了合计行数据，则优先读取接口合计行数据。
        //详见：https://www.layui.com/doc/modules/table.html#totalRow
        table.render({
            elem: '#test'
            ,url:'/exam/all'
            ,toolbar: '#toolbarDemo'
            ,title: '用户数据表'
            ,totalRow: true
            ,cols: [[
                {type: 'checkbox', fixed: 'left'}
                ,{field:'description', title:'description'}
                ,{field:'source', title:'subject'}
                ,{field:'examDate', title:'examDate'}
                ,{field:'totalTime', title:'totalTime'}
                ,{field:'grade', title:'grade'}
                ,{field:'term', title:'term'}
                ,{field:'major', title:'major'}
                ,{field:'institute', title:'institute'}
                ,{field:'totalScore', title:'totalScore'}
                ,{field:'institute', title:'institute'}
                ,{field:'tips', title:'tips'}
                ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:150}
            ]]
            ,page: false
        });

        //工具栏事件
        table.on('toolbar(test)', function(obj){
            var checkStatus = table.checkStatus(obj.config.id);
            switch(obj.event){
                case 'getCheckData':
                    var data = checkStatus.data;
                    layer.alert(JSON.stringify(data));
                    break;
                case 'getCheckLength':
                    var data = checkStatus.data;
                    layer.msg('选中了：'+ data.length + ' 个');
                    break;
                case 'isAll':
                    layer.msg(checkStatus.isAll ? '全选': '未全选')
                    break;
            };
        });
```

​	这里用到了“layui”前端框架，table.render里的url属性，类似ajax，会异步的请求/exam/all这个controller

**/exam/all**：

```java
 @GetMapping("/exam/all")
    public ApiResult findAll(){
        ApiResult apiResult;
        apiResult = ApiResultHandler.buildApiResult(0, "请求成功！", examManageService.findAll());
        return apiResult;
    }
```

​	ApiResult是自己写的一个实体类，ApiResultHandler是对这个实体类的操作，这里不作详细阐述。apiResult主要是封装了服务器响应给前端的状态码、状态的具体信息、数据。buildApiResult方法用来封装的。  这里examManageService.findAll()会将数据库里所有的考试全部检索出并用list接收。 然后通过layui框架显示到页面上。

​	当我们选中其中一场考试并且点击“参加考试”，会触发layui的“join”事件

```JS
 table.on('tool(test)', function(obj){
            var data = obj.data;
            console.log(data.examCode)
            //console.log(data);
            if(obj.event === 'join'){
                layer.confirm('确定参加考试吗', function(index){
                    window.location.href = "/toExamingPage/"+data.examCode;
                });
            }
        });
```

​	并且跳转/toExamingPage/"+data.examCode的controller。data.examCode就是选中的这场考试的编号。



**/toExamingPage/{id}**：

```java
    @GetMapping("/toExamingPage/{id}")
    public String toExamingPage(@PathVariable("id") String examCode, HttpSession session, Model model) {

        if(session.getAttribute("id")==null){
            model.addAttribute("errorMsg","session过期请重新登录");
            return "login";
        }

        //session中保存在正在考试的id
        session.setAttribute("examCode", examCode);
        //取出学生ID
        String id = String.valueOf(session.getAttribute("id"));

        model.addAttribute("examCode", examCode);

        System.out.println(examCode);

        Score score = scoreService.getScoreByExamCodeAndStudentID(examCode, id);

        //如果数据库里有已经考试的信息
        if (score != null) {
            //如果已经参加过考试了
            if (scoreService.is_marked(examCode, id)) {
                //获取分数返回
                model.addAttribute("msg", "已经参加过这个考试了成绩为:" + score.getScore());
            } else {
                model.addAttribute("msg", "已经参加过这个考试了但是老师未批改");
            }
            return "student/exam/joined";
        }
        //否则就还没考过这个试卷
        return "student/exam/exam";
    }
```

​	取出当前用户的id，结合examCode，检索Score数据库。如果发现这个学生已经考过该场考试并且已批改就返回学生的成绩，如果考过但是没批改，就返回一个提示。  如果这个学生没有考过这场考试，就跳转至"student/exam/exam"。



**"student/exam/exam.html"**：出卷的前端写法分为两个部分：模板+数据填充







**选择题模板：**

```js
<!--选择题模板-->
<script type="text/html" id="multiQuestionTemplate">
    <br>
    <from class="layui-form" action="">
        <!--题目内容-->
        <div align="left">
            <h3 class="layui-form-item" id="question" type="text"></h3>
        </div>
        <br>
        <!--四个选项-->
        <div align="left" class="layui-form-item layui-elem-quote layui-quote-nm">
            <label>A.</label>
            <label id="A">答案内容</label>
        </div>
        <div  align="left" class="layui-form-item layui-elem-quote layui-quote-nm">
            <label>B.</label>
            <label id="B">答案内容</label>
        </div>
        <div align="left" class="layui-form-item layui-elem-quote layui-quote-nm">
            <label>C.</label>
            <label id="C">答案内容</label>
        </div>
        <div align="left" class="layui-form-item layui-elem-quote layui-quote-nm">
            <label>D.</label>
            <label id="D">答案内容</label>
        </div>
        <!--考生答案-->
        <div class="layui-form-item">
            <label class="layui-form-label">答案</label>
            <div class="layui-input-block">
                <select id="answer">
                    <option value=""></option>
                    <option value="A">A</option>
                    <option value="B">B</option>
                    <option value="C">C</option>
                    <option value="D">D</option>
                </select>
            </div>
        </div>
    </from>
    <hr>

```

​	这部分是模板，到时候我们用js往里面填数据就ok了。



数据填充：==这部分有点长，慢慢讲==

```js
<script type="text/javascript" th:inline="none">

    let answerList = []
    let questionId = []
    let questionType = []
    var editorList = []

    let len; //题目总的个数

    let lenMulFill;//填空题+选择题个数

    layui.use(['form','layer'], function () {
        var form = layui.form;
        let layer = layui.layer;
        $.ajax({
            type: "get",
            url: '/paper/' + $("#examCode").text(),
            success: function (data) {
                //console.log(data)
                let multiQuestions = data.data[1];
                let fillQuestion = data.data[2];
                let calQuestion = data.data[3];
                let proveQuestion = data.data[4];

                len = multiQuestions.length+fillQuestion.length+calQuestion.length+proveQuestion.length;
                lenMulFill = multiQuestions.length+fillQuestion.length;
                //生成html模板
                let i = 0; //保存 总的下标
                //选择题
                for (let j = 0; j < multiQuestions.length; j++, i++) {
                    questionId[i] = multiQuestions[j].questionId; //保存题目ID
                    questionType[i] = '1';//选择题类型

                    $("#main").append($("#multiQuestionTemplate").text());
                    $("#question").attr("id", "question" + i);          //题干
                    $("#A").attr("id", "A" + i);
                    $("#B").attr("id", "B" + i);
                    $("#C").attr("id", "C" + i);
                    $("#D").attr("id", "D" + i);
                    $("#answer").attr("id", "answer" + i);

                    $("#question"+i).text(1+i+'.'+multiQuestions[j].question)
                    $("#A"+i).text(multiQuestions[j].answerA)
                    $("#B"+i).text(multiQuestions[j].answerB)
                    $("#C"+i).text(multiQuestions[j].answerC)
                    $("#D"+i).text(multiQuestions[j].answerD)

                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "question"+i]);
                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "A"+i]);
                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "B"+i]);
                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "C"+i]);
                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "D"+i]);

                }
                //填空题
                for (let j = 0; j < fillQuestion.length; j++, i++) {
                    questionId[i] = fillQuestion[j].questionId; //保存题目ID
                    questionType[i] = '2';//填空题类型

                    $("#main").append($("#simpleTemplate").text());
                    $("#question").attr("id","question" + i);          //题干
                    $("#answer").attr("id", "answer" + i);

                    $("#question"+i).text(1+i+'.'+fillQuestion[j].question)

                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "question"+i]);

                }
                //计算
                for (let j = 0; j < calQuestion.length; j++, i++) {
                    questionId[i] = calQuestion[j].questionId; //保存题目ID
                    questionType[i] = '3';//填空题类型

                    $("#main").append($("#EditorTemplate").text());
                    $("#question").attr("id", "question" + i);          //题干
                    $("#answer").attr("id", "answer" + i);



                    editorList[i] = editormd("answer"+i, {
                        placeholder : '欢迎使用,编写数学公式请使用 latex格式,推荐使用MathType编辑后复制出来',
                        width: "100%",
                        height: 400,
                        path : '/editor/lib/',
                        //theme : "dark",
                        //previewTheme : "dark",
                        //editorTheme : "pastel-on-dark",
                        // markdown : md,
                        codeFold : true,
                        //syncScrolling : false,
                        saveHTMLToTextarea : true,    // 保存 HTML 到 Textarea
                        searchReplace : true,
                        //watch : false,                // 关闭实时预览
                        htmlDecode : "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启
                        //toolbar  : false,             //关闭工具栏
                        //previewCodeHighlight : false, // 关闭预览 HTML 的代码块高亮，默认开启
                        emoji : true,
                        taskList : true,
                        tocm            : true,         // Using [TOCM]
                        tex : true,                   // 开启科学公式TeX语言支持，默认关闭
                        flowChart : true,             // 开启流程图支持，默认关闭
                        sequenceDiagram : true,       // 开启时序/序列图支持，默认关闭,
                        //dialogLockScreen : false,   // 设置弹出层对话框不锁屏，全局通用，默认为true
                        //dialogShowMask : false,     // 设置弹出层对话框显示透明遮罩层，全局通用，默认为true
                        //dialogDraggable : false,    // 设置弹出层对话框不可拖动，全局通用，默认为true
                        //dialogMaskOpacity : 0.4,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
                        //dialogMaskBgColor : "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
                        imageUpload : true,
                        imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                        imageUploadURL : "/image/upload",
                        onload : function() {
                            console.log('onload', this);
                            this.hideToolbar();
                            //this.fullscreen();
                            //this.unwatch();
                            //this.watch().fullscreen();

                            //this.setMarkdown("#PHP");
                            //this.width("100%");
                            //this.height(480);
                            //this.resize("100%", 640);
                        }
                        // });
                    });

                    $("#question"+i).text(1+i+'.'+calQuestion[j].question)

                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "question"+i]);
                }
                //证明
                for (let j = 0; j < proveQuestion.length; j++, i++) {
                    questionId[i] = proveQuestion[j].questionId; //保存题目ID
                    questionType[i] = '4';//填空题类型

                    $("#main").append($("#EditorTemplate").text());

                    $("#question").attr("id", "question" + i);          //题干
                    $("#answer").attr("id", "answer" + i);

                    editorList[i] = editormd("answer"+i, {
                        placeholder : '欢迎使用,编写数学公式请使用 latex格式,推荐使用MathType编辑后复制出来',
                        width: "100%",
                        height: 400,
                        path : '/editor/lib/',
                        //theme : "dark",
                        //previewTheme : "dark",
                        //editorTheme : "pastel-on-dark",
                        // markdown : md,
                        codeFold : true,
                        //syncScrolling : false,
                        saveHTMLToTextarea : true,    // 保存 HTML 到 Textarea
                        searchReplace : true,
                        //watch : false,                // 关闭实时预览
                        htmlDecode : "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启
                        //toolbar  : false,             //关闭工具栏
                        //previewCodeHighlight : false, // 关闭预览 HTML 的代码块高亮，默认开启
                        emoji : true,
                        taskList : true,
                        tocm            : true,         // Using [TOCM]
                        tex : true,                   // 开启科学公式TeX语言支持，默认关闭
                        flowChart : true,             // 开启流程图支持，默认关闭
                        sequenceDiagram : true,       // 开启时序/序列图支持，默认关闭,
                        //dialogLockScreen : false,   // 设置弹出层对话框不锁屏，全局通用，默认为true
                        //dialogShowMask : false,     // 设置弹出层对话框显示透明遮罩层，全局通用，默认为true
                        //dialogDraggable : false,    // 设置弹出层对话框不可拖动，全局通用，默认为true
                        //dialogMaskOpacity : 0.4,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
                        //dialogMaskBgColor : "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
                        imageUpload : true,
                        imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                        imageUploadURL : "/image/upload",
                        onload : function() {
                            //console.log('onload', this);
                            this.hideToolbar();
                            //this.fullscreen();
                            //this.unwatch();
                            //this.watch().fullscreen();

                            //this.setMarkdown("#PHP");
                            //this.width("100%");
                            //this.height(480);
                            //this.resize("100%", 640);
                        }
                        // });
                    });

                    $("#question"+i).text(1+i+'.'+proveQuestion[j].question)

                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "question"+i]);
                }

                form.render();
                //然后给每个DOM赋值

            }
        })
```

第一个ajax会异步请求到"/paper/{examCode}"这个controller，

```java
    @GetMapping("/paper/{examCode}")
    @ResponseBody
    public ApiResult findById(@PathVariable("examCode") Integer examCode) {

        if (examCode == null) return ApiResultHandler.buildApiResult(400, "考试ID错误！", null);

        Map<Integer, List<?>> paperByExam = getPaperByExamCode(examCode);

        if (paperByExam == null) return ApiResultHandler.buildApiResult(400, "试卷为空！", null);

        return ApiResultHandler.buildApiResult(0, "请求成功！", paperByExam);

    }
```

​	getPaperByExamCode这个函数得到了这场考试用的考卷的所有选择题，并且放到map里，并且返回一个ApiResult给前端。



详细讲讲getPaperByExamCode这个函数

**getPaperByExamCode**：

```java
public Map<Integer, List<?>> getPaperByExamCode(Integer examCode) {

        if (examCode == null) {
            return null;
        }
        //获取
        ExamManage examManage = examManageService.findById(examCode);

        Integer paperId = examManage.getPaperId();
        //选择题题库 1
        List<MultiQuestion> multiQuestionRes = multiQuestionService.findByIdAndType(paperId);
        //填空题题库 2
        List<FillQuestion> fillQuestionsRes = fillQuestionService.findByIdAndType(paperId);
        List<CalQuestion> calQuestionsRes = calQuestionService.findByIdAndType(paperId);
        List<ProveQuestion> proveQuestionsRes = proveQuestionService.findByIdAndType(paperId);
        Map<Integer, List<?>> map = new HashMap<>(8);
        map.put(1, multiQuestionRes);
        map.put(2, fillQuestionsRes);
        map.put(3, calQuestionsRes);
        map.put(4, proveQuestionsRes);
        return map;
    }
```

​	参数是examCode，方法里调用了examManageService.findById(examCode)获取到这场考试的对象，并且获取到这场考试用的考卷的paperId。 这里特别说明一下，考试和试卷不是同一个东西，一场考试只能有一张考卷，但是一张考卷可能被多场考试使用。

​	于是这里我们得知了我们这张考试用到了哪张考卷。接下来调用multiQuestionService.findByIdAndType(paperId)，就可以获取到这张考卷所有的选择题的questionId所组成的list，填空题同理。 

​	这里再提一句，考卷的信息记录在paper_manager这个数据表里，它的字段结构是{paperId，questionType，questionId}，paperId指考卷编号；questionType指题目类型，1代表选择题，2代表填空题；questionId代表的就是这道题的题号。multiQuestionService.findByIdAndType(paperId)这个方法用了一个嵌套子查询：==select * from multi_question where questionId in(select questionId from paper_manager where paperId=#{paperId} and questionType=1)==   内层就是检索paper_manager表中所有的，当前paperId的考卷里的所有questionType是1的记录，即这张考卷的所有选择题。外层就是在选择题的数据表检索出这些题号对应的选择题的详细信息，包括它们的题干、选项、正确答案等等等。  然后将这些详细数据存入list里。

​	得到所有的题型的list之后，把这四个list全部put到map里，并且返回。





接下来就是数据的填充过程了(这边图方便我直接写注释来解释)

```js
  		/*
  		  data就是后端返回过来的ApiResult，data.data就是那个map，data.data[1]就是map里key为1的value，也就是选择题的			  list~	
  		*/
					let multiQuestions = data.data[1];
               		let fillQuestion = data.data[2];
                	let calQuestion = data.data[3];
               	 	let proveQuestion = data.data[4];



					//len就是这张考卷的题总数
                	len = multiQuestions.length+fillQuestion.length+calQuestion.length+proveQuestion.length;
					//选择题和填空题的总数
                	lenMulFill = multiQuestions.length+fillQuestion.length;
                	
                	let i = 0; //维护一个题数总的下标
					//通过循环，反复填充模板
 					for (let j = 0; j < multiQuestions.length; j++, i++) {
                    	questionId[i] = multiQuestions[j].questionId; //保存题目ID
                   		questionType[i] = '1';//选择题类型

                    	$("#main").append($("#multiQuestionTemplate").text());
                    	$("#question").attr("id", "question" + i);          //题干
                    	$("#A").attr("id", "A" + i);
                    	$("#B").attr("id", "B" + i);
                    	$("#C").attr("id", "C" + i);
                    	$("#D").attr("id", "D" + i);
                    	$("#answer").attr("id", "answer" + i);
						
                        //每次循环都会渲染到不同的模板里，循环几次就会有几个模板被渲染。
                    	$("#question"+i).text(1+i+'.'+multiQuestions[j].question)
                    	$("#A"+i).text(multiQuestions[j].answerA)
                    	$("#B"+i).text(multiQuestions[j].answerB)
                    	$("#C"+i).text(multiQuestions[j].answerC)
                    	$("#D"+i).text(multiQuestions[j].answerD)

                    	window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "question"+i]);
                    	window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "A"+i]);
                    	window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "B"+i]);
                    	window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "C"+i]);
                    	window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "D"+i]);

                	}
                //填空题同理
                for (let j = 0; j < fillQuestion.length; j++, i++) {
                    questionId[i] = fillQuestion[j].questionId; //保存题目ID
                    questionType[i] = '2';//填空题类型

                    $("#main").append($("#simpleTemplate").text());
                    $("#question").attr("id","question" + i);          //题干
                    $("#answer").attr("id", "answer" + i);

                    $("#question"+i).text(1+i+'.'+fillQuestion[j].question)

                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "question"+i]);

                }
```



上面这个ajax会优先执行。执行完毕后，学生答完题，点击提交，就会走下面的代码

```js
        $("#submit").click(function () {
            $("#submit").addClass("div-cant-click")
            //获取所有答案
            let i = 0;
            for (let i=0;i<len;i++){
                //获取所有的选择题和填空题
                if(i<lenMulFill){
                    answerList[i] =  $("#answer"+i).val() ;
                }else {
                    answerList[i] = editorList[i].getMarkdown();
                }
            }

            //console.log(answerList);
            $.ajax({
                url: "/exam/submitAnswer",
                data: {
                    "answerList": answerList,
                    "questionIdList": questionId,
                    "questionTypeList": questionType,
                    "examCode": $("#examCode").text()
                },
                dataType: "json",
                type: "post",
                traditional: true,
                success: function (data) {
                    if(data.code==411){
                        $("#submit").removeClass("div-cant-click")
                        layer.alert(data.message);
                    }else {
                        // layer.confirm(data.message, function(index){
                        //     top.location.href = "/studentIndex";
                        // });
                        layer.confirm(data.message,{
                            btn: ['确认'],
                            btn1: function(){
                                top.location.href = "/studentIndex";
                            }
                        });
                    }

                }
            })

        })

```

将学生的答案存放在answerList数组里，然后ajax异步请求/exam/submitAnswer，并且向后端传四个数据。



**/exam/submitAnswer**:

```java
    @PostMapping("/submitAnswer")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult submitAnswer(@RequestParam("answerList") List<String> answerList, @RequestParam("questionIdList") List<String> questionIdList, @RequestParam("questionTypeList") List<String> questionTypeList, @RequestParam("examCode")int examCode, HttpSession session) {

        for (int i = 0; i < answerList.size(); i++) {
            if(answerList.get(i)==null||"".equals(answerList.get(i))){
                return ApiResultHandler.buildApiResult(411,"有答案为空，请重新填写提交",null);
            }
        }
        //定义事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();

        try {
            //获取考试人的id
            String studentId = String.valueOf(session.getAttribute("id"));
            //String studentId = CookieUtil.getCookieValue(request,"id");
            //获取考试信息
            ExamManage examManage = examManageService.findById(examCode);

            for (int i = 0; i < answerList.size(); i++) {
                Answer answer = new Answer();
                //如果题目类型是 选择题或者填空题，就自动批改
                if ("1".equals(questionTypeList.get(i)) || "2".equals(questionTypeList.get(i))) {
                    Question question = null;
                    if ("1".equals(questionTypeList.get(i))) {
                        question = multiQuestionService.findById(questionIdList.get(i));
                    }
                    if ("2".equals(questionTypeList.get(i))) {
                        question = fillQuestionService.findById(questionIdList.get(i));
                    }
                    //题目被删除了，可能，应该不会
                    if (question == null) {
                        System.out.println("题目不存在");
                        continue;
                    }
                    if (answerList.get(i).equals(question.getAnswer())) {
                        //答案对就满分
                        answer.setFinalScore(String.valueOf(question.getScore()));
                    } else {
                        //错了给0分
                        answer.setFinalScore("0");
                    }
                }
                answer.setStudentId(studentId);
                answer.setExamCode(String.valueOf(examCode));
                answer.setQuestionId(questionIdList.get(i));
                answer.setQuestionType(questionTypeList.get(i));
                answer.setComment("");
                answer.setStudentAnswer(answerList.get(i));


                answerService.add(answer);

            }

            //录入Score信息，但是未打分
            Score score = new Score();
            score.setStudentId(Integer.valueOf(studentId));
            score.setAnswerDate("");
            score.setExamCode(Integer.valueOf(examCode));
            //获取科目
            score.setSubject(examManage.getSource());
            //设置为还未被打分
            score.setIs_marked(false);
            //日期
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            score.setAnswerDate(simpleDateFormat.format(date));
            scoreService.add(score);

            return ApiResultHandler.buildApiResult(200, "提交试卷成功",null);
        }catch (Exception e){
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(400,"提交试卷失败",null);
        }

    }
```

大体逻辑：前端传过来了answerList数组，遍历answerList数组，下标同时对应questionTypeList数组 和questionIdList数组。对于选择题和填空题，我们使用自动计分的方法。  如果学生这题的答案与这道题的answer一致，视为答对，并将一条答题记录存入answer数据表中，一条记录的主要信息是{答题人id，考试编号，问题编号，答题人的答案，得分}。



#### 4、老师批改功能

主要是涉及到一个paper.html页面

```js
<script type="text/javascript" th:inline="none">
    layui.use('table', function(){
        var table = layui.table;

        //温馨提示：默认由前端自动合计当前行数据。从 layui 2.5.6 开始： 若接口直接返回了合计行数据，则优先读取接口合计行数据。
        //详见：https://www.layui.com/doc/modules/table.html#totalRow
        table.render({
            elem: '#test'
            ,url:'/paper/marks'
            //,toolbar: '#toolbarDemo'
            ,title: '待批改试卷表'
            ,totalRow: true
            ,cols: [[
                {type: 'checkbox', fixed: 'left'}
                ,{field:'studentName', title:'studentName'}
                ,{field:'subject', title:'subject'}
                ,{field:'answerDate', title:'Date'}
                ,{fixed: 'right', title:'操作', toolbar: '#barDemo'}
            ]]
            ,page: false
        });

        //工具栏事件
        table.on('toolbar(test)', function(obj){
            var checkStatus = table.checkStatus(obj.config.id);
            switch(obj.event){
                case 'getCheckData':
                    var data = checkStatus.data;
                    layer.alert(JSON.stringify(data));
                    break;
                case 'getCheckLength':
                    var data = checkStatus.data;
                    layer.msg('选中了：'+ data.length + ' 个');
                    break;
                case 'isAll':
                    layer.msg(checkStatus.isAll ? '全选': '未全选')
                    break;
            };
        });

        //监听工具条
        table.on('tool(test)', function(obj){
            var data = obj.data;
            console.log(data.examCode)
            //console.log(data);
            if(obj.event === 'mark'){

                layer.confirm('确定批改这个试卷吗', function(index){
                    window.location.href = "/toMarkPage/"+data.examCode+"/"+data.studentId;
                });
            }
        });

    });
</script>
```

在这个页面里，通过ajax异步请求/paper/marks这个controller

```java
@GetMapping("/paper/marks")
    @ResponseBody
    public ApiResult toBeMarkedPapers() {

        //获取需要打分的
        List<Score> toBeMarkedScore = scoreService.getToBeMarkedScore();
        List<PaperVO> list = new LinkedList<>();
        for (Score score : toBeMarkedScore) {
            PaperVO paperVO = new PaperVO();

            //获取学生名称
            Student student = studentService.findById(score.getStudentId());
            //获取考试名称
            ExamManage exam = examManageService.findById(score.getExamCode());
            if(student==null||exam==null){
                //如果学生或考试已经被删除了,就直接跳过,并逻辑删除这个成绩
                scoreService.deleteScoreById(score.getScoreId().toString());
                continue;
            }
            String studentName = student.getStudentName();

            String subject = exam.getSource();
            paperVO.setAnswerDate(score.getAnswerDate());
            paperVO.setExamCode(String.valueOf(score.getExamCode()));
            paperVO.setStudentId(String.valueOf(score.getStudentId()));
            paperVO.setStudentName(studentName);
            paperVO.setSubject(subject);

            list.add(paperVO);
        }
```

通过scoreService.getToBeMarkedScore()方法获取到socre表中所有没有被打分的学生考试记录。并将这些记录分别用PaperV0再封装。 PaperV0类的成员变量有学生编号、学生姓名、考试编号、科目姓名、作答日期这五个。  然后将所有的PaperV0组成一个list，响应给前端，通过layui框架模板显示出来。

此时如果老师选中其中一条记录进行批改试卷，就会跳转到"/toMarkPage/"+data.examCode+"/"+data.studentId这个controller里。

**/toMarkPage/"+data.examCode+"/"+data.studentId**:

```java
    @GetMapping("/toMarkPage/{examCode}/{studentId}")
    public String toMarkPage(@PathVariable("examCode") String examCode, @PathVariable("studentId") String studentId, Model model) {
        model.addAttribute("examCode", examCode);
        model.addAttribute("studentId", studentId);

        Student student = studentService.findById(Integer.valueOf(studentId));
        ExamManage examManage = examManageService.findById(Integer.valueOf(examCode));

        model.addAttribute("studentName", student.getStudentName());
        model.addAttribute("examName", examManage.getSource());

        return "teacher/exam/mark";
    }
```

这个controller没啥好讲的，就是通过前端传过来的studentId和examCode获取到studentName和subject，然后把它们都放进model里，跳转到teacher/exam/mark.html页面。

这个页面，主要部分是这个

```js
    layui.use(['form', 'layedit', 'laydate'], function () {
        var form = layui.form
            , layer = layui.layer
            , layedit = layui.layedit
            , laydate = layui.laydate;


        $.ajax({
            type: "get",
            url: '/paper/questions',
            data: {
                examCode: $("#examCode").text(),
                studentId: $("#studentId").text()
            },
            success: function (data) {
               // console.log(data.data);//数据获取到了
                globalData = data.data;
                var question = data.data;
                //开始动态渲染

                $("#head").text("考试科目：" + $("#examName").text() + '   学生：' + $("#studentName").text());
                for (let i = 0; i < question.length; i++) {
                    $("#main").append($("#questionTemplate").text());

                    $("#question").attr("id", "question" + i);     //题干
                    $("#questionId").attr("id", "questionId" + i);  //题目ID
                    $("#studentAnswer").attr("id", "studentAnswer" + i); //学生答案
                    $("#RightAnswer").attr("id", "RightAnswer" + i);  //正确答案
                    $("#fullScore").attr("id", "fullScore" + i);      //满分
                    $("#score").attr("id", "score" + i);      //打分
                }

                for (let i = 0; i < question.length; i++) {

                    $("#question" + i).text(i + 1 + '.' + question[i].question);
                    $("#questionId" + i).text(question[i].questionId);
                    $("#studentAnswer" + i).text('学生答案为：' + question[i].studentAnswer);
                    $("#RightAnswer" + i).text('正确答案为:' + question[i].rightAnswer);
                    $("#fullScore" + i).text('本题满分为：' + question[i].fullScore);
                    $("#score" + i).val(question[i].score);

                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "question"+i]);
                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "studentAnswer"+i]);
                    window.MathJax.Hub.Queue(["Typeset", MathJax.Hub, "RightAnswer"+i]);

                }

                //$("#main").append($("#submitTemplate").text());
                //console.log($("#baseModel").text())
                form.render();

                //MathJax.Hub.Queue(['Typeset', MathJax.Hub]); //加这段代码

            }
        });

        //日期
        // laydate.render({
        //     elem: '#date'
        // });
        // laydate.render({
        //     elem: '#date1'
        // });


    });
```

这边也是通过ajax异步请求后端/paper/questions，并且向后端传回去了当前正要批改的试卷的学号、考试号


```java
    @GetMapping("/paper/questions")
    @ResponseBody
    public ApiResult getQuestionsToBeMarked(@RequestParam("examCode") String examCode, @RequestParam("studentId") String studentId) {

        //这次考试这个学生需要批改的题目
        List<Answer> answerList = answerService.getAnswerByExamCodeAndStudentId(examCode, studentId);

        List<MarkVO> markVOList = new LinkedList<>();


        for (Answer answer : answerList) {
            MarkVO markVO = new MarkVO();

            Question question = new Question();
            //如果是选择题
            if ("1".equals(answer.getQuestionType())) {
                question = multiQuestionService.findById(answer.getQuestionId());
            } else if ("2".equals(answer.getQuestionType())) {
                question = fillQuestionService.findById(answer.getQuestionId());
            } else if ("3".equals(answer.getQuestionType())) {
                question = calQuestionService.findById(answer.getQuestionId());
            } else if ("4".equals(answer.getQuestionType())) {
                question = proveQuestionService.findById(answer.getQuestionId());
            }

            markVO.setQuestion(question.getQuestion());
            markVO.setQuestionId(answer.getQuestionId());
            markVO.setQuestionType(answer.getQuestionType());
            markVO.setRightAnswer(question.getAnswer());
            markVO.setStudentAnswer(answer.getStudentAnswer());
            //选择题分数会有
            markVO.setScore(answer.getFinalScore());
            //总分为多少，这题的
            markVO.setFullScore(String.valueOf(question.getScore()));

            markVOList.add(markVO);
        }

        return ApiResultHandler.buildApiResult(0, "请求成功！", markVOList);
    }
```

这边略微复杂，详细讲讲。
首先，通过sudentId和examCode可以在answer表里查出待批改试卷所有的题目。记录以answer对象存储。  而MarkV0类是干嘛的呢？  它是一个自定义的实体类，不关联数据库，而是将quesion类和answer表的字段组合。       我们来看看这个字段成员--》{questionId，questionType，question，studentAnswer，rightAnswer，score，fullscore}。  其中questionId、questionType、studentAnswer、score来自于answer类，而question，rightAnswer，fullscore来自于question类。
现在我们已经有了answer对象的记录了，通过answer记录里的questionId，就可以查出这个question的所有信息，也就是得到对应的question对象。 我们将这两个对象的属性组合后，得到MarkV0类的list，并且响应给前端。

接下来就是前端把数据用layui渲染，老师打分。这部分和学生考试那块几乎一模一样，不详细讲了。


当老师完成批阅后，按下”批阅完成“，就会触发js代码。

```js
    var questionIdList = [];
    var questionTypeList = [];
    var finalScoreList = [];

    $("#submit").click(function () {
        $("#submit").addClass("div-cant-click")
        for (let i=0; i < globalData.length; i++) {
            questionIdList[i] = globalData[i].questionId
            questionTypeList[i] = globalData[i].questionType
            finalScoreList[i] = $("#score"+i).val();
        }

        //console.log(questionIdList);
        //console.log(questionTypeList);
        //console.log(finalScoreList);

        let examCode =$("#examCode").text();
        let studentId = $("#studentId").text();

        $.ajax({
            url: "/paper/marked",
            data: {
                "questionIdList": questionIdList,
                "questionTypeList": questionTypeList,
                "finalScoreList": finalScoreList,
                "examCode":examCode,
                "studentId":studentId
            },
            dataType: "json",
            type: "post",
            traditional: true,
            success: function (data) {
                if(data.code==412){
                    layer.alert(data.message)
                    $("#submit").removeClass("div-cant-click")
                }else if(data.code==413) {
                    layer.alert(data.message)
                    $("#submit").removeClass("div-cant-click")
                }else {
                    layer.confirm(data.message,{
                        btn: ['确认'],
                        btn1: function(){
                            window.location.href = "/toPaperPage";
                        }
                    });
                }

            }

        })
    })
```

同样是用ajax，传回data给后端一个叫做/paper/marked的controller

```java
 @PostMapping("/paper/marked")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public ApiResult marked(@RequestParam("questionIdList") List<String> questionIdList,
                            @RequestParam("questionTypeList") List<String> questionTypeList,
                            @RequestParam("finalScoreList") List<String> finalScoreList,
                            @RequestParam("examCode") String examCode,
                            @RequestParam("studentId") String studentId) {

        for (int i = 0; i < finalScoreList.size(); i++) {
            if(finalScoreList.get(i)==null||"".equals(finalScoreList.get(i))){
                return ApiResultHandler.buildApiResult(412,"有些题目没有打分,请打完分后提交",null);
            }
            if(!isNumeric(finalScoreList.get(i))){
                return ApiResultHandler.buildApiResult(413,"分数不是数字，请重新输入",null);
            }
        }

        //定义事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();

        try{
            for (int i = 0; i < questionIdList.size(); i++) {
                //更新所有分数
                answerService.updateFinalScoreBy(finalScoreList.get(i),studentId,examCode,questionIdList.get(i),questionTypeList.get(i));
            }
            int totalScore=0;
            //更新完成后统计分数，给到分数表中
            for (int i = 0; i < finalScoreList.size(); i++) {
                totalScore += Integer.parseInt(finalScoreList.get(i));
            }
            scoreService.updateScoreByExamCodeAndStudentId(String.valueOf(totalScore),examCode,studentId);
            scoreService.setIsMarked(examCode,studentId);
            return ApiResultHandler.buildApiResult(200, "批阅成功！", null);
        }catch (Exception e){
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(500, "批阅失败！", null);
        }
    }

```

这个controller就很简单了。 得到了老师批改后的分数数组，就可以用它对answer表中属于这个学生这场考试的所有题的得分进行一个update。  然后统计总分，并将总分更新到score表里，且将这个学生这场考试的这条记录的isMarked字段值置为true。批改完成。

#### 5、出卷功能



##### 1、随机出卷


```js
<script>
    layui.use(['form', 'layedit', 'laydate'], function () {
        var form = layui.form
            , layer = layui.layer

        //监听提交
        form.on('submit(demo1)', function (data) {

            // var json = {size1:$("#size1").val(),size2:$("#size2").val(),size3:$("#size3").val(),size4:$("#size4").val(),
            //     section:$("#section").val(),subject:$("#subject").val(),examCode:$("#examCode").val()};
            //
            // console.log(data)
            $.ajax({
                type:"post",
                data: {size1:$("#size1").val(),size2:$("#size2").val(),size3:$("#size3").val(),size4:$("#size4").val(),
                    section:$("#section").val(),subject:$("#subject").val(),examCode:$("#examCode").text()},
                url:'/paper/RandomGenerate',
                success:function (data) {
                    if (data.code == 200) {
                        layer.confirm('试题添加成功！',{
                            btn: ['确认','取消'],
                            btn1: function(){
                                window.location.href ="/toExamPage";
                            },
                            btn2: function (){

                            }
                        });
                    } else {
                        layer.alert(data.message);
                    }

                }
            })
            //没有这句话不行，弹出框出不来
            return false;
        });
    });
</script>
```


通过ajax异步请求/paper/RandomGenerated的controller，并且将data请求到后端。



```java
@PostMapping(name = "随机生成试卷", value = "/paper/RandomGenerate")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public ApiResult RandomGenerate(@RequestParam("size1") int size1,
                                    @RequestParam("size2") int size2,
                                    @RequestParam("size3") int size3,
                                    @RequestParam("size4") int size4,
                                    @RequestParam("section") String section,
                                    @RequestParam("subject") String subject,
                                    @RequestParam("examCode") int examCode) {

        //事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            //通过ID获取exam
            ExamManage examManage = examManageService.findById(examCode);
            //然后开始生成试卷
            //通过科目和单元获取全部试题
            List<MultiQuestion> multiQuestions = multiQuestionService.findBySectionAndSubject(section, subject);
            List<FillQuestion> fillQuestions = fillQuestionService.findBySectionAndSubject(section, subject);
            List<CalQuestion> calQuestions = calQuestionService.findBySectionAndSubject(section, subject);
            List<ProveQuestion> proveQuestions = proveQuestionService.findBySectionAndSubject(section, subject);
            //如果题目数量不足
            if (multiQuestions.size() < size1 ||
                    fillQuestions.size() < size2 ||
                    calQuestions.size() < size3 ||
                    proveQuestions.size() < size4) {
                return ApiResultHandler.buildApiResult(415, "题库题目数量不足" +
                                "选择题个数为" + multiQuestions.size() +
                                "填空题个数为" + fillQuestions.size() +
                                "计算题个数为" + calQuestions.size() +
                                "证明题个数为" + proveQuestions.size() +
                                "。" + "请检查题目数量并重新提交。"
                        , null);
            }
            List<MultiQuestion> multiQuestion = pickQuestionRandomly1(multiQuestions.size(), size1, multiQuestions);
            List<FillQuestion> fillQuestion = pickQuestionRandomly2(fillQuestions.size(), size2, fillQuestions);
            List<CalQuestion> calQuestion = pickQuestionRandomly3(calQuestions.size(), size3, calQuestions);
            List<ProveQuestion> proveQuestion = pickQuestionRandomly4(proveQuestions.size(), size4, proveQuestions);
            int totalScore = 0;

            int paperId;
            if (examManage.getPaperId() == null) {
                //获取最近一次试卷编号
                int onlyPaperId = paperService.findOnlyPaperId();
                //计算出新的试卷编号
                paperId = onlyPaperId + 1;
                //设置paperID
                examManage.setPaperId(paperId);
            } else {
                paperId = examManage.getPaperId();
            }
            for (int i = 0; i < multiQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 1, multiQuestion.get(i).getQuestionId()));
                totalScore += multiQuestion.get(i).getScore();
            }
            for (int i = 0; i < fillQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 2, fillQuestion.get(i).getQuestionId()));
                totalScore += fillQuestion.get(i).getScore();
            }
            for (int i = 0; i < calQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 3, calQuestion.get(i).getQuestionId()));
                totalScore += calQuestion.get(i).getScore();
            }
            for (int i = 0; i < proveQuestion.size(); i++) {
                paperService.add(new PaperManage(paperId, 4, proveQuestion.get(i).getQuestionId()));
                totalScore += proveQuestion.get(i).getScore();
            }

            examManage.setTotalScore(totalScore);
            examManageService.update(examManage);
            return ApiResultHandler.buildApiResult(200, "试卷生成成功！", null);
        } catch (Exception e) {
            ///出现异常回滚
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(500, "试卷生成成功失败,服务器出现未知异常！", null);
        }
    }

```


大致讲讲。 前端用ajax传过来七个值，分别是四种题型的数量、科目、单元、考试编号。
这边我们的目的就是生成一张新试卷，然后让这场考试使用这张新试卷。
所以我们先拿到这场考试的记录。然后根据部门和学科，把所有的题型全部检索出来，分别存放在list里。
接下来就是随机的过程了。
这里用到了一个函数pickQuestionRandomly1(multiQuestions.size(), size1, multiQuestions);

```java
    public List<MultiQuestion> pickQuestionRandomly1(int questionSize, int pickSize, List<MultiQuestion> questions) {
        int[] randomNums = randomCommon(0, questionSize - 1, pickSize);
        List<MultiQuestion> pickedQuestions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < randomNums.length; i++) {
            pickedQuestions.add(questions.get(randomNums[i]));
        }
        return pickedQuestions;
    }
```

​	randomCommon函数的三个参数分别是：随机数下限、随机数上限、随机数个数
​	这样子就能获得size1个随机数，并在list里找出这些题目，写入list中返回出来。

​	然后我们继续讲之前的函数。

​	我们要创建一张新试卷，那么就要在papermanager里添加新的paperId的记录，于是就要找出最新的paperId，令其加1，就是现在创建的这张试卷的paperId。然后在遍历刚刚得到的随机题list，将所有随机题的questionId、questionType取出，与刚刚的paperId组成一条新的记录insert进paperManager数据表中，并且计算总分。
最后只需要更新examManager数据表中的paperId字段和score字段就ok了。



##### 2、手动出卷

```js
        form.on('submit(demo1)', function (data) {
            $.ajax({
                type: "get",
                data: {section: $("#section").val(), subject: $("#subject").val(), examCode: $("#examCode").text()},
                url: '/paper/candidateQuestions',
                success: function (data) {
                    //console.log(data)
                    $("#p1").show();
                    $("#p2").show();
                    $("#p3").show();
                    $("#p4").show();
                    $("#tip").show();
                    $("#div1").show();
                    //渲染题目表格数据
                    table.render({
                        elem: '#mul'
                        , title: '待 选择题表'
                        , totalRow: true
                        , cols: [[
                            {type: 'checkbox', fixed: 'left'}
                            , {field: 'questionId', title: 'questionId'}
                            , {field: 'subject', title: 'subject'}
                            , {field: 'section', title: 'section'}
                            , {field: 'answerA', title: 'A'}
                            , {field: 'answerB', title: 'B'}
                            , {field: 'answerC', title: 'C'}
                            , {field: 'answerD', title: 'D'}
                            , {field: 'question', title: '题目'}
                            , {field: 'rightAnswer', title: 'rightAnswer'}
                            , {field: 'analysis', title: 'analysis'}
                            , {field: 'score', title: 'score'}
                        ]]
                        , data: data.data[1]
                        , page: false
                        , limit: data.data[1].length
                    });
                    table.render({
                        elem: '#fill'
                        , title: '待 填空题表'
                        , totalRow: true
                        , cols: [[
                            {type: 'checkbox', fixed: 'left'}
                            , {field: 'questionId', title: 'questionId'}
                            , {field: 'subject', title: 'subject'}
                            , {field: 'question', title: 'question'}
                            , {field: 'answer', title: 'answer'}
                            , {field: 'score', title: 'score'}
                            , {field: 'section', title: 'section'}
                            , {field: 'analysis', title: 'analysis'}
                        ]]
                        , data: data.data[2]
                        , page: false
                        , limit: data.data[2].length
                    });
                    table.render({
                        elem: '#cal'
                        , title: '待 计算题表'
                        , totalRow: true
                        , cols: [[
                            {type: 'checkbox', fixed: 'left'}
                            , {field: 'questionId', title: 'questionId'}
                            , {field: 'subject', title: 'subject'}
                            , {field: 'question', title: 'question'}
                            , {field: 'answer', title: 'answer'}
                            , {field: 'score', title: 'score'}
                            , {field: 'section', title: 'section'}
                            , {field: 'analysis', title: 'analysis'}
                        ]]
                        , data: data.data[3]
                        , page: false
                        , limit: data.data[3].length
                    });
                    table.render({
                        elem: '#pro'
                        , title: '待 证明题表'
                        , totalRow: true
                        , cols: [[
                            {type: 'checkbox', fixed: 'left'}
                            , {field: 'questionId', title: 'questionId'}
                            , {field: 'subject', title: 'subject'}
                            , {field: 'question', title: 'question'}
                            , {field: 'answer', title: 'answer'}
                            , {field: 'score', title: 'score'}
                            , {field: 'section', title: 'section'}
                            , {field: 'analysis', title: 'analysis'}
                        ]]
                        , data: data.data[4]
                        , page: false
                        , limit: data.data[4].length
                    });
                }
            })
            //没有这句话不行，弹出框出不来
            return false;
        });
```

ajax请求到/paper/candidateQuestions，并传data。

```java
@GetMapping(name = "获取待选择题目", value = "/paper/candidateQuestions")
    @ResponseBody
    public ApiResult getCandidateQuestions(@RequestParam("section") String section,
                                           @RequestParam("subject") String subject,
                                           @RequestParam("examCode") int examCode) {
        ExamManage examManage = examManageService.findById(examCode);
        //如果没有paperID 说明没有生成过试卷，直接生成一个最新的给他,之后就直接往这个paperID里面加题目就行了
        if (examManage.getPaperId() == null) {
            int onlyPaperId = paperService.findOnlyPaperId();
            examManage.setPaperId(onlyPaperId + 1);
            //更新到数据库中
            examManageService.update(examManage);
        }
        //通过科目和单元获取全部试题
        List<MultiQuestion> multiQuestions = multiQuestionService.findBySectionAndSubject(section, subject);
        List<FillQuestion> fillQuestions = fillQuestionService.findBySectionAndSubject(section, subject);
        List<CalQuestion> calQuestions = calQuestionService.findBySectionAndSubject(section, subject);
        List<ProveQuestion> proveQuestions = proveQuestionService.findBySectionAndSubject(section, subject);

        Map<Integer, List<?>> map = new HashMap<>(8);
        map.put(1, multiQuestions);
        map.put(2, fillQuestions);
        map.put(3, calQuestions);
        map.put(4, proveQuestions);

        return ApiResultHandler.buildApiResult(0, "请求成功！", map);
    }
```

这部分很简单，如果这场exam还没有试卷，就生成一张。 接下来，通过subject和section将对应的所有的题型全部检索出来，存在map里相应到前端，再通过layui进行渲染显示。


当老师选完题目，按下提交的时候，会执行一段js。

```js
$("#submit2").click(function () {

            questionType = []
            questionId = []

            let checkStatus;
            checkStatus = table.checkStatus('mul')
            let data1 = checkStatus.data;
            checkStatus = table.checkStatus('fill')
            let data2 = checkStatus.data;
            checkStatus = table.checkStatus('cal')
            let data3 = checkStatus.data;
            checkStatus = table.checkStatus('pro')
            let data4 = checkStatus.data;

            let i = 0;
            for (let j = 0; j < data1.length; j++, i++) {
                questionType[i] = '1'
                questionId[i] = data1[j].questionId;
            }
            for (let j = 0; j < data2.length; j++, i++) {
                questionType[i] = '2'
                questionId[i] = data2[j].questionId;
            }
            for (let j = 0; j < data3.length; j++, i++) {
                questionType[i] = '3'
                questionId[i] = data3[j].questionId;
            }
            for (let j = 0; j < data4.length; j++, i++) {
                questionType[i] = '4'
                questionId[i] = data4[j].questionId;
            }
            //layer.alert(JSON.stringify(questionType));
            $.ajax({
                type: 'post',
                url: '/paper/handChoose',
                dataType: "json",
                data: {
                    "questionTypeList": questionType,
                    "questionIdList": questionId,
                    "examCode": $("#examCode").text()
                },
                traditional: true, //这句话是什么意思
                success: function (data) {
                    if (data.code == 200) {
                        layer.confirm('试题添加成功！是继续添加',{
                            btn: ['确认','取消'],
                            btn1: function(){
                                window.location.href = "/toHandChoosePage/"+$("#examCode").text();
                            },
                            btn2: function (){
                                window.location.href ="/toExamPage";
                            }
                        });
                    } else {
                        layer.alert(data.message);
                    }
                }
            })
            return false;
        })
```

先将所有的题目id装入进questionId数组，与之对应的所有的题目类型装入进questionType数组。然后通过ajax将这些data请求到/paper/handChoose的controller里。


```java
 @PostMapping(name = "提交要添加到试卷中的题目", value = "/paper/handChoose")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public ApiResult handChoose(@RequestParam("questionTypeList") List<String> questionType,
                                @RequestParam("questionIdList") List<String> questionId,
                                @RequestParam("examCode") int examCode) {
        //事务锚点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        ExamManage examManage = examManageService.findById(examCode);
        Integer paperId = examManage.getPaperId();
        try {
            //添加试题到paper表中
            for (int i = 0; i < questionId.size(); i++) {
                paperService.add(new PaperManage(paperId, Integer.valueOf(questionType.get(i)), Integer.valueOf(questionId.get(i))));
            }
            return ApiResultHandler.buildApiResult(200, "试题添加成功！", null);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
            return ApiResultHandler.buildApiResult(500, "试卷生成成功失败,服务器出现未知异常！", null);
        }


    }
```

这边也很简单，直接把传过来的这两个数组的数据和paperId组合装入进paperManager数据表就ok了。
