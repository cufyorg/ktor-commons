/*
 *	Copyright 2022 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.cufy.ktor.plugins

import com.mongodb.client.model.UpdateOptions
import io.ktor.server.application.*
import io.ktor.util.*
import org.cufy.mangaka.Document
import org.cufy.mangaka.save
import org.cufy.mangaka.schema.SchemaScopeBuilder

/**
 * The key to access the call save queue attribute.
 *
 * @since 1.0.0
 */
val SaveQueueKey = AttributeKey<MutableSet<Document>>("SaveQueueKey")

/**
 * A queue of documents to be saved once the call
 * is done.
 *
 * @since 1.0.0
 */
val ApplicationCall.saveQueue: MutableSet<Document>
    get() = attributes.computeIfAbsent(SaveQueueKey) { mutableSetOf() }

/**
 * Configuration of [SaveQueue].
 *
 * @author LSafer
 * @since 1.0.0
 */
class SaveQueueConfig {
    /**
     * The update options.
     *
     * @since 1.0.0
     */
    var options: UpdateOptions.() -> Unit = { }

    /**
     * The validation schema scope;
     * `null` to skip validation.
     *
     * @since 1.0.0
     */
    var validate: (SchemaScopeBuilder<*, Document>.() -> Unit)? = { }

    /**
     * The update schema scope.
     *
     * @since 1.0.0
     */
    var update: (SchemaScopeBuilder<*, Document>.() -> Unit) = { }

    /**
     * Set the plugin update options builder to
     * the given [block].
     *
     * @since 1.0.0
     */
    fun options(block: UpdateOptions.() -> Unit) {
        options = block
    }

    /**
     * Set the plugin update scope builder to
     * the given [block].
     *
     * @since 1.0.0
     */
    fun update(block: SchemaScopeBuilder<*, Document>.() -> Unit) {
        update = block
    }

    /**
     * Set the plugin validation scope builder to
     * the given [block].
     *
     * @since 1.0.0
     */
    fun validate(block: SchemaScopeBuilder<*, Document>.() -> Unit) {
        validate = block
    }

    /**
     * Set the plugin to not validate documents
     * before saving them.
     *
     * @since 1.0.0
     */
    fun noValidate() {
        validate = null
    }
}

/**
 * A plugin that [saves](Document.save) the
 * documents at [saveQueue] after the call is done.
 *
 * @since 1.0.0
 */
val SaveQueue = createApplicationPlugin("SaveQueue", ::SaveQueueConfig) {
    onCallRespond { call, _ ->
        call.saveQueue.forEach {
            it.save(
                options = pluginConfig.options,
                validate = pluginConfig.validate,
                block = pluginConfig.update
            )
        }
    }
}
