package com.scto.codelikebastimove.core.plugin.impl.security

import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginDescriptor
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginPermission
import java.io.File
import java.security.MessageDigest

class PluginSecurityManager {

  private val trustedSignatures = mutableSetOf<String>()
  private val grantedPermissions = mutableMapOf<String, MutableSet<PluginPermission>>()
  private val deniedPermissions = mutableMapOf<String, MutableSet<PluginPermission>>()

  fun addTrustedSignature(signature: String) {
    trustedSignatures.add(signature)
  }

  fun removeTrustedSignature(signature: String) {
    trustedSignatures.remove(signature)
  }

  fun verifyPluginSignature(pluginFile: File): VerificationResult {
    return try {
      val checksum = calculateChecksum(pluginFile)

      if (trustedSignatures.isEmpty()) {
        VerificationResult(true, checksum, "No signature verification required")
      } else if (trustedSignatures.contains(checksum)) {
        VerificationResult(true, checksum, "Signature verified")
      } else {
        VerificationResult(false, checksum, "Untrusted plugin signature")
      }
    } catch (e: Exception) {
      VerificationResult(false, null, "Verification failed: ${e.message}")
    }
  }

  private fun calculateChecksum(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
      val buffer = ByteArray(8192)
      var bytesRead: Int
      while (input.read(buffer).also { bytesRead = it } != -1) {
        digest.update(buffer, 0, bytesRead)
      }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
  }

  fun hasPermission(pluginId: String, permission: PluginPermission): Boolean {
    if (deniedPermissions[pluginId]?.contains(permission) == true) {
      return false
    }
    return grantedPermissions[pluginId]?.contains(permission) == true
  }

  fun grantPermission(pluginId: String, permission: PluginPermission) {
    grantedPermissions.getOrPut(pluginId) { mutableSetOf() }.add(permission)
    deniedPermissions[pluginId]?.remove(permission)
  }

  fun denyPermission(pluginId: String, permission: PluginPermission) {
    deniedPermissions.getOrPut(pluginId) { mutableSetOf() }.add(permission)
    grantedPermissions[pluginId]?.remove(permission)
  }

  fun revokePermission(pluginId: String, permission: PluginPermission) {
    grantedPermissions[pluginId]?.remove(permission)
    deniedPermissions[pluginId]?.remove(permission)
  }

  fun revokeAllPermissions(pluginId: String) {
    grantedPermissions.remove(pluginId)
    deniedPermissions.remove(pluginId)
  }

  fun getGrantedPermissions(pluginId: String): Set<PluginPermission> {
    return grantedPermissions[pluginId]?.toSet() ?: emptySet()
  }

  fun validatePluginPermissions(descriptor: PluginDescriptor): PermissionValidationResult {
    val requiredPermissions = descriptor.permissions
    val missingPermissions = mutableListOf<PluginPermission>()
    val grantedPermissions = mutableListOf<PluginPermission>()

    for (permission in requiredPermissions) {
      if (hasPermission(descriptor.id, permission)) {
        grantedPermissions.add(permission)
      } else {
        missingPermissions.add(permission)
      }
    }

    return PermissionValidationResult(
      valid = missingPermissions.isEmpty(),
      grantedPermissions = grantedPermissions,
      missingPermissions = missingPermissions,
    )
  }

  fun checkCompatibility(descriptor: PluginDescriptor, hostVersion: String): CompatibilityResult {
    val minVersion = parseVersion(descriptor.minHostVersion)
    val currentVersion = parseVersion(hostVersion)

    val isCompatible = compareVersions(currentVersion, minVersion) >= 0

    val maxVersionOk =
      descriptor.maxHostVersion?.let { maxVersion ->
        compareVersions(currentVersion, parseVersion(maxVersion)) <= 0
      } ?: true

    return CompatibilityResult(
      compatible = isCompatible && maxVersionOk,
      currentVersion = hostVersion,
      requiredMinVersion = descriptor.minHostVersion,
      requiredMaxVersion = descriptor.maxHostVersion,
      reason =
        when {
          !isCompatible ->
            "Host version $hostVersion is below minimum required ${descriptor.minHostVersion}"
          !maxVersionOk ->
            "Host version $hostVersion exceeds maximum supported ${descriptor.maxHostVersion}"
          else -> "Compatible"
        },
    )
  }

  private fun parseVersion(version: String): List<Int> {
    return version.split(".").mapNotNull { it.toIntOrNull() }
  }

  private fun compareVersions(v1: List<Int>, v2: List<Int>): Int {
    val maxLength = maxOf(v1.size, v2.size)
    for (i in 0 until maxLength) {
      val part1 = v1.getOrElse(i) { 0 }
      val part2 = v2.getOrElse(i) { 0 }
      if (part1 != part2) {
        return part1.compareTo(part2)
      }
    }
    return 0
  }
}

data class VerificationResult(val verified: Boolean, val checksum: String?, val message: String)

data class PermissionValidationResult(
  val valid: Boolean,
  val grantedPermissions: List<PluginPermission>,
  val missingPermissions: List<PluginPermission>,
)

data class CompatibilityResult(
  val compatible: Boolean,
  val currentVersion: String,
  val requiredMinVersion: String,
  val requiredMaxVersion: String?,
  val reason: String,
)
