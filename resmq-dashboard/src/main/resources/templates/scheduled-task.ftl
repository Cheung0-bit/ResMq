<#import "base.ftl" as base>
<@base.head>
    <#assign scheduledActive="active" in base>
</@base.head>
<@base.body>
    <div class="container">
        <div class="row table-responsive ">
            <h2>${delayTopic}</h2>
            <#if scheduledTasks?size==0>
                <h3>No Scheduled Tasks Now...</h3>
            </#if>
            <table class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th>Message Content</th>
                    <th>End Time</th>
                    <th>IsExpired</th>
                </tr>
                </thead>
                <tbody>
                <#list scheduledTasks as task>
                    <tr>
                        <td>${task.message}</td>
                        <td>${task.deadline}</td>
                        <td>
                            <#if task.expired>
                                <span class="text-danger font-weight-bold">Expired!</span>
                            <#else>
                                <span class="text-success font-weight-bold">Waiting...</span>
                            </#if>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
</@base.body>