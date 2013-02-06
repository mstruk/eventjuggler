'use strict';

var myModule = angular.module('eventjugglerServices', [ 'ngResource' ]);

myModule.service('Event', function($resource, $http, User) {

    this.getEvents = function(success) {

        var events = [];

        events.loading = false;

        events.parameters = {
            "first" : 0,
            "max" : 10,
            "sort" : "time"
        };

        events.loadNext = function(success) {
            $resource('/eventjuggler-rest/event/:eventId').query(events.parameters, function(data) {
                if (events.loading)
                    return;

                events.loading = true;

                if (data.length == 0) {
                    return;
                }

                var l = events.length;
                for ( var i = 0; i < data.length; i++) {
                    events[i + l] = data[i];
                }

                events.parameters.first += events.parameters.max;
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
        $resource('/eventjuggler-rest/event/:eventId').get({
            "eventId" : eventId,
            isArray : false
        }, function(data) {
            for ( var i in data) {
                event[i] = data[i];
            }

            event.attending = false;

            if (User.user && event.attendance) {
                for ( var i = 0; i < event.attendance.length; i++) {
                    if (event.attendance[i].login == User.user.login) {
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

        $resource('/eventjuggler-rest/event/mine').query({
            "username" : User.user.login,
            "password" : User.user.password
        }, function(data) {
            var l = events.length;
            for ( var i = 0; i < data.length; i++) {
                events[i + l] = data[i];
            }
        });

        return events;
    };

    this.attend = function(eventId, success) {
        $resource('/eventjuggler-rest/event/:eventId/rsvp').get({
            "eventId" : eventId,
            "username" : User.user.login,
            "password" : User.user.password
        }, success);
    };

    // TODO Should use $resource instead, but problems with it sending wrong
    // content-type
    this.resign = function(eventId, success) {
        var req = new XMLHttpRequest();
        req.open("DELETE", "/eventjuggler-rest/event/" + eventId + "/rsvp?username=" + User.user.login + "&password=" + User.user.password);
        req.onreadystatechange = function() {
            if (req.readyState == 4 && req.status == 204) {
                success();
            }
        };
        req.send();
    };
});

myModule.service('User', function($http, $resource, $cookieStore) {
    var self = this;
    this.user = undefined;

    var userResource = $resource('/eventjuggler-rest/user');

    this.login = function(username, password, success, error) {

        var parameters = {
            "username" : username,
            "password" : password
        };

        userResource.get(parameters, function(data) {
            if (data.login) {
                self.user = data;
                $cookieStore.put("username", username);
                $cookieStore.put("password", password);
                if (success) {
                    success(self.user);
                }
            } else {
                error();
            }
        }, error);
    };

    this.logout = function() {
        self.user = undefined;
        $cookieStore.remove("username");
        $cookieStore.remove("password");
    };

    this.getUser = function(success) {
        if (this.user) {
            success(this.user);
        } else if ($cookieStore.get("username")) {
            this.login($cookieStore.get("username"), $cookieStore.get("password"), success);
        }
    };

    this.register = function(user, success, error) {
        userResource.save(user, success, error);
    };
});