package com.mopl.mopl_batch.batch.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
	basePackages = {"com.mopl.mopl_batch.batch.Repository"},
	entityManagerFactoryRef = "businessEntityManager",
	transactionManagerRef = "businessTransactionManager"
)
@EnableJpaAuditing
public class JpaConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource businessDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean businessEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setDataSource(businessDataSource());
		em.setPackagesToScan("com.mopl.mopl_batch.batch.entity");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "validate");
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.physical_naming_strategy",
			"org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

		em.setJpaPropertyMap(properties);
		return em;
	}

	@Bean
	public PlatformTransactionManager businessTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(businessEntityManager().getObject());
		return transactionManager;
	}
}
