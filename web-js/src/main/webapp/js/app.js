'use strict';

var eventjugglerModule = angular.module('eventjuggler', [ 'eventjugglerServices', 'ngCookies' ]);
var loadCount = 0;

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
    return function(text, length, end) {
        if (!text) {
            return text;
        }

        if (isNaN(length)) {
            length = 10;
        }

        if (end === undefined) {
            end = "";
        }

        if (text.length <= length || text.length - end.length <= length) {
            return text;
        } else {
            return String(text).substring(0, length - end.length) + end;
        }
    };
});

eventjugglerModule.filter('removehtml', function () {
    return function(text) {
        return text.replace(/<(?:.|\n)*?>/gm, '');
    };
});

eventjugglerModule.filter('eventDate', function($filter) {
    return function(date) {
        if (!date) {
            return date;
        }

        var d = new Date(date);
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        d.setMilliseconds(0);

        var today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        today.setMilliseconds(0);

        if (d.getTime() == today.getTime()) {
            return "Today";
        } else if (d.getTime() <= today.getTime() + 7 * 24 * 60 * 60 * 1000) {
            return $filter('date')(date, 'EEE');
        } else if (d.getFullYear() == today.getFullYear()) {
            return $filter('date')(date, 'd MMM');
        } else {
            return $filter('date')(date, 'd MMM, yyyy');
        }
    };
});

eventjugglerModule.config(function($httpProvider) {
    $httpProvider.responseInterceptors.push('loadingInterceptor');
    var spinnerFunction = function(data, headersGetter) {
        loadCount++;
        $('#loading').show();
        return data;
    };
    $httpProvider.defaults.transformRequest.push(spinnerFunction);
});

eventjugglerModule.factory('loadingInterceptor', function($q, $window) {
    return function(promise) {
        return promise.then(function(response) {
            loadCount--;
            if (loadCount == 0) {
                $('#loading').hide();
            }
            return response;
        }, function(response) {
            loadCount--;
            if (loadCount == 0) {
                $('#loading').hide();
            }
            return $q.reject(response);
        });
    };
});

eventjugglerModule.directive('ngBackgroundImage', function($timeout, dateFilter) {
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