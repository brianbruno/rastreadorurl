package util;

import database.ConnectionController;
import org.apache.log4j.Logger;

public class LogService {

    private final static Logger logger = Logger.getLogger(LogService.class);

    public synchronized static void addLogInfo(String mensagem) {
        if(logger.isInfoEnabled()){
            logger.info(mensagem);
        }
    }

    public synchronized static void addLogError(String mensagem) {
        logger.error(mensagem);
    }


}
