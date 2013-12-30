package com.bpcbt.svfe.system;

public class UpdateUIEvent {
	public int eventType;
	public String projectName;
		
	public static final int ProjectOpened = 1;
	public static final int ProjectClosed = 2;
	
	public static final int ConfigurationsModified = 3;
	
	public static final int TargetAdded = 4;
	public static final int TargetModified = 5;
	public static final int TargetRemoved = 6;
	
	public Object object;
}
