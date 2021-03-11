var progressBarStatus = 0;
var allElements = [];

//allElements.indexOf


$(".left").click(function () {
    if (progressBarStatus > 0) {
        progressBarStatus -= 2.5;
    }
    $(".progress-bar").css({ width: progressBarStatus + '%' });

    if (progressBarStatus == 0) {
        return false;
    }
})

$(".right").click(function () {
    if (progressBarStatus != 100) {
        progressBarStatus += 2.5;
        //progressBarStatus += 50;
    }
    $(".progress-bar").css({ width: progressBarStatus + '%' });

    if (progressBarStatus == 100) {
        $(".GoToRecomandations").css("visibility", "visible");

        var tempList = new Array();
        for (var i = 1; i <= 40; i++) {
            tempList.push($('input[name=number]:checked', '#checked_radio_form_' + i).val());
        }

        allElements = tempList;

        return false;
    }

})

$(".GoToRecomandations").click(function () {

    var postData = { values: allElements };

    $.ajax({
        type: "POST",
        url: "/Recomandations/GetPredominantPersonality",
        data: postData,
        //success: function (data) {
        //    alert(data.Result);
        //},

        success: function (data) {
            if (data.isRedirect) {
                window.location.href = data.redirectUrl;
            }
        },

        dataType: "json",
        traditional: true
    });
});


$(".left").click(function () {
    var val = $('#carousel-example-generic .active').index('#carousel-example-generic .item');
});

$(".right").click(function () {
    var val = $('#carousel-example-generic .active').index('#carousel-example-generic .item');
});

//$(".right").click(function () {
//    if ($('input[name=number]:checked', '#checked_radio_form_' + i).val() === "undefined")
//        alert("You haven't choose any answers");
//});