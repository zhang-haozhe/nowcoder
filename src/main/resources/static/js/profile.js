$(function () {
    $(".follow-btn").click(follow);
});

function follow() {
    var btn = this;
    if ($(btn).hasClass("btn-info")) {
        // follow
        $.post(
            CONTEXT_PATH + "/follow",
            {"entityType": 3, "entityId": $(btn).prev().val()},
            (data) => {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    window.location.reload();
                } else {
                    alert(data.msg);
                }
            }
        )
        // $(btn).text("Followed").removeClass("btn-info").addClass("btn-secondary");
    } else {
        // Unfollow
        $.post(
            CONTEXT_PATH + "/unfollow",
            {"entityType": 3, "entityId": $(btn).prev().val()},
            (data) => {
                data = $.parseJSON(data);
                if (data.code === 0) {
                    window.location.reload();
                } else {
                    alert(data.msg);
                }
            }
        )
        // $(btn).text("Follow").removeClass("btn-secondary").addClass("btn-info");
    }
}