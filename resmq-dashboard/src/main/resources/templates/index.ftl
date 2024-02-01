<#import "base.ftl" as base>
<@base.head>
</@base.head>
<@base.body>
    <div class="container">
        <div class="row">
            <h2>Runtime Properties</h2>
            <table class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th>Key</th>
                    <th>Value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td> enable</td>
                    <td> ${resMqProperties.enable?string("true","false")} </td>
                </tr>
                <tr>
                    <td> max-queue-size</td>
                    <td> ${resMqProperties.maxQueueSize} </td>
                </tr>
                <tr>
                    <td> dead-message-delivery-count</td>
                    <td> ${resMqProperties.deadMessageDeliveryCount} </td>
                </tr>
                <tr>
                    <td> dead-message-delivery-second</td>
                    <td> ${resMqProperties.deadMessageDeliverySecond} </td>
                </tr>
                <tr>
                    <td> dead-message-scheduled-thread-pool-core-size</td>
                    <td> ${resMqProperties.deadMessageScheduledThreadPoolCoreSize} </td>
                </tr>
                <tr>
                    <td> dead-message-timer-initial-delay</td>
                    <td> ${resMqProperties.deadMessageTimerInitialDelay} </td>
                </tr>
                <tr>
                    <td> dead-message-timer-delay</td>
                    <td> ${resMqProperties.deadMessageTimerDelay} </td>
                </tr>
                <tr>
                    <td> pending-messages-pull-count</td>
                    <td> ${resMqProperties.pendingMessagesPullCount} </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="row">
            <#if resMqProperties.streams?size!=0>
                <h2>Simple Streams</h2>
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Service Name</th>
                        <th>Topic</th>
                        <th>Group</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list resMqProperties.streams as key,value>
                        <tr>
                            <td>${key}</td>
                            <td>${value.topic}</td>
                            <td>${value.group}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            <#else>
            </#if>
        </div>
    </div>
</@base.body>