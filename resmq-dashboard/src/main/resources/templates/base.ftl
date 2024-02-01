<#macro head>
    <!DOCTYPE html>
    <html lang="en">
    <#include "head.ftl">
    <body>
    <#nested>
    <#include "header.ftl">
</#macro>
<#macro body>
    <main id="main">
        <#nested>
    </main>
    </body>
    </html>
</#macro>