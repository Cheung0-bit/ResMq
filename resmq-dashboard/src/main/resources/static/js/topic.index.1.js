$(function () {

    const dataTable = $("#data_list").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/topic/pageList",
            data: function (d) {
                const obj = {};
                obj.start = d.start / 10 + 1;
                obj.length = d.length;
                obj.topic = $("#topic").val();
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        "columns": [
            {data: 'name'},
            {data: 'length'},
            {data: 'radixTreeKeys'},
            {data: 'radixTreeNodes'},
            {data: 'groups'},
            {data: 'lastGeneratedId'},
            {
                data: 'groupInfos',
                ordering: true,
                render: function (data, type, row) {
                    return '<a href="javascript:;" class="showGroupInfos" _topicName="' + row.name + '">查看</a>';
                }
            },
        ],
        "language": {
            "sProcessing": "处理中...",
            "sLengthMenu": "每页 _MENU_ 条记录",
            "sZeroRecords": "没有匹配结果",
            "sInfo": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 ) 总记录数 _MAX_ ",
            "sInfoEmpty": "无记录",
            "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix": "",
            "sSearch": "搜索:",
            "sUrl": "",
            "sEmptyTable": "表中数据为空",
            "sLoadingRecords": "载入中...",
            "sInfoThousands": ",",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上页",
                "sNext": "下页",
                "sLast": "末页"
            },
            "oAria": {
                "sSortAscending": ": 以升序排列此列",
                "sSortDescending": ": 以降序排列此列"
            }
        }
    });

    // search btn
    $('#searchBtn').on('click', function () {
        dataTable.fnDraw();
    });

    // msg 弹框
    $("#data_list").on('click', '.showGroupInfos', function () {
        const _topicName = $(this).attr('_topicName');
        $.ajax({
            type: 'POST',
            url: base_url + '/topic/groupInfos',
            data: {"topic": _topicName},
            dataType: "json",
            success: function (data) {
                ComAlertTec.show(data.data);
            }
        });
    });


});

// Com Alert by Tec theme
var ComAlertTec = {
    html: function () {
        var html =
            '<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
            '<div class="modal-dialog">' +
            '<div class="modal-content-tec">' +
            '<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' +
            '<div class="modal-footer">' +
            '<div class="text-center" >' +
            '<button type="button" class="btn btn-info ok" data-dismiss="modal" >确认</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';
        return html;
    },
    show: function (msg, callback) {
        // dom init
        if ($('#ComAlertTec').length == 0) {
            $('body').append(ComAlertTec.html());
        }

        // init com alert
        $('#ComAlertTec .alert').html(msg);
        $('#ComAlertTec').modal('show');
        $('#ComAlertTec .ok').click(function () {
            $('#ComAlertTec').modal('hide');
            if (typeof callback == 'function') {
                callback();
            }
        });
    }
};
