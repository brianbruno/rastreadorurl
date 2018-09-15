package util;

import org.apache.log4j.Logger;

public class LogService {

    private final static Logger logger = Logger.getLogger(LogService.class);
//    private final static Logger logger = null;
    private final static boolean LOGATIVO = true;


    public synchronized static void addLogInfo(String mensagem) {
        if (LOGATIVO) {
            if (logger.isInfoEnabled()) {
                logger.info(mensagem);
            }
        }
    }

    public synchronized static void addLogError(String mensagem) {
        if (LOGATIVO)
            logger.error(mensagem);
    }

    public synchronized static void addLogFatal(String mensagem) {
        if (LOGATIVO) {
            logger.fatal(mensagem);
            System.exit(0);
        }
    }

    public synchronized static void addLogWarn(String mensagem) {
        if (LOGATIVO)
            logger.warn(mensagem);
    }


}
