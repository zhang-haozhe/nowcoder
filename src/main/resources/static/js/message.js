$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");

    const toName = $("#recipient-name").val();
    const content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/message/send",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintBody").text("Success!");
            } else $("#hintBody").text(data.msg);

            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000)
        }
    )

    $("#hintModal").modal("show");
    setTimeout(function () {
        $("#hintModal").modal("hide");
    }, 2000);
}

function delete_msg() {
    // TODO 删除数据
    $(this).parents(".media").remove();
}