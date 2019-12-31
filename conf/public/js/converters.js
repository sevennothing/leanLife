var myWorker = new Worker("/lean-life/worker/js/?p=js/webworker.js");
var asciidoctor= Asciidoctor({runtime: {platform: 'browser'}});
myWorker.onmessage = function (e) {

    var data = (typeof e.data) == "string" ? JSON.parse(e.data) : e.data;

    if (data.type == "log") {

        var logLevel = data.level;

        if (logLevel) {
            if (logLevel == "error") {
                leanlife.completeWebWorkerExceptionally(data.taskId);
            }

            leanlife[logLevel].call(leanlife, JSON.stringify(data.message));
        }
    }
    else if (data.type == "afx") {
        leanlife[data.func].apply(leanlife, data.parameters);
    }

};

myWorker.onerror = function (e) {
    var data = (typeof e) == "string" ? e : e.message;
    leanlife["error"].call(afx, data);
};

myWorker.postMessage();

function getOption(options) {
    return Opal.hash(JSON.parse(options));
}

var fillOutAction = new BufferedAction();

function convertBackend(taskId, content, options) {
    var message = {
        func: arguments.callee.caller.name,
        taskId: taskId,
        content: content,
        options: options
    };

    myWorker.postMessage(JSON.stringify(message));
}

function convertAsciidoc(taskId, content, options) {
    convertBackend(taskId, content, options);
}

function convertOdf(taskId, content, options) {
    var doc = asciidoctor.$load(content, getOption(options));
    var rendered = doc.$convert();

    leanlife.completeWebWorker(taskId, rendered, doc.$backend(), doc.doctype);
}

function convertHtml(taskId, content, options) {
    convertBackend(taskId, content, options);
}

function convertDocbook(taskId, content, options) {
    convertBackend(taskId, content, options);
}

function findRenderedSelection(content) {
    return asciidoctor.$render(content);
}