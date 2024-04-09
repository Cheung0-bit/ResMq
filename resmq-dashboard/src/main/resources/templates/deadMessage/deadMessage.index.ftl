<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <title>死信队列</title>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?? && cookieMap["resmq_adminlte_settings"]?? && "off" == cookieMap["resmq_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "deadMessage" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>死信管理<small></small></h1>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="row">
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">主题</span>
                        <select class="form-control" id="topicName">
                            <option value="-1">全部</option>
                            <option value="0">无</option>
                            <#list topicList as topicInfo>
                                <option value="${topicInfo.name}">${topicInfo.name}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="col-xs-2 col-xs-offset-6">
                    <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="data_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th>从属主题</th>
                                    <th>消费分组</th>
                                    <th>消息ID</th>
                                    <th>消息内容</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                                <tfoot></tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- footer -->
    <@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>

<script src="${request.contextPath}/static/js/deadMessage.index.1.js"></script>

</body>
</html>
