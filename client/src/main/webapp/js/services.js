'use strict';

var eventjugglerServices = angular.module('eventjugglerServices', [ 'ngResource' ]);

eventjugglerServices.service('User', function($resource, $http, $cookieStore) {
    var accregisterRes = $resource('/eventjuggler-server/accregister');
    var signinRes = $resource('/eventjuggler-server/signin');
    var userInfoRes = $resource('/eventjuggler-server/userinfo');
    var logoutRes = $resource('/eventjuggler-server/logout');
    var facebookRes = $resource('/eventjuggler-server/facebook');
    var twitterRes = $resource('/eventjuggler-server/twitter');

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
                    // Remove nulls that are a result of
                    // picketlink-extensions-core UserInfoEndpoint class
                    // where userInfo.setFullName = user.getFirstName() + " " +
                    // user.getLastName()
                    // without checking for null
                    user.name = userInfo.fullName.replace(/\ null$/g, '').replace(/^null\ /g, '');
                }

                user.roles = userInfo.roles;
                user.loggedIn = true;

                if (success) {
                    localStorage.setItem("logged-in", "true");
                    
                    success();
                } else {
                    localStorage.removeItem("logged-in");
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
        localStorage.removeItem("logged-in");
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

    user.loginFacebook = function(success, error) {
        facebookRes.save({}, function(response) {
            if (response.loggedIn) {
                loadUserInfo(success);
            } else if (error) {
                error();
            }
        }, error);
    };

    user.loginTwitter = function(success, error) {
        twitterRes.save({}, function(response) {
            if (response.loggedIn) {
                loadUserInfo(success);
            } else if (error) {
                error();
            }
        }, error);
    };

    if (!user.loggedIn && localStorage.getItem("logged-in")) {
        loadUserInfo();
    }

    return user;
});

eventjugglerServices.service('Event', function($resource, User, $http, $routeParams, $rootScope) {
    var eventsRes = $resource('/eventjuggler-server/events');
    var eventRes = $resource('/eventjuggler-server/event/:eventId');
    var mineRes = $resource('/eventjuggler-server/events/mine');
    var rsvpRes = $resource('/eventjuggler-server/rsvp/:eventId');
    var popularRes = $resource('/eventjuggler-server/events/popular');
    var relatedRes = $resource('/eventjuggler-server/events/related/:eventId');

    this.getEvents = function(success) {
        var events = [];

        events.loading = false;
        events.completed = false;

        events.parameters = {
            first : 0,
            max : 10,
            sort : "time"
        };
        
        if ($routeParams.tag) {
            events.parameters.tag = $routeParams.tag;
        }
        
        if ($routeParams.user) {
            events.parameters.user = $routeParams.user;
        }

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
        eventRes.get({
            "eventId" : eventId
        }, function(data) {
            for ( var i in data) {
                event[i] = data[i];
            }

            event.attending = false;

            if (User.loggedIn && event.attendance) {
                for ( var i = 0; i < event.attendance.length; i++) {
                    if (event.attendance[i].user == User.username) {
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

    this.createEvent = function(event, success, error) {
        var result = eventRes.save(event, success, error);
    };

    this.deleteEvent = function(event, success, error) {
        var result = eventRes.delete({
            "eventId" : event.id
        }, success, error);
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

    this.getEventsPopular = function() {
        var events = [];

        popularRes.query(null, function(data) {
            var l = events.length;
            for ( var i = 0; i < data.length; i++) {
                events[i + l] = data[i];
            }
        });

        return events;
    };

    this.getEventsRelated = function(eventId) {
        var events = [];

        relatedRes.query({
            "eventId" : eventId
        }, function(data) {
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

eventjugglerServices.service('Utils', function() {
    this.uuid = function() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
        };
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    };
});