package sungaron.foodiespot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//Auditing을 활용할 수 있도록
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
