package com.anovikov.diplomclient.task_framework;

public interface IProtection {
	
	boolean checkConfig(IConfig config);
	
	IConfig getStandartConfig();
}
