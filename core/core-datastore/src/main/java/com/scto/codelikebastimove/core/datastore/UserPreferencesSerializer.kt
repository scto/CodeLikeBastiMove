package com.scto.codelikebastimove.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.scto.codelikebastimove.core.datastore.proto.ThemeMode
import com.scto.codelikebastimove.core.datastore.proto.UserPreferencesProto
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferencesProto> {
    override val defaultValue: UserPreferencesProto = UserPreferencesProto.newBuilder()
        .setThemeMode(ThemeMode.THEME_MODE_FOLLOW_SYSTEM)
        .setDynamicColorsEnabled(true)
        .build()

    override suspend fun readFrom(input: InputStream): UserPreferencesProto {
        try {
            return UserPreferencesProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPreferencesProto, output: OutputStream) {
        t.writeTo(output)
    }
}
