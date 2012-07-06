window.code_editor = function() {
    this.editor = ace.edit("editor");
    this.editor.setTheme("ace/theme/twilight");
    this.editor.setShowPrintMargin(false);
    this.debug = function() {
        var contents = editor.getSession().getValue();
        $('.debug').html(contents);
    }
    this.editor.getSession().on('change', _.debounce(this.debug, 10));
}

$(function() {
      window.code_editor();
});