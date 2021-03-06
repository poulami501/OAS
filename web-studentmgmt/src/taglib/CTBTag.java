/*
 * Created on Mar 31, 2004
 *
 */
package taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.tagext.TagSupport;

 
/**
 * @author Tai Truong
 *
 */
 
public class CTBTag extends TagSupport {
	protected static final int NO_ROWSPAN = -1;
	protected static final int NO_COLSPAN = -1;
	protected static final int NO_HEIGHT = -1;
	protected static final int NO_WIDTH = -1;
	
	protected static final int NO_BORDER = 0;
	protected static final int NO_CELLPADDING = 0;
	protected static final int NO_CELLSPACING = 0;
	
	protected static final String ALIGN_RIGHT = "right";
	protected static final String ALIGN_LEFT = "left";
	protected static final String ALIGN_CENTER = "center";
	protected static final String VALIGN_TOP = "top";
	protected static final String VALIGN_MIDDLE = "middle";
	protected static final String VALIGN_BOTTOM = "bottom";
	protected static final String VALIGN_BASELINE = "baseline";
	
	protected static final String NO_CLASS = null;
	protected static final String DEFAULT_WIDTH = "100";
	protected static final String DEFAULT_HEIGHT = "20";
	protected static final String RADIO_SIZE = "5%";
	protected static final int DEFAULT_CELLSPACING = 0;




    // Table Tag - <table>
	protected void displayTableStart() throws IOException {
		displayTableStart("transparent");
	}

	protected void displayColorTableStart(String width_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"");
		if(width_ != null)
			buff.append(" width=\"" + width_ + "\"");
        buff.append(" >");
		writeToPage(buff.toString());
	}

	protected void displayTableStartWithStyle(String class_, String style_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<table ");
		if(class_ != null){
			buff.append(" class=\"" + class_ + "\"");
		}
		if(style_ != null){
			buff.append(" style=\"" + style_ + "\"");
		}
		buff.append(">");
		writeToPage(buff.toString());	
	}
    
	protected void displayTableStart(String class_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<table ");
		if(class_ != null){
			buff.append(" class=\"" + class_ + "\"");
		}
		buff.append(">");
		writeToPage(buff.toString());	
	}
    
	protected void displayTableEnd() throws IOException {
		writeToPage("</table>");
	}


	
    
    // Row Tag - <tr>
	protected void displayRowStart() throws IOException {
        displayRowStart("transparent");
    }
        
	protected void displayRowStart(String class_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<tr");
		if(class_ != null){
			buff.append(" class=\"" + class_ + "\"");
		}
		buff.append(">");
		writeToPage(buff.toString());	
	}
    
	protected void displayRowEnd() throws IOException {
		writeToPage("</tr>");
	}

    
    

    // Column Tag - <td>
	protected void displayCellStart() throws IOException {
	    displayCellStart("transparent");
	}

	protected void displayCellStart(String class_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<td");
		if(class_ != null)
			buff.append(" class=\"" + class_ + "\"");
		buff.append(" >");
		writeToPage(buff.toString());
	}

	protected void displayCellStartColspan(String class_, String width_, String colspan_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<td");
		if(class_ != null)
			buff.append(" class=\"" + class_ + "\"");
		if(width_ != null)
			buff.append(" width=\"" + width_ + "\"");
		if(colspan_ != null)
			buff.append(" colspan=\"" + colspan_ + "\"");
		buff.append(">");
		writeToPage(buff.toString());
	}

	protected void displayCellStart(String class_, String width_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<td");
		if(class_ != null)
			buff.append(" class=\"" + class_ + "\"");
		if(width_ != null)
			buff.append(" width=\"" + width_ + "\""); 
		buff.append(" >");
		writeToPage(buff.toString());
	}

	protected void displayCellStart(String class_, String width_, String align_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<td");
		if(class_ != null)
			buff.append(" class=\"" + class_ + "\"");
		if(width_ != null)
			buff.append(" width=\"" + width_ + "\""); 
		if(align_ != null)
			buff.append(" align=\"" + align_ + "\""); 
		buff.append(" >");
		writeToPage(buff.toString());
	}

	protected void displayCellStartWithStyle(String id_, String class_, String style_) throws IOException {
		StringBuffer buff = new StringBuffer();
		buff.append("<td");
		if(id_ != null)
			buff.append(" id=\"" + id_ + "\"");
		if(class_ != null)
			buff.append(" class=\"" + class_ + "\"");
		if(style_ != null)
			buff.append(" style=\"" + style_ + "\""); 
		buff.append(" >");
		writeToPage(buff.toString());
	}

	protected void displayCellEnd() throws IOException {
		writeToPage("</td>");
	}


	// miscellance
	protected void writeToPage(String value) throws IOException {
		pageContext.getOut().print(value);
	}
    
		
	
}
