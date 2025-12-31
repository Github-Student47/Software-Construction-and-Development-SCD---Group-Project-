package com.scd.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;

/**
 * Spring MVC Configuration Class
 * 
 * This class replaces the traditional XML-based Spring configuration
 * using Java-based configuration with annotations.
 * 
 * Configuration includes:
 * - Component scanning for Spring beans
 * - View resolver configuration
 * - Data source configuration for MySQL
 * - JdbcTemplate configuration for database operations
 * - Static resource handling
 * 
 * Demonstrates:
 * - Spring Java-based configuration
 * - Dependency injection
 * - Bean definition and lifecycle
 * 
 * Team Members:
 * - Muhammad Kashan Tariq (212145)
 * - Adeel Hussain (221829)
 * - Syed Abdain (221855)
 * - Muhammad Tauseef (221789)
 */
@Configuration
@EnableWebMvc  // Enable Spring MVC for web requests
@ComponentScan(basePackages = "com.scd.project")  // Scan for @Component, @Controller, @Repository, @Service
public class MvcConfig implements WebMvcConfigurer {
    
    /**
     * Configure view resolver for JSP files
     * 
     * This bean defines how view names are resolved to actual JSP files.
     * Prefix: /WEB-INF/views/ (files are not directly accessible)
     * Suffix: .jsp (all views are JSP files)
     */
    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(1);
        return resolver;
    }
    
    /**
     * Configure data source for MySQL database connection
     * 
     * This bean defines the database connection parameters.
     * Update the password with your MySQL password.
     */
    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        // MySQL JDBC Driver class name
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Database connection URL
        // Format: jdbc:mysql://host:port/database_name
        // Using localhost, default port 3306, database name: studentsmanagementsystem
        dataSource.setUrl("jdbc:mysql://localhost:3306/studentsmanagementsystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        
        // Database username (change if different)
        dataSource.setUsername("root");
        
        // Database password (UPDATE THIS WITH YOUR MYSQL PASSWORD)
        // If your MySQL has no password, set to empty string: ""
        dataSource.setPassword("");  // <-- CHANGE THIS TO YOUR MYSQL PASSWORD
        
        return dataSource;
    }
    
    /**
     * Configure JdbcTemplate for database operations
     * 
     * JdbcTemplate simplifies JDBC operations and handles exceptions.
     */
    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }
    
    /**
     * Configure static resource handling (CSS, JavaScript, Images)
     * 
     * This allows serving static resources from /resources/ directory.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
}
