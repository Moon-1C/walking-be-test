package com.walking.api.data;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = DataConfig.BASE_PACKAGE)
public class DataConfig {
	public static final String BASE_PACKAGE = "com.walking.data";
	public static final String SERVICE_NAME = "walking";
	public static final String MODULE_NAME = "data";
	public static final String BEAN_NAME_PREFIX = "data";
	public static final String PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME;
}
