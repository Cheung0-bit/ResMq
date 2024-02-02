<#import "base.ftl" as base>
<@base.head>
    <#assign scheduledActive="active" in base>
</@base.head>
<@base.body>
    <div class="container">
        <div class="row table-responsive ">
            <#if scheduledMessages?size==0>
                <h2>No Scheduled Topics Now...</h2>
            <#else>
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Scheduled Name</th>
                        <th>Delay Task Count</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list scheduledMessages as scheduledMessage>
                        <tr>
                            <td>
                                <a href="${urlPrefix}/scheduled/${scheduledMessage.scheduledName}">${scheduledMessage.scheduledName}</a>
                            </td>
                            <td>${scheduledMessage.messages}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </#if>
        </div>
    </div>
</@base.body>