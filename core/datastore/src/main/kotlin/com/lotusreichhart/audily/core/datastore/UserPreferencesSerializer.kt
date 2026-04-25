package com.lotusreichhart.audily.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Serializer cho [UserPreferencesProto].
 * Xử lý việc đọc/ghi dữ liệu Proto từ/vào file trên ổ đĩa.
 */
class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferencesProto> {

    override val defaultValue: UserPreferencesProto =
        UserPreferencesProto.getDefaultInstance()

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
