package com.github.liaomengge.service.base_framework;

import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 16/9/14.
 */
public abstract class AbstractBaseFrameworkLauncher {

    protected static final Logger log = LyLogger.getInstance(AbstractBaseFrameworkLauncher.class);
    protected static final byte[] lifeCycleLock = new byte[0];

}
