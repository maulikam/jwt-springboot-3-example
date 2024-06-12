package com.example.oauth.authorization.example.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String requestHeader = request.getHeader("Authorization");
		// Bearer dfgfdgdfgdfgfdg`
		String username = "";
		String token = "";

		log.info("Header {}" + requestHeader);

		if (requestHeader != null && requestHeader.startsWith("Bearer")) {
			token = requestHeader.substring(7);
            username = jwtTokenHelper.getUsernameFromToken(token);
		} else {
			log.info("Invalid header, invalid token");
		}

		if (username != null
					&& SecurityContextHolder.getContext().getAuthentication() ==null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			Boolean validateToken = this.jwtTokenHelper.validateToken(token, userDetails);

			if (validateToken) {
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities());
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(auth);
			}

			filterChain.doFilter(request, response);

		}
	}
}
