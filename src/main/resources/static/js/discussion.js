$(function () {
    $("#pinBtn").click(setPin);
    $("#featureBtn").click(setFeature);
    $("#deleteBtn").click(setDelete);
})


function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "postId": postId},
        (data) => {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus === 1 ? 'Liked' : 'Like');
            } else {
                alert(data.msg);
            }
        }
    )
}

function setPin() {
    $.post(
        CONTEXT_PATH + "/discussion/pin",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("pinBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    )
}

function setFeature() {
    $.post(
        CONTEXT_PATH + "/discussion/feature",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("featureBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    )
}

function setDelete() {
    $.post(
        CONTEXT_PATH + "/discussion/delete",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    )
}