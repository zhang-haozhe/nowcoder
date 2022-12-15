$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");

    // getting title and content
    const title = $("#recipient-name").val();
    const content = $("#message-text").val();
    //sending async request(POST)
    $.post(
        CONTEXT_PATH + "/discussion/add",
        {
            "title": title,
            "content": content,
        },
        function (data) {
            data = $.parseJSON(data);
            // displaying response message
            $("#hintBody").text(data.msg);
            // displaying modal
            $("#hintModal").modal("show");
            // hiding the modal after 2 sec
            setTimeout(function () {
                $("#hintModal").modal("hide");
                // refreshing page
                if (data.code == 200) {
                    window.location.reload();
                }
            }, 2000);
        }
    );
}