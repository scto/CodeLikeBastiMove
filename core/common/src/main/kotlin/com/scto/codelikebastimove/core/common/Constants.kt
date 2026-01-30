package com.scto.codelikebastimove.core.common

import com.blankj.utilcode.util.PathUtils

// From https://github.com/PsiCodes/ktxpy
const val PYTHON_PACKAGE_URL_64_BIT =
  "https://github.com/PsiCodes/ktxpy/raw/master/app/arch_arm64-v8a/assets/python.7z"
const val PYTHON_PACKAGE_URL_32_BIT =
  "https://github.com/PsiCodes/ktxpy/raw/master/app/arch_arm32/assets/python.7z"

val APP_EXTERNAL_DIR = "${PathUtils.getExternalStoragePath()}/CLBM"

const val ORGANIZATION_NAME = "scto"
const val APPLICATION_REPOSITORY_NAME = "CodeLikeBastiMove"

const val KEY_GIT_USERNAME = "git_username"
const val KEY_GIT_PASSWORD = "git_password"
const val KEY_GIT_USER_INFO = "git_user_info"
const val KEY_GIT_USER_ACCESS_TOKEN = "git_user_access_token"

object PreferenceKeys {
  const val RECENT_FOLDER_1 = "recent_folder_1"
  const val RECENT_FOLDER_2 = "recent_folder_2"
  const val RECENT_FOLDER_3 = "recent_folder_3"
  const val RECENT_FOLDER_4 = "recent_folder_4"
  const val RECENT_FOLDER_5 = "recent_folder_5"

  const val PLUGINS_PATH = "plugins_path"
}

object PluginConstants {
  val PLUGIN_HOME_PATH = "${PathUtils.getInternalAppFilesPath()}/plugins"
}
