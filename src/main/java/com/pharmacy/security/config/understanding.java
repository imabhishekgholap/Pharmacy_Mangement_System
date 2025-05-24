/*
@Component
//public class JWTAuthenticationFilter extends OncePerRequestFilter
//{
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); //	HttpServletRequest request
//    	What it is:
        //
//        	An object representing the incoming HTTP request from the client.
//        	Provided by the servlet container (like Tomcat) automatically when a request arrives.
        //
//        	What it contains:
//        	Headers (e.g., Authorization: Bearer xxx)
//        	HTTP method (GET/POST/etc.)
//        	URL path (/api/resource)
//        	Query parameters (?id=123)
//        	Body data (for POST/PUT)
//        	Client IP, cookies, etc.
//        	Where it comes from:
        //
//        	Created by the servlet container (e.g., Tomcat) for every incoming request.
//        	Passed through the filter chain by Spring.
        //
//        	Key uses in your code:
//        	final String authHeader = request.getHeader("Authorization"); // Get JWT from headers
//        	new WebAuthenticationDetailsSource().buildDetails(request); // Get client IP/session info
        	
        	
        	
//        								HttpServletResponse response
//        								What it is:
//        							
//        								An object representing the HTTP response that will be sent back to the client.
//        							
//        								Provided by the servlet container.
//        							
//        								What you can do with it:
//        							
//        								Set status codes (response.setStatus(401))
//        							
//        								Add headers (response.setHeader("Location", "/login"))
        	
//        	Though not directly modified in your filter, it's passed to filterChain.doFilter() to allow subsequent filters or controllers to write the response.
        	
        	
//        	 FilterChain filterChain
//        	 What it is:
        //
//        	 Represents the chain of filters that need to be executed for the request.
        //
//        	 Spring Security installs multiple filters (JWT validation, CSRF, etc.) in this chain.
        //
//        	 Key methods:
        //
//        	 filterChain.doFilter(request, response):
//        	 Passes control to the next filter in the chain (or to the controller if no filters remain).
        	
        	
//        	throws ServletException, IOException
//        	ServletException: Thrown if the filter encounters a servlet-specific error.
        //
//        	IOException: Thrown for input/output errors (e.g., reading request body fails).
        	
//        	If no JWT is found:

        if(authHeader==null || !authHeader.startsWith("Bearer"))
        {
//filterChain.doFilter(request, response); // Skip authentication
            filterChain.doFilter(request, response);//        
            return;
        }
//   If JWT exists:
//Validate token → set up SecurityContext.
        final String jwtToken = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwtToken);

//        When the user sends a request with a valid JWT, your filter (JwtAuthenticationFilter) checks it.
//        It sets the user details into SecurityContextHolder like this:
//
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        Later, if you want to check who is logged in, you use:
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        This gives you the logged-in user's identity.    
//        You won’t know who the current authenticated user is.
//
//        You can't perform role checks, username checks, etc., manually in your code.
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName(); // gives logged-in usernam
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(username!=null && authentication==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwtToken, userDetails)){
                // Create authentication object and set it in the SecurityContext
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());

              //This line is typically used in custom authentication filters to enhance the authentication process with additional request details
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//              buildDetails(request)://This method takes the HttpServletRequest object as input and generates a WebAuthenticationDetails object.
                
//              It stores the authenticated user inside the current thread’s SecurityContext.
//              This means: ✔️ From now on, your app knows who the user is for the rest of this request.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        }
        // Continue with the filter chain
        filterChain.doFilter(request,response);
    }
}




WEBSEC-CONFIG
The WebSecurityConfig in your code snippet is a class, not an interface. It is typically a custom configuration class in Spring Security, 
where you define security-related configurations such as authentication, authorization, and HTTP security rules.
package com.pharmacy.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pharmacy.security.jwtValidation.JWTAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig
{
    private final UserDetailsService userDetailsService;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{


        return  httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(request->request
                                                    .requestMatchers("register","login").permitAll()
                                                    .anyRequest().authenticated())
                    .userDetailsService(userDetailsService)
                    When you use .httpBasic(Customizer.withDefaults()) in Spring Security, it enables HTTP Basic Authentication with default settings. Here's what happens in the filter chain:

//Adds BasicAuthenticationFilter:
//This filter is added to the Spring Security filter chain.
//It intercepts HTTP requests and checks for the Authorization header containing Basic Authentication credentials (username and password encoded in Base64).
                    .httpBasic(Customizer.withDefaults())
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

@Bean Annotation:

The bCryptPasswordEncoder() method is annotated with @Bean, making the BCryptPasswordEncoder available as a Spring-managed bean.
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder(14);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception{
        return configuration.getAuthenticationManager();
    }
}

*/