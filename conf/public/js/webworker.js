var leanlife = {};

// importScripts("/lean-life/resource/js/?p=js/request-logger.js");
importScripts("/lean-life/resource/js/?p=js/webworker-console.js");
importScripts("/lean-life/resource/js/?p=js/buffhelper.js");
importScripts("/lean-life/resource/js/?p=js/ajax.js");
importScripts("/lean-life/resource/js/?p=js/jade.js");
importScripts("/lean-life/resource/js/?p=js/md5.js");
importScripts("/lean-life/resource/js/?p=js/prototypes.js");
importScripts("/lean-life/resource/js/?p=js/asciidoctor-browser.js");
var asciidoctor = Asciidoctor({runtime: {platform: 'browser'}});
importScripts("/lean-life/resource/js/?p=js/asciidoctor-docbook.js");
Asciidoctor.DocBook();
importScripts("/lean-life/resource/js/?p=js/asciidoctor-data-line.js");
importScripts("/lean-life/resource/js/?p=js/asciidoctor-data-uri.js");
importScripts("/lean-life/resource/js/?p=js/asciidoctor-chart-block.js");
importScripts("/lean-life/resource/js/?p=js/asciidoctor-extension-helpers.js");
importScripts("/lean-life/resource/js/?p=js/asciidoctor-block-extensions.js");

// // TODO: compare behaviour for asciimath
importScripts("/lean-life/resource/js/?p=js/asciidoctor-block-macro-extensions.js");
importScripts("/lean-life/resource/js/?p=js/asciidoctor-inline-macro-extensions.js");

importScripts("/lean-life/resource/js/?p=js/asciidoctor-reveal.js");
importScripts("/lean-life/resource/js/?p=js/asciidoctor-deck.js");
importScripts("/lean-life/resource/js/?p=js/outliner.js");
importScripts("/lean-life/resource/js/?p=js/webworker-converters.js");

self.onerror = function (e) {
    console.error(e);
};

var lastTaskId = "";
self.onmessage = function (e) {
    try {

        if (!e.data) {
            return;
        }

        var data = JSON.parse(e.data) || {};

        var func = data.func;
        var content = data.content;
        var options = data.options;
        var taskId = data.taskId;
        lastTaskId = taskId || "";

        if (func && func in self) {
            var self2 = self[func];
            if(self2){
                self2(taskId, content, options);
            }

        }

    }
    catch (e) {
        console.error(e);
    }
};
