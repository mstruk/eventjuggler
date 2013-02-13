'use strict';

var eventjugglerServices = angular.module('eventjugglerServices', [ 'ngResource' ]);

eventjugglerServices.service('User', function($resource, $http, $cookieStore) {
    var accregisterRes = $resource('/eventjuggler-rest/accregister');
    var signinRes = $resource('/eventjuggler-rest/signin');
    var userInfoRes = $resource('/eventjuggler-rest/userinfo');
    var logoutRes = $resource('/eventjuggler-rest/logout');

    var user = {
        username : null,
        password : null,
        roles : null,
        loggedIn : false
    };

    var loadUserInfo = function(success) {
        userInfoRes.get(function(userInfo) {
            if (userInfo.userId) {
                user.username = userInfo.userId;
                user.name = userInfo.userId;

                if (userInfo.fullName && userInfo.fullName != "null null") {
                    user.name = userInfo.fullName;
                }

                user.roles = userInfo.roles;
                user.loggedIn = true;

                if (success) {
                    success();
                }
            }
        });
    };

    user.login = function(success, error) {
        signinRes.save({
            userId : user.username,
            password : user.password
        }, function(response) {
            if (response.loggedIn) {
                $http.defaults.headers.common['Auth-Token'] = response.token;
                sessionStorage.setItem("auth-token", response.token);

                loadUserInfo(success);
            } else if (error) {
                error();
            }
        }, error);
    };

    user.logout = function() {
        user.username = null;
        user.password = null;
        user.loggedIn = false;

        logoutRes.get();
        sessionStorage.removeItem("auth-token");
    };

    user.register = function(user, success, error) {
        accregisterRes.save(user, function(response) {
            if (response.registered) {
                success();
            } else {
                error(response.status);
            }
        }, error);
    };

    if (!user.loggedIn && sessionStorage.getItem("auth-token")) {
        $http.defaults.headers.common['Auth-Token'] = sessionStorage.getItem("auth-token");

        loadUserInfo();
    }

    return user;
});

eventjugglerServices.service('Event', function($resource, User, $http, $routeParams, $rootScope) {
    var eventsRes = $resource('/eventjuggler-rest/event/:eventId');
    var mineRes = $resource('/eventjuggler-rest/event/mine');
    var rsvpRes = $resource('/eventjuggler-rest/event/:eventId/rsvp');

    this.getEvents = function(success) {
        var events = [];

        events.loading = false;
        events.completed = false;

        events.parameters = {
            first : 0,
            max : 10,
            sort : "time"
        };

        if ($routeParams.query) {
            events.parameters.query = $routeParams.query;
        }

        events.loadNext = function(success) {
            if (events.loading || events.completed)
                return;

            events.loading = true;

            eventsRes.query(events.parameters, function(data) {
                if (data.length > 0) {
                    var l = events.length;
                    for ( var i = 0; i < data.length; i++) {
                        events[i + l] = data[i];
                    }

                    events.parameters.first += events.parameters.max;
                } else {
                    events.completed = true;
                }
                
                events.loading = false;

                if (success) {
                    success(events);
                }
            });
        };

        events.loadNext(success);

        return events;
    };

    this.getEvent = function(eventId, success) {
        var event = {};
        eventsRes.get({
            "eventId" : eventId
        }, function(data) {
            for ( var i in data) {
                event[i] = data[i];
            }

            event.attending = false;

            if (User.loggedIn && event.attendance) {
                for ( var i = 0; i < event.attendance.length; i++) {
                    if (event.attendance[i].login == User.username) {
                        event.attending = true;
                    }
                }
            }

            if (success) {
                success(event);
            }
        });
        return event;
    };

    this.getEventsUser = function() {
        var events = [];

        mineRes.query(null, function(data) {
            var l = events.length;
            for ( var i = 0; i < data.length; i++) {
                events[i + l] = data[i];
            }
        });

        return events;
    };

    this.attend = function(eventId, success) {
        rsvpRes.get({
            "eventId" : eventId
        }, success);
    };

    this.resign = function(eventId, success) {
        rsvpRes['delete']({
            "eventId" : eventId
        }, success);
    };
});