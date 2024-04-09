$(function () {

    const dataTable = $("#data_list").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/deadMessage/pageList",
            data: function (d) {
                const obj = {};
                obj.start = d.start / 10 + 1;
                obj.length = d.length;
                obj.topic = $("#topicName").val();
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        "columns": [
            {data: 'originTopic'},
            {data: 'groupName'},
            {data: 'id'},
            {data: 'message'}
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

});
