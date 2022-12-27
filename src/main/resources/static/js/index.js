$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");

    // before sending Ajax request, setting the CSRF token in the request header
    // const token = $("meta[name='_csrf']").attr("content");
    // const header = $("meta[name='_csrf_header']").attr("content");
    // $(document).ajaxSend((e, xhr, options) => {
    //     xhr.setRequestHeader(header, token);
    // })

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