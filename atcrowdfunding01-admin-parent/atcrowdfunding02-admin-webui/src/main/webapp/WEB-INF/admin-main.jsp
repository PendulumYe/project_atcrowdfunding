<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>

<body>

<%@ include file="/WEB-INF/include-nav.jsp" %>
<div class="container-fluid">
    <div class="row">
        <%@ include file="/WEB-INF/include-sidebar.jsp" %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">控制面板</h1>
            Credentials：<security:authentication property="credentials"/><br/>
            <p>显示出来才发现，principal原来是我们自己封装的SecurityAdmin对象</p>
            <p>SpringSecurity 处理完登录操作之后把登录成功的 User 对象以 principal 属性名存入了 UsernamePasswordAuthenticationToken 对象中去</p>
            Principal（principal 属性的类名）：<security:authentication property="principal.class.name"/><br/>
            Principal中的密码（密码擦除了）：<security:authentication property="principal.password"/><br/>
            访问 SecurityAdmin 对象的属性 loginAcct：<security:authentication property="principal.originalAdmin.loginAcct"/><br/>
            访问 SecurityAdmin 对象的属性 userPswd：<security:authentication property="principal.originalAdmin.userPswd"/><br/>
            访问 SecurityAdmin 对象的属性 userName：<security:authentication property="principal.originalAdmin.userName"/><br/>
            访问 SecurityAdmin 对象的属性 email：<security:authentication property="principal.originalAdmin.email"/><br/>
            访问 SecurityAdmin 对象的属性 createTime：<security:authentication property="principal.originalAdmin.createTime"/><br/>
            <div class="row placeholders">
                <security:authorize access="hasAnyRole('经理','系统最高权限管理员')" >
                <div class="col-xs-6 col-sm-3 placeholder">
                    <img data-src="holder.js/200x200/auto/sky" class="img-responsive" alt="Generic placeholder thumbnail">
                    <h4>Label</h4>
                    <span class="text-muted">只有“经理” / "系统最高权限管理员"才可以看到</span>
                </div>
                </security:authorize>
                <security:authorize access="hasAuthority('role:delete')">
                <div class="col-xs-6 col-sm-3 placeholder">
                    <img data-src="holder.js/200x200/auto/vine" class="img-responsive" alt="Generic placeholder thumbnail">
                    <h4>Label</h4>
                    <span class="text-muted">只有拥有“role:delete”权限的用户才可以看到</span>
                </div>
                </security:authorize>
                <security:authorize access="hasAnyRole('部长','系统最高权限管理员')">
                <div class="col-xs-6 col-sm-3 placeholder">
                    <img data-src="holder.js/200x200/auto/sky" class="img-responsive" alt="Generic placeholder thumbnail">
                    <h4>Label</h4>
                    <span class="text-muted">只有“部长” / "系统最高权限管理员"才可以看到</span>
                </div>
                </security:authorize>
                <security:authorize access="hasAuthority('user:save')">
                <div class="col-xs-6 col-sm-3 placeholder">
                    <img data-src="holder.js/200x200/auto/vine" class="img-responsive" alt="Generic placeholder thumbnail">
                    <h4>Label</h4>
                    <span class="text-muted">只有拥有“user:save”权限的用户才可以看到</span>
                </div>
                </security:authorize>
            </div>

        </div>
    </div>
</div>
</body>
</html>