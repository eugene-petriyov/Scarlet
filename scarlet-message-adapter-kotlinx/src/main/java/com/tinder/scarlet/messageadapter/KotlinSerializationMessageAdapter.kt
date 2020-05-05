package com.tinder.scarlet.messageadapter

import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializerByTypeToken
import java.lang.reflect.Type

class KotlinSerializationMessageAdapter<T>(
        private val serializer: KSerializer<T>,
        private val stringFormat: StringFormat) : MessageAdapter<T> {
    override fun fromMessage(message: Message): T {
        val stringValue = when (message) {
            is Message.Text -> message.value
            is Message.Bytes -> String(message.value)
        }
        return stringFormat.parse(serializer, stringValue)
    }

    override fun toMessage(data: T): Message {
        return Message.Text(stringFormat.stringify(serializer, data))
    }

    class Factory(private val stringFormat: StringFormat) : MessageAdapter.Factory {
        override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<*> {
            return KotlinSerializationMessageAdapter(serializerByTypeToken(type), stringFormat)
        }

    }
}

@JvmName("create")
fun StringFormat.asMessageAdapterFactory(): MessageAdapter.Factory {
    return KotlinSerializationMessageAdapter.Factory(this)
}