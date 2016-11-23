package com.gongchuangsu.helloweb.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 创建过滤器
 * 说明：在任意一个类上添加了一个注解@EnableWebSecurity，就可以创建一个名为 springSecurityFilterChain 的Filter
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	private DataSource dataSource;
	
	protected void configure(HttpSecurity http) throws Exception{
		http
			.authorizeRequests()
				.antMatchers("/tables").hasRole("ADMIN") // 只有拥有ROLE_ADMIN权限的用户才能访问/admin请求
				.antMatchers("/**").authenticated() // 用户只有通过认证才能访问请求
				.and()	
			.formLogin()
				.usernameParameter("username") // 默认为username
				.passwordParameter("password") // 默认为password
				.loginPage("/login")           // 指定登陆界面的位置，否则为默认登录页
				.defaultSuccessUrl("/home")    // 默认为登录界面（/login）
				//.failureUrl("/login.jsp?error=true") // 设置登陆失败界面
				.permitAll() // 允许所有用户都可以访问登陆界面
				.and()
			.logout()
				.logoutUrl("/logout") 
				.logoutSuccessUrl("/login") // 指定退出界面
			.and()
			.csrf().disable(); // 禁用CSRF		
	}
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth			
			.jdbcAuthentication()
				.dataSource(dataSource)
				// 认证查询
				.usersByUsernameQuery(
						"select username, password, enabled " +
						"from user_info where username = ?")
				// 权限查询
				.authoritiesByUsernameQuery(
						"select username, role " + 
						"from user_roles where username = ?");		 
	}
}