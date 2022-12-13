package com.rajeshkawali.service;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.rajeshkawali.constant.UserConstants;
import com.rajeshkawali.dto.User;
import com.rajeshkawali.exception.DataException;
import com.rajeshkawali.exception.UserServiceException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

/**
 * @author Rajesh_Kawali
 *
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
	
	public static final String CLASS_NAME = UserServiceImpl.class.getName();

	@Autowired
	private WebClient webClient;

	@Override
	public List<User> getAllUsers() {
		String _function = ".getAllUsers";
		log.info(CLASS_NAME + _function + "::ENTER");
		try {
			List<User> userList = webClient.get().uri(UserConstants.GET_ALL_USERS_V1).retrieve().bodyToFlux(User.class)
					.collectList().block();
			log.info(CLASS_NAME + _function + "::User List Size: {}", userList.size());
			return userList;
		} catch (WebClientResponseException e) {
			log.info(CLASS_NAME + _function + "::Error response code is : {}", e.getRawStatusCode());
			log.info(CLASS_NAME + _function + "::Response message is: {}", e.getResponseBodyAsString());
			log.error(CLASS_NAME + _function + "::WebClientResponseException is : {}", e);
			throw e;
		} catch (Exception e) {
			log.error(CLASS_NAME + _function + "::Exception is : {}", e);
			throw e;
		}
	}
	
	@Override
	public User addUser(User user) {
		String _function = ".addUser";
		log.info(CLASS_NAME + _function + "::ENTER");
		try {
			return webClient.post().uri(UserConstants.ADD_USER_V1).bodyValue(user).retrieve().bodyToMono(User.class)
					.block();
		} catch (WebClientResponseException e) {
			log.info(CLASS_NAME + _function + "::Error response code is : {}", e.getRawStatusCode());
			log.info(CLASS_NAME + _function + "::Response message is: {}", e.getResponseBodyAsString());
			log.error(CLASS_NAME + _function + "::WebClientResponseException is : {}", e);
			throw e;
		} catch (Exception e) {
			log.error(CLASS_NAME + _function + "::Exception is : {}", e);
			throw e;
		}
	}

	@Override
	public User userById(Long userId) {
		String _function = ".userById";
		log.info(CLASS_NAME + _function + "::ENTER");
		try {
			return webClient.get().uri(UserConstants.USER_BY_ID_V1, userId).retrieve().bodyToMono(User.class).block();
		} catch (WebClientResponseException e) {
			log.info(CLASS_NAME + _function + "::Error response code is : {}", e.getRawStatusCode());
			log.info(CLASS_NAME + _function + "::Response message is: {}", e.getResponseBodyAsString());
			log.error(CLASS_NAME + _function + "::WebClientResponseException is : {}", e);
			throw e;
		} catch (Exception e) {
			log.error(CLASS_NAME + _function + "::Exception is : {}", e);
			throw e;
		}
	}

	@Override
	public List<User> getUserByName(String userName) {
		String _function = ".getUserByName";
		log.info(CLASS_NAME + _function + "::ENTER");
		try {
			String uri = UriComponentsBuilder.fromUriString(UserConstants.GET_USER_BY_NAME_V1)
					.queryParam("userName", userName).build().toUriString();
			log.info(CLASS_NAME + _function + "::uri: {}", uri);
			return webClient.get().uri(uri).retrieve().bodyToFlux(User.class).collectList().block();
		} catch (WebClientResponseException e) {
			log.info(CLASS_NAME + _function + "::Error response code is : {}", e.getRawStatusCode());
			log.info(CLASS_NAME + _function + "::Response message is: {}", e.getResponseBodyAsString());
			log.error(CLASS_NAME + _function + "::WebClientResponseException is : {}", e);
			throw e;
		} catch (Exception e) {
			log.error(CLASS_NAME + _function + "::Exception is : {}", e);
			throw e;
		}
	}
	
	@Override
	public User updateUser(Long id, User user) {
		String _function = ".updateUser";
		log.info(CLASS_NAME + _function + "::ENTER");
		try {
			return webClient.put().uri(UserConstants.USER_BY_ID_V1, id).bodyValue(user).retrieve()
					.bodyToMono(User.class).block();
		} catch (WebClientResponseException e) {
			log.info(CLASS_NAME + _function + "::Error response code is : {}", e.getRawStatusCode());
			log.info(CLASS_NAME + _function + "::Response message is: {}", e.getResponseBodyAsString());
			log.error(CLASS_NAME + _function + "::WebClientResponseException is : {}", e);
			throw e;
		} catch (Exception e) {
			log.error(CLASS_NAME + _function + "::Exception is : {}", e);
			throw e;
		}
	}
	
	@Override
	public String deleteUser(Long id) {
		String _function = ".deleteUser";
		log.info(CLASS_NAME + _function + "::ENTER");
		try {
			return webClient.delete().uri(UserConstants.USER_BY_ID_V1, id).retrieve().bodyToMono(String.class).block();
		} catch (WebClientResponseException e) {
			log.info(CLASS_NAME + _function + "::Error response code is : {}", e.getRawStatusCode());
			log.info(CLASS_NAME + _function + "::Response message is: {}", e.getResponseBodyAsString());
			log.error(CLASS_NAME + _function + "::WebClientResponseException is : {}", e);
			throw e;
		} catch (Exception e) {
			log.error(CLASS_NAME + _function + "::Exception is : {}", e);
			throw e;
		}
	}

	public User getUserByIdWithRetry(Long userId) {
		String _function = ".getUserByIdWithRetry";
		log.info(CLASS_NAME + _function + "::ENTER");
		try {
			return webClient.get().uri(UserConstants.USER_BY_ID_V1, userId).retrieve().bodyToMono(User.class)
					.retryWhen(fixedRetry).block();
		} catch (WebClientResponseException e) {
			log.info(CLASS_NAME + _function + "::Error response code is : {}", e.getRawStatusCode());
			log.info(CLASS_NAME + _function + "::Response message is: {}", e.getResponseBodyAsString());
			log.error(CLASS_NAME + _function + "::WebClientResponseException is : {}", e);
			throw e;
		} catch (Exception ex) {
			log.error(CLASS_NAME + _function + "::Exception is : {}", ex);
			throw new UserServiceException(ex.getMessage());
		}
	}
	
	public User getUserByIdWithCustomErrorHandling(Long userId) {
		String _function = ".getUserByIdWithCustomErrorHandling";
		log.info(CLASS_NAME + _function + "::ENTER");
		return webClient.get().uri(UserConstants.USER_BY_ID_V1, userId).retrieve()
				.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
				.bodyToMono(User.class).block();
	}

	public User addNewUserWithCustomErrorHandling(User user) {
		String _function = ".addNewUserWithCustomErrorHandling";
		log.info(CLASS_NAME + _function + "::ENTER");
		return webClient.post().uri(UserConstants.ADD_USER_V1).bodyValue(user).retrieve()
				.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
				.bodyToMono(User.class).block();
	}

	public String errorEndpoint() {
		String _function = ".errorEndpoint";
		log.info(CLASS_NAME + _function + "::ENTER");
		return webClient.get().uri(UserConstants.ERROR_USER_V1).retrieve()
				.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
				.bodyToMono(String.class).block();
	}

	public String errorEndpointFixedRetry() {
		String _function = ".errorEndpointFixedRetry";
		log.info(CLASS_NAME + _function + "::ENTER");
		return webClient.get().uri(UserConstants.ERROR_USER_V1).retrieve()
				.onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxErrorResponse(clientResponse))
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxErrorResponse(clientResponse))
				.bodyToMono(String.class).retryWhen(fixedRetry5xx).block();
	}

	public static Retry<?> fixedRetry = Retry.anyOf(WebClientResponseException.class)
			.fixedBackoff(Duration.ofSeconds(2)).retryMax(3).doOnRetry((exception) -> {
				log.info("Exception is : " + exception);
			});

	public static Retry<?> fixedRetry5xx = Retry.anyOf(UserServiceException.class).fixedBackoff(Duration.ofSeconds(2))
			.retryMax(3).doOnRetry((exception) -> {
				log.info("Exception is : " + exception);
			});

	public Mono<RuntimeException> handle4xxErrorResponse(ClientResponse clientResponse) {
		String _function = ".handle4xxErrorResponse";
		Mono<String> errorResponse = clientResponse.bodyToMono(String.class);
		return errorResponse.flatMap((message) -> {
			log.error(CLASS_NAME + _function + "::ErrorResponse Code is: {} and Exception message is : {}",clientResponse.rawStatusCode(),message);
			throw new DataException(message);
		});
	}
	
	public Mono<UserServiceException> handle5xxErrorResponse(ClientResponse clientResponse) {
		String _function = ".handle5xxErrorResponse";
		Mono<String> errorResponse = clientResponse.bodyToMono(String.class);
		return errorResponse.flatMap((message) -> {
			log.error(CLASS_NAME + _function + "::ErrorResponse Code is: {} and Exception message is : {}",clientResponse.rawStatusCode(),message);
			throw new UserServiceException(message);
		});
	}
}
