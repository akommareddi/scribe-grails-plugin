package com.rivetlabs.scribe.oauth

import java.util.concurrent.TimeUnit

import org.scribe.builder.ServiceBuilder
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.SignatureType
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.model.Verifier
import org.scribe.oauth.OAuthService

class ScribeService {

	static transactional = false
	def grailsApplication

	private Map providerMap = new HashMap()

	// Thirty Seconds
	static final int DEFAULT_TIMEOUT = 30000
	String successUri
	String failureUri
	int connectTimeout
	int readTimeout
	boolean debug

	public OAuthServiceProvider getOAuthProvider(String providerName) {
		OAuthServiceProvider oaProvider = providerMap[providerName]
		if (!oaProvider) {
			OAuthServiceConfig oaConfig = setup(providerName)
			oaProvider = new OAuthServiceProvider(oaConfig)
			providerMap[providerName] = oaProvider
		}
		oaProvider
	}

	private OAuthServiceConfig setup(String providerName) {
		if (!grailsApplication.config?.scribe) {
			throw new IllegalStateException('Scribe configuration not found.')
		}

		ConfigObject conf = grailsApplication.config.scribe
		connectTimeout = conf.connectTimeout ?: DEFAULT_TIMEOUT
		readTimeout = conf.readTimeout ?: DEFAULT_TIMEOUT
		successUri = conf.successUri
		failureUri = conf.failureUri
		debug = conf.debug
		
		if (providerName!=null) {
			conf = conf[(providerName)]
			if (!conf['debug']) {
				if (debug) conf['debug'] = debug
			}
			if (!conf.successUri) conf['successUri'] = successUri
			if (!conf.failureUri) conf['failureUri'] = failureUri
		}
		
		return new OAuthServiceConfig(conf)
	}

	public Response retrieveProtectedResource(String providerName, Token accessToken, Verb verb, String url, int connectTimeout, int readTimeout) {
		OAuthServiceProvider oaProvider = providerMap[providerName]
		if (!oaProvider) {
			log.error("ERROR: Provider ${providerName} not found, please check configuration")
		}
		OAuthRequest oAuthRequest = new OAuthRequest(verb, url);
		oAuthRequest.setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
		oAuthRequest.setReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
		oaProvider.service.signRequest(accessToken, oAuthRequest);
		Response oauthResponse = oAuthRequest.send();
		return oauthResponse
	}

}
