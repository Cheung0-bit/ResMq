<#import "base.ftl" as base>
<@base.head>
    <#assign deadActive="active" in base>
</@base.head>
<@base.body>
    <div class="container">
        <#if dlq?size==0>
            <h2>No Dead Letter Now</h2>
        <#else>
            <#list dlq as messageSummary>
                <h2 class="text-danger">${messageSummary.originTopic}</h2>
                <#list messageSummary.deadGroupMessages as deadGroupMessage>
                    <h4 class="text-warning">${deadGroupMessage.groupName}</h4>
                    <div class="row table-responsive ">
                        <table class="table table-bordered table-hover">
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Message</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list deadGroupMessage.deadMessages as deadMessage>
                                <tr>
                                    <td>${deadMessage.id}</td>
                                    <td>${deadMessage.message}</td>
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