package com.bpcbt.svfe.eclipse;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;

import com.bpcbt.svfe.SVFEActivator;
import com.bpcbt.svfe.system.Configuration;
import com.bpcbt.svfe.system.Project;
import com.bpcbt.svfe.system.Target;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;



public class SVFEBuilder extends IncrementalProjectBuilder {
	
	private String 		 failureReason;
			
	public static final String BUILDER_ID = "com.bpcbt.svfe.eclipse.SVFEBuilder";
	public static final String BUILDER_SUCCESS = "SUCCESS.com.bpcbt.svfe.eclipse.SVFEBuilder";
		
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		
		/* SVFE variables */
		Project currentProject = SVFEActivator.getDefault().getProjectsList().projects.get(getProject().getName());
		Configuration currentConfig = currentProject.configurations.get(currentProject.getDefaultConfigurationName());
		
		/* JSCH variables */
		Session 		session			=null;
		ChannelSftp 	sftpChannel		=null;
		ChannelShell 	shellChannel	=null;
		
		InputStream 	shellInputStream	= null;
		OutputStream	shellOutputStream 	= null;
		OutputStream 	consoleStream		= null;
		
		Date buildStartDate = Calendar.getInstance().getTime();

		Exception 	buildException = null;
		String 		buildResult;
				
		int			numTargets=0;
		
		numTargets = currentProject.targets.size();
		
		monitor.beginTask("Building SVFE", numTargets + 1);
		monitor.subTask("Building libs");		
			
		try {
			buildResult = "connecting to server";
			consoleStream = prepareConsole();
		} catch (Exception e) {e.printStackTrace(); throw e;}
		
		/* Now only build errors are going to arise! */
		try{
			showStartBuildMessage(consoleStream);
			
			session = prepareSession(currentProject, currentConfig);
			shellChannel = prepareShellChannel(session);
			sftpChannel = prepareSFTPChannel(session);
			
			shellInputStream = shellChannel.getInputStream();
			shellOutputStream = shellChannel.getOutputStream();
			
			/* Prepare PS */
			String PSCommand = "PS1=\"" + BUILDER_ID + "\"\n";
			shellOutputStream.write(PSCommand.getBytes());
			shellOutputStream.flush();
			
			waitForShellCommandCompletion(shellInputStream, null, monitor);			
						
			buildLibs(currentProject, currentConfig, shellOutputStream, shellInputStream, sftpChannel, consoleStream, monitor);
					
			for (Target currentTarget : currentProject.targets.values())
			{
				if (currentTarget.isChecked())
					buildTarget(currentProject, currentTarget, currentConfig, shellInputStream, shellOutputStream, sftpChannel, consoleStream, monitor);
			}
		} catch (Exception e) {buildException = e;}
			

		if (shellChannel != null) 
		{
			if (shellOutputStream != null)
				try {	shellOutputStream.write(3);	} catch (IOException e) {}
			shellChannel.disconnect();
		}
		if (sftpChannel != null) sftpChannel.disconnect();
		if (session != null) session.disconnect();

		buildResult = "***************************************************************************************\n";
		try {
			consoleStream.write(buildResult.getBytes());
		} catch (IOException e) {}

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");		
		if (buildException==null)
		{
			buildResult = "Build of SVFE has completed successfully at  " + dateFormat.format(Calendar.getInstance().getTime());
		}
		else if (buildException instanceof BuildCancelledException)
		{
			buildResult = "Build of SVFE has been cancelled when " + failureReason;
		}
		else
		{
			buildResult = "Build of SVFE has failed when " + failureReason;
		}

		long startTime = buildStartDate.getTime();
		long endTime   = Calendar.getInstance().getTime().getTime();
		long diff = endTime - startTime;
		long diffMSeconds = diff % 1000;
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;

		buildResult += ", total build time " + String.format("%02d",diffHours) + ":" + String.format("%02d",diffMinutes) + ":" + String.format("%02d",diffSeconds) + "." + String.format("%03d", diffMSeconds)+"\n";
		try {
			consoleStream.write(buildResult.getBytes());
			consoleStream.flush();
		} catch (IOException e) {}
        
/*
		if (buildException!=null)
		{
			if (buildException instanceof CoreException)
				throw (CoreException) buildException;
			else
			{
				buildException.printStackTrace();
				throw new CoreException(null);
			}
		}*/
		return null;	
	}
	
	
	
	private String waitForShellCommandCompletion(InputStream in, OutputStream out, IProgressMonitor monitor) throws Exception
	{
		byte[] inbytes = new byte[32*1024];
		String instr;
		String[] strs;
		String previousRemainder="";
		int numbytes;		
		String result = null;
		
		while (previousRemainder.compareTo(BUILDER_ID)!=0)
		{
			while (in.available()==0)
			{
				if (monitor.isCanceled())
				{
					throw new BuildCancelledException();
				}
				Thread.sleep(100);				
			}
			
			numbytes=in.read(inbytes);				
			instr = new String(inbytes, 0, numbytes);
			strs = instr.split("\n");
			
			strs[0] = previousRemainder+strs[0];
			
			if (out != null)
			{
				for (int i = 0; i<strs.length-2;i++)
				{
					strs[i]+="\n";
					out.write(strs[i].getBytes());
				}
			}
					
			
			if (instr.substring(instr.length() -1).compareTo("\n")!=0)
			{
				if (strs.length >1)				
				{
					result = strs[strs.length-2];
				}
				previousRemainder=strs[strs.length-1];
			}				
			else
			{
				strs[strs.length-1]+="\n";
				out.write(strs[strs.length-1].getBytes());
				previousRemainder = "";
				result = strs[strs.length-1];
			}
		}
		return result;
	}
	
		
	private OutputStream prepareConsole () throws CoreException
	{
		class ConsolePrepareTask implements Runnable{
			public OutputStream consoleStream;
			public void run() {
				try	{
					/* Prepare console for output */
					MessageConsole myConsole;
					String name = "Build console";
					ConsolePlugin plugin = ConsolePlugin.getDefault();
					IConsoleManager conMan = plugin.getConsoleManager();
					IConsole[] existing = conMan.getConsoles();
					for (int i = 0; i < existing.length; i++)
						if (name.equals(existing[i].getName()))
							myConsole=(MessageConsole) existing[i];
					//no console found, so create a new one
					myConsole = new MessageConsole(name, null);
					conMan.addConsoles(new IConsole[]{myConsole});

					IWorkbenchWindow win = PlatformUI.getWorkbench().getWorkbenchWindows()[0];

					IWorkbenchPage page = win.getActivePage();
					String id = IConsoleConstants.ID_CONSOLE_VIEW;
					IConsoleView view;
					view = (IConsoleView) page.showView(id);
					view.display(myConsole);
					consoleStream = myConsole.newMessageStream();					
				} catch (PartInitException e) {	e.printStackTrace();}				
			}
		};
		ConsolePrepareTask consolePrepareTask = new ConsolePrepareTask();
		Display.getDefault().syncExec(consolePrepareTask);
		return consolePrepareTask.consoleStream; 
	}
	
	private void showStartBuildMessage(OutputStream consoleStream) throws Exception
	{
		String currentOutputBuffer;
		Project currentProject = SVFEActivator.getDefault().getProjectsList().projects.get(getProject().getName());
		Configuration currentConfig = currentProject.configurations.get(currentProject.getDefaultConfigurationName());

		currentOutputBuffer = "***************************************************************************************\n";
		consoleStream.write(currentOutputBuffer.getBytes());	

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();			
		currentOutputBuffer = "Started SVFE build at " + dateFormat.format(cal.getTime()) +" with following settings:\n";
		consoleStream.write(currentOutputBuffer.getBytes());

		currentOutputBuffer = "Make clean libs:     "+(currentProject.isCleanLibs()? "YES" : "NO") + "\n";
		currentOutputBuffer+= "Make clean modules:  "+(currentProject.isCleanModule()? "YES" : "NO") + "\n";
		currentOutputBuffer+= "Restart after build: "+(currentProject.isRestart()? "YES" : "NO") + "\n";
		currentOutputBuffer+= "Gzip and copy files: "+(currentProject.isGzip()? "YES" : "NO") + "\n";		
		consoleStream.write(currentOutputBuffer.getBytes());			

		currentOutputBuffer = "Configuration "+ currentConfig.getConfigurationName() + "\n";
		consoleStream.write(currentOutputBuffer.getBytes());

		currentOutputBuffer = "\tbuilding on  [" + currentConfig.getServerUsername() + "@" + currentConfig.getServerName() +"], auth by " + (currentConfig.isAuthByKey()? "public key" : "password") + "\n";
		consoleStream.write(currentOutputBuffer.getBytes());

		currentOutputBuffer = "\tsources at [" + currentConfig.getServerSrcLocation() + "]";
		if (currentProject.isGzip())
			currentOutputBuffer += ", gzipped binaries are copied to [" + currentConfig.getGzipFilesLocation() + "]";
		currentOutputBuffer+= "\n";
		consoleStream.write(currentOutputBuffer.getBytes());

		currentOutputBuffer = "Building targets at:                ";

		currentOutputBuffer+= "Target name:    ";

		if (currentProject.isRestart())
			currentOutputBuffer += "Restart    ";

		if (currentProject.isGzip())
			currentOutputBuffer += "Gzipping";

		currentOutputBuffer += "\n";
		consoleStream.write(currentOutputBuffer.getBytes());
		consoleStream.flush();

		for (Target currentTarget : currentProject.targets.values())
		{
			if (currentTarget.isChecked())
			{
				currentOutputBuffer = String.format("%-35.35s ", currentTarget.getTargetLocation());
				currentOutputBuffer+= String.format("%-15.15s ", currentTarget.getTargetName());

				if (currentProject.isRestart())
					currentOutputBuffer += String.format("%-10.10s ", currentTarget.getTargetRestartAliases());

				if (currentProject.isGzip())
					currentOutputBuffer += currentTarget.getTargetGzipFiles();

				currentOutputBuffer += "\n";
				consoleStream.write(currentOutputBuffer.getBytes());
				consoleStream.flush();
			}
			
		}

		currentOutputBuffer = "***************************************************************************************\n";
		consoleStream.write(currentOutputBuffer.getBytes());
		consoleStream.flush();
	}
	
	private Session prepareSession (Project currentProject, Configuration currentConfig) throws Exception
	{
		failureReason = "creating remote session";
		
		Session session;		
		JSch jsch = new JSch();
		
		String host = currentConfig.getServerName();
		String user = currentConfig.getServerUsername();		
		if (currentConfig.isAuthByKey())
		{
			jsch.addIdentity(currentConfig.getKeyLocation());
		}
		session = jsch.getSession(user, host);
		
		if (!currentConfig.isAuthByKey())
		{
			session.setPassword(currentConfig.getServerPassword());
		}
		
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		
		session.setConfig(config);			
		session.connect();
		return session;
	}
	
	private ChannelShell prepareShellChannel (Session session) throws Exception
	{
		Channel channel = session.openChannel("shell");
		channel.connect();
		return (ChannelShell) channel;
	}
	
	private ChannelSftp prepareSFTPChannel (Session session) throws Exception
	{
		Channel channel = session.openChannel("sftp");
		channel.connect();
		return (ChannelSftp) channel;
	}	
	
	private void syncDir (File localDir, String remotePath, String fileDirPrefix, ChannelSftp sftpChannel, OutputStream consoleStream, IProgressMonitor monitor) throws Exception
	{	
		String 	currentOutputBuffer;
		int		remoteFileMTime;
		int		localFileMTime;
		File[] files = localDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return ! (file.isDirectory() && file.getName().startsWith("."));				
			}
		});
		
		@SuppressWarnings("rawtypes")
		java.util.Vector remoteFiles = sftpChannel.ls(remotePath);		
		
		for (File currentFile : files)
		{
			localFileMTime = (int) (currentFile.lastModified() / 1000);
			if (monitor.isCanceled())
				throw new BuildCancelledException();
			if (currentFile.isDirectory())
			{
				syncDir(currentFile, remotePath+"/"+currentFile.getName(), fileDirPrefix+"/"+currentFile.getName(), sftpChannel, consoleStream, monitor);
			}
			else
			{
				LsEntry targetFile = null;
				for (Object obj : remoteFiles)
				{
					if (obj instanceof LsEntry)
					{
						LsEntry entry = (LsEntry) obj;
						if (entry.getFilename().compareTo(currentFile.getName())==0)
						{
							targetFile = entry;
							break;
						}
					}
				}
				if (targetFile != null)
				{
					remoteFileMTime = targetFile.getAttrs().getMTime();
					if ( Math.abs(remoteFileMTime - localFileMTime)<=2*60 )	/* Time difference less than 2 minutes */
						continue; /* file is in sync */					
				}
				
				currentOutputBuffer = "Writing file " +  fileDirPrefix + "/" + currentFile.getName() + "...    ";
				consoleStream.write(currentOutputBuffer.getBytes());
				consoleStream.flush();
				
				sftpChannel.put(currentFile.getAbsolutePath(), remotePath);
				sftpChannel.setMtime(remotePath + "/" + currentFile.getName(), localFileMTime);

				currentOutputBuffer = "done\n";
				consoleStream.write(currentOutputBuffer.getBytes());
				consoleStream.flush();				
			}
		}
	}
	
	private void buildLibs(Project currentProject, Configuration currentConfiguration, OutputStream shellOut, InputStream shellIn, ChannelSftp sftp, OutputStream consoleStream, IProgressMonitor monitor) throws Exception
	{
		String currentOutputBuffer;
		String projectPath;
		String remotePath;
			
		
		failureReason = "syncing includes";				
		currentOutputBuffer = "***********************************Syncing includes************************************\n";
		consoleStream.write(currentOutputBuffer.getBytes());
		consoleStream.flush();
		
		projectPath = getProject().getFolder("include").getRawLocation().toOSString();
		/*!TODO REMOVE ME */
		projectPath = "D:\\work\\workspace\\Mauritius SBM (svfe_sbm)\\include";
		remotePath = currentConfiguration.getServerSrcLocation() + "/include";
		syncDir(new File(projectPath), remotePath, "/include", sftp, consoleStream, monitor);
		
		failureReason = "syncing libs";
		currentOutputBuffer = "*************************************Syncing libs**************************************\n";
		consoleStream.write(currentOutputBuffer.getBytes());
		consoleStream.flush();
		
		projectPath = getProject().getFolder("lib").getRawLocation().toOSString();
		/*!TODO REMOVE ME */
		projectPath = "D:\\work\\workspace\\Mauritius SBM (svfe_sbm)\\lib";
		
		remotePath = currentConfiguration.getServerSrcLocation() + "/lib";			
		syncDir(new File(projectPath), remotePath, "/lib", sftp, consoleStream, monitor);
		
		failureReason = "making libs";
		currentOutputBuffer = "*************************************Making libs***************************************\n";
		consoleStream.write(currentOutputBuffer.getBytes());
		consoleStream.flush();
		
		currentOutputBuffer = "cd " + currentConfiguration.getServerSrcLocation() + "/lib && make ";
		if (currentProject.isCleanLibs())
			currentOutputBuffer += "clean ";
			
		currentOutputBuffer += "all && echo \"" + BUILDER_SUCCESS + "\"\n";
		shellOut.write(currentOutputBuffer.getBytes());
		shellOut.flush();
		
		currentOutputBuffer = waitForShellCommandCompletion(shellIn, consoleStream, monitor);
		
		if (currentOutputBuffer.compareTo(BUILDER_SUCCESS)==0)
			throw new CoreException(null);	
	}
	
	private void buildTarget(Project currentProject, Target currentTarget, Configuration currentConfiguration, InputStream shellInputStream, OutputStream shellOutputStream, ChannelSftp sftpChannel, OutputStream consoleStream, IProgressMonitor monitor ) throws Exception
	{
		String currentOutputBuffer = "";
		String projectPath;
		String remotePath;
		
		failureReason = "syncing target "+ currentTarget.getTargetName();
		
		for (int i = 0; i< (87 - 8 - currentTarget.getTargetName().length())/2; i++)
			currentOutputBuffer += "*";		
		currentOutputBuffer += "Syncing ";
		currentOutputBuffer += currentTarget.getTargetName();
		for (int i = 0; i < (87 - currentOutputBuffer.length()); i++)
			currentOutputBuffer += "*";
		
		consoleStream.write(currentOutputBuffer.getBytes());
		consoleStream.flush();
		
		projectPath = getProject().getFolder(currentTarget.getTargetLocation()).getRawLocation().toOSString();
		/*!TODO REMOVE ME */
		projectPath = "D:\\work\\workspace\\Mauritius SBM (svfe_sbm)\\lib";
		
		remotePath = currentConfiguration.getServerSrcLocation() + currentTarget.getTargetLocation();			
		syncDir(new File(projectPath), remotePath, "/" + currentTarget.getTargetLocation(), sftpChannel, consoleStream, monitor);
		
		failureReason = "making target" + currentTarget.getTargetName();
		for (int i = 0; i< (87 - 6 - currentTarget.getTargetName().length())/2; i++)
			currentOutputBuffer += "*";		
		currentOutputBuffer += "Making ";
		currentOutputBuffer += currentTarget.getTargetName();
		for (int i = 0; i < (87 - currentOutputBuffer.length()); i++)
			currentOutputBuffer += "*";
		
		currentOutputBuffer = "cd " + currentConfiguration.getServerSrcLocation() + "/"+ currentTarget.getTargetLocation() + " && make ";
		if (currentProject.isCleanModule())
			currentOutputBuffer += "clean ";
			
		currentOutputBuffer += "all install ";
		if (currentProject.isRestart())
			currentOutputBuffer += "shutpro " + currentTarget.getTargetRestartAliases() + "&& sleep 3 && startpro_forced " + currentTarget.getTargetRestartAliases();
		
		if (currentProject.isGzip())
			currentOutputBuffer += "&& gzip -9 " + currentTarget.getTargetGzipFiles();
			
		currentOutputBuffer += "&& echo \"" + BUILDER_SUCCESS + "\"\n";
		shellOutputStream.write(currentOutputBuffer.getBytes());
		shellOutputStream.flush();
		
		currentOutputBuffer = waitForShellCommandCompletion(shellInputStream, consoleStream, monitor);
		
		if (currentOutputBuffer.compareTo(BUILDER_SUCCESS)==0)
		{
			throw new CoreException(null);
		}
		else
		{
			String[] gzippedFiles = currentTarget.getTargetGzipFiles().split(" ");
			for (String currentFileName : gzippedFiles)
			{
				currentFileName += ".gz";
				sftpChannel.get(remotePath + "/" + currentFileName, currentConfiguration.getGzipFilesLocation());
			}
		}
	}
	
	public SVFEBuilder() {}	
}


class BuildCancelledException extends Exception
{private static final long serialVersionUID = -6963378292358305020L;}


