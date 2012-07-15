window.code_editor = function() {
    this.editor = ace.edit("editor");
    this.editor.setTheme("ace/theme/twilight");
    this.editor.setShowPrintMargin(false);
    this.broadcast = function() {
        var contents = editor.getSession().getValue();
        var output = {
            content: contents
        }
        $('.debug').html(JSON.stringify(output)); //change this to POST to a url
    }
    //this.editor.getSession().on('change', _.debounce(this.broadcast, 10));
}

window.poll_session = function(session_hash) {
    $.ajax({
        url: "http://localhost:8080/session/" + session_hash,
        success: function(data, status, xhr) {
            var contents = data;
            window.editor.getSession().setValue(contents);
        },
        error: function(data, status, xhr) {alert("something went wrong");}
    });
}

$(function() {
    window.code_editor();
    $('#connect-btn').on('click', function() {
        var session_hash = $('#session-hash').val();
        window.poll_session(session_hash);
    })
});
