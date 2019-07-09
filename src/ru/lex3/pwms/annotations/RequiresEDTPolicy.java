package ru.lex3.pwms.annotations;

/**
 * EDT execution policy. Code can be executed in EDT synchronously - through
 * <code>javax.swing.SwingUtilities.invokeAndWait(Runnable)</code>, - or asynchronously - through
 * <code>javax.swing.SwingUtilities.invokeLater(Runnable)</code>.
 *
 * @author Eugene Matyushkin aka Skipy
 * @version $Id: RequiresEDTPolicy.java 422 2010-08-17 13:40:35Z skipy_ru $
 * @since 13.08.2010
 */
public enum RequiresEDTPolicy {

    /**
     * Asynchronous execution
     */
    ASYNC,

    /**
     * Synchronous execution
     */
    SYNC
}
