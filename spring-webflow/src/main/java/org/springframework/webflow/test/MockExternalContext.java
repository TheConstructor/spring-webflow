/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.test;

import java.io.StringWriter;
import java.io.Writer;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.binding.collection.SharedMapDecorator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Mock implementation of the {@link ExternalContext} interface.
 * @see ExternalContext
 * @author Keith Donald
 */
public class MockExternalContext implements ExternalContext {

	private String contextPath;

	private ParameterMap requestParameterMap = new MockParameterMap();

	private MutableAttributeMap<Object> requestMap = new LocalAttributeMap<>();

	private SharedAttributeMap<Object> sessionMap = new LocalSharedAttributeMap<>(
			new SharedMapDecorator<>(new HashMap<>()));

	private SharedAttributeMap<Object> globalSessionMap = sessionMap;

	private SharedAttributeMap<Object> applicationMap = new LocalSharedAttributeMap<>(
			new SharedMapDecorator<>(new HashMap<>()));

	private Object nativeContext = new Object();

	private Object nativeRequest = new Object();

	private Object nativeResponse = new Object();

	private Principal currentUser;

	private Locale locale;

	private StringWriter responseWriter = new StringWriter();

	private boolean ajaxRequest;

	private Boolean responseAllowed;

	private boolean responseComplete;

	private boolean flowExecutionRedirectRequested;

	private String flowDefinitionRedirectFlowId;

	private MutableAttributeMap<Object> flowDefinitionRedirectFlowInput;

	private String externalRedirectUrl;

	private boolean redirectInPopup;

	/**
	 * Creates a mock external context with an empty request parameter map. Allows for bean style usage.
	 */
	public MockExternalContext() {
	}

	/**
	 * Creates a mock external context with the specified parameters in the request parameter map. All other properties
	 * of the external context can be set using the appropriate setter.
	 * @param requestParameterMap the request parameters
	 */
	public MockExternalContext(ParameterMap requestParameterMap) {
		if (requestParameterMap != null) {
			this.requestParameterMap = requestParameterMap;
		}
	}

	// implementing external context

	public String getContextPath() {
		return contextPath;
	}

	public ParameterMap getRequestParameterMap() {
		return requestParameterMap;
	}

	public MutableAttributeMap<Object> getRequestMap() {
		return requestMap;
	}

	public SharedAttributeMap<Object> getSessionMap() {
		return sessionMap;
	}

	public SharedAttributeMap<Object> getGlobalSessionMap() {
		return globalSessionMap;
	}

	public SharedAttributeMap<Object> getApplicationMap() {
		return applicationMap;
	}

	public Principal getCurrentUser() {
		return currentUser;
	}

	public Locale getLocale() {
		return locale;
	}

	public Object getNativeContext() {
		return nativeContext;
	}

	public Object getNativeRequest() {
		return nativeRequest;
	}

	public Object getNativeResponse() {
		return nativeResponse;
	}

	public boolean isAjaxRequest() {
		return ajaxRequest;
	}

	public String getFlowExecutionUrl(String flowId, String flowExecutionKey) {
		return "/" + flowId + "?execution=" + flowExecutionKey;
	}

	public Writer getResponseWriter() {
		assertResponseAllowed();
		return responseWriter;
	}

	public boolean isResponseAllowed() {
		if (responseAllowed != null) {
			return responseAllowed;
		} else {
			return !responseComplete;
		}
	}

	public boolean isResponseComplete() {
		return responseComplete;
	}

	public void recordResponseComplete() {
		responseComplete = true;
	}

	public boolean isResponseCompleteFlowExecutionRedirect() {
		return flowExecutionRedirectRequested;
	}

	public void requestFlowExecutionRedirect() throws IllegalStateException {
		flowExecutionRedirectRequested = true;
		recordResponseComplete();
	}

	public void requestFlowDefinitionRedirect(String flowId, MutableAttributeMap<?> input) throws IllegalStateException {
		flowDefinitionRedirectFlowId = flowId;
		flowDefinitionRedirectFlowInput = new LocalAttributeMap<>();
		if (input != null) {
			flowDefinitionRedirectFlowInput.putAll(input);
		}
		recordResponseComplete();
	}

	public void requestExternalRedirect(String uri) throws IllegalStateException {
		externalRedirectUrl = uri;
		recordResponseComplete();
	}

	public void requestRedirectInPopup() throws IllegalStateException {
		if (isRedirectRequested()) {
			redirectInPopup = true;
		} else {
			throw new IllegalStateException(
					"Only call requestRedirectInPopup after a redirect has been requested by calling requestFlowExecutionRedirect, requestFlowDefinitionRedirect, or requestExternalRedirect");
		}
	}

	/**
	 * Set the context path of the application.
	 * @param contextPath the context path
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * Set the request parameter map.
	 * @see ExternalContext#getRequestParameterMap()
	 */
	public void setRequestParameterMap(ParameterMap requestParameterMap) {
		this.requestParameterMap = requestParameterMap;
	}

	/**
	 * Set the request attribute map.
	 * @see ExternalContext#getRequestMap()
	 */
	public void setRequestMap(MutableAttributeMap<Object> requestMap) {
		this.requestMap = requestMap;
	}

	/**
	 * Set the session attribute map.
	 * @see ExternalContext#getSessionMap()
	 */
	public void setSessionMap(SharedAttributeMap<Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

	/**
	 * Set the global session attribute map. By default the session attribute map and the global session attribute map
	 * are one and the same.
	 * @see ExternalContext#getGlobalSessionMap()
	 */
	public void setGlobalSessionMap(SharedAttributeMap<Object> globalSessionMap) {
		this.globalSessionMap = globalSessionMap;
	}

	/**
	 * Set the application attribute map.
	 * @see ExternalContext#getApplicationMap()
	 */
	public void setApplicationMap(SharedAttributeMap<Object> applicationMap) {
		this.applicationMap = applicationMap;
	}

	/**
	 * Set the native context object.
	 * @param nativeContext the native context
	 */
	public void setNativeContext(Object nativeContext) {
		this.nativeContext = nativeContext;
	}

	/**
	 * Set the native request object.
	 * @param nativeRequest the native request object
	 */
	public void setNativeRequest(Object nativeRequest) {
		this.nativeRequest = nativeRequest;
	}

	/**
	 * Set the native response object.
	 * @param nativeResponse the native response object
	 */
	public void setNativeResponse(Object nativeResponse) {
		this.nativeResponse = nativeResponse;
	}

	/**
	 * Sets the current user principal.
	 * @param currentUser the current user
	 */
	public void setCurrentUser(Principal currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * Convenience method that sets the current user principal as a string.
	 * @param currentUser the current user name
	 */
	public void setCurrentUser(String currentUser) {
		this.currentUser = new MockPrincipal(currentUser);
	}

	/**
	 * Sets the client locale.
	 * @param locale the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	// convenience helpers

	/**
	 * Returns the request parameter map as a {@link MockParameterMap} for convenient access in a unit test.
	 * @see #getRequestParameterMap()
	 */
	public MockParameterMap getMockRequestParameterMap() {
		return (MockParameterMap) requestParameterMap;
	}

	/**
	 * Puts a request parameter into the mock parameter map.
	 * @param parameterName the parameter name
	 * @param parameterValue the parameter value
	 */
	public void putRequestParameter(String parameterName, String parameterValue) {
		getMockRequestParameterMap().put(parameterName, parameterValue);
	}

	/**
	 * Puts a multi-valued request parameter into the mock parameter map.
	 * @param parameterName the parameter name
	 * @param parameterValues the parameter values
	 */
	public void putRequestParameter(String parameterName, String[] parameterValues) {
		getMockRequestParameterMap().put(parameterName, parameterValues);
	}

	/**
	 * Puts a MultipartFile request parameter into the mock parameter map.
	 * @param parameterName the parameter name
	 * @param parameterValue the parameter value
	 */
	public void putRequestParameter(String parameterName, MultipartFile parameterValue) {
		getMockRequestParameterMap().put(parameterName, parameterValue);
	}

	/**
	 * Puts a multi-valued MultipartFile request parameter into the mock parameter map.
	 *
	 * @param parameterName the parameter name
	 * @param parameterValue the parameter value
	 */
	public void putRequestParameter(String parameterName, List<MultipartFile> parameterValue) {
		getMockRequestParameterMap().put(parameterName, parameterValue);
	}

	/**
	 * Sets the id of the event that should be signaled by this context. For use when resuming a flow. This method
	 * depends on a MockViewFactory being configured for parsing the event id on a resume operation.
	 * @param eventId the id of the event to signal
	 */
	public void setEventId(String eventId) {
		putRequestParameter("_eventId", eventId);
	}

	/**
	 * Set whether this request is an ajax request.
	 * @param ajaxRequest true or false
	 */
	public void setAjaxRequest(boolean ajaxRequest) {
		this.ajaxRequest = ajaxRequest;
	}

	/**
	 * Set the response allows flag to a value for testing.
	 * @param responseAllowed true or false
	 */
	public void setResponseAllowed(boolean responseAllowed) {
		this.responseAllowed = Boolean.valueOf(responseAllowed);
	}

	/**
	 * Returns the implementation of this mock context's response writer.
	 * @return the underlying string writer to use for asserting a specific response was written
	 */
	public StringWriter getMockResponseWriter() {
		return responseWriter;
	}

	/**
	 * Returns the flag indicating if a flow execution redirect response has been requested by the flow.
	 */
	public boolean getFlowExecutionRedirectRequested() {
		return flowExecutionRedirectRequested;
	}

	/**
	 * Returns the flag indicating if a flow definition redirect response has been requested by the flow.
	 */
	public boolean getFlowDefinitionRedirectRequested() {
		return flowDefinitionRedirectFlowId != null;
	}

	/**
	 * Returns the id of the flow definition to redirect to. Only set when {@link #getFlowDefinitionRedirectRequested()}
	 * returns true.
	 */
	public String getFlowRedirectFlowId() {
		return flowDefinitionRedirectFlowId;
	}

	/**
	 * Returns the input to pass the flow definition through the redirect. Only set when
	 * {@link #getFlowDefinitionRedirectRequested()} returns true.
	 */
	public MutableAttributeMap<Object> getFlowRedirectFlowInput() {
		return flowDefinitionRedirectFlowInput;
	}

	/**
	 * Returns the flag indicating if an external redirect response has been requested by the flow.
	 */
	public boolean getExternalRedirectRequested() {
		return externalRedirectUrl != null;
	}

	/**
	 * Returns the URL to redirect to. Only set if {@link #getExternalRedirectRequested()} returns true.
	 */
	public String getExternalRedirectUrl() {
		return externalRedirectUrl;
	}

	/**
	 * If a redirect response has been requested, indicates if the redirect should be issued from a popup dialog.
	 */
	public boolean getRedirectInPopup() {
		return redirectInPopup;
	}

	// internal helpers

	private void assertResponseAllowed() throws IllegalStateException {
		if (!isResponseAllowed()) {
			if (getFlowExecutionRedirectRequested()) {
				throw new IllegalStateException(
						"A response is not allowed because a redirect has already been requested on this ExternalContext");
			}
			if (getFlowDefinitionRedirectRequested()) {
				throw new IllegalStateException(
						"A response is not allowed because a flowRedirect has already been requested on this ExternalContext");
			}
			if (getExternalRedirectRequested()) {
				throw new IllegalStateException(
						"A response is not allowed because an externalRedirect has already been requested on this ExternalContext");
			}
			if (responseComplete) {
				throw new IllegalStateException(
						"A response is not allowed because one has already been completed on this ExternalContext");
			} else {
				throw new IllegalStateException("A response is not allowed");
			}
		}
	}

	public boolean isRedirectRequested() {
		return getFlowExecutionRedirectRequested() || getFlowDefinitionRedirectRequested()
				|| getExternalRedirectRequested();
	}

	private class MockPrincipal implements Principal {
		private String name;

		private MockPrincipal(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
