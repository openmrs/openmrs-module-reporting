package org.openmrs.module.reporting.report.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmrs.util.OpenmrsUtil;

/**
 * Helper utility that you wrap around a POI Workbook to help manage cell styles
 * 
 * This class was adapted from org.pih.StyleHelper in PIH-EMR.
 */
@SuppressWarnings("unchecked")
public class ExcelStyleHelper {

    Workbook wb;
    Map fonts = new HashMap();
    Map styles = new HashMap();
    Collection fontAttributeNames = new HashSet();
    Collection fontAttributeStarting = new ArrayList();
    short dateFormat;

    public ExcelStyleHelper(Workbook wb) {
        this.wb = wb;
        fontAttributeNames.add("bold");
        fontAttributeNames.add("italic");
        fontAttributeNames.add("underline");
        fontAttributeStarting.add("size=");
        DataFormat df = wb.createDataFormat();
        dateFormat = df.getFormat("d-mmm-yy");
    }

    public Font getFont(String s) {
        SortedSet att = new TreeSet();
        for (StringTokenizer st = new StringTokenizer(s, ","); st.hasMoreTokens();) {
            String str = st.nextToken().trim().toLowerCase();
            if (str.equals("")) {
                continue;
            }
            att.add(str);
        }
        String descriptor = OpenmrsUtil.join(att, ",");
        if (styles.containsKey(descriptor)) {
            return (Font) fonts.get(descriptor);
        }
		else {
            Font font = wb.createFont();
            for (Iterator i = att.iterator(); i.hasNext();) {
                String str = (String) i.next();
                if (str.equals("bold")) {
                    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                }
				else if (str.equals("italic")) {
                    font.setItalic(true);
                }
				else if (str.equals("underline")) {
                    font.setUnderline(Font.U_SINGLE);
                }
				else if (str.startsWith("size=")) {
                    str = str.substring(5);
                    font.setFontHeightInPoints(Short.parseShort(str));
                }
            }
            fonts.put(descriptor, font);
            return font;
        }
    }
    
    /**
     * You should pass in a comma-separated string containing attributes:
     *    bold
     *    italic
     *    underline
     *    size=##
     *    wraptext
     *    border=all | bottom | top | left | right
     *    align=center | left | right | fill
     *    date
     * 
     * @param s
     * @return
     */
    public CellStyle getStyle(String s) {
        SortedSet att = new TreeSet();
        SortedSet fontAtts = new TreeSet();
        for (StringTokenizer st = new StringTokenizer(s, ","); st.hasMoreTokens();) {
            String str = st.nextToken().trim().toLowerCase();
            if (str.equals("")) {
                continue;
            }
            boolean isFont = false;
            if (fontAttributeNames.contains(str)) {
                isFont = true;
            }
			else {
                for (Iterator i = fontAttributeStarting.iterator(); i.hasNext();) {
                    if (str.startsWith((String) i.next())) {
                        isFont = true;
                        break;
                    }
                }
            }
            (isFont ? fontAtts : att).add(str);
        }
        SortedSet allAtts = new TreeSet();
        allAtts.addAll(att);
        allAtts.addAll(fontAtts);
        String descriptor = OpenmrsUtil.join(allAtts, ",");
        if (styles.containsKey(descriptor)) {
            return (CellStyle) styles.get(descriptor);
        }
		else {
            CellStyle style = wb.createCellStyle();
            if (fontAtts.size() > 0) {
                Font font = getFont(OpenmrsUtil.join(fontAtts, ","));
                style.setFont(font);
            }
            for (Iterator i = att.iterator(); i.hasNext();) {
                helper(style, (String) i.next());
            }
            styles.put(descriptor, style);
            return style;
        }
    }

    public CellStyle getAugmented(CellStyle style, String s) {
        String desc = null;
        for (Iterator i = styles.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            if (e.getValue().equals(style)) {
                desc = (String) e.getKey();
            }
        }
        if (desc == null) {
            throw new IllegalArgumentException(
                    "StyleHelper.getAugmented() can only take a style registered with this StyleHelper");
        }
        if (desc.equals("")) {
            desc = s;
        } else {
            desc += "," + s;
        }
        return getStyle(desc);
    }
    
    private void helper(CellStyle style, String s) {
        if (s.equals("wraptext")) {
            style.setWrapText(true);
        } else if (s.startsWith("align=")) {
            s = s.substring(6);
            if (s.equals("left")) {
                style.setAlignment(CellStyle.ALIGN_LEFT);
            } else if (s.equals("center")) {
                style.setAlignment(CellStyle.ALIGN_CENTER);
            } else if (s.equals("right")) {
                style.setAlignment(CellStyle.ALIGN_RIGHT);
            } else if (s.equals("fill")) {
                style.setAlignment(CellStyle.ALIGN_FILL);
            }
        } else if (s.startsWith("border=")) {
            s = s.substring(7);
            if (s.equals("all")) {
                style.setBorderTop(CellStyle.BORDER_THIN);
                style.setBorderBottom(CellStyle.BORDER_THIN);
                style.setBorderLeft(CellStyle.BORDER_THIN);
                style.setBorderRight(CellStyle.BORDER_THIN);
            } else if (s.equals("top")) {
                style.setBorderTop(CellStyle.BORDER_THIN);
            } else if (s.equals("bottom")) {
                style.setBorderBottom(CellStyle.BORDER_THIN);
            } else if (s.equals("left")) {
                style.setBorderLeft(CellStyle.BORDER_THIN);
            } else if (s.equals("right")) {
                style.setBorderRight(CellStyle.BORDER_THIN);
            }
        } else if (s.equals("date")) {
            style.setDataFormat(dateFormat);
        }
    }

}
