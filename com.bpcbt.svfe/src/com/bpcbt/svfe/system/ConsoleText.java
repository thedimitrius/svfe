package com.bpcbt.svfe.system;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class ConsoleText {
	private	StyledText 		text;
	private boolean			inControlSeq;
	private StringBuilder	controlSeq;
	
	private StringBuilder	screenUpdateBuffer;
	
	private int 			terminalWidth;	/* In characters */
	private int				terminalHeight;	/* In characters */
	
	private int				cursorX;		/* Left-to-right, left position is 0 */
	private int				cursorY;		/* Bottom-to-top, bottom position is 0 */ 
	private int				cursorAbsolute;	/* Relative to beginning of the whole buffer, in symbols - used for text.charAt();	*/
	
	private boolean			cursorVisible;

	private StringBuilder 	emptyLine;
	
	private static final int	buffer_size = 1000;	/* Lines */
	
	
	private static final int	CODE_ESC=27;
	private static final int	CODE_A	=	65;
	private static final int	CODE_Z	=	90;
	private static final int	CODE_a	=	97;
	private static final int	CODE_z	=	122;
	
	private StyleRange 			cursorStyleRange;
	
	Vector<ConsoleResizeListener> resizeListeners;
	
	
	public void initialize(CTabItem item, CTabFolder tabFolder)
	{		
		inControlSeq 	= false;
		controlSeq		= new StringBuilder();
		screenUpdateBuffer = new StringBuilder();
		cursorVisible 	= true;
	
		resizeListeners = new Vector<ConsoleResizeListener>();
		
		text = new StyledText(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI| SWT.WRAP);
		text.setCaret(null); /* Make caret invisible - cursor is controlled by terminal!*/
		item.setControl(text);			
		text.setFont(new Font(Display.getDefault(), "Lucida Console", 8, SWT.NORMAL));
		text.setEditable(false);
		
		tabFolder.setSelection(item);	
				
		/* Initial data population */
		GC gc = new GC(text);
		gc.setFont(text.getFont());
		FontMetrics fm = gc.getFontMetrics();
		
		terminalWidth = (text.getSize().x - text.getVerticalBar().getSize().x) / fm.getAverageCharWidth();
		terminalWidth-=1;
		terminalHeight = text.getSize().y / fm.getHeight();
		emptyLine = new StringBuilder();		
		for (int i = 0; i<terminalWidth; i++)
		{
			emptyLine.append(" ");
		}
		for (int i = 0; i< terminalHeight-1; i++)
		{
			text.append(emptyLine.toString()+"\n");
		}		
		text.append(emptyLine.toString());
		

		cursorX=0;
		cursorY=text.getLineCount()-1;		
		cursorStyleRange = new StyleRange();
		cursorStyleRange.start = 0;
		cursorStyleRange.length = 1;
		cursorStyleRange.fontStyle = SWT.BOLD;
		cursorStyleRange.background = new Color(Display.getDefault(), 127, 255, 127);
		forceCursorUpdate();
		text.setStyleRange(cursorStyleRange);
				
		text.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				GC gc = new GC(text);
				gc.setFont(text.getFont());
				FontMetrics fm = gc.getFontMetrics();
				StringBuilder line = new StringBuilder();
				terminalWidth = (text.getSize().x - text.getVerticalBar().getSize().x) / fm.getAverageCharWidth();
				terminalWidth-=1;
				terminalHeight = text.getSize().y / fm.getHeight();
				
				/* Modifying previous lines */
				for (int i = 0; i<text.getLineCount(); i++)
				{
					int position_start = text.getOffsetAtLine(i);
					line.setLength(0);
					line.append(text.getLine(i));
					int lineLength = line.length();
					if (lineLength<terminalWidth)
					{
						for (int j =  lineLength; j<terminalWidth; j++)
						{
							line.append(" ");
						}						
					}
					else if (line.length()>terminalWidth)
					{
						int position = lineLength - 1;
						while (position>=terminalWidth && line.charAt(position)==' ')
						{
							line.setLength(position);
							position--;
						}
					}
					text.replaceTextRange(position_start, lineLength, line.toString());
				}
				
				emptyLine.setLength(0);
				for (int i = 0; i<terminalWidth; i++)
				{
					emptyLine.append(" ");
				}
				
				ConsoleResizeEvent resizeEvent = new ConsoleResizeEvent();
				resizeEvent.terminalHeight=terminalHeight;
				resizeEvent.terminalWidth=terminalWidth;
				
				for (ConsoleResizeListener listener: resizeListeners)
					listener.handleEvent(resizeEvent);					
			}
		});
	}
	
	public void addResizeConsoleListener (ConsoleResizeListener listener){resizeListeners.add(listener);}
	
	public void removeResizeConsoleListener (ConsoleResizeListener listener){resizeListeners.remove(listener);}
	
	public Point getTerminalSize(){	return new Point(terminalWidth, terminalHeight);}
	
	
	public void parseIncomingText(String consoleText)
	{
		int currentPosition = 0;
		while (currentPosition < consoleText.length())
		{
			if (inControlSeq || consoleText.charAt(currentPosition) == CODE_ESC)
			{
				flushScreenUpdateBuffer();
				currentPosition=parseEscapeSeq(consoleText, currentPosition);
				continue;
			}
			if (processControlCharacter(consoleText.charAt(currentPosition)))
			{
				currentPosition++;
				continue;
			}
			
			/* Did not locate neither control sequence, nor control character. So, just add the char to update buffer */
			screenUpdateBuffer.append(consoleText.charAt(currentPosition));
			currentPosition++;
		}
		flushScreenUpdateBuffer();		
	
	}
	
	public boolean isFocusControl() { return text.isFocusControl();}
	public Widget getWidget() {return text;}
	
	private int parseEscapeSeq(String consoleText, int currentPosition)
	{
		inControlSeq = true;
		/* First, locate the end of escape sequence */
		while (    currentPosition < consoleText.length() 
				&& !(consoleText.charAt(currentPosition) >=CODE_A && consoleText.charAt(currentPosition) <= CODE_Z)
				&& !(consoleText.charAt(currentPosition) >=CODE_a && consoleText.charAt(currentPosition) <= CODE_z)
			   )
		{
			controlSeq.append(consoleText.charAt(currentPosition));
			currentPosition++;
		}
		
		if (currentPosition == consoleText.length())
		{
			/* Reached end of input, but still did not get end of sequence. Exit, and leave inControlSeq flag true */			
		}
		else
		{
			/* We have located end of sequence! Now analyze it! */
			controlSeq.append(consoleText.charAt(currentPosition));
			currentPosition++;
			inControlSeq = false;
			processEscapeSeq();
			controlSeq.setLength(0);
		}
		return currentPosition;
	}
	
	private void processEscapeSeq()
	{
		char seqChar = controlSeq.charAt(controlSeq.length()-1);
		
		/* Working with control sequence arguments */
		int numArguments = 0;
		String[] arguments;
		int[]	 intArgs;
		
		try {
			/* Control correctness */
			controlSeq.delete(0, 2);						/* remove first 2 chars, which are actually always ^] 	*/
			controlSeq.deleteCharAt(controlSeq.length()-1);	/* remove last char - we have already saved it 			*/
		} catch (Exception e){return;}
		
		/* Now, in controlSeq variable we have only the parameters of the sequence (if any). Let's parse them!	*/
		arguments = controlSeq.toString().split(";");
		numArguments = arguments.length;
		intArgs = new int[numArguments];
		for (int i =0; i<numArguments; i++)
		{
			try{
				intArgs[i]=Integer.parseInt(arguments[i]);
			} catch (NumberFormatException e)
			{
				intArgs[i]=0;
			}
		}
		
		switch (seqChar)
		{
		case 'A': /* CUU – Cursor Up                         */
			cursorY+=(intArgs[0]==0?1:intArgs[0]);			
			break;
		case 'B': /* CUD – Cursor Down                       */
			cursorY-=(intArgs[0]==0?1:intArgs[0]);
		    break;		    
		case 'C': /* CUF – Cursor Forward                    */
			cursorX+=(intArgs[0]==0?1:intArgs[0]);
		    break;
		case 'D': /* CUB – Cursor Back                       */
			cursorX-=(intArgs[0]==0?1:intArgs[0]);
		    break;
		case 'E': /* CNL – Cursor Next Line                  */
			cursorX=0;
			cursorY-=(intArgs[0]==0?1:intArgs[0]);
		    break;
		case 'F': /* CPL – Cursor Previous Line              */
			cursorX=0;
			cursorY+=(intArgs[0]==0?1:intArgs[0]);
		    break;
		case 'G': /* CHA – Cursor Horizontal Absolute        */
			cursorX=intArgs[0];
		    break;
		case 'H': /* CUP – Cursor Position                   */
		case 'f': /* HVP – Horizontal and Vertical Position  */
			/* Little comment on how the String.split() function works:
			 * "first;second".split(";") 	= {first}, {second}
			 * ";second".split(";") 		= {}	 , {second}
			 * "first;".split(";") 			= {first}
			 * "first".split(";") 			= {first}
			 * "".slit(";") 				= {}
			 * As you see, arg0 is always present, and it always denotes first argument.
			 * For the second argument - we have to check number of arguments */
			cursorY = terminalHeight - (intArgs[0]==0?1:intArgs[0]);
			cursorX = (numArguments==1 || intArgs[1]==0? 1 : intArgs[1])-1;
		    break;
		case 'J': /* ED –  Erase Display                     */
			switch (intArgs[0])
			{
			case 0:	/* Erase from cursor to end of screen */
				/* TODO */
				break;
			case 1:	/* Erase from cursor to beginning of screen */
				/* TODO */
				break;
			case 2:	/* Erase entire screen and move cursor to top left corner */
				/* FIXME */
				int screen00coords = text.getOffsetAtLine(text.getLineCount()-terminalHeight);
				text.replaceTextRange(screen00coords, text.getCharCount() - screen00coords, "");				
				break;
			default:
				/* Unknown modifier - ignore escape sequence */
				break;
			}
		    break;
		case 'K': /* EL –  Erase in Line                     */
			switch (intArgs[0])
			{
			case 0: /* Erase from cursor to end of line */
				/* FIXME */
				text.replaceTextRange(cursorAbsolute, text.getLine(text.getLineAtOffset(cursorAbsolute)).length() - cursorX, "");
				cursorX--;
				break;
			case 1: /* Erase from cursor to beginning of line */
				StringBuilder str = new StringBuilder();
				int deletedLength = 0;
				int offsetOfLine = text.getOffsetAtLine(text.getLineAtOffset(cursorAbsolute));
				deletedLength = cursorAbsolute - offsetOfLine;
				for (int i=0; i<deletedLength; i++)
				{
					str.append(" ");
				}
				text.replaceTextRange(offsetOfLine, deletedLength, str.toString());
				break;
			case 2: /* Erase whole line */
				/* TODO */
				break;
			default:
				/* Unknown modifier - ignore escape sequence */
				break;
			}
		    break;
		case 'S': /* SU –  Scroll Up                         */
			/* NOT SUPPORTED YET */
		    break;
		case 'T': /* SD –  Scroll Down                       */
			/* NOT SUPPORTED YET */
		    break;		
		case 'm': /* SGR – Select Graphic Rendition          */
			/* NOT SUPPORTED YET */
		    break;
		case 'n': /* DSR – Device Status Report              */
			/* NOT SUPPORTED YET */
		    break;
		case 's': /* SCP – Save Cursor Position              */
			/* NOT SUPPORTED YET */
		    break;
		case 'u': /* RCP – Restore Cursor Position           */
			/* NOT SUPPORTED YET */
		    break;
		case 'l': /* DECTCEM - Hides the cursor              */
			cursorVisible=false;
		    break;
		case 'h': /* DECTCEM - Shows the cursor              */
			cursorVisible=true;
			break;
		default:	/* Unsupported - just ignore the char */
			break;				
		}
		forceCursorUpdate();
	}
	
	private boolean processControlCharacter(char character)
	{
		switch (character)
		{
		case '\b':  /* BACKSPACE, but in reality - CURSOR LEFT */
			flushScreenUpdateBuffer();
			cursorX--;
			forceCursorUpdate();
			break;
		case '\n':	/* NEWLINE */			
			insertNewline();
			break;
		case '\r':	/* CARRIAGE RETURN */
			cursorX=0;
			forceCursorUpdate();
			break;										
		case 7:		/* BELL */
			java.awt.Toolkit.getDefaultToolkit().beep();
			break;
		default:
			return false;
		}		
		return true;		
	}
	
	private void forceCursorUpdate(){
		Display.getDefault().syncExec(new Runnable(){
			public void run(){
				updateCursorAbsolute();
			}
		});
	}
	
	private void updateCursorAbsolute()
	{
		cursorAbsolute = text.getOffsetAtLine(text.getLineCount() - cursorY -1) + cursorX;
		/*text.setStyleRange(null);*/
		if (cursorVisible)
		{
			cursorStyleRange.start = cursorAbsolute;
			//text.setStyleRange(cursorStyleRange);
		}
		text.setTopIndex(text.getLineCount() - 1);
	}
	
	private void flushScreenUpdateBuffer()
	{
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				text.replaceTextRange(cursorAbsolute, screenUpdateBuffer.length(), screenUpdateBuffer.toString());
				cursorX+=screenUpdateBuffer.length();
				screenUpdateBuffer.setLength(0);	
				updateCursorAbsolute();
			}			
		});		
	}
	
	private void insertNewline()
	{
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				int lineNumber;
				int	eolPos;
				flushScreenUpdateBuffer();
				lineNumber = text.getLineAtOffset(cursorAbsolute);
				eolPos = text.getOffsetAtLine(lineNumber) + text.getLine(lineNumber).length();
				if (cursorY==0)
					text.replaceTextRange(eolPos, 0, "\n" + emptyLine);
				else
					cursorY--;
				cursorX=0;
				updateCursorAbsolute();
						
			}
		});
	}
}
