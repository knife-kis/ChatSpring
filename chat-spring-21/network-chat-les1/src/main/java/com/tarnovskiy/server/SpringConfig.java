package com.tarnovskiy.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class SpringConfig {

    @Bean
    public ServerMain serverMain(DataSource dataSource) throws SQLException {
        return new ServerMain(dataSource);
    }


    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUsername("root");
        ds.setPassword("123123");
        ds.setUrl("jdbc:sqlite:C:\\Users\\Maks-Oks\\Desktop\\geekbrains\\SPRING\\hw01\\chat-spring-21\\network-chat-les1\\mydb.db");
        return ds;
    }

}
