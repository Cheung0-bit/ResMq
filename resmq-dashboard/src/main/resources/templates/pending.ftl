<#import "base.ftl" as base>
<@base.head>
    <#assign pendingActive="active" in base>
</@base.head>
<@base.body>
    <div class="container">
        <#if pdm?size==0>
            <h2>No Pending Queue Now</h2>
        <#else>
            <#list pdm as messageSummary>
                <h2 class="text-danger">${messageSummary.originTopic}</h2>
                <#list messageSummary.commonGroupMessages as commonGroupMessage>
                    <h4 class="text-warning">${commonGroupMessage.groupName}</h4>
                    <div class="row table-responsive ">
                        <table class="table table-bordered table-hover">
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Message</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list commonGroupMessage.commonMessages as pendingMessage>
                                <tr>
                                    <td>${pendingMessage.id}</td>
                                    <td>${pendingMessage.message}</td>
                                </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </#list>
            </#list>
        </#if>
    </div>
</@base.body>