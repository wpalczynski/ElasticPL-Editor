/**
 * @name        Simple Java NotePad
 * @package     ph.notepad
 * @file        UI.java
 * @author      SORIA Pierre-Henry
 * @email       pierrehs@hotmail.com
 * @link        http://github.com/pH-7
 * @copyright   Copyright Pierre-Henry SORIA, All Rights Reserved.
 * @license     Apache (http://www.apache.org/licenses/LICENSE-2.0)
 * @create      2012-05-04
 * @update      2016-21-03
 * 
 * 
 * @modifiedby  Achintha Gunasekara
 * @modweb      http://www.achinthagunasekara.com
 * @modemail    contact@achinthagunasekara.com
 * 
 * @modifiedby  Evil Knievel and the Elastic Community
 */

package simplejavatexteditor;

import javax.swing.JTextPane;

public class SimpleJavaTextEditor extends JTextPane {
	
	private static final long serialVersionUID = 1L;
	public final static String NAME = "Elastic PL Editor";
	public final static double VERSION = 1.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        new UI().setVisible(true);
	}

}