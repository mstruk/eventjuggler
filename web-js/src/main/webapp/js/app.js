'use strict';

var eventjugglerModule = angular.module('eventjuggler', [
		'eventjugglerServices', 'ngCookies' ]);

eventjugglerModule.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/events', {
		templateUrl : 'partials/event-list.html',
		controller : EventListCtrl
	}).when('/events/mine', {
		templateUrl : 'partials/event-mine.html',
		controller : EventMineCtrl
	}).when('/events/:eventId', {
		templateUrl : 'partials/event-detail.html',
		controller : EventDetailCtrl
	}).when('/register', {
		templateUrl : 'partials/register.html',
		controller : RegisterCtrl
	}).otherwise({
		redirectTo : '/events'
	});
} ]);

eventjugglerModule.filter('substring', function() {
	return function(text, length) {
		if (!text) {
			return text;
		}

		if (!length) {
			length = 100;
		}

		text = text.replace(/<(?:.|\n)*?>/gm, '');

		if (text.length < 100) {
			return text;
		} else {
			return text.substring(0, length) + "...";
		}
	};
});

eventjugglerModule.config(function($httpProvider) {
	$httpProvider.responseInterceptors.push('loadingInterceptor');
	var spinnerFunction = function(data, headersGetter) {
		$('#loading').show();
		return data;
	};
	$httpProvider.defaults.transformRequest.push(spinnerFunction);
});

eventjugglerModule.factory('loadingInterceptor', function($q, $window) {
	return function(promise) {
		return promise.then(function(response) {
			$('#loading').hide();
			return response;
		}, function(response) {
			$('#loading').hide();
			return $q.reject(response);
		});
	};
});

eventjugglerModule.directive('ngBackgroundImage',
		function($timeout, dateFilter) {
			return function(scope, element, attrs) {
				var backgroundImage = null;

				function updateTime() {
					element.css({
						'background-image' : 'url(' + backgroundImage + ')',
						'background-size' : 'cover'
					});
				}

				scope.$watch(attrs.ngBackgroundImage, function(value) {
					backgroundImage = value;
					updateTime();
				});
			};
		});