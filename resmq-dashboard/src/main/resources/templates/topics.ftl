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
                <#list topicsInfo as topicInfo>
                    <tr>
                        <td><a href="${urlPrefix}/topics/${topicInfo.name}">${topicInfo.name}</a></td>
                        <td>${topicInfo.length}</td>
                        <td>${topicInfo.radixTreeKeys}</td>
                        <td>${topicInfo.radixTreeNodes}</td>
                        <td>${topicInfo.groups}</td>
                        <td>${topicInfo.lastGeneratedId}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </div>
</@base.body>