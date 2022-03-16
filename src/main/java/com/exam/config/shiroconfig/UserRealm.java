package com.exam.config.shiroconfig;


import com.exam.entity.Student;
import com.exam.entity.Teacher;
import com.exam.entity.User;
import com.exam.serviceimpl.StudentServiceImpl;
import com.exam.serviceimpl.TeacherServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/12/18
 */
@SuppressWarnings("AlibabaRemoveCommentedCode")
public class UserRealm extends AuthorizingRealm {

    private final String TEACHER = "1";

    private final String STUDENT = "2";

    @Autowired
    private StudentServiceImpl studentService;
    @Autowired
    private TeacherServiceImpl teacherService;

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("授权=>doGetAuthorizationInfo");

        //获取授权信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //拿到当前登录对象
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();

        //添加数据库里的权限
        if(currentUser.getRole()==null) {
            return info;
        } else if(TEACHER.equals(currentUser.getRole())){
            //如果是老师，就赋予老师的角色
            info.addRole("TEACHER");
        }else if(STUDENT.equals(currentUser.getRole())){
            //如果是学生，就赋予学生的角色
            info.addRole("STUDENT");
        }

        /**
         * 添加权限
         * info.addStringPermission(currentUser.getPerms());
         * info.addStringPermission("user:add");
         * 添加角色
         *  List<String> roles= new LinkedList<>();
         *  roles.add("admin");
         *  info.addRoles(roles);
         */
        return info;
    }

    /**
     * 认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("认证=>doGetAuthenticationInfo");

        UsernamePasswordToken userToken = (UsernamePasswordToken) token;


        //从数据库获取这个人
        Student student = studentService.findByUsername(userToken.getUsername());
        Teacher teacher = teacherService.findByUserName(userToken.getUsername());

        //如果不存在这个账号
        if(student==null&&teacher==null) {
            //抛出账号不存在异常
            throw new UnknownAccountException();
        }

        if(student!=null){
            //密码认证自动做,这里要传入对象,这里传入用户对象，可以用
            //Subject subject = SecurityUtils.getSubject();
            //subject.getPrincipal(); 拿到到对象

            return new SimpleAuthenticationInfo(student,student.getPwd(),this.getName());
        }else {
            //密码认证自动做,这里要传入对象
            return new SimpleAuthenticationInfo(teacher,teacher.getPwd(),this.getName());
        }
    }
}
