package com.bpcbt.svfe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


import com.bpcbt.svfe.system.Configuration;
import com.bpcbt.svfe.system.ConsoleResizeEvent;
import com.bpcbt.svfe.system.ConsoleResizeListener;
import com.bpcbt.svfe.system.ConsoleText;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class ConsoleView extends ViewPart {
	public static final String contextId = "com.bpcbt.svfe.context";
	
	private	CTabFolder 	tabFolder;
	private	CTabItem	plusTabItem;
	
	private Vector<SVFEServerConsole> consoles;
			
	private class SVFEServerConsole
	{
		private ConsoleText		text;
		private	CTabItem		item;
		
		private ChannelShell 	shell;
		private	Session 		session;
		private InputStream 	serverInputStream;
		private OutputStream 	serverOutputStream;
		
		private ReaderThread thread;
				
		class ReaderThread extends Thread {
			boolean needsStop = false;
			public void run() {
				
				int numBytes = 0;
				byte[] bytes = new byte[32*1024];
				String consoleText;
				
				try{
					while (!needsStop)
					{
						if (serverInputStream.available()>0)
						{
							numBytes = serverInputStream.read(bytes);
							consoleText = new String(bytes, 0, numBytes);
							text.parseIncomingText(consoleText);							
						}
						else
						{
							Thread.sleep(100);
						}
					}
				} catch (IOException | InterruptedException e) {e.printStackTrace();}
			}
			public void requestStop() {needsStop = true;}			
		}
		
		public void initialize(Configuration conf) throws Exception
		{			
			/* Initializing item itself */
			item = new CTabItem(tabFolder, SWT.CLOSE, tabFolder.getItemCount()-1);
			text = new ConsoleText();
			text.initialize(item, tabFolder);
			
			/* Set up resize manager to understand terminal size */
			text.addResizeConsoleListener(new ConsoleResizeListener() {
				public void handleEvent(ConsoleResizeEvent e) {
					try{
						shell.setPtySize(e.terminalWidth, e.terminalHeight, -1, -1);					
					} catch (Exception ex) {ex.printStackTrace();}
				}				
			});			
			
			/* Set up keypress intercepter to send it to remote server */
			Display.getDefault().addFilter(SWT.KeyDown, new Listener(){
				public void handleEvent(Event event) {
					if (event.widget == text.getWidget()){	
						try{
							if (event.character != 0){								
								event.doit=false;
								if (event.character == 127)	/*delete character */
								{
									serverOutputStream.write(new byte[]{27, '[', '3', '~'});
									serverOutputStream.flush();
								}
								else
								{
									serverOutputStream.write(event.character);
									serverOutputStream.flush();
								}

							}else if (event.keyCode >= SWT.ARROW_UP && event.keyCode <= SWT.ARROW_RIGHT){
								event.doit=false;
								byte chr = 'A';
								chr+=(event.keyCode - SWT.ARROW_UP);
								/*Dang! right and left are swapped */
								if (chr =='C'){ chr = 'D';}else if (chr=='D'){chr = 'C';}								
								serverOutputStream.write(new byte[]{27, '[', chr});
								serverOutputStream.flush();
							}
						} 
						catch (Exception error) {
							System.out.println ("Failed writing! Or else!");
							error.printStackTrace();
						}
					}					
				}
			});
			
			
			
			item.setText(conf.getServerUsername() + "@" + conf.getServerName());
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			text.parseIncomingText("Started connection attempt to " + conf.getServerUsername() + "@" + conf.getServerName() + " at " + dateFormat.format(cal.getTime()) +"\n ");
			
			/* Creating server connection */
			/* Prepare and connect SSH channel */
			JSch jsch = new JSch();			
			String host = conf.getServerName();
			String user = conf.getServerUsername();		
			if (conf.isAuthByKey())
				jsch.addIdentity(conf.getKeyLocation());
			session = jsch.getSession(user, host);			
			if (!conf.isAuthByKey())
				session.setPassword(conf.getServerPassword());			
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");			
			session.setConfig(config);			
			session.connect();
			shell = (ChannelShell) session.openChannel("shell");
			shell.setPtySize(text.getTerminalSize().x, text.getTerminalSize().y, -1, -1);
			shell.connect();
			serverInputStream = shell.getInputStream();
			serverOutputStream = shell.getOutputStream();		
			

			/* Prepare output */
			thread = new ReaderThread();
			thread.start();
				
			/* Add closing listener - close connection to server on exit*/
			item.addDisposeListener(new DisposeListener(){
				public void widgetDisposed(DisposeEvent e) {
					thread.requestStop();
					shell.disconnect();
					session.disconnect();
					tabFolder.setSelection(-1);
					consoles.removeElement(SVFEServerConsole.this);
				}				
			});					
		}		
		
		public boolean isActive() { return text.isFocusControl();}
		public void sendChar(char character) {
			try{
				serverOutputStream.write(character);
				serverOutputStream.flush();
			}catch(Exception e){}
		}
	}
	
	
	public void createPartControl(Composite parent) {
			
		tabFolder = new CTabFolder(parent, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		plusTabItem = new CTabItem(tabFolder, SWT.NONE);
		plusTabItem.setText("+");		
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		plusTabItem.setControl(composite);
		
		Button btnCreateNewConnection = new Button(composite, SWT.NONE);
		btnCreateNewConnection.setImage(ResourceManager.getPluginImage("org.eclipse.team.cvs.ui", "/icons/full/eview16/console_view.gif"));
		btnCreateNewConnection.setBounds(10, 10, 183, 25);
		btnCreateNewConnection.setText("Create new connection");
				
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected (SelectionEvent e)
			{				
				if (e.item instanceof CTabItem)
				{
					CTabItem currentItem = (CTabItem) e.item;
					if (currentItem.getText().compareTo("+") == 0) {
						createNewItem();
						e.doit = false;
					}					
				}
			}
		});
		
		btnCreateNewConnection.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e)
			{
				createNewItem();
			}
		});
		
		tabFolder.setSelection(plusTabItem);
		
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(contextId);
		SVFEActivator.getDefault().setActiveConsoleView(this);
	}
	
	
	public void dispose()
	{
		super.dispose();
		SVFEActivator.getDefault().setActiveConsoleView(null);
	}	
	
	private void createNewItem(){
		
		/* Prepare Tab Item */
		try {
			Configuration selectedConfig = SVFEActivator.getDefault().openServersList(getSite().getWorkbenchWindow().getShell());
			if (selectedConfig !=null)
			{
				SVFEServerConsole console = new SVFEServerConsole();
				console.initialize(selectedConfig);
				consoles.add(console);
			}			
		} catch (Exception e) {e.printStackTrace();}
	}	
	
	public void sendCharToActiveConsole(char character)
	{
		for (SVFEServerConsole console : consoles){
			if (console.isActive())	{
				console.sendChar(character);
				return;
			}
		}
	}
	
	public ConsoleView() { consoles = new Vector<SVFEServerConsole>();}
	public void setFocus() {}	
}

