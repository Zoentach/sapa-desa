package id.go.tapselkab.sapa_desa.core.data.token

import java.io.File

interface TokenStorage {
    fun save(token: String)
    fun get(): String?
    fun clear()
}

class FileTokenStorage(
    private val file: File = File(System.getProperty("user.home"), ".myapp_token")
) : TokenStorage {
    override fun save(token: String) {
        file.writeText(token)
    }

    override fun get(): String? {
        return if (file.exists()) file.readText().takeIf { it.isNotBlank() } else null
    }

    override fun clear() {
        if (file.exists()) file.delete()
    }
}
