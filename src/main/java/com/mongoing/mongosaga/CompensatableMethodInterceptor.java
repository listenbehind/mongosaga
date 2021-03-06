package com.mongoing.mongosaga;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author TJ Tang
 * @version $Id$
 */
@Component
public class CompensatableMethodInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CompensatableMethodInterceptor.class);
    private int count=0;

    @Autowired
    private CompensationManager manager;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        manager.startTx();

        Object ret = null;
        try {
            
            log.debug("#### START COMPENSATABLE TX {} ####", invocation.getMethod().toGenericString());
            ret = invocation.proceed();
            //manager.resetTx();
        } 
        catch(Throwable t){            
            manager.doCompensation();
            throw t;
        }
        finally {           
            //manager.endTx();
            log.debug("#### END COMPENSATABLE TX {} #####", invocation.getMethod().toGenericString());
        }
        return ret;
    }

}
