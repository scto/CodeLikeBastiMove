package com.termux.shared.termux

object TermuxConstants {
    const val TERMUX_PACKAGE_NAME = "com.termux"
    const val TERMUX_APP_NAME = "Termux"
    
    const val TERMUX_PREFIX_DIR_PATH = "/data/data/com.termux/files/usr"
    const val TERMUX_HOME_DIR_PATH = "/data/data/com.termux/files/home"
    const val TERMUX_BIN_PREFIX_DIR_PATH = "$TERMUX_PREFIX_DIR_PATH/bin"
    const val TERMUX_ETC_PREFIX_DIR_PATH = "$TERMUX_PREFIX_DIR_PATH/etc"
    const val TERMUX_LIB_PREFIX_DIR_PATH = "$TERMUX_PREFIX_DIR_PATH/lib"
    const val TERMUX_TMP_PREFIX_DIR_PATH = "$TERMUX_PREFIX_DIR_PATH/tmp"
    
    const val TERMUX_DEFAULT_SHELL = "bash"
    const val TERMUX_DEFAULT_SHELL_PATH = "$TERMUX_BIN_PREFIX_DIR_PATH/$TERMUX_DEFAULT_SHELL"
    
    const val TERMUX_PROPERTIES_PRIMARY_FILE_PATH = "$TERMUX_HOME_DIR_PATH/.termux/termux.properties"
    const val TERMUX_PROPERTIES_SECONDARY_FILE_PATH = "$TERMUX_HOME_DIR_PATH/.config/termux/termux.properties"
    
    const val TERMUX_FLOAT_APP_PACKAGE_NAME = "com.termux.window"
    const val TERMUX_STYLING_APP_PACKAGE_NAME = "com.termux.styling"
    const val TERMUX_API_APP_PACKAGE_NAME = "com.termux.api"
    const val TERMUX_TASKER_APP_PACKAGE_NAME = "com.termux.tasker"
    const val TERMUX_BOOT_APP_PACKAGE_NAME = "com.termux.boot"
    const val TERMUX_WIDGET_APP_PACKAGE_NAME = "com.termux.widget"
    
    object Actions {
        const val ACTION_EXECUTE = "$TERMUX_PACKAGE_NAME.EXECUTE"
        const val ACTION_SERVICE_STOP = "$TERMUX_PACKAGE_NAME.service_stop"
        const val ACTION_SERVICE_WAKE_LOCK = "$TERMUX_PACKAGE_NAME.service_wake_lock"
        const val ACTION_SERVICE_WAKE_UNLOCK = "$TERMUX_PACKAGE_NAME.service_wake_unlock"
    }
    
    object Extras {
        const val EXTRA_EXECUTE_IN_BACKGROUND = "$TERMUX_PACKAGE_NAME.execute.background"
        const val EXTRA_ARGUMENTS = "$TERMUX_PACKAGE_NAME.execute.arguments"
        const val EXTRA_WORKDIR = "$TERMUX_PACKAGE_NAME.execute.workdir"
        const val EXTRA_SESSION_ACTION = "$TERMUX_PACKAGE_NAME.execute.session_action"
        const val EXTRA_COMMAND_PATH = "$TERMUX_PACKAGE_NAME.execute.command_path"
        const val EXTRA_RESULT_BUNDLE = "$TERMUX_PACKAGE_NAME.execute.result_bundle"
    }
}
