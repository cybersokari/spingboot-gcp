package co.gatedaccess.web.http.response

class TokenBody {
    var token: String? = null

    constructor()

    constructor(token: String?) {
        this.token = token
    }


    fun setToken(token: String?): TokenBody {
        this.token = token
        return this
    }
}
