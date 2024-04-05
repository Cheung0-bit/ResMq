<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <title>消息记录</title>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?? && cookieMap["resmq_adminlte_settings"]?exists && "off" == cookieMap["resmq_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "message" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>消息管理<small></small></h1>
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

                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">消息主题</span>
                        <input type="text" class="form-control" id="topic" autocomplete="on"
                               placeholder="请输入消息主题，支持正则匹配">
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
                                    <th>主题名</th>
                                    <th>Stream长度</th>
                                    <th>Radix树长度</th>
                                    <th>Radix树节点数</th>
                                    <th>广播组数</th>
                                    <th>最后偏移</th>
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
<#--<script src="${request.contextPath}/static/js/message.index.1.js"></script>-->

</body>
</html>
