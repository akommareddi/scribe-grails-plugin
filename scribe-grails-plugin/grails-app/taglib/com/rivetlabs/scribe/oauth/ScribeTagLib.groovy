package com.rivetlabs.scribe.oauth

class ScribeTagLib {

    static namespace = 'scribe'

    def oauthconnect = { attrs, body ->
		def link = g.createLink(controller:'scribe', action:'authenticate')

		if(attrs['provider']) {
			link += '?provider='+attrs['provider']
		}
		out << g.link([url: link], body)
    }
}
