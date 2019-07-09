package ru.lex3.pwms.interfaces;

import ru.lex3.pwms.annotations.RequiresEDT;
import ru.lex3.pwms.annotations.RequiresEDTPolicy;

/**
 * Callback class to use from {@link Loader} to publish data, perform UI changes etc. Thread safety
 * should be taken into account when implementing this interface
 *
 * @author Eugene Matyushkin aka Skipy
 * @version $Id: UICallback.java 422 2010-08-17 13:40:35Z skipy_ru $
 * @since 09.07.2010
 */
public interface UICallback {

    /**
     * Refresh last data which has just been read. This method can be called from outside EDT.
     *
     */
  //  @RequiresEDT
    void refreshValues();


    /**
     * Displays error message. This method can be called from outside EDT.
     *
     * @param message message to display
     */
   // @RequiresEDT(RequiresEDTPolicy.SYNC)
   // void showError(String message);
}
