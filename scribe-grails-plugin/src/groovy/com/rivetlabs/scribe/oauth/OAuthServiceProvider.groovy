package com.rivetlabs.scribe.oauth

import groovy.util.ConfigObject;

import org.scribe.builder.ServiceBuilder
import org.scribe.model.Token
import org.scribe.model.Verifier
import org.scribe.oauth.OAuthService

class OAuthServiceProvider {

	OAuthServiceConfig config
	OAuthService service

	OAuthServiceProvider(OAuthServiceConfig config) {
		this.config = config;
		buildService()
	}

	void buildService() {
		ServiceBuilder builder =  new ServiceBuilder()
				.provider(config.apiClass)
				.apiKey(config.apiKey)
				.apiSecret(config.apiSecret);

		if (config.callback) builder.callback(config.callback)
		if (config.signatureType) builder.signatureType(config.signatureType)
		if (config.scope) builder.scope(config.scope)
		if (config.debug) builder = builder.debug()

		service = builder.build()
	}

	Token getRequestToken() {
		return service.requestToken
	}

	String getAuthorizationUrl(Token token) {
		return service.getAuthorizationUrl(token)
	}

	Token getAccessToken(Token token, Verifier verifier) {
		return service.getAccessToken(token, verifier)
	}

	String oAuthVersion() {
		return service.version
	}

}
