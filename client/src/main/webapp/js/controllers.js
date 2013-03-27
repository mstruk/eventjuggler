'use strict';

function EventListCtrl($scope, Event, $routeParams, $location) {
    $scope.events = Event.getEvents(function loadUntilPageIsFull(events) {
        $scope.events = events;
        $scope.$watch('events', function() {
            if ($("body").height() < $(window).height() && !$scope.events.completed) {
                $scope.events.loadNext(loadUntilPageIsFull);
            }
        });
    });

    $scope.popular = Event.getEventsPopular();

    $(window).scroll(function() {
        if ($(window).scrollTop() == $(document).height() - $(window).height()) {
            $scope.loading = true;
            $scope.events.loadNext();
        }
    });

    var currentPath = $location.path();
    $scope.$watch(function() {
        return $location.path();
    }, function() {
        if ($location.path() != currentPath) {
            $(window).unbind("scroll");
        }
    });
}

function EventCreateCtrl($scope, Event, $routeParams, $location) {
    $scope.e = {};
    
    if ($routeParams.eventId) {
        $scope.e = Event.getEvent($routeParams.eventId, function(event) {
            delete $scope.e.attending;
            
            if ($scope.e.tags) {
                $scope.tags = $scope.e.tags.join();
            }
            
            if ($scope.e.time) {
                var date = new Date($scope.e.time);
                $scope.year = date.getFullYear();
                $scope.month = date.getMonth() + 1;
                $scope.date = date.getDate();
            }
        });
    }

    $scope.create = function() {
        $scope.e.time = new Date($scope.year, $scope.month - 1, $scope.date).getTime();
        
        if ($scope.tags) {
            $scope.e.tags = $scope.tags.split(",");
            $scope.e.tags.forEach(function(v, i, a) { a[i] = v.trim(); });
        }
        
        delete $scope.e.year;
        delete $scope.e.month;
        delete $scope.e.date;
        
        $scope.created = false;
        $scope.failed = false;
        
        var createEvent = function() { Event.createEvent($scope.e, function(result) {
            $location.url("/events");
        }, function(status) {
            $scope.status = "failed";
        });
        }
        
        var image = document.getElementById("imageFile").files;
        if (image.length > 0) {
            $scope.e.imageId = image[0].name;
            
            var xhr = new XMLHttpRequest();
            xhr.open("POST", "/eventjuggler-server/image/" + image[0].name, true);
            xhr.setRequestHeader("Content-type", "application/octet-stream");
            xhr.send(image[0]);
            
            xhr.onreadystatechange = function(){ 
                if ( xhr.readyState == 4 ) { 
                    if ( xhr.status == 200 ) {
                        $scope.$apply(function() {
                                createEvent();
                        });
                    } else {
                        $scope.$apply(function() {
                            $scope.status = "failed";
                        });
                    }
                }
            }
        } else {
            createEvent();
        }
    };
}

function EventSearchCtrl($scope, Event, $location) {
    $scope.search = function() {
        $location.url("/events?query=" + $scope.query);
        delete $scope.query;
    };
}

function EventMineCtrl($scope, Event) {
    $scope.events = Event.getEventsUser();
}

function EventDetailCtrl($scope, $routeParams, $location, Event, User) {
    $scope.event = Event.getEvent($routeParams.eventId);

    $scope.related = Event.getEventsRelated($routeParams.eventId);

    $scope.attend = function() {
        Event.attend($scope.event.id, function() {
            $scope.event = Event.getEvent($routeParams.eventId);
        });
    };

    $scope.resign = function() {
        Event.resign($scope.event.id, function() {
            $scope.event = Event.getEvent($routeParams.eventId);
        });
    };

    $scope.edit = function() {
        $location.url("/events/" + $scope.event.id + "/edit");
    };
    
    $scope.delete = function() {
        Event.deleteEvent($scope.event, function() {
            $location.url("/events");
        });
    };
}


var oauthPopup;
var loginSuccess;
var loginFailed;

function sendMainPage() {
    oauthPopup.close();
    loginSuccess();
}

function sendMainPageError() {
    oauthPopup.close();
    loginFailed();
}

function UserCtrl($scope, User, $location) {
    $scope.user = User;

    $scope.login = function() {
        $scope.failed = false;

        User.login(function() {
            $('#loginModal').modal('hide');
            $location.path('/events');
        }, function() {
            $scope.failed = true;
        });
    };

    $scope.loginFacebook = function () {
        $scope.failed = false;

        loginSuccess = function() {
            User.loginFacebook(function() {
                $('#loginModal').modal('hide');
                $location.path('/events');
            }, function() {
                $scope.failed = true;
            });
        }

        loginFailed = function() {
            $scope.failed = true;
        }

        oauthPopup = window.open("/eventjuggler-server/facebook", "name", "height=768, width=1024");
        oauthPopup.focus();
    };

    $scope.loginTwitter = function () {
        $scope.failed = false;

        loginSuccess = function() {
            User.loginTwitter(function() {
                $('#loginModal').modal('hide');
                $location.path('/events');
            }, function() {
                $scope.failed = true;
            });
        }

        oauthPopup = window.open("/eventjuggler-server/twitter", "name", "height=768, width=1024");
        oauthPopup.focus();
    };
}

function RegisterCtrl($scope, User) {
    $scope.u = {};

    $scope.register = function() {
        $scope.registered = false;
        $scope.failed = false;

        User.register($scope.u, function(response) {
            $scope.registered = true;
            $scope.u = {};
        }, function(status) {
            $scope.failed = true;
            $scope.failedMessage = status;
        });
    };
}
