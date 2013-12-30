package com.bpcbt.svfe.system;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class Target {
	private 	Preferences prefs;
	private 	String 		targetLocation;
	private 	String 		targetRestartAliases;
	private 	String 		targetGzipFiles;	
	private 	boolean		isChecked;
	private 	String		targetName;	
	private		String		targetID;
	Target () {	}
	
	public boolean initFromPrefs (Preferences prefs)
	{
		this.prefs = prefs;
		targetLocation = prefs.get("targetLocation", "/");
		targetRestartAliases = prefs.get("targetRestartAliases", "");
		targetGzipFiles = prefs.get("targetGzipFiles", "");
		isChecked = prefs.getBoolean("isChecked", false);
		targetName = prefs.get("targetName", "");
		targetID = prefs.get("targetID", "-1");
		return true;
	}
	
	public void initialize(Preferences prefs, String targetLocation, String targetRestartAliases, String targetGzipFiles, boolean isChecked, String targetName, String targetID)
	{
		this.prefs = prefs;
		this.targetLocation = targetLocation;
		this.targetRestartAliases = targetRestartAliases;
		this.targetGzipFiles = targetGzipFiles;
		this.isChecked = isChecked;
		this.targetName = targetName;
		this.targetID = targetID;
		flushPrefs();
	}
	
	
	public String getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(String targetLocation) {
		this.targetLocation = targetLocation;
		flushPrefs();
	}

	public String getTargetRestartAliases() {
		return targetRestartAliases;
	}

	public void setTargetRestartAliases(String targetRestartAliases) {
		this.targetRestartAliases = targetRestartAliases;
		flushPrefs();
	}

	public String getTargetGzipFiles() {
		return targetGzipFiles;
	}

	public void setTargetGzipFiles(String targetGzipFiles) {
		this.targetGzipFiles = targetGzipFiles;
		flushPrefs();
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		flushPrefs();
	}
	
	public String getTargetName() {
		return targetName;
	}
	
	public void setTargetName(String targetName) {
		this.targetName=targetName;
		flushPrefs();
	}
	
	public String getTargetID() {
		return targetID;
	}
	
	
	private void flushPrefs ()
	{
		prefs.put("targetName", targetName);
		prefs.put("targetLocation", targetLocation);
		prefs.put("targetRestartAliases", targetRestartAliases);
		prefs.put("targetGzipFiles", targetGzipFiles);
		prefs.putBoolean("isChecked", isChecked);
		prefs.put("targetID", targetID);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	public void removePrefs()
	{
		try {
			prefs.removeNode();
		} catch (BackingStoreException e) {	e.printStackTrace();}
	}
	
	
}
