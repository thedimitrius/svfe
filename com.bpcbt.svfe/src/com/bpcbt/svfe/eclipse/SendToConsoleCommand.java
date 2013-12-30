package com.bpcbt.svfe.eclipse;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.widgets.Event;

import com.bpcbt.svfe.SVFEActivator;

public class SendToConsoleCommand implements IHandler {

	public void addHandlerListener(IHandlerListener handlerListener) {}
	public void dispose() {}
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object obj = event.getTrigger();
		if (obj instanceof Event)
		{
			Event evt = (Event) obj;
			SVFEActivator.getDefault().getActiveConsoleView().sendCharToActiveConsole(evt.character);
		}		
		return null;
	}
	public boolean isEnabled() {return true;}
	public boolean isHandled() {return true;}
	public void removeHandlerListener(IHandlerListener handlerListener) { System.out.println("Removed!");}

}
