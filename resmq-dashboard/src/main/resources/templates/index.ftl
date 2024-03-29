<!DOCTYPE html>
<html>
<head>
    <#import "./common/common.macro.ftl" as netCommon>
    <title>消息队列中心</title>
    <@netCommon.commonStyle />
    <!-- daterangepicker -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?? && cookieMap["resmq_adminlte_settings"]?exists && "off" == cookieMap["resmq_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "index" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>运行报表</h1>
        </section>

        <!-- Main content -->
        <section class="content">

            <!-- 报表导航 -->
            <div class="row">
                <div class="col-md-3 col-sm-6 col-xs-12">
                    <div class="info-box bg-blue">
                        <span class="info-box-icon"><i class="fa fa-folder"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">主题数量</span>
                            <span class="info-box-number">${topicCount}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">消息队列主题数量</span>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 col-xs-12">
                    <div class="info-box bg-green">
                        <span class="info-box-icon"><i class="fa fa-inbox"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">消息总量</span>
                            <span class="info-box-number">${totalCount}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">已经投递到主题中的消息总量</span>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 col-xs-12">
                    <div class="info-box bg-yellow">
                        <span class="info-box-icon"><i class="fa fa-clock-o"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">延迟消息</span>
                            <span class="info-box-number">${delayCount}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">待投递的消息总量</span>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 col-xs-12">
                    <div class="info-box bg-red">
                        <span class="info-box-icon"><i class="fa fa-exclamation-circle"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">死信总量</span>
                            <span class="info-box-number">${dlqCount}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">死信队列中的消息总量</span>
                        </div>
                    </div>
                </div>
            </div>

            <#-- 消息报表：时间区间筛选，左侧折线图 + 右侧饼图 -->
            <div class="row">
                <div class="col-md-12">
                    <div class="box">
                        <div class="box-header with-border">
                            <h3 class="box-title">消息报表</h3>

                            <div class="pull-right box-tools">
                                <button type="button" class="btn btn-primary btn-sm daterange pull-right"
                                        data-toggle="tooltip" id="filterTime">
                                    <i class="fa fa-calendar"></i>
                                </button>
                            </div>

                        </div>
                        <div class="box-body">
                            <div class="row">
                                <#-- 左侧折线图 -->
                                <div class="col-md-8">
                                    <div id="lineChart" style="height: 350px;"></div>
                                </div>
                                <#-- 右侧饼图 -->
                                <div class="col-md-4">
                                    <div id="pieChart" style="height: 350px;"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <#-- 系统参数表 -->
            <div class="row">
                <div class="col-md-12">
                    <div class="box">
                        <div class="box-header with-border">
                            <h3 class="box-title">系统运行参数</h3>
                        </div>
                        <div class="box-body">
                            <div class="row">
                                <table class="table table-striped">
                                    <thead>
                                    <tr>
                                        <th>参数名</th>
                                        <th>参数值</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>enable</td>
                                        <td>${resMqProperties.enable?string("true","false")}</td>
                                    </tr>
                                    <tr>
                                        <td>maxQueueSize</td>
                                        <td>${resMqProperties.maxQueueSize}</td>
                                    </tr>
                                    <tr>
                                        <td>deadMessageDeliveryCount</td>
                                        <td>${resMqProperties.deadMessageDeliveryCount}</td>
                                    </tr>
                                    <tr>
                                        <td>deadMessageDeliverySecond</td>
                                        <td>${resMqProperties.deadMessageDeliverySecond}(s)</td>
                                    </tr>
                                    <tr>
                                        <td>deadMessageScheduledThreadPoolCoreSize</td>
                                        <td>${resMqProperties.deadMessageScheduledThreadPoolCoreSize}</td>
                                    </tr>
                                    <tr>
                                        <td>deadMessageTimerInitialDelay</td>
                                        <td>${resMqProperties.deadMessageTimerInitialDelay}(s)</td>
                                    </tr>
                                    <tr>
                                        <td>deadMessageTimerDelay</td>
                                        <td>${resMqProperties.deadMessageTimerDelay}(s)</td>
                                    </tr>
                                    <tr>
                                        <td>pendingMessagesPullCount</td>
                                        <td>${resMqProperties.pendingMessagesPullCount}</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </section>
    </div>
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<#-- echarts -->
<script src="${request.contextPath}/static/plugins/echarts/echarts.common.min.js"></script>
<script src="${request.contextPath}/static/js/index.js"></script>
</body>
</html>