<#import "base.ftl" as base>
<@base.head>
    <#assign topicsActive="active" in base>
</@base.head>
<@base.body>
    <div class="container">
        <div class="row table-responsive ">
            <table class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Length</th>
                    <th>Radix Tree Keys</th>
                    <th>Radix Tree Nodes</th>
                    <th>Group Counts</th>
                    <th>Last Generated Id</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${topicInfo.name}</td>
                    <td>${topicInfo.length}</td>
                    <td>${topicInfo.radixTreeKeys}</td>
                    <td>${topicInfo.radixTreeNodes}</td>
                    <td>${topicInfo.groups}</td>
                    <td>${topicInfo.lastGeneratedId}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="container">
        <#list groupInfos as groupInfo>
            <div class="row table-responsive ">
                <h2>${groupInfo.name}</h2>
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Consumers</th>
                        <th>Pending Count</th>
                        <th>Last Delivered Id</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>${groupInfo.consumers}</td>
                        <td>${groupInfo.pending}</td>
                        <td>${groupInfo.lastDeliveredId}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </#list>
    </div>
</@base.body>