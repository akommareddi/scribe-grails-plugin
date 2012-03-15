package com.rivetlabs.scribe.oauth

import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.model.Verifier

class ScribeController {

	static transactional = false
    private final Token EMPTY_TOKEN = null
	private String provider = null

    ScribeService scribeService

    def callback = {
		
		Token requestToken = (Token) session[OAuthSessionAttributes.ATTR_OAUTH_REQUEST_TOKEN]
		String providerName = session[OAuthSessionAttributes.ATTR_OAUTH_PROVIDER_NAME]
		OAuthServiceProvider oaProvider = scribeService.getOAuthProvider(providerName)
		
		String successUri = oaProvider.config.successUri  
		String failureUri = oaProvider.config.failureUri 
		
		String oauthVerifierKey = "oauth_verifier"
		if (oaProvider.oAuthVersion() == SupportedOauthVersion.TWO) {
			oauthVerifierKey = "code"
		}
		String oauthVerifier = params[oauthVerifierKey]
		
		if (!oauthVerifier) {
			log.error("ERROR: OAuth verifier missing in ${params}")
			return redirect(uri: failureUri)
		}

		Verifier verifier = new Verifier(oauthVerifier)
        if (!verifier) {
			log.error("ERROR: Invalid OAuth verifier ${oauthVerifier}")
            return redirect(uri: failureUri)
        }
		Token accessToken = oaProvider.getAccessToken(requestToken, verifier);
		
		session[OAuthSessionAttributes.ATTR_OAUTH_ACCESS_TOKEN] = accessToken
		session.removeAttribute(OAuthSessionAttributes.ATTR_OAUTH_REQUEST_TOKEN)

		Response oAuthResponse = scribeService.retrieveProtectedResource(
				providerName, accessToken, Verb.GET,
				oaProvider.config.protectedResource, 
				oaProvider.config.connectTimeout,  
				oaProvider.config.readTimeout)
		println(oAuthResponse.getBody());
		
        return redirect(uri: successUri)
    }

    def authenticate = {

		def providerName = params.provider
		OAuthServiceProvider oaProvider = scribeService.getOAuthProvider(providerName)

		Token requestToken = (Token) session[OAuthSessionAttributes.ATTR_OAUTH_REQUEST_TOKEN]
		Token accessToken = (Token) session[OAuthSessionAttributes.ATTR_OAUTH_ACCESS_TOKEN]
		if(requestToken == null || accessToken == null) {
	        requestToken = EMPTY_TOKEN
	        if (oaProvider.oAuthVersion() == SupportedOauthVersion.ONE) {
        		requestToken = oaProvider.requestToken
	        }
			session[OAuthSessionAttributes.ATTR_OAUTH_REQUEST_TOKEN] = requestToken
		}
		session[OAuthSessionAttributes.ATTR_OAUTH_PROVIDER_NAME] = providerName
		String redirectUrl = oaProvider.getAuthorizationUrl(requestToken) 
		return redirect(url:redirectUrl)
    }

}
