$(document).ready(function () {
    $('.btnFollow').click(function (event) {
        alert(event.target.id + ' followed!! :)');

        var url = $('.btnFollow').data("url");

        $.get(url, { email: event.target.id, toFollow: "true" });
    });
});

$(document).ready(function () {
    $('.btnUnfollow').click(function () {
        alert(event.target.id + ' unfollowed!! :(');

        var url = $('.btnUnfollow').data("url");

        $.get(url, { email: event.target.id, toFollow: "false" });    
    });
});