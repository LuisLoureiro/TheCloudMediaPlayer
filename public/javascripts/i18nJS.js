window.loadI18nJS = function () {
    var jq,
        head,
        args = arguments,
        stringFormatArgs,
        messagesUrl = '/assets/messages/i18nJS.{0}',
        language = window.navigator.language || window.navigator.userLanguage || window.navigator.browserLanguage,
        successFunc = function (data) {
            window.I18nJS = JSON.parse(data);
            $.each(args, function (idx, resourceUrl) {
                $.getScript(resourceUrl);
            });
        },
        failFunc = function () {
            if (language.indexOf('-') !== -1) {
                /* Trying to load the messages file that has a name with the language code and without the contry code.*/
                $.get(messagesUrl.format(language.substring(0, language.indexOf('-'))), successFunc)
                    .fail(function () {console.log('Fail!!'); });
            }
        },
        loadFunc = function () {
            /* Load the file with the translated messages. */
            $.get(messagesUrl.format(language), successFunc).fail(failFunc);
        };
    /* String.format */
    if (!String.prototype.format) {
        String.prototype.format = function () {
            stringFormatArgs = arguments;
            return this.replace(/\{([0-9]+)\}/g, function (match, number) {
                return typeof stringFormatArgs[number] !== 'undefined' ? stringFormatArgs[number] : match;
            });
        };
    }
    /* Check for jQuery and load it if not present. */
    if (!window.jQuery) {
        jq = document.createElement('script');
        head = document.head || document.getElementsByTagName('head')[0] || document.documentElement;
        jq.type = 'text/javascript';
        jq.src = 'http://code.jquery.com/jquery-1.10.2.min.js';
        jq.onload = jq.onreadystatechange = function () {
            if (!jq.readyState || /loaded|complete/.test(jq.readyState)) {
                jq.onload = jq.onreadystatechange = null;
                jq = undefined;
                
                loadFunc();
            }
        };
        head.appendChild(jq);
    } else {
        loadFunc();
    }
};